package bg.fmi.spring.course.project.interfaces.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bg.fmi.spring.course.project.dao.Ride;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
}
