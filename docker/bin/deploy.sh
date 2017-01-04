#!/bin/bash
#set -x

usage="$(basename "$0") [-e|--dbdump] [-p|--port] [-n|--name]"

while [[ $# > 1 ]]
do
  args="$1"
  case $args in
    -d|--dbdump)
    db_dump="$2"
    shift # past argument
    ;;
    -p|--port)
    port="$2"
    shift # past argument
    ;;
    -n|--name)
    container_name="$2"
    shift # past argument
    ;;
    *)
            # unknown option
    ;;
  esac
  shift # past argument or value
done
if [[ -n $1 ]]; then
    echo "Unrecognized command line argument"
    echo "$usage"
    exit 1
fi

# Check input
if [ -z "$port" ]; then
  port="8080"
fi

if [ -z "$container_name" ]; then
  # Get current branch name
  current_branch=$(git branch | grep "*" | awk -F" " '{print $2}')
  # Get timestamp
  timestamp=$(date +%s)
  container_name="$current_branch-$timestamp"
fi

# Get current directory path
bin_dir="$(pwd)/$(dirname $0 | sed 's/^[./]*//')"

# Export variables for docker-compose consumption
export BIN_DIR=$bin_dir
export DB_DUMP=$db_dump
export PORT=$port 

env | grep BIN_DIR

# Bring up docker environment

docker-compose -p $container_name -f $bin_dir/../config/deploy.yml up
