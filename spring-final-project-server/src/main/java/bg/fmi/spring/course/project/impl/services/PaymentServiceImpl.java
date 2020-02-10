package bg.fmi.spring.course.project.impl.services;

import bg.fmi.spring.course.project.constants.PaymentType;
import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.dao.Payment;
import bg.fmi.spring.course.project.interfaces.services.PaymentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Override
    public List<Payment> getAllPayments() {
        //Get Payments from DB
        return null;
    }

    @Override
    public Payment newPayment(Account passenger, Account driver, double amount, PaymentType paymentType) {
        return null;
    }

    @Override
    public Payment getVouchers(Account account, int amount) {
        return null;
    }

    @Override
    public Payment giftVoucher(Account givenBy, Account givenTo, int amount) {
        return null;
    }
}
