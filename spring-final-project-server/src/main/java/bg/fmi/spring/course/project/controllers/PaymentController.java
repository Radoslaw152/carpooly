package bg.fmi.spring.course.project.controllers;

import bg.fmi.spring.course.project.constants.PaymentType;
import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.dao.Payment;
import bg.fmi.spring.course.project.dao.Ride;
import bg.fmi.spring.course.project.interfaces.services.PaymentService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/payment")
public class PaymentController {

    public PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/user",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Payment>> getRatingsFromUser(@RequestParam("usr") String username) {
        return ResponseEntity.ok(paymentService.getAllPaymentsForUser(username));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/user/json",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Payment>> getRatingsFromUser(@RequestBody @Valid Account user) {
        return ResponseEntity.ok(paymentService.getAllPaymentsForUser(user));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/ride-json",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Payment>> getRatingsFromUser(@RequestBody @Valid Ride ride) {
        return ResponseEntity.ok(paymentService.getAllPaymentsForRide(ride));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/ride-id/{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Payment>> getRatingsFromUser(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getAllPaymentsForRide(id));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/add-json-full",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Payment> addPayment(
            @RequestBody @Valid Payment payment, Authentication authentication) {

        if (payment.getOwner().getEmail() == null) {
            Account from = (Account) authentication.getPrincipal();
            payment.setOwner(from);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.newPayment(payment));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/add-json-verify",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Payment> addPaymentWithVerification(
            @RequestBody @Valid Payment payment, Authentication authentication) {

        if (payment.getOwner().getEmail() == null) {
            Account from = (Account) authentication.getPrincipal();
            payment.setOwner(from);
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        paymentService.newPaymentWIthVerification(
                                payment.getOwner(), payment.getRide(),
                                payment.getAmount(), payment.getPaymentType()));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/add",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Payment> addPayment(
            @RequestParam("user") String user,
            @RequestParam("driver") String driver,
            @RequestParam("amount") Double amount,
            @RequestParam("type") PaymentType paymentType,
            Authentication authentication) {

        Account from = (Account) authentication.getPrincipal();
        if (!user.equals(from.getEmail())) {
            throw new RuntimeException(
                    String.format(
                            "Provided user email (%s) does not match logged in user email (%s)",
                            user, from.getEmail()));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.newPayment(user, driver, amount, paymentType));
    }
}
