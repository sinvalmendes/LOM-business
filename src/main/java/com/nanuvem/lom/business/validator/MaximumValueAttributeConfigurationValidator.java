package com.nanuvem.lom.business.validator;

import java.util.List;

import com.nanuvem.lom.business.validator.configuration.AttributeValidator;
import com.nanuvem.lom.business.validator.configuration.ConfigurationFieldValidator;

public class MaximumValueAttributeConfigurationValidator implements
		ValueValidator<Integer> {

	public void validate(List<ValidationError> errors, String attribute,
			String value, Integer maxValue, boolean defaultValue) {

		if (value == null || Integer.parseInt(value) > maxValue) {
			String message = (defaultValue) ? "the default value is greater than maxvalue"
					: "The value for '" + attribute
							+ "' must be smaller or equal to " + maxValue;
			ValidationError.addError(errors, message);
		}
	}

	public AttributeValidator createFieldValidator(String field) {
		return new ConfigurationFieldValidator(field, Integer.class);
	}
}
