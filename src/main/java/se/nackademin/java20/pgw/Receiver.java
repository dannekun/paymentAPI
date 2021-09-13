package se.nackademin.java20.pgw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.nackademin.java20.pgw.application.PaymentService;
import se.nackademin.java20.pgw.domain.Payment;
import se.nackademin.java20.pgw.domain.PaymentNotificationService;
import se.nackademin.java20.pgw.domain.PaymentRepository;
import se.nackademin.java20.pgw.persistance.PaymentMessageDto;

/**
 * Created by Daniel Bojic
 * Date: 2021-09-13
 * Time: 11:17
 * Project: payment-gate-way
 * Copyright: MIT
 */
@Component
public class Receiver {

    private static final Logger LOG = LoggerFactory.getLogger(Receiver.class);

    PaymentService paymentService;

    public Receiver(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @RabbitListener(queues = "payments")
    public void recieveMessage(PaymentMessageDto paymentMessageDto){
        LOG.info("MEDD KOMISH FRAMISH");
        System.out.println("MEDDELANDET KOM FRAM!!!!!");


        System.out.println(paymentMessageDto.getReference());
        System.out.println(paymentMessageDto.getPaymentId());
        System.out.println(paymentMessageDto.getStatus());


        try {
            paymentService.createPayment(paymentMessageDto.getReference());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
