package bg.fmi.spring.course.project.impl.services;

import bg.fmi.spring.course.project.dao.Account;
import bg.fmi.spring.course.project.dao.Ride;
import bg.fmi.spring.course.project.dao.Voucher;
import bg.fmi.spring.course.project.interfaces.repositories.VoucherRepository;
import bg.fmi.spring.course.project.interfaces.services.AccountService;
import bg.fmi.spring.course.project.interfaces.services.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class VoucherServiceImpl implements VoucherService {

    @Autowired
    public VoucherRepository voucherRepository;

    @Autowired
    private AccountService accountService;

    @Override
    public List<Voucher> getAll() {
        return voucherRepository.findAll();
    }

    @Override
    public List<Voucher> getAll(String user) {
        return getAll().stream().filter(voucher ->
                voucher.getOwner().getEmail().equals(user))
                .collect(Collectors.toList());
    }

    @Override
    public List<Voucher> getAll(Account user) {
        return getAll(user.getEmail());
    }

    @Override
    public List<Voucher> getAll(Ride ride) {
        return getAll().stream().filter(voucher ->
                voucher.getDriver().getEmail().equals(ride.getDriver().getEmail()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Voucher> getVoucher(String passenger, String driver) {
        Optional<Voucher> voucherOptional =  getAll(passenger).stream().
                filter(voucher ->  voucher.getDriver().getEmail().equals(driver)).findAny();
        if(!voucherOptional.isPresent()){
            throw new RuntimeException(
                    String.format("No voucher for user %s from user %s", passenger, driver));
        }
        return voucherOptional;
    }

    @Override
    public Optional<Voucher> getVoucher(Account passenger, Account driver) {
        return getVoucher(passenger.getEmail(), driver.getEmail());
    }

    private Voucher deleteVoucher(Account user, Account driver){
        Optional<Voucher> voucher = getVoucher(user,driver);
        if(voucher.isPresent()) {
            voucherRepository.delete(voucher.get());
            return voucher.get();
        }
        throw new RuntimeException(
                String.format("Cannot delete voucher owned by %s for driver %s because no such voucher exists",
                        user.getEmail(), driver.getEmail()));
    }

    private Voucher deleteVoucher(String user, String driver){
        Optional<Voucher> voucher = getVoucher(user,driver);
        if(voucher.isPresent()) {
            voucherRepository.delete(voucher.get());
            return voucher.get();
        }
        throw new RuntimeException(
                String.format("Cannot delete voucher owned by %s for driver %s because no such voucher exists",
                        user, driver));
    }

    private Voucher editVoucher(Voucher voucher) {
        deleteVoucher(voucher.getOwner(), voucher.getDriver());
        return voucherRepository.save(voucher);
    }

    @Override
    public Voucher giveVouchers(Account user, Account driver, int amount) {
        if(amount <= 0)
            throw new RuntimeException("Illegal voucher amount");
        Optional<Voucher> existing = getVoucher(user, driver);
        Voucher voucher = new Voucher();
        if(existing.isPresent()){
            voucher = existing.get();
            voucher.setAmount(voucher.getAmount()+amount);
        } else {
            voucher.setDriver(driver);
            voucher.setOwner(user);
            voucher.setAmount(amount);
        }
        return voucherRepository.save(voucher);
    }

    @Override
    public Voucher giveVouchers(String passenger, String driver, int amount) {
        Optional<Account> passAcc = accountService.getAccountByEmail(passenger);
        if(passAcc.isPresent()){
            Optional<Account> driverAcc = accountService.getAccountByEmail(driver);
            if(driverAcc.isPresent()){
                return giveVouchers(passAcc.get(), driverAcc.get(), amount);
            }
            throw new RuntimeException("No user with email = " + driver);
        }
        throw new RuntimeException("No user with email = " + passenger);
    }

    @Override
    public Voucher consumeVoucher(Account passenger, Account driver) {
        Optional<Voucher> existing = getVoucher(passenger, driver);
        if(existing.isPresent()){
            Voucher voucher = existing.get();
            if(voucher.getAmount() == 1) {
                return deleteVoucher(passenger, driver);
            } else {
                voucher.setAmount(voucher.getAmount()-1);
                return editVoucher(voucher);
            }
        }
        throw new RuntimeException(
                String.format("Could not use voucher by %s for driver %s",
                        passenger.getEmail(), driver.getEmail()));
    }

    @Override
    public Voucher consumeVoucher(String passenger, String driver) {
        Optional<Account> passAcc = accountService.getAccountByEmail(passenger);
        if(passAcc.isPresent()) {
            Optional<Account> driverAcc = accountService.getAccountByEmail(driver);
            if(driverAcc.isPresent()){
                return consumeVoucher(passAcc.get(), driverAcc.get());
            }
            throw new RuntimeException("Could not find user with username = " + driver);
        }
        throw new RuntimeException("Could not find user with username = " + passenger);
    }

}
