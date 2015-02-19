package com.nanuvem.lom.business.validator;

import java.util.List;

import com.nanuvem.lom.business.validator.configuration.AttributeValidator;
import com.nanuvem.lom.business.validator.configuration.ConfigurationFieldValidator;

public class MinimumLengthAttributeConfigurationValidator implements
		ValueValidator<Integer> {

	public void validate(List<ValidationError> errors, String attribute,
			String value, Integer minLength, boolean defaultValue) {

		if (value.length() < minLength) {

			String message = (defaultValue) ? "the default value is smaller than minlength"
					: "The value for '" + attribute
							+ "' must have a minimum length of " + minLength
							+ " characters";
			ValidationError.addError(errors, message);
		}
	}

	public AttributeValidator createFieldValidator(String field) {
		return new ConfigurationFieldValidator(field, Integer.class);
	}

}
