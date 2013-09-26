package com.akirkpatrick.mm;

import com.akirkpatrick.mm.model.Account;
import com.akirkpatrick.mm.model.Project;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class EntityTests extends Assert {
    @Configuration
    private class TestConfig {

    }

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MovieMakerService mms;

    @PersistenceContext
    private EntityManager em;

    @Test(expected = NoResultException.class)
    public void testEmptyDatabaseNoUsers() {
        assertNotNull(applicationContext);
        assertNotNull(mms);
        mms.authenticate("alfie", null);
    }

    @Test
    public void testEntityManager() {
        em.createQuery("select a from Account a").getResultList();
    }

    @Test
    @Transactional
    public void testCreateAccount() {
        mms.createAccount("alfie", "password");
        Account a=mms.authenticate("alfie", "password");
        assertEquals("alfie", a.getUsername());
    }

    @Test
    @Transactional
    public void testAddProjectWithFrames() {
        Account a=mms.createAccount("alfie", "password");
        Project p=mms.createProject("test1");
        a.addProject(p);
        p.addFrame("x");

        em.flush();

        Account b=mms.authenticate("alfie", "password");
        List<Project> projList=b.getProjects();
        assertEquals(1, projList.size());
        List<String> frames=projList.get(0).getFrames();
        assertEquals(1, frames.size());
    }

    @Test
    @Transactional
    public void testCheckAccountExists() {
        Account a=mms.createAccount("alfie", "password");

        assertTrue(mms.isAccount("alfie"));
        assertFalse(mms.isAccount("john"));
    }
}
