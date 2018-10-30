package com.nokia.neo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws IOException {
        // read environment variables
        System.out.println("DEBUG read property : " + System.getProperty("neo.rabbitmq.host"));
        System.out.println("DEBUG read property : " + System.getProperty("neo.rabbitmq.port"));

        SpringApplication.run(Application.class, args);
    }
}