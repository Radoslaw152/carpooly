package bg.fmi.spring.course.project.interfaces.repositories;

import bg.fmi.spring.course.project.dao.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long> {}
