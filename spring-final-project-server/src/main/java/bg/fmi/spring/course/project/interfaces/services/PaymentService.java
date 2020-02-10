package bg.fmi.spring.course.project.interfaces.services;

import bg.fmi.spring.course.project.dao.Payment;
import java.util.List;

public interface PaymentService {
    List<Payment> getAllPayments();
}
