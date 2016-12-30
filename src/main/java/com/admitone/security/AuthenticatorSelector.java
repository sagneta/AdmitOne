
package com.admitone.security;



import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.picketlink.annotations.PicketLink;
import org.picketlink.authentication.Authenticator;
import org.picketlink.authentication.internal.IdmAuthenticator;


/** 
    <p>
    If singleSignOn is set to TRUE then single sign on mode is assumed.
    That means only the userID is matched.
    </p>

 *
 * <a href="mailto:sagneta@gmail.com">Steve 'Cräsh' Agneta</a>
 *
 */

@RequestScoped
@Named
public class AuthenticatorSelector {
    @Inject private Instance<IdmAuthenticator> idmAuthenticator;

    @Produces
    @PicketLink
    public Authenticator selectAuthenticator() {
        return idmAuthenticator.get();
    }
}
