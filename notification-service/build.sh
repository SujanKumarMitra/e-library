#!/bin/sh

docker volume create e-library_maven_repo
docker run -it --rm -v e-library_maven_repo:/root/.m2  -v "$(pwd)":/usr/src/mymaven -w /usr/src/mymaven maven mvn package -DskipTests
docker build -t notification-service:1.0.0 .
docker image prune -f