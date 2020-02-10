package bg.fmi.spring.course.project.interfaces.services;

import bg.fmi.spring.course.project.dao.Payment;
import java.util.List;

import bg.fmi.spring.course.project.constants.PaymentType;
import bg.fmi.spring.course.project.dao.Account;

public interface PaymentService {
    List<Payment> getAllPayments();
    Payment newPayment(Account passenger, Account driver, double amount, PaymentType paymentType);
    Payment getVouchers(Account account, int amount);
    Payment giftVoucher(Account givenBy, Account givenTo, int amount);
}
