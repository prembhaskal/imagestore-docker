package com.nokia.neo.events;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class RMQEventPublisher implements EventPublisher {

    public final static String IMAGESTORE_QUEUE = "imagestore";

    private final Connection connection;
    private final Channel channel;
    private final Gson gson;

    public RMQEventPublisher() throws Exception {

        gson = new Gson();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(AppProperties.getRabbitMQHost());
        factory.setPort(AppProperties.getRabbitMQPort());
        factory.setUsername("prem");
        factory.setPassword("prem");
        connection = factory.newConnection();
        channel = connection.createChannel();

        channel.queueDeclare(IMAGESTORE_QUEUE, false, false, false, null);
    }

    @Override
    public synchronized void sendEvent(StoreEvent storeEvent) throws IOException {
        try {
            String message = gson.toJson(storeEvent);
            channel.basicPublish("", IMAGESTORE_QUEUE, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    protected void finalize() throws Throwable {

        if (channel != null) {
            channel.close();
        }

        if (connection != null) {
            connection.close();
        }

        super.finalize();

    }
}
