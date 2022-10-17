package com.github.jfcaballerop.sender.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.QueueSpecification;
import reactor.rabbitmq.Sender;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class senderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(senderService.class);
    private static final String QUEUE = "demo-queue-service";


    @Autowired
    Sender sender;

    public void sendAutoMesssage(String txt) throws InterruptedException {
        LOGGER.info("*** Service " + txt);

        // Number of message that will be sent to the queue
        int messageCount = 10;

        // CountDownLatch to keep track of the threads
        CountDownLatch latch = new CountDownLatch(messageCount);

        // Represent the list of messages that will be sent
        Flux<OutboundMessage> outboundFlux = Flux
                .range(1, messageCount)
                .map(i -> new OutboundMessage("", QUEUE, ("Message2 - " + i + " - " + txt).getBytes()));

      /*
				1. Declare the queue.
				2. After the queue declaration is completed, it will call another Publisher (sendWithPublishConfirms method), which takes the flux of messages declared earlier.
				3. In case of error, it will log the error.
				4. Finally it subscribe a consumer to this flux, here we can check if the message if being acknowledged by the queue. We also decrement the CountDownLatch by one.
			*/
        sender.declareQueue(QueueSpecification.queue(QUEUE))
                .thenMany(sender.sendWithPublishConfirms(outboundFlux))
                .doOnError(e -> LOGGER.error("Send failed", e))
                .subscribe(m -> {
                    if (m.isAck()) {
                        int i = 0;
                        LOGGER.info("Message [" + i + " - " + txt + " ] sent");
                        i++;
//                        LOGGER.info("Message [" + latch.getCount() + " - " + txt + " ] sent");
//                        latch.countDown();
                    }
                });

        // Make sure all the threads are completed.
        latch.await(3L, TimeUnit.SECONDS);
    }

}
