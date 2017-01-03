#!/bin/bash

set -e

host="$1"
shift
cmd="$@"

sleep 30

until pg_isready -h "$host"; do
  >&2 echo "Postgres is unavailable - sleeping"
  sleep 1
done

>&2 echo "Postgres is up - executing command"
exec $cmd