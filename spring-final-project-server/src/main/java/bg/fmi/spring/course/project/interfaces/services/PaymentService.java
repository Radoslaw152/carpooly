package bg.fmi.spring.course.project.interfaces.services;

import bg.fmi.spring.course.project.constants.PaymentType;
import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.dao.Payment;
import bg.fmi.spring.course.project.dao.Ride;
import java.util.List;

public interface PaymentService {

    List<Payment> getAllPayments();

    List<Payment> getAllPaymentsForUser(Account user);

    List<Payment> getAllPaymentsForUser(String user);

    List<Payment> getAllPaymentsForRide(Ride ride);

    List<Payment> getAllPaymentsForRide(Long rideId);

    Payment newPayment(Account passenger, Ride ride, double amount, PaymentType paymentType);

    Payment newPayment(String passenger, Long rideId, double amount, PaymentType paymentType);

    Payment newPayment(Payment payment);

    Payment newPaymentWIthVerification(
            Account passenger, Ride ride, double amount, PaymentType paymentType);
}
