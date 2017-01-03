#!/bin/bash
# Populate database schema with scripts
set -x

psql -U ${DB_USER} -d ${DB_NAME} <<-EOSQL
  \i /openempi_init_scripts/01_create_new_database_schema-3.0.0.sql;
  \i /openempi_init_scripts/02_create_person_entity_model_definition.sql;
  \i /openempi_init_scripts/03_create_person_reference_tables.sql;
  \i /openempi_init_scripts/04_update_database_schema-3.1.0.sql;
EOSQL