package com.nokia.neo.imagestore;

import com.nokia.neo.AppProperties;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.nokia.neo.imagestore.ImageStoreRMQEventService.IMAGESTORE_QUEUE;

public class RMQEventlListener implements Runnable {

    private final Connection connection;
    private final Channel channel;

    private final DeliverCallback deliverCallback;
    private final String QUEUE_NAME;
    private final CancelCallback cancelCallback;

    public RMQEventlListener(String queueName, DeliverCallback deliverCallback, CancelCallback cancelCallback) throws IOException, TimeoutException {
        this.QUEUE_NAME = queueName;
        this.deliverCallback = deliverCallback;
        this.cancelCallback = cancelCallback;
        
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(AppProperties.getRabbitMQHost());
            factory.setPort(AppProperties.getRabbitMQPort());
            factory.setUsername("prem");
            factory.setPassword("prem");
            connection = factory.newConnection();
            channel = connection.createChannel();
        }
        catch(IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void run() {
        try {
            channel.queueDeclare(IMAGESTORE_QUEUE, false, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
        }
        catch (IOException e) {
            e.printStackTrace();
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
