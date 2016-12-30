package com.admitone.test;



import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.transaction.UserTransaction;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.picketlink.Identity;

import com.admitone.main.AdministrationService;
import com.admitone.main.AuthenticationService;
import com.admitone.main.UserService;
import com.admitone.persistence.entities.Order;
import com.admitone.persistence.entities.Order.ORDER_TYPE;
import com.admitone.security.interfaces.IIdentityManagementService;
import com.admitone.utils.MiscUtils;

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
    
    @PersistenceContext(unitName = IIdentityManagementService.PERSISTENCE_UNIT, type = PersistenceContextType.TRANSACTION)
    @Getter private EntityManager entityManager;

    @Resource 
    private UserTransaction transaction;
    
    @EJB
    private AdministrationService adminService;
    
    @EJB
    private AuthenticationService authenticationService;
    
    @EJB
    private UserService userService;

    @Inject
    private Identity identity;
    
    /////////////////////////////////////////////////////////////////////////
    //                      Unit Tests below this point                    //
    /////////////////////////////////////////////////////////////////////////

    @Deployment
    public static WebArchive createDeployment() {
        val a = ShrinkWrap.create(ZipImporter.class, "testadmitone.war").importFrom(new File(ROOT_WAR_DEPLOYMENT_LOCATION)).as(WebArchive.class);
        log.info("Admit One War: " + a.toString(true));
        return a;
    }

    @Before
    public void setUp() throws Exception {
        Response response = authenticationService.login("admin", "admin");
        Assert.assertEquals("Should be OK if method invocation was successful. ", Status.OK.getStatusCode() , response.getStatus());
    }

    @After
    public void tearDown() throws Exception {
        authenticationService.logout();
    }

    
    @Test
    public void sanityCheck() throws Exception {
        Assert.assertTrue("I ran ok!", true);
        assertThat(entityManager).isNotNull();
    }


    
    @Test
    public void basic_CRUD() throws Exception {
        final Order order = Order.builder()
            .id(MiscUtils.generateUUID())
            .userID(identity.getAccount().getId())
            .tickets(101)
            .toShowID(1)
            .orderType(ORDER_TYPE.Purchase)
            .canceled(false)
            .build();

        transaction.begin();
        getEntityManager().persist(order);
        transaction.commit();

        final Order order1 = getEntityManager().find(Order.class, order.getId());
        assertThat(order1).isNotNull();
        assertThat(order1.getId()).isNotNull().isEqualTo(order.getId());
        assertThat(order1.getTickets()).isNotNull().isEqualTo(101);

        order1.setTickets(90);
        transaction.begin();
        getEntityManager().merge(order1);
        transaction.commit();

        final Order order2 = getEntityManager().find(Order.class, order.getId());
        assertThat(order2).isNotNull();
        assertThat(order2.getId()).isNotNull().isEqualTo(order.getId());
        assertThat(order2.getTickets()).isNotNull().isEqualTo(90);

        transaction.begin();
        final Order deleteOrder = (getEntityManager().contains(order2)) ? order2: getEntityManager().merge(order2);
        getEntityManager().remove(deleteOrder);
        transaction.commit();

        final Order order3 = getEntityManager().find(Order.class, order.getId());
        assertThat(order3).isNull();
    }
    
    @Test
    public void basicTestOfQueries() throws Exception {

        final String userID = identity.getAccount().getId();
        
        final Order order = Order.builder()
            .id(MiscUtils.generateUUID())
            .userID(userID)
            .tickets(101)
            .toShowID(1)
            .orderType(ORDER_TYPE.Purchase)
            .canceled(false)
            .build();

        final Order order2 = Order.builder()
            .id(MiscUtils.generateUUID())
            .userID(userID)
            .tickets(50)
            .toShowID(2)
            .fromShowID(3)
            .orderType(ORDER_TYPE.Exchange)
            .canceled(false)
            .build();
        
        transaction.begin();
        getEntityManager().persist(order);
        getEntityManager().persist(order2);
        transaction.commit();

        try {

            List<Order> orders = Order.findAll(getEntityManager(), 1000, 0);

            assertThat(orders)
                .isNotNull()
                .hasSize(2)
                .contains(order, order2)
                .filteredOn(o -> o.getToShowID() == 1)
                .containsOnly(order);


            
            long count = Order.findCountOfAllAttendingPerUser(getEntityManager(), userID);
            assertThat(count).isEqualTo(2);

            
            orders = Order.findAllAttendingPerUser(getEntityManager(), userID, 1000, 0);
            assertThat(orders)
                .isNotNull()
                .hasSize(2)
                .contains(order, order2)
                .filteredOn(o -> o.getToShowID() == 1)
                .containsOnly(order);


            long ticketCount = Order.findAllTicketsOwnedPerUser(getEntityManager(),  userID);
            assertThat(ticketCount).isEqualTo(151);


            orders = Order.findOrderHistoryPerUser(getEntityManager(), userID, 1000, 0);

            assertThat(orders)
                .isNotNull()
                .hasSize(2)
                .contains(order, order2)
                .filteredOn(o -> o.getToShowID() == 1)
                .containsOnly(order);

            orders = Order.findOrderRangedPerUser(getEntityManager(), userID, 0, 2, 1000, 0); 

            assertThat(orders)
                .isNotNull()
                .hasSize(2)
                .contains(order, order2)
                .filteredOn(o -> o.getToShowID() == 1)
                .containsOnly(order);
            
            orders = Order.findSpecificPurchaseOrExchange(getEntityManager(),  userID, 1, 101, 1000, 0);
            
            assertThat(orders)
                .isNotNull()
                .hasSize(1)
                .filteredOn(o -> o.getToShowID() == 1 && o.getTickets() == 101)
                .containsOnly(order);

            orders = Order.findAllTicketsOwnedPerUserAndShow(getEntityManager(), userID, 1, 100, 0);

            assertThat(orders)
                .isNotNull()
                .hasSize(1)
                .filteredOn(o -> o.getToShowID() == 1)
                .containsOnly(order);

            count = Order.findCountOfAllTicketsOwnedPerUserAndShow(getEntityManager(),  userID, 1);
            assertThat(count).isEqualTo(1);
            
            count = Order.findCountOfAllTicketsOwnedPerUserAndShow(getEntityManager(),  userID, 2);
            assertThat(count).isEqualTo(1);
            
            count = Order.findCountOfAllTicketsOwnedPerUserAndShow(getEntityManager(),  userID, 3);
            assertThat(count).isEqualTo(0);
            
        } finally {
            transaction.begin();
            getEntityManager().remove(washEntity(order));
            getEntityManager().remove(washEntity(order2));
            transaction.commit();
        }
    }




    @Test
    public void testPurchaseAndHistory() throws Exception {
        Response response = userService.purchase(79, 1);
        Assert.assertEquals("Should be OK if method invocation was successful.", Status.OK.getStatusCode(), response.getStatus());
        
        final Order order = (Order)response.getEntity();
        assertThat(order).isNotNull();

        try {
            final Order orderRead = getEntityManager().find(Order.class, order.getId());
            orderRead.setUsername("admin");
            
            assertThat(orderRead).isNotNull().isEqualTo(order);
            assertThat(orderRead.getTickets()).isEqualTo(79);
            assertThat(orderRead.getToShowID()).isEqualTo(1);

            response = userService.history(1000,0);
            Assert.assertEquals("Should be OK if method invocation was successful.", Status.OK.getStatusCode(), response.getStatus());

            @SuppressWarnings("unchecked")
            final List<Order> orders = (List<Order>)response.getEntity();
            assertThat(orders)
                .isNotNull()
                .hasSize(1)
                .containsOnly(orderRead);
            

        } finally {
            transaction.begin();
            getEntityManager().remove(washEntity(order));
            transaction.commit();
        }
    }

    // Not going to test all the guards.
    @Test(expected=javax.ejb.EJBException.class)
    public void basicNegativeGuardTesting_1() throws Exception {
        userService.purchase(-1,1);
    }

    @Test(expected=javax.ejb.EJBException.class)
    public void basicNegativeGuardTesting_2() throws Exception {
        userService.purchase(101,-11);
    }


    @Test
    public void testCancel_1() throws Exception {
        Response response = userService.purchase(79, 1);
        Assert.assertEquals("Should be OK if method invocation was successful.", Status.OK.getStatusCode(), response.getStatus());
        
        final Order order = (Order)response.getEntity();
        assertThat(order).isNotNull();

        
        Order orderRead = null;
        try {
            response = userService.cancel(79, 1);
            Assert.assertEquals("Should be OK if method invocation was successful.", Status.OK.getStatusCode(), response.getStatus());

            orderRead = getEntityManager().find(Order.class, order.getId());
            
            assertThat(orderRead).isNotNull();
            assertThat(orderRead.getTickets()).isEqualTo(79);
            assertThat(orderRead.getCanceled()).isTrue();

            

        } finally {
            if(orderRead != null) {
                transaction.begin();
                getEntityManager().remove(washEntity(orderRead));
                transaction.commit();
            }
        }
    }


    @Test
    public void testCancel_2() throws Exception {
        Response response = userService.purchase(79, 1);
        Assert.assertEquals("Should be OK if method invocation was successful.", Status.OK.getStatusCode(), response.getStatus());
        
        final Order order = (Order)response.getEntity();
        assertThat(order).isNotNull();

        
        Order orderRead = null;
        try {
            response = userService.cancel(20, 1);
            Assert.assertEquals("Should be OK if method invocation was successful.", Status.OK.getStatusCode(), response.getStatus());

            orderRead = getEntityManager().find(Order.class, order.getId());
            
            assertThat(orderRead).isNotNull();
            assertThat(orderRead.getTickets()).isEqualTo(59);
            assertThat(orderRead.getCanceled()).isFalse();

            

        } finally {
            if(orderRead != null) {
                transaction.begin();
                getEntityManager().remove(washEntity(orderRead));
                transaction.commit();
            }
        }
    }
    
    @Test
    public void testCancel_3() throws Exception {
        Response response = userService.purchase(10, 1);
        Assert.assertEquals("Should be OK if method invocation was successful.", Status.OK.getStatusCode(), response.getStatus());
        
        final Order order1 = (Order)response.getEntity();
        assertThat(order1).isNotNull();

        response = userService.purchase(10, 1);
        Assert.assertEquals("Should be OK if method invocation was successful.", Status.OK.getStatusCode(), response.getStatus());
        
        final Order order2 = (Order)response.getEntity();
        assertThat(order2).isNotNull();

        response = userService.purchase(10, 1);
        Assert.assertEquals("Should be OK if method invocation was successful.", Status.OK.getStatusCode(), response.getStatus());
        
        final Order order3 = (Order)response.getEntity();
        assertThat(order3).isNotNull();
        
        List<Order> orders = null;
        try {
            response = userService.cancel(22, 1);
            Assert.assertEquals("Should be OK if method invocation was successful.", Status.OK.getStatusCode(), response.getStatus());

            orders = Order.findAll(getEntityManager(), 1000, 0);

            assertThat(orders)
                .isNotNull()
                .hasSize(3)
                .filteredOn(o -> o.getCanceled())
                .hasSize(2);

            assertThat(orders)
                .isNotNull()
                .hasSize(3)
                .filteredOn(o -> !o.getCanceled() && o.getTickets() == 8)
                .hasSize(1);
                

        } finally {
            if(orders != null) {
                transaction.begin();
                orders.forEach(o -> getEntityManager().remove(washEntity(o)));
                transaction.commit();
            }
        }
    }
    
    /////////////////////////////////////////////////////////////////////////
    //                            private methods                          //
    /////////////////////////////////////////////////////////////////////////

    private Order washEntity(final Order order) {
        return (getEntityManager().contains(order)) ? order: getEntityManager().merge(order);
    }
}

