package com.sap.s4hana.sample.validation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ParameterNameProvider;

import org.apache.bval.jsr.parameter.DefaultParameterNameProvider;

import com.google.common.annotations.VisibleForTesting;

/**
 * Optional: Improves Bean Validation error messages by overriding
 * {@link DefaultParameterNameProvider} which simply names all parameters as
 * "arg" + running number
 * <p>
 * Requires Java Complier {@code -parameters} option to work properly.
 * The option is set in main {@code pom.xml} file for the parent project.
 *
 */
public class ReflectionParameterNameProvider implements ParameterNameProvider {

	@Override
	public List<String> getParameterNames(Constructor<?> constructor) {
		return getParameterNamesViaReflection(constructor);
	}

	@Override
	public List<String> getParameterNames(Method method) {
		return getParameterNamesViaReflection(method);
	}
	
	@VisibleForTesting
	protected static List<String> getParameterNamesViaReflection(Executable executable) {
		return Stream.of(executable.getParameters())
				.map(Parameter::getName)
				.collect(Collectors.toList());
	}

}
