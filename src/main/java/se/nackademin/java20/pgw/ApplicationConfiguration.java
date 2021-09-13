package se.nackademin.java20.pgw;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import se.nackademin.java20.pgw.application.PaymentService;
import se.nackademin.java20.pgw.domain.PaymentNotificationService;
import se.nackademin.java20.pgw.domain.PaymentRepository;
import se.nackademin.java20.pgw.persistance.PaymentRepositoryHibernate;
import se.nackademin.java20.pgw.persistance.RabbitNotificationService;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableScheduling
@ConfigurationProperties("lab2")
public class ApplicationConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        final RestTemplate restTemplate = new RestTemplate();

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        restTemplate.setMessageConverters(messageConverters);

        return new RestTemplate();
    }

    @Bean
    public PaymentRepository paymentRepository(EntityManager em) {
        return new PaymentRepositoryHibernate(em);
    }

    @Bean
    public PaymentNotificationService paymentNotificationService(RabbitTemplate template, ObjectMapper objectMapper) {
        return new RabbitNotificationService(template, objectMapper);
    }

    final String topicExchangeName = "payments-exchange";

    static final String queueName = "payments";


    @Bean
    Queue queue() {
        return new Queue(queueName, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName);
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("#");
    }

    @Bean
    public PaymentService personalFinanceService(PaymentRepository paymentRepository, PaymentNotificationService paymentNotificationService) {
        return new PaymentService(paymentRepository, paymentNotificationService);
    }

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }



    @Value("lab2.host")
    private String host;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

}
