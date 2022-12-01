This project is the Spring Boot backend of a photo sharing app. It is for my thesis that has a focus on cloud native scalable application development.

---

Ports:

| service name           | port        |
|------------------------|-------------|
| rabbitmq               | 15672, 5672 |
| zipkin                 | 9411        |
| kafka                  | 9092        |
| zookeeper              | 2181        |
| api-gateway            | 8765        |
| naming-server          | 8761        |
| notification-service   | 8101        |
| authentication-service | 8082        |
| time-service           | 8081        |
| user-service           | 8000        |
| friend-service         | 8003        |
| interaction-service    | 8002        |
| post-service           | 8001        |
| postgres               | 5432        |

Service host name environment variables:
(this table is only useful if the services are running on GKE. It generates environment variables for the services and these variables can be used for service discovery in the FeignClient. When being used, FeignClient must have the url tag in the following form:
```url = "${<environment variable>:http://localhost}:<the port that the service is running on>"```)

| service name           | environment variable     |
|------------------------|--------------------------|
| user-service           | USER_SERVICE_SERVICE_HOST        |
| post-service           | POST_SERVICE_SERVICE_HOST        |
| interaction-service    | INTERACTIONS_SERVICE_SERVICE_HOST |
| friend-service         | FRIEND_SERVICE_SERVICE_HOST      |
| time-service           | TIME_SERVICE_SERVICE_HOST        |
