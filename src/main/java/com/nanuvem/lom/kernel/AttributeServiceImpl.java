package com.nanuvem.lom.kernel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;

import com.nanuvem.lom.api.Attribute;
import com.nanuvem.lom.api.Entity;
import com.nanuvem.lom.api.MetadataException;
import com.nanuvem.lom.api.dao.AttributeDao;
import com.nanuvem.lom.api.dao.DaoFactory;
import com.nanuvem.lom.kernel.deployer.AttributeTypeDeployer;
import com.nanuvem.lom.kernel.deployer.Deployers;
import com.nanuvem.lom.kernel.util.JsonNodeUtil;
import com.nanuvem.lom.kernel.validator.AttributeConfigurationValidator;
import com.nanuvem.lom.kernel.validator.ValidationError;

public class AttributeServiceImpl {

	private AttributeDao attributeDao;

	private EntityServiceImpl entityService;

	private final Integer MINIMUM_ATTRIBUTE_SEQUENCE = 1;

	private final String PREFIX_EXCEPTION_MESSAGE_CONFIGURATION = "Invalid configuration for attribute";

	private Deployers deployers;

	AttributeServiceImpl(DaoFactory dao, EntityServiceImpl entityService,
			Deployers deployers) {
		this.entityService = entityService;
		this.deployers = deployers;
		this.attributeDao = dao.createAttributeDao();

	}

	private void validateCreate(Attribute attribute) {
		this.validateExistingAttributeNotInEntity(attribute);
		defineAttributeSequenceNumber(attribute);

		this.validateNameAttribute(attribute);

		if (attribute.getType() == null) {
			throw new MetadataException("The type of a Attribute is mandatory");
		}
		this.validateAttributeConfiguration(attribute);
	}

	private void defineAttributeSequenceNumber(Attribute attribute) {
		int currentNumberOfAttributes = attribute.getEntity().getAttributes()
				.size();
		if (attribute.getSequence() != null) {
			boolean minValueForSequence = attribute.getSequence() < MINIMUM_ATTRIBUTE_SEQUENCE;
			boolean maxValueForSequence = currentNumberOfAttributes + 1 < attribute
					.getSequence();

			if (minValueForSequence || maxValueForSequence) {
				throw new MetadataException(
						"Invalid value for Attribute sequence: "
								+ attribute.getSequence());
			}
		} else {
			attribute.setSequence(currentNumberOfAttributes + 1);
		}
	}

	private void validateNameAttribute(Attribute attribute) {
		if (attribute.getName() == null || attribute.getName().isEmpty()) {
			throw new MetadataException("The name of an Attribute is mandatory");
		}

		if (!Pattern.matches("[a-zA-Z1-9]{1,}", attribute.getName())) {
			throw new MetadataException("Invalid value for Attribute name: "
					+ attribute.getName());
		}
	}

	private List<Attribute> findAllAttributesForEntity(Entity entity) {
		if (entity != null && !entity.getFullName().isEmpty()) {
			Entity foundEntity = entityService.findByFullName(entity
					.getFullName());
			if (foundEntity != null && foundEntity.getAttributes() != null
					&& foundEntity.getAttributes().size() > 0) {
				return foundEntity.getAttributes();
			}
		}
		return null;
	}

	private void validateAttributeConfiguration(Attribute attribute) {
		String configuration = attribute.getConfiguration();
		if (configuration != null && !configuration.isEmpty()) {
			JsonNode jsonNode = JsonNodeUtil.validate(configuration,
					"Invalid value for Attribute configuration: "
							+ configuration);
			validateFieldNames(attribute, jsonNode);
			validateFieldValues(attribute, jsonNode);
		}
	}

	private void validateFieldNames(Attribute attribute, JsonNode jsonNode) {
		Iterator<String> fieldNames = jsonNode.getFieldNames();
		while (fieldNames.hasNext()) {
			String fieldName = fieldNames.next();
			if (!this.deployers.get(attribute.getType().name())
					.containsConfigurationField(fieldName)) {

				throw new MetadataException(
						"Invalid configuration for attribute "
								+ attribute.getName() + ": the " + fieldName
								+ " configuration attribute is unknown");
			}
		}
	}

