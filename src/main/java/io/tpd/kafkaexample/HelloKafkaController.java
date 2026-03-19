package io.tpd.kafkaexample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@RestController
public class HelloKafkaController {

    private static final Logger logger =
            LoggerFactory.getLogger(HelloKafkaController.class);

    private final RabbitTemplate template;
    private final StreamBridge streamBridge;
    private final String topicName;
    private final int messagesPerRequest;
    private CountDownLatch latch;

    public HelloKafkaController(
            final RabbitTemplate template,
            final StreamBridge streamBridge,
            @Value("${tpd.topic-name}") final String topicName,
            @Value("${tpd.messages-per-request}") final int messagesPerRequest) {
        this.template = template;
        this.streamBridge = streamBridge;
        this.topicName = topicName;
        this.messagesPerRequest = messagesPerRequest;
    }

    @GetMapping("/hello")
    public String hello() throws Exception {
        latch = new CountDownLatch(messagesPerRequest);
        IntStream.range(0, messagesPerRequest)
                .forEach(i -> streamBridge.send(topicName, new PracticalAdvice("A Practical Advice", i)));
        latch.await(60, TimeUnit.SECONDS);
        logger.info("All messages received");
        return "Hello RabbitMQ!";
    }

    // Note: RabbitMQ consumers are typically configured via @RabbitListener
    // This is a placeholder for demonstration; actual implementation would require
    // appropriate RabbitMQ listener configuration
}