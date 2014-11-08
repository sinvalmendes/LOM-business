package com.nanuvem.lom.kernel;

import com.nanuvem.lom.api.Facade;
import com.nanuvem.lom.api.tests.UpdateAttributeServiceTest;
import com.nanuvem.lom.kernel.dao.MemoryDaoFactory;

public class KernelUpdateAttributeServiceTest extends UpdateAttributeServiceTest {

	@Override
	public Facade createFacade() {
		return new KernelFacade(new MemoryDaoFactory());
	}

}