	private void validateFieldValues(Attribute attribute, JsonNode jsonNode) {
		List<ValidationError> errors = new ArrayList<ValidationError>();
		AttributeTypeDeployer deployer = this.deployers.get(attribute.getType()
				.name());
		for (AttributeConfigurationValidator validator : deployer
				.getValidators()) {
			validator.validate(errors, jsonNode);
		}

		if (!errors.isEmpty()) {
			String errorMessage = "";
			for (ValidationError error : errors) {
				if (errorMessage.isEmpty()) {
					errorMessage += PREFIX_EXCEPTION_MESSAGE_CONFIGURATION
							+ " " + attribute.getName() + ": "
							+ error.getMessage();
				} else {
					errorMessage += ", " + error.getMessage();
				}
			}
			throw new MetadataException(errorMessage);
		}
	}

	private Entity validateExistingEntityForAttribute(Attribute attribute) {
		Entity entity = null;
		try {
			entity = entityService.findById(attribute.getEntity().getId());
		} catch (MetadataException e) {
			throw new MetadataException("Invalid Entity: "
					+ attribute.getEntity().getFullName());
		}
		return entity;
	}

	private void validateExistingAttributeNotInEntity(Attribute attribute) {
		List<Attribute> foundAttributes = this
				.findAllAttributesForEntity(attribute.getEntity());
		if (foundAttributes != null) {
			for (Attribute at : foundAttributes) {
				if (at.getName().equalsIgnoreCase(attribute.getName())) {
					this.throwMetadataExceptionOnAttributeDuplication(attribute);
				}
			}
		}
	}

	private void validateExistingAttributeNotInEntityOnUpdate(
			Attribute attribute) {
		if (attribute.getId() != null) {
			Attribute foundAttributes = this
					.findAttributeByNameAndEntityFullName(attribute.getName(),
							attribute.getEntity().getFullName());
			if (foundAttributes != null) {
				if (!attribute.getId().equals(foundAttributes.getId())) {
					this.throwMetadataExceptionOnAttributeDuplication(attribute);
				}
			}
		}
	}

	private void throwMetadataExceptionOnAttributeDuplication(
			Attribute attribute) {
		throw new MetadataException("Attribute duplication on "
				+ attribute.getEntity().getFullName()
				+ " Entity. It already has an attribute "
				+ StringUtils.lowerCase(attribute.getName() + "."));
	}

	public Attribute create(Attribute attribute) {
		Entity entity = validateExistingEntityForAttribute(attribute);
		attribute.setEntity(entity);
		this.validateCreate(attribute);
		return this.attributeDao.create(attribute);
	}

	public List<Attribute> listAllAttributes(String entityFullName) {
		Entity entity = entityService.findByFullName(entityFullName);
		return entity.getAttributes();
	}

	public Attribute findAttributeById(Long id) {
		if (id != null) {
			return this.attributeDao.findAttributeById(id);
		} else {
			return null;
		}
	}

	public Attribute findAttributeByNameAndEntityFullName(String nameAttribute,
			String entityFullName) {

		if ((nameAttribute != null && !nameAttribute.isEmpty())
				&& (entityFullName != null && !entityFullName.isEmpty())) {
			if (!entityFullName.contains(".")) {
				entityFullName = EntityServiceImpl.DEFAULT_NAMESPACE + "."
						+ entityFullName;
			}
			return this.attributeDao.findAttributeByNameAndEntityFullName(
					nameAttribute, entityFullName);
		}
		return null;
	}

	public Attribute update(Attribute attribute) {
		this.validateNameAttribute(attribute);
		this.validateUpdateSequence(attribute);
		this.validateUpdateType(attribute);
		this.validateExistingAttributeNotInEntityOnUpdate(attribute);
		this.validateAttributeConfiguration(attribute);

		return this.attributeDao.update(attribute);
	}

	private void validateUpdateType(Attribute attribute) {
		Attribute attributeFound = this.findAttributeById(attribute.getId());

		if (!attributeFound.getType().equals(attribute.getType())) {
			throw new MetadataException(
					"Can not change the type of an attribute");
		}
	}

	private void validateUpdateSequence(Attribute attribute) {
		Entity entity = entityService.findById(attribute.getEntity().getId());
		int currentNumberOfAttributes = entity.getAttributes()
				.get(entity.getAttributes().size() - 1).getSequence();

		if (attribute.getSequence() != null) {
			boolean minValueForSequence = attribute.getSequence() < MINIMUM_ATTRIBUTE_SEQUENCE;
			boolean maxValueForSequence = currentNumberOfAttributes < attribute
					.getSequence();

			if (!(minValueForSequence || maxValueForSequence)) {
				return;
			}
		}
		throw new MetadataException("Invalid value for Attribute sequence: "
				+ attribute.getSequence());
	}
}