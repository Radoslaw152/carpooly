package bg.fmi.spring.course.project.interfaces.services;

import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.dao.Ride;
import bg.fmi.spring.course.project.dao.Voucher;
import java.util.List;
import java.util.Optional;

public interface VoucherService {

    List<Voucher> getAll();

    List<Voucher> getAll(String user);

    List<Voucher> getAll(Account user);

    List<Voucher> getAll(Ride ride);

    Optional<Voucher> getVoucher(String passenger, String driver);

    Optional<Voucher> getVoucher(Account passenger, Account driver);

    Voucher giveVouchers(Account passenger, Account driver, int amount);

    Voucher giveVouchers(String passenger, String driver, int amount);

    Voucher consumeVoucher(Account passenger, Account driver);

    Voucher consumeVoucher(String passenger, String driver);
}
