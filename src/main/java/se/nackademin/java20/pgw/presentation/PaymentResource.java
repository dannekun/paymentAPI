package se.nackademin.java20.pgw.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import se.nackademin.java20.pgw.application.PaymentService;
import se.nackademin.java20.pgw.domain.Payment;

import java.util.HashMap;
import java.util.Map;

@Controller
public class PaymentResource {

    private final PaymentService paymentService;

    public PaymentResource(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payment")
    public ResponseEntity<Void> createPayment(@RequestBody PaymentDto paymentDto) {
        paymentService.createPayment(paymentDto.getReference());


        return ResponseEntity.noContent().build();
    }




}
