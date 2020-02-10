package bg.fmi.spring.course.project.interfaces.services;

import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.dao.Ride;
import bg.fmi.spring.course.project.dao.Voucher;
import java.util.List;
import java.util.Optional;

public interface VoucherService {
    public List<Voucher> getAll();

    public List<Voucher> getAll(String user);

    public List<Voucher> getAll(Account user);

    public List<Voucher> getAll(Ride ride);

    public Optional<Voucher> getVoucher(String passenger, String driver);

    public Optional<Voucher> getVoucher(Account passenger, Account driver);

    public Voucher giveVouchers(Account user, Account driver, int amount);

    public Voucher giveVouchers(String passenger, String driver, int amount);

    public Voucher consumeVoucher(Account passenger, Account driver);

    public Voucher consumeVoucher(String passenger, String driver);
}
