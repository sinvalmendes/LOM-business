package com.nanuvem.lom.kernel.entity;

import com.nanuvem.lom.api.Facade;
import com.nanuvem.lom.api.tests.entity.DeleteEntityTest;
import com.nanuvem.lom.kernel.KernelFacade;
import com.nanuvem.lom.kernel.dao.MemoryDaoFactory;

public class KernelDeleteEntityTest extends DeleteEntityTest {

	private MemoryDaoFactory daoFactory;

	@Override
	public Facade createFacade() {
		daoFactory = new MemoryDaoFactory();
		return new KernelFacade(daoFactory);
	}

}
