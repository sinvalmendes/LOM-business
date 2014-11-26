package com.nanuvem.lom.business.validator.configuration;

import java.util.List;

import org.codehaus.jackson.JsonNode;

import com.nanuvem.lom.api.AttributeValue;
import com.nanuvem.lom.business.validator.ValidationError;
import com.nanuvem.lom.business.validator.ValueValidator;

public class AttributeValidatorWithValue<T> implements
		AttributeValidator {

	protected ValueValidator<T> valueValidator;
	protected String defaultField;
	protected String field;
	private Class<T> clazz;

	public AttributeValidatorWithValue(String field,
			String defaultField, ValueValidator<T> valueValidator, Class<T> clazz) {
		this.field = field;
		this.defaultField = defaultField;
		this.valueValidator = valueValidator;
		this.clazz = clazz;
	}

	
	public void validateDefault(List<ValidationError> errors, JsonNode configuration) {
		AttributeValidator fieldValidator = new ConfigurationFieldValidator(field, clazz);  
		fieldValidator.validateDefault(errors, configuration);

		if (configuration.has(field)) {
			if (configuration.has(defaultField)) {

				String defaultValue = configuration.get(defaultField).asText();

				T configurationValue = getConfigurationValue(configuration);
				valueValidator.validate(errors, null, defaultValue,
						configurationValue, true);
			}
		}
	}

	public void validateValue(List<ValidationError> errors,
			JsonNode configuration, AttributeValue value) {
		if (configuration != null && configuration.has(field)) {

			T configurationValue = getConfigurationValue(configuration);
			valueValidator.validate(errors, value.getAttribute().getName(), value.getValue(),
					configurationValue, false);
		}
	}

	@SuppressWarnings("unchecked")
	protected T getConfigurationValue(JsonNode configuration) {
		
		JsonNode jsonNode = configuration.get(field);
		
		if (jsonNode.isTextual()) {
			return (T) jsonNode.asText();
		}

		if (jsonNode.isIntegralNumber()) {
			return (T) ((Integer) jsonNode.asInt());
		}

		return null;
		
	}


}