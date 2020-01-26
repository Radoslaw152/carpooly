package bg.fmi.spring.course.project.interfaces.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bg.fmi.spring.course.project.dao.Ratings;

@Repository
public interface RatingsRepository extends JpaRepository<Ratings, Long> {
}
