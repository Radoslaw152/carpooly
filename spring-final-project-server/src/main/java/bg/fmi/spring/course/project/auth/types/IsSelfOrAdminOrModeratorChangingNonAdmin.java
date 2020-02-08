package bg.fmi.spring.course.project.auth.types;

import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize(
        "isAuthenticated() and ((#email != #null and #email == principal.getEmail()) or (#id != #null and #id == principal.getId()) or hasRole('ADMIN'))"
                + " (hasRole('MODERATOR') and #account.getRole().toString() != 'ADMIN')")
public @interface IsSelfOrAdminOrModeratorChangingNonAdmin {}
