package bg.fmi.spring.course.project.auth;

import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Primary
@Component
public class AuthEventPublisher implements AuthenticationEventPublisher {
  @Override
  public void publishAuthenticationSuccess(Authentication authentication) {
    log.info("{} authenticated succesfully", authentication.getName());
  }

  @Override
  public void publishAuthenticationFailure(AuthenticationException e, Authentication authentication) {
    log.warn("{} failed to authenticate , exception is {}", authentication.getName(), e.getMessage());
  }
}
