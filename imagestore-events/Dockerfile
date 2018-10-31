FROM openjdk:alpine

COPY target/com.nokia.neo.imagestore-events.jar  /usr/src/com.nokia.neo.imagestore-events.jar

EXPOSE 8082

CMD java -Dneo.rabbitmq.host=$RABBITMQ_SER_SERVICE_HOST -Dneo.rabbitmq.port=$RABBITMQ_SER_SERVICE_PORT -jar /usr/src/com.nokia.neo.imagestore-events.jar

