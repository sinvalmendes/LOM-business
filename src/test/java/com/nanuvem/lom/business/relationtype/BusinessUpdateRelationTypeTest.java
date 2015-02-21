package com.nanuvem.lom.business.relationtype;

import org.junit.Before;

import com.nanuvem.lom.api.Facade;
import com.nanuvem.lom.api.tests.relationtype.UpdateRelationTypeTest;
import com.nanuvem.lom.business.BusinessFacade;
import com.nanuvem.lom.kernel.dao.MemoryDaoFactory;

public class BusinessUpdateRelationTypeTest extends UpdateRelationTypeTest {

    private MemoryDaoFactory daoFactory;

    @Override
    public Facade createFacade() {
        daoFactory = new MemoryDaoFactory();
        return new BusinessFacade(daoFactory);
    }

    @Before
    public void setUp() {
        this.createFacade();
    }
}
