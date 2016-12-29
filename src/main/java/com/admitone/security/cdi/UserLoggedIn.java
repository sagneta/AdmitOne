package com.admitone.security.cdi;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;
import javax.enterprise.util.Nonbinding;
import org.apache.deltaspike.security.api.authorization.SecurityBindingType;

/**
 * <p>
 * This annotation can be used on methods and types to define a security constraint where only authenticated users can invoke
 * them.
 * </p>
 *
 *
 * @see AuthorizationManager
 */
@Target({ TYPE, METHOD })
@Retention(RUNTIME)
@SecurityBindingType
@Documented
@Qualifier
public @interface UserLoggedIn {

    public static final String ALLOW_TEMPORARY_PASSWORDS = "AllowTemporaryPasswords"; // Means method is allowed for users with temporary passwords.


    @Nonbinding
    String value() default "";

}
