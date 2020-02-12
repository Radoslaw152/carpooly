package bg.fmi.spring.course.project.impl.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bg.fmi.spring.course.project.constants.PaymentType;
import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.dao.Payment;
import bg.fmi.spring.course.project.dao.Ride;
import bg.fmi.spring.course.project.exceptions.NotFoundException;
import bg.fmi.spring.course.project.interfaces.repositories.PaymentRepository;
import bg.fmi.spring.course.project.interfaces.services.AccountService;
import bg.fmi.spring.course.project.interfaces.services.PaymentService;
import bg.fmi.spring.course.project.interfaces.services.RideService;
import bg.fmi.spring.course.project.interfaces.services.VoucherService;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private RideService rideService;

    @Autowired
    public VoucherService voucherService;

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
                                .filter(p -> getAccountOfPaymentOwner(p).getEmail().equals(user))
                                .collect(Collectors.toList());
    }

    public Ride getRideByPayment(Payment payment) {
        return rideService
                .getRideById(payment.getRideId())
                .orElseThrow(
                        () ->
                                NotFoundException.generateForTypeAndIdTypeAndId(
                                        "ride", "id", payment.getRideId().toString()));
    }

    public Account getAccountOfPaymentOwner(Payment payment) {
        return accountService.getAccountById(payment.getRideId());
    }

    @Override
    public List<Payment> getAllPaymentsForRide(Ride ride) {
        return paymentRepository.findAll().stream()
                                .filter(
                                        p ->
                                                getRideByPayment(p)
                                                        .getDriver()
                                                        .getEmail()
                                                        .equals(ride.getDriver().getEmail())
                                                        && getRideByPayment(p)
                                                        .getPathCoordinates()
                                                        .get(0)
                                                        .equals(ride.getPathCoordinates().get(0))
                                                        && getRideByPayment(p)
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
                                .filter(payment -> getRideByPayment(payment).getId().equals(rideId))
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
        payment.setRideId(ride.getId());
        payment.setOwnerAccountId(passenger.getId());
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
            Optional<Ride> requestedRide =
                    rideService.getRidesByDriver(ride.getDriver().getEmail()).stream()
                               .filter(
                                       rideOpt ->
                                               rideOpt.getPathCoordinates()
                                                      .get(0)
                                                      .equals(
                                                              ride.getPathCoordinates()
                                                                  .get(0))
                                                       && rideOpt.getPathCoordinates()
                                                                 .get(getAllPayments().size() - 1)
                                                                 .equals(
                                                                         ride.getPathCoordinates()
                                                                             .get(
                                                                                     getAllPayments()
                                                                                             .size()
                                                                                             - 1)))
                               .findFirst();
            if (requestedRide.isPresent()) {
                List<Payment> passList = requestedRide.get().getPassengers();
                for (Payment p : passList) {
                    Account accountOfPaymentOwner = getAccountOfPaymentOwner(p);
                    Ride rideByPayment = getRideByPayment(p);
                    if (accountOfPaymentOwner.getEmail().equals(passenger.getEmail())
                            && rideByPayment.getId().equals(requestedRide.get().getId())) {
                        return newPayment(passAcc.get(), requestedRide.get(), amount, paymentType);
                    }
                }
                throw new RuntimeException(
                        String.format(
                                "No passenger with email %s found in the ride %s",
                                passenger.getEmail(), requestedRide.get().getId()));
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
            String passenger, Long rideId, double amount, PaymentType paymentType) {
        Optional<Account> passengerAcc = accountService.getAccountByEmail(passenger);
        if (!passengerAcc.isPresent()) {
            throw new RuntimeException("No such user: " + passenger);
        }
        Optional<Ride> rideById = rideService.getRideById(rideId);
        if (!rideById.isPresent()) throw new RuntimeException("No ride with id " + rideId);

        return newPayment(passengerAcc.get(), rideById.get(), amount, paymentType);
    }

    @Override
    public Payment newPayment(Payment payment) {
        return paymentRepository.save(payment);
    }
}
