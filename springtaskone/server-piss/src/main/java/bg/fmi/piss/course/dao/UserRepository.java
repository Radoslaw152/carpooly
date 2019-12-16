package bg.fmi.piss.course.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import bg.fmi.piss.course.models.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
}
