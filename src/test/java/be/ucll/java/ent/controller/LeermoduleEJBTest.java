package be.ucll.java.ent.controller;

import be.ucll.java.ent.domain.LeermoduleDTO;
import be.ucll.java.ent.model.LeermoduleDAO;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class LeermoduleEJBTest {
    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private LeermoduleEJB ejb;
    private LeermoduleDTO testLeermodule;
    private long testLeermoduleID;

    @Before //Before every single test
    public void setUp() throws Exception {
        entityManagerFactory = Persistence.createEntityManagerFactory("LocalPostgresPU2");
        entityManager = entityManagerFactory.createEntityManager();
        ejb = new LeermoduleEJB();
        ejb.setEm(entityManager);
        ejb.setDao(new LeermoduleDAO(entityManager));

        entityManager.getTransaction().begin();

        // Create some testdata
        Calendar myCalendar = new GregorianCalendar(1999, 11, 25);
        Date myDate = myCalendar.getTime();
        testLeermodule = new LeermoduleDTO(0,"MGP12345", "Java Advanced", "2021-22");
        testLeermoduleID = ejb.createLeermodule(testLeermodule);
        // System.out.println("Unit tests. Created test Leermodule: " + testLeermodule);

        entityManager.getTransaction().commit();
    }

    @Test
    public void testCreateLeermoduleOK() {
        LeermoduleDTO lm = new LeermoduleDTO();
        lm.setCode("TestLM");
        lm.setBeschrijving("TestBeschrijving");
        lm.setSchooljaar("2020-2021");
        long tempid = ejb.createLeermodule(lm);

        LeermoduleDTO tempLM = ejb.getLeermoduleById(tempid);
        Assert.assertEquals(lm, tempLM);

        ejb.deleteLeermodule(tempid);
    }

    @Test
    public void getAllLeermodulesOK(){
        List<LeermoduleDTO> lst = ejb.getAllLeermodules();
        assert(lst.size() > 0);
    }

    @Test
    public void getAllLeermodulesCount(){
        List<LeermoduleDTO> lst = ejb.getAllLeermodules();
        long cnt = ejb.countLeermodules();
        Assert.assertEquals(lst.size(), cnt);
    }

    @After // After every single test
    public void tearDown() throws Exception {
        entityManager.getTransaction().begin();
        ejb.deleteLeermodule(testLeermoduleID);
        entityManager.getTransaction().commit();

        entityManager.close();
        entityManagerFactory.close();
    }

}