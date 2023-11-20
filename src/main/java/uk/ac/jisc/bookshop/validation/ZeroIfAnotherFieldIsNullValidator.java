package uk.ac.jisc.bookshop.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


import org.apache.commons.beanutils.BeanUtils;
import uk.ac.jisc.bookshop.validation.constraint.ZeroIfAnotherFieldIsNull;

import java.lang.reflect.InvocationTargetException;

public class ZeroIfAnotherFieldIsNullValidator implements ConstraintValidator<ZeroIfAnotherFieldIsNull, Object> {

    private String fieldName;
    private String dependFieldName;

    @Override
    public void initialize(ZeroIfAnotherFieldIsNull constraintAnnotation) {
        fieldName = constraintAnnotation.fieldName();
        dependFieldName = constraintAnnotation.dependFieldName();
    }

    @Override
    public boolean isValid(Object bean, ConstraintValidatorContext constraintValidatorContext) {
        try {
            String fieldValue = BeanUtils.getProperty(bean,fieldName);
            String dependFieldValue = BeanUtils.getProperty(bean, dependFieldName);
            if(fieldValue == null && !dependFieldValue.equals("0")){
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext.buildConstraintViolationWithTemplate(constraintValidatorContext.getDefaultConstraintMessageTemplate()).addPropertyNode(dependFieldName).addConstraintViolation();
                return false;
            }
           return true;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

    }

}
