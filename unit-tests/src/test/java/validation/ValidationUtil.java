package validation;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public class ValidationUtil {
	
	protected static Validator validator;
	static {
		ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
	    ValidationUtil.validator = validatorFactory.getValidator();
	}
	
	/**
	 * Convenience method to call {@link Validator#validate(Object, Class...)}
	 */
	public static <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
		return validator.validate(object, groups);
	}

	/**
	 * @return property paths of {@code violations}
	 * @see ConstraintViolation#getPropertyPath()
	 */
	public static <T> Collection<String> getPropertyPaths(Set<ConstraintViolation<T>> violations) {
		if (violations == null) {
			throw new IllegalArgumentException("violations is null");
		}
		
		return violations.stream()
			.map(ConstraintViolation::getPropertyPath)
			.map(Objects::toString)
			.collect(toList());
	}
	
	/**
	 * @return property paths of violated properties of {@code object}
	 * @see ValidationUtil#getViolatedPropertyPaths(Object)
	 */
	public static <T> Collection<String> getViolatedPropertyPaths(T object, Class<?>... groups) {
		return getPropertyPaths(validate(object, groups));
	}

}