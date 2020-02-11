package bg.fmi.spring.course.project.impl.services;

import bg.fmi.spring.course.project.constants.PaymentType;
import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.dao.Payment;
import bg.fmi.spring.course.project.dao.Ride;
import bg.fmi.spring.course.project.interfaces.repositories.PaymentRepository;
import bg.fmi.spring.course.project.interfaces.services.AccountService;
import bg.fmi.spring.course.project.interfaces.services.PaymentService;
import bg.fmi.spring.course.project.interfaces.services.RideService;
import bg.fmi.spring.course.project.interfaces.services.VoucherService;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired private PaymentRepository paymentRepository;

    @Autowired private AccountService accountService;

    @Autowired private RideService rideService;

    @Autowired public VoucherService voucherService;

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public List<Payment> getAllPaymentsForUser(Account user) {
        return getAllPaymentsForUser(user.getEmail());
    }

    @Override
    public List<Payment> getAllPaymentsForUser(String user) {
        return paymentRepository.findAll().stream()
                .filter(p -> p.getOwner().getEmail().equals(user))
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> getAllPaymentsForRide(Ride ride) {
        return paymentRepository.findAll().stream()
                .filter(
                        p ->
                                p.getRide()
                                                .getDriver()
                                                .getEmail()
                                                .equals(ride.getDriver().getEmail())
                                        && p.getRide()
                                                .getPathCoordinates()
                                                .get(0)
                                                .equals(ride.getPathCoordinates().get(0))
                                        && p.getRide()
                                                .getPathCoordinates()
                                                .get(getAllPayments().size() - 1)
                                                .equals(
                                                        ride.getPathCoordinates()
                                                                .get(getAllPayments().size() - 1)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> getAllPaymentsForRide(Long rideId) {
        return paymentRepository.findAll().stream()
                .filter(payment -> payment.getRide().getId().equals(rideId))
                .collect(Collectors.toList());
    }

    @Override
    public Payment newPayment(
            Account passenger, Ride ride, double amount, PaymentType paymentType) {
        Payment payment = new Payment();
        if (PaymentType.VOUCHER.equals(paymentType)) {
            if (!voucherService.getVoucher(passenger, ride.getDriver()).isPresent())
                throw new RuntimeException(
                        String.format(
                                "User %s does not have a voucher for ride %s",
                                passenger.getEmail(), ride.toString()));
            voucherService.consumeVoucher(passenger, ride.getDriver());
        } else {
            if (ride.getPrice() > amount)
                throw new RuntimeException(
                        String.format(
                                "Payment amount (%s) is less than ride cost (%s)",
                                amount, ride.getPrice()));
        }
        payment.setRide(ride);
        payment.setOwner(passenger);
        payment.setDateCompleted(new Date());
        payment.setPaymentType(paymentType);
        payment.setPaid(true);
        payment.setAmount(amount);
        return paymentRepository.save(payment);
    }

    @Override
    public Payment newPaymentWIthVerification(
            Account passenger, Ride ride, double amount, PaymentType paymentType) {
        Optional<Account> passAcc = accountService.getAccountByEmail(passenger.getEmail());
        if (passAcc.isPresent()) {
            Optional<Ride> rideOpt = rideService.getRideByDriver(ride.getDriver().getEmail());
            if (rideOpt.isPresent()
                    && rideOpt.get()
                            .getPathCoordinates()
                            .get(0)
                            .equals(ride.getPathCoordinates().get(0))
                    && rideOpt.get()
                            .getPathCoordinates()
                            .get(getAllPayments().size() - 1)
                            .equals(ride.getPathCoordinates().get(getAllPayments().size() - 1))) {
                List<Payment> passList = rideOpt.get().getPassengers();
                for (Payment p : passList) {
                    if (p.getOwner().getEmail().equals(passenger)
                            && p.getRide().getId() == rideOpt.get().getId()) {
                        return newPayment(passAcc.get(), rideOpt.get(), amount, paymentType);
                    }
                }
                throw new RuntimeException(
                        String.format(
                                "No passenger with email %s found in the ride %s",
                                passenger.getEmail(), rideOpt.get().getId()));
            } else {
                throw new RuntimeException(
                        String.format(
                                "No ride with driver = %s , start = %s and finish = %s found ",
                                ride.getDriver(),
                                ride.getPathCoordinates().get(0),
                                ride.getPathCoordinates().get(getAllPayments().size() - 1)));
            }
        }
        throw new RuntimeException(
                String.format("No user with email = %s found", passenger.getEmail()));
    }

    @Override
    public Payment newPayment(
            String passenger, String driver, double amount, PaymentType paymentType) {
        Optional<Account> passengerAcc = accountService.getAccountByEmail(passenger);
        if (!passengerAcc.isPresent()) {
            throw new RuntimeException("No such user: " + passenger);
        }
        Optional<Ride> ride = rideService.getRideByDriver(driver);
        if (!ride.isPresent()) throw new RuntimeException("No ride with driver " + driver);

        return newPayment(passengerAcc.get(), ride.get(), amount, paymentType);
    }

    @Override
    public Payment newPayment(Payment payment) {
        return paymentRepository.save(payment);
    }
}
