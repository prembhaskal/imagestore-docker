FROM openjdk:alpine

COPY target/com.nokia.neo.imagestore.jar  /usr/src/com.nokia.neo.imagestore.jar

EXPOSE 8080

CMD java -Dneo.rabbitmq.host=$RABBITMQ_SER_SERVICE_HOST -Dneo.rabbitmq.port=$RABBITMQ_SER_SERVICE_PORT -jar /usr/src/com.nokia.neo.imagestore.jar

