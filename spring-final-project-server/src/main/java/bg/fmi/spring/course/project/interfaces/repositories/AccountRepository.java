package bg.fmi.spring.course.project.interfaces.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bg.fmi.spring.course.project.dao.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
}
