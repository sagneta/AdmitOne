package com.admitone.main;

import static com.admitone.security.interfaces.IIdentityManagementService.USER_ROLE;

import javax.ejb.Stateful;
import javax.inject.Inject;
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

import com.admitone.security.cdi.RolesAllowed;
import com.admitone.security.cdi.UserLoggedIn;

import lombok.extern.slf4j.Slf4j;

/** <p> For User Role REST endpoints </p>

 *
 * <a href="mailto:Stephen.Agneta@bjondinc.com">Steve 'Crash' Agneta</a>
 *
 */


@Path("/user")
@Stateful
@Slf4j
public class UserService {
    private static final String DEFAULT_LIMIT  = "1000"; // arbitrary.
    private static final String DEFAULT_OFFSET = "0"; // Begining.


    @Inject
    private Identity identity;

    
    @POST
    @Path("/purchase")
    @UserLoggedIn
    @RolesAllowed({USER_ROLE})
    @Produces(MediaType.APPLICATION_JSON)
    public Response purchase(
                             @NotNull(message="tickets must not be null.") @FormParam("tickets") Integer tickets,
                             @NotNull(message="showid must not be null.")  @FormParam("showid") Integer showID) throws Exception {

        final User user = getCurrentUser(); 

        log.info("Purchase: username({}) tickets({}) showID({})", user.getLoginName(), tickets, showID);
        // Essentially you can add infinite amount of purchases
        // on any show ID (event).


        log.info("Completed");
        return Response.ok("Ok").build();    
    }

    @POST
    @Path("/exchange")
    @UserLoggedIn
    @RolesAllowed({USER_ROLE})
    @Produces(MediaType.APPLICATION_JSON)
    public Response exchange(
                             @NotNull(message="tickets must not be null.")    @FormParam("tickets") Integer tickets,
                             @NotNull(message="fromShowid must not be null.") @FormParam("fromShowid") Integer fromShowID,
                             @NotNull(message="toShowid must not be null.")   @FormParam("toShowid") Integer toShowID
                             ) throws Exception {

        final User user = getCurrentUser(); 
        
        log.info("Exchange: username({}) tickets({}) fromShowID({}) toShowID({})", user.getLoginName(), tickets, fromShowID, toShowID);
        
        // First check if the user has a sufficient number of tickets to exchange.
        // Second go through each purchase of that showID and change it to an exchange until all tickets are exchanged.
        // Third if the last purchase -> exchange record has left over tickets then INSERT a purchase for the remainder fromShowID tickets.

        log.info("Completed");        
        return Response.ok("Ok").build();    
    }


    @POST
    @Path("/cancel")
    @UserLoggedIn
    @RolesAllowed({USER_ROLE})
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancel(
                           @NotNull(message="tickets must not be null.")  @FormParam("tickets") Integer tickets,
                           @NotNull(message="showid must not be null.")   @FormParam("showid") Integer showID
                           ) throws Exception {

        final User user = getCurrentUser(); 
        
        log.info("Cancel: username({}) tickets({}) showID({})", user.getLoginName(), tickets, showID);
        
        // First check if the user has a sufficient number of tickets to cancel.
        // Second go through each purchase of that showID and set the purchase to canceled.
        // Third if the last canceled purchase record has left over tickets then INSERT a purchase for the remainder showID tickets.

        log.info("Completed");        
        return Response.ok("Ok").build();    
    }


    @GET
    @Path("/history")
    @RolesAllowed({USER_ROLE})
    @Produces(MediaType.APPLICATION_JSON)
    public Response history(
                            @QueryParam("limit")  @DefaultValue(DEFAULT_LIMIT) final int iLimit, 
                            @QueryParam("offset") @DefaultValue(DEFAULT_OFFSET) final int iOffset              
                           ) throws Exception {

        final User user = getCurrentUser(); 
        
        log.info("History: username({}) limit({}) offset({})", user.getLoginName(), iLimit, iOffset);


        log.info("Completed");        
        return Response.ok("Ok").build();    
    }


    /////////////////////////////////////////////////////////////////////////
    //                            Private Methods                          //
    /////////////////////////////////////////////////////////////////////////


    private User getCurrentUser() {
        return (User)identity.getAccount();
    }
}
