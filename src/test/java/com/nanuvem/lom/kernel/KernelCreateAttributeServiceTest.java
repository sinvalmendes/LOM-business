package com.nanuvem.lom.kernel;

import com.nanuvem.lom.api.Facade;
import com.nanuvem.lom.api.tests.CreateAttributeServiceTest;
import com.nanuvem.lom.kernel.dao.MemoryDaoFactory;

public class KernelCreateAttributeServiceTest extends CreateAttributeServiceTest {

	@Override
	public Facade createFacade() {
		return new KernelFacade(new MemoryDaoFactory());
	}

}
