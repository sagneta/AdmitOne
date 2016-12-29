package com.admitone.test;



import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.admitone.main.AdministrationService;

import lombok.Getter;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/** JUnit Test Suite 
 *
 * @author Stephen Agneta
 * @since Build 1.000
 *
 */
@RunWith(Arquillian.class)
@Slf4j
public class TestAdmitOne {
    public static final String ROOT_WAR_DEPLOYMENT_LOCATION = "./build/libs/admitone.war";
    
    @PersistenceContext(unitName = "admitone", type = PersistenceContextType.TRANSACTION)
    @Getter private EntityManager entityManager;

    @Resource 
    private UserTransaction transaction;
    
    @EJB
    private AdministrationService adminService;
    
    
    /////////////////////////////////////////////////////////////////////////
    //                      Unit Tests below this point                    //
    /////////////////////////////////////////////////////////////////////////

    @Deployment
    public static WebArchive createDeployment() {
        val a = ShrinkWrap.create(ZipImporter.class, "testadmitone.war").importFrom(new File(ROOT_WAR_DEPLOYMENT_LOCATION)).as(WebArchive.class);
        log.info("Admit One War: " + a.toString(true));
        return a;
    }
    
    @After
    public void cleanup() throws Exception {
    }
    
    @Test
    public void sanityCheck() throws Exception {
        Assert.assertTrue("I ran ok!", true);
        assertThat(entityManager).isNotNull();
    }

    

    
    @Test
    public void bjondService_CRUD() throws Exception {
        // final BjondService entity = new BjondService();
        // entity.setId(UUID.randomUUID().toString());
        // entity.setGroupID(UUID.randomUUID().toString());
        // entity.setEndpoint("This is an endpoint");

        // transaction.begin();
        // entityManager.persist(entity);
        // transaction.commit();
        
        // final List<BjondService> list = BjondService.findAllByGroupId(entityManager, entity.getGroupID());
        // assertThat(list).isNotNull().hasSize(1);
        // assertThat(list.get(0).getId()).isEqualTo(entity.getId());

        // transaction.begin();
        // // Load it within the transaction to ensure it is attached. I just want to remove it.
        // final List<BjondService> list2 = BjondService.findAllByGroupId(entityManager, entity.getGroupID());
        // entityManager.remove(list2.get(0));
        // transaction.commit();

        // final List<BjondService> list3 = BjondService.findAllByGroupId(entityManager, entity.getGroupID());
        // assertThat(list3).isNotNull().hasSize(0);
        
    }
    
    
}

