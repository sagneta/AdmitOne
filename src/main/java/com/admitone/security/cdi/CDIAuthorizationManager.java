package com.admitone.security.cdi;

import static fj.data.Array.array;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.interceptor.InvocationContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.deltaspike.security.api.authorization.Secures;
import org.picketlink.Identity;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.Account;

import com.admitone.security.interfaces.IIdentityManagementService;

import lombok.extern.slf4j.Slf4j;


/**
 * <p>
 * Provides all annotated authorization capabilities for applications using PicketLink.
 * </p>
 *
 *
 */
@ApplicationScoped
@Slf4j
public class CDIAuthorizationManager {

    @EJB
    protected IIdentityManagementService identityService;
    
    /**
     * <p>
     * Authorization method for the {@link RolesAllowed} annotation.
     * </p>
     *
     * @param ctx
     * @param identity
     * @return
     */
    @Secures
    @RolesAllowed
    public boolean restrictRoles(final InvocationContext ctx, final Identity identity, final IdentityManager identityManager) {
        if(!identity.isLoggedIn()) {
            return false;
        }

        final boolean valid = array(getRestrictedRoles(ctx)).exists(restrictedRole -> checkSystemRole(identityManager, identity.getAccount(), restrictedRole));

        if(!valid){
            final String message = String.format("User does not have necessary Role(s) {%s}", StringUtils.join(getRestrictedRoles(ctx),"|"));
            log.error(message);
        }

        return valid;
    }

    /**
     * <p>Checks if the resources protected with the {@link UserLoggedIn} annotation are visible only for authenticated users.</p>
     *
     * @param ctx
     * @param identity
     * @return
     */
    @Secures
    @UserLoggedIn
    public boolean isUserLoggedIn(final InvocationContext ctx, final Identity identity) {
        return identity.isLoggedIn();
    }
    


    /////////////////////////////////////////////////////////////////////////
    //                            Private Methods                          //
    /////////////////////////////////////////////////////////////////////////
    
    /**
     * <p>
     * Returns the restricted roles defined by the use of the {@link RolesAllowed} annotation. If the annotation is not
     * present a empty array is returned.
     * </p>
     *
     * @param ctx
     * @return
     */
    private String[] getRestrictedRoles(InvocationContext ctx) {
        RolesAllowed restrictedRolesAnnotation = AnnotationUtil.getDeclaredAnnotation(RolesAllowed.class, ctx);

        if (restrictedRolesAnnotation != null) {
            return restrictedRolesAnnotation.value();
        }

        return new String[] {};
    }


    /**
	 *  Returns true if the restrictedRoleName is associated for user. 
     *  
	 * @param identityManager
	 * @param user
	 * @param restrictedRoleName
	 * @return true if the role exist for this user and false otherwise.
	 */
    private boolean checkSystemRole(final IdentityManager identityManager, final Account user, final String restrictedRoleName) {
        return identityService.hasRole(identityManager, user, restrictedRoleName);
    }
    
}
