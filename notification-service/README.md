## Notification Service

### Dependencies
1. MongoDB
2. Kafka
3. Zookeeper

### Build
`sh build.sh`
### Start
`sh start.sh`
### Stop
`sh stop.sh`

### Security Scopes
1. **NOTIFICATION_PRODUCE**: For Producers
2. **NOTIFICATION_CONSUME**: For Consumers

### Swagger endpoint
Scheme: `http://hostname:port/swagger-ui.html`

Example: `http://localhost:8080/swagger-ui.html`