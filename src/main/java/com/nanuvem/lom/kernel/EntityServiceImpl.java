package com.nanuvem.lom.kernel;

import java.util.List;
import java.util.regex.Pattern;

import com.nanuvem.lom.api.Entity;
import com.nanuvem.lom.api.MetadataException;
import com.nanuvem.lom.api.dao.DaoFactory;
import com.nanuvem.lom.api.dao.EntityDao;

public class EntityServiceImpl {

	private EntityDao dao;

	public static final String DEFAULT_NAMESPACE = "default";

	EntityServiceImpl(DaoFactory factory) {
		this.dao = factory.createEntityDao();
	}

	public Entity create(Entity entity) {
		validateEntity(entity);
		return dao.create(entity);
	}

	private void validateEntity(Entity entity) {
		if (entity.getName() == null || entity.getName().equals("")) {
			throw new MetadataException("The name of an Entity is mandatory");
		}

		if (entity.getNamespace() == null || entity.getNamespace().equals("")) {
			entity.setNamespace(DEFAULT_NAMESPACE);
		}

		validadeNameAndNamespacePattern(entity);
		validadeEntityDuplication(entity);
	}

	private void validadeNameAndNamespacePattern(Entity entity) {
		if (!Pattern.matches("[a-zA-Z1-9.]{1,}", entity.getNamespace())) {
			throw new MetadataException("Invalid value for Entity namespace: "
					+ entity.getNamespace());
		}

		if (!Pattern.matches("[a-zA-Z1-9]{1,}", entity.getName())) {
			throw new MetadataException("Invalid value for Entity name: "
					+ entity.getName());
		}
	}

	private void validadeEntityDuplication(Entity entity) {
		String readEntityQuery = entity.getNamespace() + "." + entity.getName();
		Entity found = null;
		try {
			found = findByFullName(readEntityQuery);
		} catch (MetadataException me) {
			found = null;
		}

		if (found != null && found.getName().equals(entity.getName())
				&& found.getNamespace().equals(entity.getNamespace())) {
			StringBuilder message = new StringBuilder();
			message.append("The ");
			if (!entity.getNamespace().equals(DEFAULT_NAMESPACE)) {
				message.append(entity.getNamespace());
				message.append(".");
			}
			message.append(entity.getName());
			message.append(" Entity already exists");
			throw new MetadataException(message.toString());
		}
	}

	// There is no test case for classFullName = null. How should the message
	// being thrown exception in this case?
	public Entity findByFullName(String classFullName) {
		String namespace = null;
		String name = null;

		if (classFullName.contains(".")) {
			namespace = classFullName.substring(0,
					classFullName.lastIndexOf("."));
			name = classFullName.substring(classFullName.lastIndexOf(".") + 1,
					classFullName.length());
		} else {
			namespace = DEFAULT_NAMESPACE;
			name = classFullName;
		}

		if (!Pattern.matches("[a-zA-Z1-9.]{1,}", namespace)
				&& !namespace.isEmpty()) {
			this.formatStringAndThrowsExceptionInvalidKeyForEntity(classFullName);
		}

		if (!Pattern.matches("[a-zA-Z1-9]{1,}", name) && !name.isEmpty()) {
			this.formatStringAndThrowsExceptionInvalidKeyForEntity(classFullName);
		}

		if (namespace.isEmpty()) {
			namespace = DEFAULT_NAMESPACE;
		}

		Entity classByNamespaceAndName = dao.findByFullName(namespace + "."
				+ name);

		if (classByNamespaceAndName == null) {
			if (classFullName.startsWith(".")) {
				classFullName = classFullName.substring(1);
			}
			if (classFullName.endsWith(".")) {
				classFullName = classFullName.substring(0,
						classFullName.length() - 1);
			}
			throw new MetadataException("Entity not found: " + classFullName);
		}
		return classByNamespaceAndName;
	}

	public Entity findById(Long id) {
		return this.dao.findById(id);
	}

	public List<Entity> listAll() {
		return dao.listAll();
	}

	public List<Entity> listByFullName(String fragment) {
		if (fragment == null) {
			fragment = "";
		}

		if (!Pattern.matches("[a-zA-Z1-9.]{1,}", fragment)
				&& !fragment.isEmpty()) {
			throw new MetadataException("Invalid value for Entity full name: "
					+ fragment);
		}

		return this.dao.listByFullName(fragment);
	}

	private void formatStringAndThrowsExceptionInvalidKeyForEntity(String value) {
		if (value.startsWith(".")) {
			value = value.substring(1);
		}
		if (value.endsWith(".")) {
			value = value.substring(0, value.length() - 1);
		}
		throw new MetadataException("Invalid key for Entity: " + value);

	}

	public Entity update(Entity updateEntity) {
		this.validateEntityOnUpdate(updateEntity);
		this.validateEntity(updateEntity);
		return this.dao.update(updateEntity);
	}

	private void validateEntityOnUpdate(Entity updateEntity) {
		if (updateEntity.getId() == null && updateEntity.getVersion() == null) {
			throw new MetadataException(
					"The version and id of an Entity are mandatory on update");
		} else if (updateEntity.getId() == null) {
			throw new MetadataException(
					"The id of an Entity is mandatory on update");
		} else if (updateEntity.getVersion() == null) {
			throw new MetadataException(
					"The version of an Entity is mandatory on update");
		}
	}

	public void delete(long id) {
		this.dao.delete(id);
	}

}