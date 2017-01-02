package com.admitone.main;

import static javax.ejb.TransactionAttributeType.REQUIRED;

import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
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
import org.picketlink.Identity.AuthenticationResult;
import org.picketlink.credential.DefaultLoginCredentials;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.basic.Realm;

import com.admitone.security.RealmSelector;
import com.admitone.security.interfaces.IIdentityManagementService;
import com.admitone.utils.ErrorCodes;
import com.admitone.utils.NetworkUtils;

import lombok.extern.slf4j.Slf4j;

/** 
    <p>
    
    </p>

 *
 * <a href="mailto:sagneta@gmail.com">Steve 'Crash' Agneta</a>
 *
 */


@Path("/authenticate")
@Stateful(passivationCapable = false)
@TransactionAttribute(REQUIRED)
@Slf4j
public class AuthenticationService {
    @Inject
    private RealmSelector realmSelector;
    
    @Inject
    private DefaultLoginCredentials credentials;

    @Inject
    private Identity identity;

    @EJB
    private IIdentityManagementService identityService;
    

    @POST
    @Path("/loginform")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response loginForm(@NotNull(message="username must not be null.") @DefaultValue("defuser")    @FormParam("username") String username,
                              @NotNull(message="password must not be null.") @DefaultValue("defpass")   @FormParam("password") String password) throws Exception {

        log.info("LOGINFORM: username({}) password({})", username, password);
        return login("admitOneRealm", username, password);
    }

    
    @GET
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@NotNull(message="username must not be null.") @QueryParam("username") String username,
                          @NotNull(message="password must not be null.") @QueryParam("password") String password) throws Exception {

        return login("admitOneRealm", username, password);
    }


    /**
     *  <code>logout</code> method will logout the currently logged in user assuming one exists.
     *  Safe to call if you are not logged in.
     *
     * @return a <code>Response</code> value
     * @exception Exception if an error occurs
     */
    @POST
    @Path("/logout")
    public Response logout() throws Exception {

        if (this.identity.isLoggedIn()) {
            log.info("Logging out of picketlink.");
            identity.logout();
        }

        return (!this.identity.isLoggedIn()) ? Response.ok().entity(NetworkUtils.generateSuccessMap("Logout successful.")).type(MediaType.APPLICATION_JSON_TYPE).build() : Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    

    /**
     *  <code>login</code> method will login a user. Internal usage only.
     *  Password is cleartext. Tenant is a Realm.
     *  This is internal only thus it doesn't count failed password attempts.
     *
     * @param tenant a <code>String</code> value
     * @param username a <code>String</code> value
     * @param password a <code>String</code> value
     * @return a <code>Response</code> value
     * @exception Exception if an error occurs
     */
    public Response login(final String tenant, final String username, final String password) throws Exception {

        log.info("login invoked.");
        // Check if we already have a realmsetup.
        final IdentityManager identityManager = generateIdentityManagerFromRealm(tenant);
        if(null == identityManager) {
            return NetworkUtils.errorResponse(Response.Status.UNAUTHORIZED, "Could not find tenant: " + tenant, ErrorCodes.ADMITONE_HTTP_ERROR_CODES.UNKNOWN_TENANT);
        }

        if (!this.identity.isLoggedIn()) {
            this.credentials.setUserId(username);
            this.credentials.setPassword(password);
            AuthenticationResult result = this.identity.login();

            if (AuthenticationResult.FAILED.equals(result)){
                return NetworkUtils.errorResponse(Response.Status.UNAUTHORIZED, "Failed to authenticate TENANT " + tenant + " USER " + username, ErrorCodes.ADMITONE_HTTP_ERROR_CODES.AUTHENTICATION_FAILED);
            }

        }
        
        return Response.ok().entity(this.identity.getAccount()).type(MediaType.APPLICATION_JSON_TYPE).build();
    }



    /////////////////////////////////////////////////////////////////////////
    //                        Private below this point                     //
    /////////////////////////////////////////////////////////////////////////
    
	/**
	 * Given a realm will return a corresponding IdentityManager.
     * Attempts to reuse an already extent IdentityManager.
	 * 
	 * @param realm
	 * @return
	 */
	private IdentityManager generateIdentityManagerFromRealm(final String realm) {

        IdentityManager identityManager;
		if(realmSelector.getTenantRealm() != null && realmSelector.getTenantRealm().getName().equals(realm)) {
		    identityManager = identityService.forRealm(realmSelector.getTenantRealm());

		} else { //initialize one 
		    Realm tenantRealm = identityService.getRealm(realm);        
		    if(null == tenantRealm) {
		        return null;
		    }

		    realmSelector.setPartition(tenantRealm);        
		    realmSelector.setTenantRealm(tenantRealm);
		    identityManager = identityService.forRealm(tenantRealm);
		}
		return identityManager;
	}

    
}
