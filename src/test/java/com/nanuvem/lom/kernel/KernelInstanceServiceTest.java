package com.nanuvem.lom.kernel;

import com.nanuvem.lom.api.Facade;
import com.nanuvem.lom.api.tests.InstanceServiceTest;
import com.nanuvem.lom.kernel.dao.MemoryDaoFactory;

public class KernelInstanceServiceTest extends InstanceServiceTest {

	private MemoryDaoFactory daoFactory;

	@Override
	public Facade createFacade() {
		daoFactory = new MemoryDaoFactory();
		return new KernelFacade(daoFactory);
	}

}
