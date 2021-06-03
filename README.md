# spring-cloud-data-flow-demo

This is a sample project which uses Spring Cloud Data Flow

## Overview

This project contains:
- **file-upload**: a simple web project which user can upload CSV files
- **metric-stream-rabiit**: contains 3 apps to run a complete stream processing
- **taskdemo**: a simple task project, which consumes csv file, calculates and print result

## Build

Run it in all projects

```bash
$ ./mvnw clean package
```

## Run

### Run file upload website

```bash
$ cd file-upload
$ ./mvnw spring-boot:run
```

### Run MySQL

```bash
$ docker run -p 3306:3306 --name mysql -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=task -d mysql:5.7.25
```
### Run RabbitMQ

```bash
$ docker run -d --hostname rabbitmq --name rabbitmq -p 15672:15672 -p 5672:5672 rabbitmq:3.7.14-management
```

### Run Skipper Server

Download Skipper Server jar

```bash
$ wget https://repo.spring.io/release/org/springframework/cloud/spring-cloud-skipper-server/2.6.2/spring-cloud-skipper-server-2.6.2.jar
```
Run the server

```bash
$ java -jar spring-cloud-skipper-server-2.6.2.jar "--spring.datasource.url=jdbc:mysql://localhost:3306/task?useSSL=false"  "--spring.datasource.username=root"  "--spring.datasource.password=password" "--spring.datasource.driverClassName=org.mariadb.jdbc.Driver"
```

### Run Data Flow Server

Download Data Flow Server and Data Flow Shell

```bash
$ wget https://repo.spring.io/release/org/springframework/cloud/spring-cloud-dataflow-server/2.7.2/spring-cloud-dataflow-server-2.7.2.jar

$ wget https://repo.spring.io/release/org/springframework/cloud/spring-cloud-dataflow-shell/2.7.2/spring-cloud-dataflow-shell-2.7.2.jar
```

Run the server

```bash
$ java -jar spring-cloud-dataflow-server-2.7.2.jar "--spring.datasource.url=jdbc:mysql://localhost:3306/task?useSSL=false"  "--spring.datasource.username=root"  "--spring.datasource.password=password" "--spring.datasource.driverClassName=org.mariadb.jdbc.Driver"
```

Run the shell

```bash
$ java -jar spring-cloud-dataflow-shell-2.7.2.jar
```

### Run the task

Run in the Data Flow shell

```bash
# Register the application
$ app register --name bmi-calculator --uri maven://com.luanvv:bmi:0.0.1-SNAPSHOT --type task 
```

```bash
# Create the task
$ task create --name bmi-calculator-task --definition bmi-calculator
```
Go to http://localhost:8888 and upload a csv file. A new task will be launch and process the file 
or you can run the task manually:

```bash
# View task execution status
$ task launch --name bmi-calculator-task --properties app.bmi-calculator.bmi.file.path=<csv filePath>>
```

View task status
```bash
$ task execution status <taskid>
```

## Reference
- https://dataflow.spring.io/docs/installation/