package com.nanuvem.lom.kernel;

import java.util.List;

import com.nanuvem.lom.api.Entity;
import com.nanuvem.lom.api.Facade;
import com.nanuvem.lom.api.dao.DaoFactory;

public class KernelFacade implements Facade {

	private EntityServiceImpl entityService;

	public KernelFacade(DaoFactory daoFactory) {
		entityService = new EntityServiceImpl(daoFactory);
	}
	
	public EntityServiceImpl getEntityService() {
		return this.entityService;
	}
	
	public Entity create(Entity entity) {
		return entityService.create(entity);
	}

	public Entity findEntityById(Long id) {
		return entityService.findById(id);
	}

	public Entity findEntityByFullName(String fullName) {
		return entityService.findByFullName(fullName);
	}

	public List<Entity> listAllEntities() {
		return entityService.listAll();
	}

	public List<Entity> listEntitiesByFullName(String fragment) {
		return entityService.listByFullName(fragment);
	}

	public Entity update(Entity entity) {
		return entityService.update(entity);
	}

	public void deleteEntity(Long id) {
		entityService.delete(id);
	}

}
