package com.nanuvem.lom.kernel.validator;

import org.codehaus.jackson.JsonNode;

public class IntegerAttributeConfigurationValidatorWithDefault extends
		AttributeConfigurationValidatorWithDefault<Integer> {

	public IntegerAttributeConfigurationValidatorWithDefault(String field,
			String defaultField, ValueValidator<Integer> valueValidator) {
		super(field, defaultField, valueValidator);
	}

	protected Integer getConfigurationValue(JsonNode configuration) {
		return configuration.get(field).asInt();
	}

}