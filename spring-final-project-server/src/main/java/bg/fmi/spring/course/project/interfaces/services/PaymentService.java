package bg.fmi.spring.course.project.interfaces.services;

import java.util.List;

import bg.fmi.spring.course.project.dao.Payment;
public interface PaymentService {
    List<Payment> getAllPayments();
}
