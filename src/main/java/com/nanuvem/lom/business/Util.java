package com.nanuvem.lom.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.SerializationUtils;

import com.nanuvem.lom.api.Attribute;
import com.nanuvem.lom.api.AttributeValue;
import com.nanuvem.lom.api.Entity;
import com.nanuvem.lom.api.Instance;
import com.nanuvem.lom.api.MetadataException;
import com.nanuvem.lom.business.validator.ValidationError;

public class Util {

    static void removeDefaultNamespace(Entity entity) {
        if (entity == null) {
            return;
        }

        String namespace = entity.getNamespace();

        if (namespace == null) {
            return;
        }

        if (namespace.equals(EntityServiceImpl.DEFAULT_NAMESPACE)) {
            entity.setNamespace("");
        }
    }

    static void removeDefaultNamespace(List<Entity> list) {
        for (Entity entity : list) {
            removeDefaultNamespace(entity);
        }
    }

    static void setDefaultNamespace(Entity entity) {
        if (entity == null) {
            return;
        }

        if (entity.getNamespace() == null || entity.getNamespace().equals("")) {
            entity.setNamespace(EntityServiceImpl.DEFAULT_NAMESPACE);
        }
    }

    @SuppressWarnings("unchecked")
    static <T extends Serializable> T clone(T t) {
        return (T) SerializationUtils.clone(t);
    }

    static <T extends Serializable> List<T> clone(List<T> ts) {
        List<T> clones = new ArrayList<T>();

        for (T t : ts) {
            clones.add(clone(t));
        }

        return clones;
    }

    static String setDefaultNamespace(String fullName) {
        if (fullName != null && !fullName.contains(".")) {
            fullName = EntityServiceImpl.DEFAULT_NAMESPACE + "." + fullName;
        }
        return fullName;
    }

    static void removeDefaultNamespace(Attribute attribute) {
        if (attribute != null) {
            removeDefaultNamespace(attribute.getEntity());
        }
    }

    public static void removeDefaultNamespace(Instance instance) {
        if (instance != null) {
            removeDefaultNamespace(instance.getEntity());
        }
    }

    public static void removeDefaultNamespace(AttributeValue value) {
        if (value != null) {
            removeDefaultNamespace(value.getInstance());
        }
    }

    static void throwValidationErrors(List<ValidationError> errors, String message) {
        if (!errors.isEmpty()) {
            String errorMessage = "";
            for (ValidationError error : errors) {
                if (errorMessage.isEmpty()) {
                    errorMessage += message + error.getMessage();
                } else {
                    errorMessage += ", " + error.getMessage();
                }
            }
            throw new MetadataException(errorMessage);
        }
    }

    public static void removeDefaultNamespaceForInstance(List<Instance> instances) {
        for (Instance instance : instances) {
            removeDefaultNamespace(instance);
        }
    }
}
