package com.nanuvem.lom.business.entity;

import com.nanuvem.lom.api.Facade;
import com.nanuvem.lom.api.tests.entity.UpdateEntityTest;
import com.nanuvem.lom.business.BusinessFacade;
import com.nanuvem.lom.kernel.dao.MemoryDaoFactory;

public class BusinessUpdateEntityTest extends UpdateEntityTest {

    private MemoryDaoFactory daoFactory;

    @Override
    public Facade createFacade() {
        daoFactory = new MemoryDaoFactory();
        return new BusinessFacade(daoFactory);
    }

}
