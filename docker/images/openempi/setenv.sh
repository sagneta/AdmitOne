#!/bin/bash
# Define environment variables required for Tomcat

export OPENEMPI_HOME=/etc/opt/openempi-3.1.0
export CATALINA_OPTS="-Xms128m -Xmx2048m -XX:MaxPermSize=512m \
                      -Dopenempi.home=$OPENEMPI_HOME \
                      -Djdbc.url=jdbc\:postgresql\://$OPENEMPI_DB_HOST:$OPENEMPI_DB_PORT/$OPENEMPI_DB_NAME"