package se.nackademin.java20.pgw.persistance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import se.nackademin.java20.pgw.domain.Payment;
import se.nackademin.java20.pgw.domain.PaymentNotificationService;

import java.util.HashMap;
import java.util.Map;

public class RabbitNotificationService implements PaymentNotificationService {


    private final static Logger LOG = LoggerFactory.getLogger(RabbitNotificationService.class);

    private final RabbitTemplate template;
    private final ObjectMapper objectMapper;

    public RabbitNotificationService(RabbitTemplate template, ObjectMapper objectMapper) {

        this.template = template;
        this.objectMapper = objectMapper;
    }

    @Override
    public void notifyPaid(Payment payment) {

        try {
            String object = objectMapper.writeValueAsString(new PaymentMessageDto(payment.getReference(), Long.toString(payment.getId()), payment.getStatus()));
            LOG.info("Sending {}", object);

            PaymentMessageDto paymentMessageDto = new PaymentMessageDto(payment.getReference(),Long.toString(payment.getId()), payment.getStatus());
            template.convertAndSend("payments-exchange2", payment.getReference(), paymentMessageDto);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }




}
