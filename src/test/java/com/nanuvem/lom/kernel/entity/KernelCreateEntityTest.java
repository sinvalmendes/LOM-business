package com.nanuvem.lom.kernel.entity;

import com.nanuvem.lom.api.Facade;
import com.nanuvem.lom.api.tests.entity.CreateEntityTest;
import com.nanuvem.lom.kernel.KernelFacade;
import com.nanuvem.lom.kernel.dao.MemoryDaoFactory;

public class KernelCreateEntityTest extends CreateEntityTest {

	private MemoryDaoFactory daoFactory;

	@Override
	public Facade createFacade() {
		daoFactory = new MemoryDaoFactory();
		return new KernelFacade(daoFactory);
	}

}
