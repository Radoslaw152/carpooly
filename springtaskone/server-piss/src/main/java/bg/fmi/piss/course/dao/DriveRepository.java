package bg.fmi.piss.course.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import bg.fmi.piss.course.models.Drive;

@Repository
public interface DriveRepository extends MongoRepository<Drive, String> {
}
