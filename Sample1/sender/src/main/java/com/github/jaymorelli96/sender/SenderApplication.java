package com.github.jaymorelli96.sender;

import com.github.jaymorelli96.sender.services.senderService;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.Sender;

import javax.annotation.PreDestroy;

@SpringBootApplication
public class SenderApplication {

    // Name of our Queue
    private static final String QUEUE = "demo-queue-3";
    // slf4j logger
    private static final Logger LOGGER = LoggerFactory.getLogger(SenderApplication.class);
    // Connection to RabbitMQ
    @Autowired
    private Mono<Connection> connectionMono;

    public static void main(String[] args) {
        SpringApplication.run(SenderApplication.class, args).close();
    }

    // Make sure the connection before the program is finished
    @PreDestroy
    public void close() throws Exception {
        connectionMono.block().close();
    }

    // Runner class
    @Component
    static class Runner implements CommandLineRunner {

        @Autowired
        Sender sender;
        @Autowired
        private senderService senderSvc;

        @Override
        public void run(String... args) throws Exception {

            senderSvc.sendAutoMesssage("** TEXT **");

        }
    }
}
