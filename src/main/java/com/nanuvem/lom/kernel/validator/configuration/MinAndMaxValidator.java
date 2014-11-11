package com.nanuvem.lom.kernel.validator.configuration;


import java.util.List;

import org.codehaus.jackson.JsonNode;

import com.nanuvem.lom.api.AttributeValue;
import com.nanuvem.lom.kernel.validator.ValidationError;

public class MinAndMaxValidator implements
		AttributeValidator {

	private String maxField;
	private String minField;

	public MinAndMaxValidator(String maxField, String minField) {
		this.maxField = maxField;
		this.minField = minField;
	}

	public void validateDefault(List<ValidationError> errors, JsonNode configuration) {

		if (configuration.has(maxField) && configuration.has(minField)) {

			int minLengthValue = configuration.get(minField).getIntValue();
			int maxLengthValue = configuration.get(maxField).getIntValue();

			if (minLengthValue > maxLengthValue) {
				ValidationError.addError(errors, "the " + minField + " is greater than "
						+ maxField);
			}
		}

	}

	public void validateValue(List<ValidationError> errors,
			JsonNode configuration, AttributeValue value) {
		//Do not need to validate value
	}

}
