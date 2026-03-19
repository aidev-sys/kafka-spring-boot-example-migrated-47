package io.tpd.rabbitmqexample;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class RabbitMqExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitMqExampleApplication.class, args);
    }

    @Configuration
    static class RabbitConfig {

        @Value("${tpd.topic-name}")
        private String topicName;

        @Bean
        public TopicExchange exchange() {
            return new TopicExchange(topicName);
        }

        @Bean
        public Queue queue() {
            return new Queue(topicName);
        }

        @Bean
        public Binding binding() {
            return BindingBuilder.bind(queue()).to(exchange()).with(topicName);
        }

        @Bean
        public MessageConverter messageConverter() {
            return new Jackson2JsonMessageConverter();
        }

        @Bean
        public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
            RabbitTemplate template = new RabbitTemplate(connectionFactory);
            template.setMessageConverter(messageConverter());
            return template;
        }
    }
}