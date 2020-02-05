package bg.fmi.spring.course.project.interfaces.repositories;

import bg.fmi.spring.course.project.dao.ChangeRoleRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChangeRoleRequestRepository extends MongoRepository<ChangeRoleRequest, Long> {}
