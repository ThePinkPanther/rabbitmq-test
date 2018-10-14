package test;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Rimantas Jacikevičius on 18.8.22.
 */
@Configuration
@EnableRabbit
public class RabbitConfig {

    @Value("${spring.rabbitmq.username:guest}")
    private String username;
    @Value("${spring.rabbitmq.password:guest}")
    private String password;
    @Value("${spring.rabbitmq.port:5672}")
    private Integer port;
    @Value("${spring.rabbitmq.host:localhost}")
    private String host;

    @Bean("rabbitConnectionFactory")
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host, port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);

        return connectionFactory;
    }

    @Bean
    public AmqpAdmin amqpAdmin(
            @Autowired @Qualifier("rabbitConnectionFactory") ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            @Autowired @Qualifier("rabbitConnectionFactory") ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(3);
        factory.setMessageConverter(producerJackson2MessageConverter());
        factory.setMaxConcurrentConsumers(10);
        factory.setDefaultRequeueRejected(false);
        factory.setMissingQueuesFatal(true);
        return factory;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue testQueue() {
        return new Queue(Listener.TEST_QUEUE, false);
    }

}
