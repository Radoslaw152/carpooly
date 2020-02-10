package bg.fmi.spring.course.project.interfaces.repositories;

import bg.fmi.spring.course.project.dao.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {}
