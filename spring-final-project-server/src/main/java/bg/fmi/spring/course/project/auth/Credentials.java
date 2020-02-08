package bg.fmi.spring.course.project.auth;

import bg.fmi.spring.course.project.utils.ValidationUtils;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonDeserialize(builder = Credentials.ValidatedCredentialsBuilder.class)
public class Credentials {
    @NotNull private String email;
    @NotNull private String password;

    @JsonPOJOBuilder
    public static class ValidatedCredentialsBuilder extends CredentialsBuilder {
        private static final Validator VALIDATOR =
                Validation.buildDefaultValidatorFactory().getValidator();

        @Override
        public Credentials build() {
            return ValidationUtils.validate(super.build());
        }
    }
}
