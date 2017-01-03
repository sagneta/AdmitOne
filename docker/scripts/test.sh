#!/bin/bash

# Exit on error
set -e
# Check for uninitialized variables
set -u

# Some vars
export TERM=${TERM:-dumb}
CODEDIR="$HOME/code"
NPMDIR="$HOME/.local"
GRADLE_OPTS=-Dgradle.user.home=$HOME/.gradle

# This is executed on EXIT; essentially it's a finally clause even in -e.
finally() {
  RET=$?
  if [ $RET -ne 0 ]; then
    echo 'Error raised during build/test.'
  else
    echo "Build/Test process succeeded!"
  fi
  exit $RET
}

prettyecho() {
  text=$1

  echo
  echo
  echo "********** $1 **********"
}

travis_wait_test() {
  local cmd="$@"
  local log_file=travis_wait_test_$$.log

  time $cmd > $log_file 2>&1 &
  local cmd_pid=$!

  while   ps -p $cmd_pid -o pid= > /dev/null 2>&1
  do
    echo Test process $cmd_pid is active, tests are still running, Free Memory: $(free -m | head -2 | tail -fn 1 | awk '{print $7}')MB....
    sleep 60
  done

  wait $cmd_pid
  local result=$?
  cat $log_file
  exit $result
}

trap finally EXIT

if [ -d $CODEDIR ]; then

  prettyecho "Available VM resources"
  echo Total Available Ram: $(free -m | head -2 | tail -fn 1 | awk '{print $2}')MB
  echo Total Available CPU: $(cat /proc/cpuinfo | grep processor | wc -l)

  prettyecho "Buildstamp, migrate and build"
  cd $CODEDIR
  time gradle clean cleanBuildstamp buildstamp flywayMigrate assemble

  prettyecho "Install nodejs as user jboss"
  cd $HOME
  wget  http://nodejs.org/dist/v5.4.1/node-v5.4.1-linux-x64.tar.gz > /dev/null 2>&1
  mkdir -p $NPMDIR
  tar --strip-components 1 -xzvf node-v* -C $NPMDIR > /dev/null 2>&1
  rm -f node-v5.4.1-linux-x64.tar.gz
  export "PATH=$HOME/.local/bin:$PATH"

  prettyecho 'Running client side diagnostics'
  cd $CODEDIR/clients/html5-desktop
  # Install required npm modules
  prettyecho "Running npm install karma-phantomjs-launcher phantomjs-prebuilt karma-firefox-launcher"
  time npm install karma-phantomjs-launcher phantomjs-prebuilt karma-firefox-launcher >/dev/null
  prettyecho "Running npm install -g grunt-cli"
  time npm install -g grunt-cli >/dev/null
  prettyecho "Running npm install"
  time npm install >/dev/null
  # Run grunt
  time grunt lint
  time grunt test
 
  cd $CODEDIR
  prettyecho 'Installing build dependencies'
  time npm install -g generator-angular > /dev/null

  prettyecho "Evaluating Production Code"
  time gradle findbugsMain

  prettyecho "Evaluating Test Code"
  time gradle findbugsTest

  prettyecho "Executing All Unit and Integration Tests"
  time gradle :libraries:bjond-clj:test :services:server-core:test

  prettyecho "Finished running all tests"
else
  prettyecho "code directory $CODEDIR not found"
  exit 1
fi

# Explicit exit required for docker to stop
makepretty "Finished all tests"
exit 0
