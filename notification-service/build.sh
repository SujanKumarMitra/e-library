#!/bin/sh

# builds the project without running tests and builds a docker image

#mvn clean package # to run tests before build
mvn clean package -DskipTests
docker build -t notification-service:1.0.0 .
docker image prune -f