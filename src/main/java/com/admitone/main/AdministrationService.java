package com.admitone.main;


import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import lombok.extern.slf4j.Slf4j;



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

}
