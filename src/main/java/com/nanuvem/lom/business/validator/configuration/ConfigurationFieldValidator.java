package com.nanuvem.lom.business.validator.configuration;

import java.util.List;

import org.codehaus.jackson.JsonNode;

import com.nanuvem.lom.api.AttributeValue;
import com.nanuvem.lom.business.validator.ValidationError;

public class ConfigurationFieldValidator implements AttributeValidator {

	protected String field;
	private Class<?> clazz;

	public ConfigurationFieldValidator(String field, Class<?> clazz) {
		this.field = field;
		this.clazz = clazz;
	}

	public void validateDefault(List<ValidationError> errors,
			JsonNode configuration) {

		if (clazz.isAssignableFrom(Boolean.class) && configuration.has(field)) {
			if (!configuration.get(field).isBoolean()) {
				ValidationError.addError(errors, "the " + field
						+ " value must be true or false literals");
			}
		}

		if (clazz.isAssignableFrom(Integer.class) && configuration.has(field)) {
			if (!configuration.get(field).isIntegralNumber()) {
				ValidationError.addError(errors, "the " + field
						+ " value must be an integer number");
			}
		}

		if (clazz.isAssignableFrom(String.class) && configuration.has(field)) {
			if (!configuration.get(field).isTextual()) {
				ValidationError.addError(errors, "the " + field
						+ " value must be a string");
			}
		}
	}

	public void validateValue(List<ValidationError> errors,
			JsonNode configuration, AttributeValue value) {
		// Do not need to validate value
	}

}
