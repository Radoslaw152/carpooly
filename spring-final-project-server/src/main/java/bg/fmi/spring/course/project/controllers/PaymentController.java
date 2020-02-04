package bg.fmi.spring.course.project.controllers;

import bg.fmi.spring.course.project.interfaces.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/payment")
public class PaymentController {
    @Autowired
    public PaymentService paymentService;
}
