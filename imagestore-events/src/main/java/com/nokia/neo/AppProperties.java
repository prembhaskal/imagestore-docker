package com.nokia.neo;

public class AppProperties {

    public static String getRabbitMQHost() {
        return System.getProperty("neo.rabbitmq.host");
    }

    public static int getRabbitMQPort() {
        String portStr = System.getProperty("neo.rabbitmq.port");
        try {
            return Integer.parseInt(portStr);
        }
        catch (NumberFormatException nfe) {
            throw new RuntimeException("invalid number in rabbit mq port property " + portStr);
        }
    }

}
