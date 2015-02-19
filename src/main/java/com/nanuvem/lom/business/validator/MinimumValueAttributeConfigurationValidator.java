package com.nanuvem.lom.business.validator;

import java.util.List;

import com.nanuvem.lom.business.validator.configuration.AttributeValidator;
import com.nanuvem.lom.business.validator.configuration.ConfigurationFieldValidator;

public class MinimumValueAttributeConfigurationValidator implements
		ValueValidator<Integer> {

	public void validate(List<ValidationError> errors, String attribute,
			String value, Integer minValue, boolean defaultValue) {

		if (value == null || Integer.parseInt(value) < minValue) {

			String message = (defaultValue) ? "the default value is smaller than minvalue"
					: "The value for '" + attribute
							+ "' must be greater or equal to " + minValue;
			ValidationError.addError(errors, message);
		}

	}

	public AttributeValidator createFieldValidator(String field) {
		return new ConfigurationFieldValidator(field, Integer.class);
	}

}
