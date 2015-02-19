package com.nanuvem.lom.business.validator.definition;

import java.util.List;

import com.nanuvem.lom.business.validator.configuration.AttributeValidator;

public interface AttributeTypeDefinition {

	List<AttributeValidator> getValidators();

	/**
	 * Returns true when this attribute type deployer accepts the 'fieldName'
	 * configuration field
	 */
	boolean containsConfigurationField(String fieldName);

	Class<?> getAttributeClass();
}
