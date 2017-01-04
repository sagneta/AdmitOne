#!/bin/sh

export TESTSUITE=$1

echo "Logging in to docker hub"
docker login -e="development@bjondinc.com" -u="bjonddocker" -p="kl4NK37"

echo "Run docker compose to bring up test env"
docker-compose -p travis_test -f config/docker-compose-test.yml up
