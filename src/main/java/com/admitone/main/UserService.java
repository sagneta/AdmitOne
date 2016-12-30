package com.admitone.main;

import static com.admitone.security.interfaces.IIdentityManagementService.USER_ROLE;
import static com.admitone.security.interfaces.IIdentityManagementService.SYSTEM_ADMINISTRATION_ROLE;

import javax.ejb.Stateful;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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

    @GET
    @Path("/ping")
    @UserLoggedIn
    @RolesAllowed({USER_ROLE, SYSTEM_ADMINISTRATION_ROLE})
    @Produces(MediaType.APPLICATION_JSON)
    public Response ping() throws Exception {
        return Response.ok("Ok").build();    
    }

}
