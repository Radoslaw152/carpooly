package bg.fmi.spring.course.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import bg.fmi.spring.course.models.Post;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
}
