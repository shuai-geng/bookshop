package uk.ac.jisc.bookshop.validation.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import uk.ac.jisc.bookshop.validation.ZeroIfAnotherFieldIsNullValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = ZeroIfAnotherFieldIsNullValidator.class)
@Repeatable(ZeroIfAnotherFieldIsNull.List.class)
@Documented
public @interface ZeroIfAnotherFieldIsNull {

    String fieldName();
    String dependFieldName();

    String message() default "default message";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    @Target({TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        ZeroIfAnotherFieldIsNull[] value();
    }
}
