# Demo Springboot Project

This is a simple demo Spring Boot project that demonstrates the basic structure and functionality of a Spring Boot application.

The goal is to get prometheus metrics and healthcheck using Spring Boot Actuator.


## Build the project

To build the project, you can use the following command:

```bash
./gradlew build
```
This will compile the source code, run tests, and package the application into a JAR file located in the `build/libs` directory.



To test:

```bash
curl -X GET "http://localhost:8080/actuator/health" -H "accept: application/json"
```





Test the api hello:

```bash
curl http://localhost:8080/api/hello
```


Get the metrics:

```bash
curl -X GET "http://localhost:8080/actuator/prometheus"
```

