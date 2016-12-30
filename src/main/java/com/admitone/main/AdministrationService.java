package com.admitone.main;


import static com.admitone.security.interfaces.IIdentityManagementService.SYSTEM_ADMINISTRATION_ROLE;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.admitone.security.cdi.RolesAllowed;

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
        return Response.ok("Ok").build();    
    }

    @GET
    @Path("/search")
    @RolesAllowed({SYSTEM_ADMINISTRATION_ROLE})
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(@NotNull(message="username must not be null.") @QueryParam("username") String username,
                           @NotNull(message="startShowID must not be null") @QueryParam("startShowID") Integer startShowID,
                           @NotNull(message="endShowID must not be null") @QueryParam("endShowID") Integer endShowID
                           ) throws Exception {

        log.info("Search: username({}) startShowID({}) endShowID({})", username, startShowID, endShowID);


        log.info("Completed");        
        return Response.ok("Ok").build();    
    }

    
    
}
