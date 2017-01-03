#/bin/bash

# Start postgres in the background
/usr/pgsql-9.5/bin/postgres -D /var/lib/pgsql/9.5/data -p 5432 &
# Sleep for a few while postgres comes up
sleep 15
# Run openempi init scripts
/openempi_db_init.sh
# Stop postgres
/usr/pgsql-9.5/bin/pg_ctl -D /var/lib/pgsql/9.5/data -p 5432 stop
# restart postgres in the foreground
/usr/pgsql-9.5/bin/postgres -D /var/lib/pgsql/9.5/data -p 5432