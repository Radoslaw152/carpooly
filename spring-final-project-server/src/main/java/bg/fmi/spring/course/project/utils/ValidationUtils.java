package bg.fmi.spring.course.project.utils;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import org.springframework.util.CollectionUtils;

public class ValidationUtils {
    private static final Validator VALIDATOR =
            Validation.buildDefaultValidatorFactory().getValidator();

    public static <T> T validate(T instance) {
        Set<ConstraintViolation<T>> constraintViolations = VALIDATOR.validate(instance);
        if (!CollectionUtils.isEmpty(constraintViolations)) {
            throw new ConstraintViolationException(constraintViolations);
        }
        return instance;
    }
}
