package se.nackademin.java20.pgw.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import se.nackademin.java20.pgw.ApplicationConfiguration;
import se.nackademin.java20.pgw.domain.Payment;
import se.nackademin.java20.pgw.domain.PaymentNotificationService;
import se.nackademin.java20.pgw.domain.PaymentRepository;
import se.nackademin.java20.pgw.presentation.PaymentDto;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PaymentService implements Serializable {
    private final static Logger LOG = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final PaymentNotificationService paymentNotificationService;

    HttpHeaders headers = new HttpHeaders();
    HttpEntity requestEntity = new HttpEntity<>(headers);
    Map<String, String> uriVariables = new HashMap<>();

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    ApplicationConfiguration applicationConfiguration;

    @Autowired
    RestTemplate restTemplate;

    public PaymentService(PaymentRepository paymentRepository, PaymentNotificationService paymentNotificationService) {
        this.paymentRepository = paymentRepository;
        this.paymentNotificationService = paymentNotificationService;
    }

    @Transactional
    public Payment createPayment(String reference) {
        LOG.info("Creating payment");
        final Payment created = paymentRepository.save(new Payment(reference, "CREATED"));
        paymentNotificationService.notifyPaid(created);
        return created;
    }

    @Transactional
    public void performPayments() {
        LOG.info("Checking payments to be performed");
        Duration duration = Duration.ofSeconds(10);
        List<Payment> payments = paymentRepository.finalAllUnpaid()
                .stream()
                .filter(p -> p.getCreated().plus(duration).isBefore(Instant.now()))
                .collect(Collectors.toList());
        LOG.info("Found {} payments to be performed", payments.size());
        payments.forEach(this::handlePayment);
    }

    private void handlePayment(Payment payment) {
        LOG.info("Marking payment {} as paid", payment.getId());
        payment.markAsPaid();
        paymentRepository.save(payment);
        paymentNotificationService.notifyPaid(payment);

    }





    public void sendMail(){


      // String url = "http://"+applicationConfiguration.getHost()+"/payment/receive";
        String url = "http://lab2:8080/payment/receive";

        headers.setContentType(MediaType.APPLICATION_JSON);
        PaymentDto paymentDto = new PaymentDto();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));



        Map<String, Object> map = new HashMap<>();
        map.put("reference", "12");
        map.put("amount", "1");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = this.restTemplate.postForEntity(url,entity,String.class);




        /*
        LOG.info("Mail används");
        String url = "http://localhost:8080/payment/receive?id=12";

        uriVariables.put("id", "id");
        uriVariables.put("idq", "12");

       ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

         */

    }
}
