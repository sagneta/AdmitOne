package com.admitone.main;


import static com.admitone.security.interfaces.IIdentityManagementService.SYSTEM_ADMINISTRATION_ROLE;
import static com.admitone.utils.MiscUtils.iDEFAULT_LIMIT;
import static com.admitone.utils.MiscUtils.iDEFAULT_OFFSET;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.admitone.persistence.entities.Order;
import com.admitone.security.cdi.RolesAllowed;
import com.admitone.security.interfaces.IIdentityManagementService;
import com.admitone.utils.NetworkUtils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 *  For Administration Role REST interface.
 * 
 * @author 
 */
@Path("/administration")
@Stateful
@Slf4j
public class AdministrationService {
    @PersistenceContext(unitName = IIdentityManagementService.PERSISTENCE_UNIT, type=PersistenceContextType.TRANSACTION)
    @Getter private EntityManager entityManager;

    @EJB
    private IIdentityManagementService identityService;

    // @Inject
    // private IdentityManager identityManager;
    
    @PostConstruct
    protected void startService()  {
        log.info("AdministrationService service started successfully.");
    }
    
    
    /**
	 * Sanity check for deployment check. This application is only a demo.
     * Example usage: http://localhost:8080/admitone/services/administration/ping
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
    @GET
    @Path("/ping")
    @Produces(MediaType.APPLICATION_JSON)
    public Response ping() throws Exception {
        return Response.ok(NetworkUtils.generateSuccessMap("Ping was successful.")).build();    
    }

    @POST
    @Path("/search/form")
    @RolesAllowed({SYSTEM_ADMINISTRATION_ROLE})
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchForm(
                               @NotNull(message="startShowID must not be null") @FormParam("startshowid") final Integer startShowID,
                               @NotNull(message="endShowID must not be null") @FormParam("endshowid") final Integer endShowID
                               ) throws Exception {
        return search(startShowID,  endShowID);
    }
    
    @GET
    @Path("/search")
    @RolesAllowed({SYSTEM_ADMINISTRATION_ROLE})
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(
                           @NotNull(message="startShowID must not be null") @QueryParam("startshowid") final Integer startShowID,
                           @NotNull(message="endShowID must not be null") @QueryParam("endshowid") final Integer endShowID
                           ) throws Exception {

        log.info("Search: startShowID({}) endShowID({})", startShowID, endShowID);
        
        final List<Order> list = Order.findOrderRanged(getEntityManager(),
                                                       Integer.min(startShowID, endShowID),
                                                       Integer.max(startShowID, endShowID),
                                                       iDEFAULT_LIMIT, iDEFAULT_OFFSET);
                                                              

        list.forEach(o -> o.setUsername("fill this in"));
        
        log.info("Completed");        
        return Response.ok(list).build();    
    }

    
    
}
