#!/bin/sh

# Exit on error
set -e

export TERM=${TERM:-dumb}

CODEDIR="$HOME/code"

if [ -d $CODEDIR ]; then
  # cd to the code directory mounted from host
  cd $CODEDIR
  # Restore custom DB
  dropdb -U bjondhealth -h db bjondhealth
  createdb -U bjondhealth -h db bjondhealth
  if [ -f $DB_DUMP ]; then
    pg_restore -Fc --clean -U bjondhealth -h db -d bjondhealth $DB_DUMP || echo "Dont exit if restore has errors, it always does" 
  fi
  # Build, migrate and deploy app
  npm install --save-dev browserfy
  npm install --save-dev babel-cli
  npm install --save-dev react
  npm install --save-dev react-dom
  npm install --save-dev babel-preset-react
  npm install --save-dev babel-preset-es2015
  npm install --save-dev react-button 
  
  gradle flywayclean flywayMigrate
  gradle
  gradle deployroot
  # Start wildfly
  $JBOSS_HOME/bin/standalone.sh -b 0.0.0.0
else
  echo "code directory $CODEDIR not found"
  exit 1
fi
