#!/bin/bash

DB_NAME=${DB_NAME:-}
DB_USER=${DB_USER:-}
DB_PASS=${DB_PASS:-}
PG_CONFDIR="/var/lib/pgsql/9.5/data"

__create_user() {
  #Grant rights
  usermod -G wheel postgres

  # Check to see if we have pre-defined credentials to use
if [ -n "${DB_USER}" ]; then
  if [ -z "${DB_PASS}" ]; then
    echo ""
    echo "WARNING: "
    echo "No password specified for \"${DB_USER}\". Generating one"
    echo ""
    DB_PASS=$(pwgen -c -n -1 12)
    echo "Password for \"${DB_USER}\" created as: \"${DB_PASS}\""
  fi
    echo "Creating user \"${DB_USER}\"..."
    echo "CREATE ROLE ${DB_USER} with CREATEROLE login superuser PASSWORD '${DB_PASS}';" |
      sudo -u postgres -H /usr/pgsql-9.5/bin/postgres --single \
       -c config_file=${PG_CONFDIR}/postgresql.conf -D ${PG_CONFDIR}
  
fi

if [ -n "${DB_NAME}" ]; then
  echo "Creating database \"${DB_NAME}\"..."
  echo "CREATE DATABASE ${DB_NAME};" | \
    sudo -u postgres -H /usr/pgsql-9.5/bin/postgres --single \
     -c config_file=${PG_CONFDIR}/postgresql.conf -D ${PG_CONFDIR}

  if [ -n "${DB_USER}" ]; then
    echo "Granting access to database \"${DB_NAME}\" for user \"${DB_USER}\"..."
    echo "GRANT ALL PRIVILEGES ON DATABASE ${DB_NAME} to ${DB_USER};" |
      sudo -u postgres -H /usr/pgsql-9.5/bin/postgres --single \
      -c config_file=${PG_CONFDIR}/postgresql.conf -D ${PG_CONFDIR}
  fi
fi
}

# Call all functions
__create_user
