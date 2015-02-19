package com.nanuvem.lom.business;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;

import com.nanuvem.lom.api.Attribute;
import com.nanuvem.lom.api.AttributeValue;
import com.nanuvem.lom.api.Entity;
import com.nanuvem.lom.api.Instance;
import com.nanuvem.lom.api.MetadataException;
import com.nanuvem.lom.api.dao.AttributeValueDao;
import com.nanuvem.lom.api.dao.DaoFactory;
import com.nanuvem.lom.api.dao.InstanceDao;
import com.nanuvem.lom.api.util.JsonNodeUtil;
import com.nanuvem.lom.business.validator.ValidationError;
import com.nanuvem.lom.business.validator.configuration.AttributeTypeValidator;
import com.nanuvem.lom.business.validator.configuration.AttributeValidator;
import com.nanuvem.lom.business.validator.definition.AttributeTypeDefinition;
import com.nanuvem.lom.business.validator.definition.AttributeTypeDefinitionManager;

public class InstanceServiceImpl {

	private final String PREFIX_EXCEPTION_MESSAGE_VALUE = "Invalid value for the Instance. ";

	private InstanceDao instanceDao;
	private AttributeValueDao attributeValueDao;
	private EntityServiceImpl entityService;
	private AttributeServiceImpl attributeService;
	private AttributeTypeDefinitionManager definitionManager;

	InstanceServiceImpl(DaoFactory daoFactory, EntityServiceImpl entityService,
			AttributeServiceImpl attributeService,
			AttributeTypeDefinitionManager definitionManager) {
		this.entityService = entityService;
		this.attributeService = attributeService;
		this.definitionManager = definitionManager;
		this.instanceDao = new InstanceDaoDecorator(
				daoFactory.createInstanceDao());
		this.attributeValueDao = new AttributeValueDaoDecorator(
				daoFactory.createAttributeValueDao());
	}

	public Instance create(Instance instance) {
		if (instance.getEntity() == null) {
			throw new MetadataException(
					"Invalid value for Instance entity: The entity is mandatory");
		}
		Entity entity;
		try {
			entity = this.entityService.findById(instance.getEntity().getId());
		} catch (MetadataException e) {
			throw new MetadataException("Unknown entity id: "
					+ instance.getEntity().getId());
		}

		instance.setEntity(entity);
		validateAndAssignDefaultValueInAttributesValues(instance, entity);
		List<AttributeValue> values = instance.getValues();
		for (AttributeValue value : values) {
			Attribute attribute = attributeService.findAttributeById(value
					.getAttribute().getId());
			validateValue(attribute.getConfiguration(), value);
		}

		List<AttributeValue> originalValues = new ArrayList<AttributeValue>(
				values);

		values.clear();
		Instance newInstance = this.instanceDao.create(instance);

		for (AttributeValue value : originalValues) {
			value.setInstance(newInstance);
			this.attributeValueDao.create(value);
		}

		return instanceDao.findInstanceById(newInstance.getId());
	}

	private void validateValue(String configuration, AttributeValue value) {
		List<ValidationError> errors = new ArrayList<ValidationError>();

		AttributeTypeDefinition definition = definitionManager.get(value
				.getAttribute().getType().name());
		AttributeTypeValidator typeValidator = new AttributeTypeValidator(
				definition.getAttributeClass());
		typeValidator.validateValue(errors, null, value);

		if (configuration != null && !configuration.isEmpty()) {
			JsonNode jsonNode = load(configuration);

			for (AttributeValidator validator : definition.getValidators()) {
				validator.validateValue(errors, jsonNode, value);
			}
		}

		Util.throwValidationErrors(errors, PREFIX_EXCEPTION_MESSAGE_VALUE);

	}

	private void validateAndAssignDefaultValueInAttributesValues(
			Instance instance, Entity entity) {

		for (AttributeValue attributeValue : instance.getValues()) {
			if (!(entity.getAttributes()
					.contains(attributeValue.getAttribute()))) {
				throw new MetadataException("Unknown attribute for "
						+ instance.getEntity().getFullName() + ": "
						+ attributeValue.getAttribute().getName());
			}
			// this.validateTypeOfValue(attributeValue);

			String configuration = attributeValue.getAttribute()
					.getConfiguration();
			if (configuration != null && !configuration.isEmpty()) {
				JsonNode jsonNode = load(configuration);
				this.applyDefaultValueWhenAvailable(attributeValue, jsonNode);
			}
		}
	}

	private JsonNode load(String configuration) {
		JsonNode jsonNode = JsonNodeUtil.validate(configuration,
				"Invalid value for Attribute configuration: " + configuration);
		return jsonNode;
	}

	private void applyDefaultValueWhenAvailable(AttributeValue attributeValue,
			JsonNode jsonNode) {

		String defaultConfiguration = Attribute.DEFAULT_CONFIGURATION_NAME;
		if (jsonNode.has(defaultConfiguration)) {
			String defaultField = jsonNode.get(defaultConfiguration).asText();

			if (attributeValue.getValue() == null && defaultField != null) {
				attributeValue.setValue(defaultField);
			}
		}
	}

	public Instance findInstanceById(Long id) {
		return this.instanceDao.findInstanceById(id);
	}

	public List<Instance> findInstancesByEntityId(Long entityId) {
		return this.instanceDao.findInstancesByEntityId(entityId);
	}
}

class InstanceDaoDecorator implements InstanceDao {

	private InstanceDao instanceDao;

	public InstanceDaoDecorator(InstanceDao instanceDao) {
		this.instanceDao = instanceDao;
	}

	public Instance create(Instance instance) {
		Instance createdInstance = Util.clone(instanceDao.create(Util
				.clone(instance)));
		Util.removeDefaultNamespace(createdInstance);
		return createdInstance;
	}

	public Instance findInstanceById(Long id) {
		Instance instance = Util.clone(instanceDao.findInstanceById(id));
		Util.removeDefaultNamespace(instance);
		return instance;
	}

	public Instance update(Instance instance) {
		Instance updatedInstance = Util.clone(instanceDao.update(Util
				.clone(instance)));
		Util.removeDefaultNamespace(updatedInstance);
		return updatedInstance;
	}

	public void delete(Long id) {
		instanceDao.delete(id);
	}

	public List<Instance> findInstancesByEntityId(Long entityId) {
		List<Instance> instances = Util.clone(instanceDao
				.findInstancesByEntityId(entityId));
		Util.removeDefaultNamespaceForInstance(instances);
		return instances;
	}

}

class AttributeValueDaoDecorator implements AttributeValueDao {

	private AttributeValueDao attributeValueDao;

	public AttributeValueDaoDecorator(AttributeValueDao attributeValueDao) {
		this.attributeValueDao = attributeValueDao;
	}

	public AttributeValue create(AttributeValue value) {
		AttributeValue createdValue = Util.clone(attributeValueDao.create(Util
				.clone(value)));
		Util.removeDefaultNamespace(createdValue);
		return createdValue;
	}
}