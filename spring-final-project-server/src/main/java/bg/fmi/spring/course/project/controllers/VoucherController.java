package bg.fmi.spring.course.project.controllers;

import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.dao.Payment;
import bg.fmi.spring.course.project.dao.Ride;
import bg.fmi.spring.course.project.dao.Voucher;
import bg.fmi.spring.course.project.interfaces.services.PaymentService;
import bg.fmi.spring.course.project.interfaces.services.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/voucher")
public class VoucherController {

    public VoucherService voucherService;

    @Autowired
    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Voucher>> getAllVouchers() {
        return ResponseEntity.ok(voucherService.getAll());
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/get/by-user",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Voucher>> getRatingsFromUser(@RequestParam("user") String username) {
        return ResponseEntity.ok(voucherService.getAll(username));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/get/by-user-json",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Voucher>> getRatingsFromUser(@RequestBody @Valid Account user) {
        return ResponseEntity.ok(voucherService.getAll(user));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/get/by-ride-json",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Voucher>> getRatingsFromUser(@RequestBody @Valid Ride ride) {
        return ResponseEntity.ok(voucherService.getAll(ride));
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/get",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Voucher> getRatingsFromUser(@RequestParam("user") String username,
                                                            @RequestParam("driver") String driver) {

        Optional<Voucher> voucher = voucherService.getVoucher(username, driver);
        return voucher.map(ResponseEntity::ok).orElse(null);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/get-json",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Voucher> getRatingsFromUser(
            @RequestBody @Valid Account passenger,
            @RequestBody @Valid Account driver) {

        Optional<Voucher> voucher = voucherService.getVoucher(passenger, driver);
        return voucher.map(ResponseEntity::ok).orElse(null);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/delete-json",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Voucher> deleteVoucher(
            @RequestBody @Valid Account passenger,
            @RequestBody @Valid Account driver) {

        return deleteVoucher(passenger, driver);
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/add",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Voucher> giveVoucher(
            @RequestParam("user") String passenger,
            @RequestParam("driver") String driver,
            @RequestParam("amount") Integer amount) {

        return ResponseEntity.ok(voucherService.giveVouchers(passenger,driver,amount));
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/add-json",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Voucher> giveVoucher(@RequestBody @Valid Voucher voucher) {

        return ResponseEntity.ok(
                voucherService.giveVouchers(voucher.getOwner(), voucher.getDriver(),voucher.getAmount())
        );
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/use-json",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Voucher> userVoucher(@RequestBody @Valid Account passenger,
                                               @RequestBody @Valid Account driver) {

        return ResponseEntity.ok(
                voucherService.consumeVoucher(passenger, driver)
        );
    }

    @RequestMapping(
            method = RequestMethod.POST,
            value = "/use",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Voucher> userVoucher(@RequestParam("user") String passenger,
                                               @RequestParam("driver") String driver) {

        return ResponseEntity.ok(
                voucherService.consumeVoucher(passenger, driver)
        );
    }
}
