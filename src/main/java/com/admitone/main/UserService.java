package com.admitone.main;

import static com.admitone.security.interfaces.IIdentityManagementService.USER_ROLE;
import static com.admitone.utils.MiscUtils.DEFAULT_LIMIT;
import static com.admitone.utils.MiscUtils.DEFAULT_OFFSET;
import static com.admitone.utils.MiscUtils.iDEFAULT_OFFSET;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.picketlink.Identity;
import org.picketlink.idm.model.basic.User;

import com.admitone.persistence.entities.Order;
import com.admitone.persistence.entities.Order.ORDER_TYPE;
import com.admitone.security.cdi.RolesAllowed;
import com.admitone.security.cdi.UserLoggedIn;
import com.admitone.security.interfaces.IIdentityManagementService;
import com.admitone.utils.MiscUtils;
import com.google.common.base.Preconditions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j; 


/** <p> For User Role REST endpoints </p>

 *
 * <a href="mailto:sagneta@gmail.com">Steve 'Crash' Agneta</a>
 *
 */


@Path("/user")
@Stateful
@Slf4j
public class UserService {

    @PersistenceContext(unitName = IIdentityManagementService.PERSISTENCE_UNIT, type=PersistenceContextType.TRANSACTION)
    @Getter private EntityManager entityManager;

    @Inject
    private Identity identity;

    
    @POST
    @Path("/purchase")
    @UserLoggedIn
    @RolesAllowed({USER_ROLE})
    @Produces(MediaType.APPLICATION_JSON)
    @TransactionAttribute(REQUIRES_NEW)
    public Response purchase(
                             @NotNull(message="tickets must not be null.") @FormParam("tickets") Integer tickets,
                             @NotNull(message="showid must not be null.")  @FormParam("showid") Integer showID) throws Exception {

        final User user = getCurrentUser(); 

        log.info("Purchase: username({}) tickets({}) showID({})", user.getLoginName(), tickets, showID);

        // Guards
        Preconditions.checkState(tickets >= 0, "tickets must be > 0");
        Preconditions.checkState(showID >= 0,  "showID must be > 0");

        // Essentially you can add infinite amount of purchases
        // on any show ID (event).
        final Order order = Order.builder()
            .id(MiscUtils.generateUUID())
            .userID(user.getId())
            .username(user.getLoginName())
            .tickets(tickets)
            .toShowID(showID)
            .orderType(ORDER_TYPE.Purchase)
            .canceled(false)
            .build();
                     
        getEntityManager().persist(order);

        log.info("Completed");
        return Response.ok(order).build();    
    }

    @POST
    @Path("/exchange")
    @UserLoggedIn
    @RolesAllowed({USER_ROLE})
    @Produces(MediaType.APPLICATION_JSON)
    @TransactionAttribute(REQUIRES_NEW)
    public Response exchange(
                             @NotNull(message="tickets must not be null.")    @FormParam("tickets") Integer tickets,
                             @NotNull(message="fromShowid must not be null.") @FormParam("fromShowid") Integer fromShowID,
                             @NotNull(message="toShowid must not be null.")   @FormParam("toShowid") Integer toShowID
                             ) throws Exception {

        final User user = getCurrentUser(); 
        
        log.info("Exchange: username({}) tickets({}) fromShowID({}) toShowID({})", user.getLoginName(), tickets, fromShowID, toShowID);

        // Guards
        Preconditions.checkState(tickets >= 0, "tickets must be > 0");
        Preconditions.checkState(fromShowID >= 0,  "fromShowID must be > 0");
        Preconditions.checkState(toShowID >= 0,  "toShowID must be > 0");
        
        // First check if the user has a sufficient number of tickets to exchange.
        final long ticketsOwned = Order.findCountOfAllTicketsOwnedPerUserAndShow(getEntityManager(), user.getId(), fromShowID);
        Preconditions.checkState(ticketsOwned >= tickets,  "User owns insufficient number of tickets: " + ticketsOwned);
        
        // Second go through each purchase/exchange of that showID and cancel it or reduce the outstanding tickets.
        final List<Order> ownedList  = Order.findAllTicketsOwnedPerUserAndShow(getEntityManager(), user.getId(), fromShowID, iDEFAULT_OFFSET, iDEFAULT_OFFSET);
        final List<Order> updateList = extractTickets(tickets, ownedList);

        // Third add the exchange.
        final Order order = Order.builder()
            .id(MiscUtils.generateUUID())
            .userID(user.getId())
            .tickets(tickets)
            .toShowID(toShowID)
            .fromShowID(fromShowID)
            .orderType(ORDER_TYPE.Exchange)
            .canceled(false)
            .build();

        updateList.forEach(o -> getEntityManager().merge(o));
        getEntityManager().persist(order);
        
        log.info("Completed");        
        return Response.ok(order).build();    
    }


