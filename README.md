# E-Library

This system is part of the Niketan project.

## Functional Requirements
Refer to [requirements.pdf](./requirements.pdf)

## System Architecture
![architectural_diagram.png](./architectural_diagram.png)


## Tech Stack
1. [PostgreSQL](https://www.postgresql.org/)
2. [MongoDB](https://www.mongodb.com/)
3. [Redis](https://redis.io/)
4. [Kafka](https://kafka.apache.org/)
5. [Spring Boot](https://spring.io/projects/spring-boot)
6. [Reactive Streams (Reactor)](https://projectreactor.io/)

## Dependencies
1. [Docker](https://docs.docker.com/get-docker/)
2. [Docker Compose](https://docs.docker.com/compose/install/)
## How to use?
1. Build the artifacts [`sh build.sh`](./build.sh)
2. Launch all services [`sh start.sh`](./start.sh)
3. See status of all services [`sh status.sh`](./status.sh)
4. Stop all services [`sh stop.sh`](./stop.sh)

## NOTE
1. Refer to individual services for their 
requirements and behaviour
2. The project uses Docker, not all the services are publicly accessible,
check which services are accessible by 
\
    [`sh status.sh`](./status.sh)