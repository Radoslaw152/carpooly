package bg.fmi.spring.course.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import bg.fmi.spring.course.models.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
}
