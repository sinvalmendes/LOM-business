package com.nanuvem.lom.kernel.attribute;

import com.nanuvem.lom.api.Facade;
import com.nanuvem.lom.api.tests.attribute.ObjectAttributeTest;
import com.nanuvem.lom.kernel.KernelFacade;
import com.nanuvem.lom.kernel.dao.MemoryDaoFactory;

public class KernelObjectAttributeTest extends ObjectAttributeTest {

	@Override
	public Facade createFacade() {
		return new KernelFacade(new MemoryDaoFactory());
	}

}
