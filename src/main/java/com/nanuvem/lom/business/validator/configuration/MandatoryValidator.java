package com.nanuvem.lom.business.validator.configuration;

import java.util.List;

import org.codehaus.jackson.JsonNode;

import com.nanuvem.lom.api.Attribute;
import com.nanuvem.lom.api.AttributeValue;
import com.nanuvem.lom.business.validator.ValidationError;

public class MandatoryValidator implements AttributeValidator {

	public void validateDefault(List<ValidationError> errors,
			JsonNode configuration) {
		// Do not need to validate default value
	}

	public void validateValue(List<ValidationError> errors,
			JsonNode configuration, AttributeValue value) {
		if (configuration != null
				&& configuration.has(Attribute.MANDATORY_CONFIGURATION_NAME)) {

			boolean isMandatory = configuration.get(
					Attribute.MANDATORY_CONFIGURATION_NAME).getBooleanValue();

			if (isMandatory
					&& (value.getValue() == null || value.getValue().isEmpty())) {
				ValidationError.addError(errors, "The value for the '"
						+ value.getAttribute().getName()
						+ "' attribute is mandatory");
			}
		}
	}

}
