package bg.fmi.spring.course.project.interfaces.repositories;

import bg.fmi.spring.course.project.dao.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {}
