#!/bin/sh

# Exit on error
set -e

export TERM=${TERM:-dumb}

CODEDIR="$HOME/code"

if [ -d $CODEDIR ]; then
  # cd to the code directory mounted from host
  cd $CODEDIR
  # Restore custom DB
  createdb -U bjondhealth -h db admitone

  # Build, migrate and deploy app
  npm install --save-dev browserfy
  npm install --save-dev babel-cli
  npm install --save-dev react
  npm install --save-dev react-dom
  npm install --save-dev babel-preset-react
  npm install --save-dev babel-preset-es2015
  npm install --save-dev react-button 
  npm install --save-dev jquery
  
  gradle flywayMigrate
  gradle
  gradle deploy
  # Start wildfly
  $JBOSS_HOME/bin/standalone.sh -b 0.0.0.0
else
  echo "code directory $CODEDIR not found"
  exit 1
fi
