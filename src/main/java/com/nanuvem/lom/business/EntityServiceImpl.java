package com.nanuvem.lom.business;

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
        this.dao = new EntityDaoDecorator(factory.createEntityDao());
    }

    public Entity create(Entity entity) {
        validateEntity(entity);
        Entity createdEntity = dao.create(entity);
        return createdEntity;
    }

    private void validateEntity(Entity entity) {
        if (entity.getName() == null || entity.getName().equals("")) {
            throw new MetadataException("The name of an Entity is mandatory");
        }

        if (entity.getNamespace() == null) {
            entity.setNamespace("");
        }

        lowerCase(entity);

        validadeNameAndNamespacePattern(entity);
        validadeEntityDuplication(entity);
    }

    private void lowerCase(Entity entity) {
        entity.setName(entity.getName().toLowerCase());
        entity.setNamespace(entity.getNamespace().toLowerCase());
    }

    private void validadeNameAndNamespacePattern(Entity entity) {
        String namespace = entity.getNamespace();

        if (!namespace.equals("") && !Pattern.matches("[a-zA-Z1-9.]{1,}", namespace)) {
            throw new MetadataException("Invalid value for Entity namespace: " + namespace);
        }

        if (!Pattern.matches("[a-zA-Z1-9]{1,}", entity.getName())) {
            throw new MetadataException("Invalid value for Entity name: " + entity.getName());
        }
    }

    private void validadeEntityDuplication(Entity entity) {
        Entity found = null;
        try {
            found = findByFullName(entity.getFullName());
        } catch (MetadataException me) {
            found = null;
        }

        if (found != null && !found.getId().equals(entity.getId())) {
            StringBuilder message = new StringBuilder();
            message.append("The ");
            message.append(found.getFullName());
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
            namespace = classFullName.substring(0, classFullName.lastIndexOf("."));
            name = classFullName.substring(classFullName.lastIndexOf(".") + 1, classFullName.length());
        } else {
            namespace = "";
            name = classFullName;
        }

        if (!Pattern.matches("[a-zA-Z1-9.]{1,}", namespace) && !namespace.isEmpty()) {
            this.formatStringAndThrowsExceptionInvalidKeyForEntity(classFullName);
        }

        if (!Pattern.matches("[a-zA-Z1-9]{1,}", name) && !name.isEmpty()) {
            this.formatStringAndThrowsExceptionInvalidKeyForEntity(classFullName);
        }

        if (namespace.isEmpty()) {
            namespace = "";
        }

        Entity classByNamespaceAndName = dao.findByFullName(classFullName);

        if (classByNamespaceAndName == null) {
            if (classFullName.startsWith(".")) {
                classFullName = classFullName.substring(1);
            }
            if (classFullName.endsWith(".")) {
                classFullName = classFullName.substring(0, classFullName.length() - 1);
            }
            throw new MetadataException("Entity not found: " + classFullName);
        }

        return classByNamespaceAndName;
    }

    public Entity findById(Long id) {
        Entity entity = this.dao.findById(id);
        return entity;
    }

    public List<Entity> listAll() {
        List<Entity> list = dao.listAll();
        return list;
    }

    public List<Entity> listByFullName(String fragment) {
        if (fragment == null) {
            fragment = "";
        }

        if (!Pattern.matches("[a-zA-Z1-9.]{1,}", fragment) && !fragment.isEmpty()) {
            throw new MetadataException("Invalid value for Entity full name: " + fragment);
        }

        List<Entity> list = this.dao.listByFullName(fragment);
        return list;
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

    public Entity update(Entity entity) {
        this.validateEntityOnUpdate(entity);
        this.validateEntity(entity);
        Entity updatedEntity = this.dao.update(entity);
        return updatedEntity;
    }

    private void validateEntityOnUpdate(Entity updateEntity) {
        if (updateEntity.getId() == null && updateEntity.getVersion() == null) {
            throw new MetadataException("The version and id of an Entity are mandatory on update");
        } else if (updateEntity.getId() == null) {
            throw new MetadataException("The id of an Entity is mandatory on update");
        } else if (updateEntity.getVersion() == null) {
            throw new MetadataException("The version of an Entity is mandatory on update");
        }
    }

    public void delete(long id) {
        this.dao.delete(id);
    }

}

class EntityDaoDecorator implements EntityDao {

    private EntityDao entityDao;

    public EntityDaoDecorator(EntityDao entityDao) {
        this.entityDao = entityDao;
    }

    public Entity create(Entity entity) {
        Entity entityClone = Util.clone(entity);
        Util.setDefaultNamespace(entityClone);

        Entity createdEntity = entityDao.create(entityClone);

        Entity createdEntityClone = Util.clone(createdEntity);
        Util.removeDefaultNamespace(createdEntityClone);
        return createdEntityClone;
    }

    public List<Entity> listAll() {
        List<Entity> list = Util.clone(entityDao.listAll());
        Util.removeDefaultNamespace(list);
        return list;
    }

    public Entity findById(Long id) {
        Entity entity = Util.clone(entityDao.findById(id));
        Util.removeDefaultNamespace(entity);
        return entity;
    }

    public List<Entity> listByFullName(String fragment) {
        List<Entity> list = Util.clone(entityDao.listByFullName(fragment));
        Util.removeDefaultNamespace(list);
        return list;
    }

    public Entity findByFullName(String fullName) {
        fullName = Util.setDefaultNamespace(fullName);

        Entity entity = Util.clone(entityDao.findByFullName(fullName));
        Util.removeDefaultNamespace(entity);
        return entity;
    }

    public Entity update(Entity entity) {
        Entity entityClone = Util.clone(entity);
        Util.setDefaultNamespace(entityClone);

        Entity updatedEntity = entityDao.update(entityClone);

        Entity updatedEntityClone = Util.clone(updatedEntity);
        Util.removeDefaultNamespace(updatedEntityClone);
        return updatedEntityClone;
    }

    public void delete(Long id) {
        entityDao.delete(id);
    }

}