package bg.fmi.spring.course.project.interfaces.repositories;

import bg.fmi.spring.course.project.dao.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {}
