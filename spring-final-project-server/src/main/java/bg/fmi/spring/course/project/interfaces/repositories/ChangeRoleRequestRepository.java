package bg.fmi.spring.course.project.interfaces.repositories;

import bg.fmi.spring.course.project.dao.ChangeRoleRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangeRoleRequestRepository extends JpaRepository<ChangeRoleRequest, Long> {}
