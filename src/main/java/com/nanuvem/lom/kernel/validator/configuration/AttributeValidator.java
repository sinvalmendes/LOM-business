package com.nanuvem.lom.kernel.validator.configuration;

import java.util.List;

import org.codehaus.jackson.JsonNode;

import com.nanuvem.lom.api.AttributeValue;
import com.nanuvem.lom.kernel.validator.ValidationError;

public interface AttributeValidator {

	void validateDefault(List<ValidationError> errors, JsonNode configuration);

	void validateValue(List<ValidationError> errors, JsonNode configuration, AttributeValue value);
}