## Library Service

### Dependencies
1. PostgreSQL
2. Kafka
3. Zookeeper

### Build
`sh build.sh`
### Start
`sh start.sh`
### Stop
`sh stop.sh`

### Security Scopes
1. **ROLE_STUDENT**: Given to students
2. **ROLE_TEACHER**: Given to teachers
3. **ROLE_LIBRARIAN**: Given to librarians
4. **ROLE_ADMIN**: Given to admins

### Scope Precedence
`ROLE_ADMIN > ROLE_LIBRARIAN > ROLE_TEACHER > ROLE_STUDENT`

### Swagger endpoint
Scheme: `http://hostname:port`

Example: `http://localhost:8080`