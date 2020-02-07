package bg.fmi.spring.course.project.interfaces.repositories;

import bg.fmi.spring.course.project.dao.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RideRepository extends JpaRepository<Ride, Long> {}
