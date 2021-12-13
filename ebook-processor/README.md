## EBook Processor

### Dependencies
1. Redis

### Build
`sh build.sh`
### Start
`sh start.sh`
### Stop
`sh stop.sh`

### Security Scopes
1. **ROLE_LIBRARIAN**: Given to librarians
2. **WRITE_ASSET**: Needed for [asset-service](../asset-service)
### Swagger endpoint
Scheme: `http://hostname:port/`