    @POST
    @Path("/cancel")
    @UserLoggedIn
    @RolesAllowed({USER_ROLE})
    @Produces(MediaType.APPLICATION_JSON)
    @TransactionAttribute(REQUIRES_NEW)
    public Response cancel(
                           @NotNull(message="tickets must not be null.")  @FormParam("tickets") Integer tickets,
                           @NotNull(message="showid must not be null.")   @FormParam("showid") Integer showID
                           ) throws Exception {

        final User user = getCurrentUser(); 
        
        log.info("Cancel: username({}) tickets({}) showID({})", user.getLoginName(), tickets, showID);

        // Guards
        Preconditions.checkState(tickets >= 0, "tickets must be > 0");
        Preconditions.checkState(showID >= 0,  "showID must be > 0");

        
        // First check if the user has a sufficient number of tickets to cancel.
        final long ticketsOwned = Order.findCountOfAllTicketsOwnedPerUserAndShow(getEntityManager(), user.getId(), showID);
        Preconditions.checkState(ticketsOwned >= tickets,  "User owns insufficient number of tickets: " + ticketsOwned);


        // Second go through each purchase of that showID and set the purchase to canceled.
        final List<Order> ownedList = Order.findAllTicketsOwnedPerUserAndShow(getEntityManager(), user.getId(), showID, iDEFAULT_OFFSET, iDEFAULT_OFFSET);
        final List<Order> updateList = extractTickets(tickets, ownedList);
        

        updateList.forEach(o -> getEntityManager().merge(o));
        
        log.info("Completed");        
        return Response.ok("Ok").build();    
    }


    @GET
    @Path("/history")
    @RolesAllowed({USER_ROLE})
    @Produces(MediaType.APPLICATION_JSON)
    public Response history(
                            @QueryParam("limit")  @DefaultValue(DEFAULT_LIMIT)  final int iLimit, 
                            @QueryParam("offset") @DefaultValue(DEFAULT_OFFSET) final int iOffset              
                           ) throws Exception {

        final User user = getCurrentUser(); 
        
        log.info("History: username({}) limit({}) offset({})", user.getLoginName(), iLimit, iOffset);

        final List<Order> list = Order.findOrderHistoryPerUser(getEntityManager(), user.getId(), iLimit, iOffset);

        list.forEach(o -> o.setUsername(user.getLoginName()));
        
        log.info("Completed");        
        return Response.ok(list).build();    
    }


    /////////////////////////////////////////////////////////////////////////
    //                            Private Methods                          //
    /////////////////////////////////////////////////////////////////////////


    private User getCurrentUser() {
        return (User)identity.getAccount();
    }

	/**
	 * @param tickets
	 * @param ownedList
	 * @param updateList
	 */
	private List<Order> extractTickets(final Integer tickets, final List<Order> ownedList) {
        final List<Order> updateList = new ArrayList<>();
            
		int ticketCount = tickets;
        for(final Order order : ownedList) {
            if(order.getTickets() <= ticketCount) {
                order.setCanceled(true);
                updateList.add(order);
                ticketCount -= order.getTickets();
            } else { 
                order.setTickets(order.getTickets() - ticketCount);
                updateList.add(order);
                ticketCount = 0;
            }

            if(0 == ticketCount){break;}
        }

        return updateList;
	}

    
}
