package bg.fmi.spring.course.project.auth.types;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.access.prepost.PreAuthorize;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("isAuthenticated() and ((#email != #null and #email == principal.getEmail()) or (#id != #null and #id == principal.getId()) or hasRole('ADMIN'))")
public @interface IsSelfOrAdmin {
}
