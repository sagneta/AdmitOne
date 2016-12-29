package com.admitone.security.cdi;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;

import org.apache.deltaspike.security.api.authorization.SecurityBindingType;

/**
 * <p>
 * This annotation can be used on methods and types to define a security constraint where only the specified roles are allowed to invoke
 * them.
 * </p>
 *
 *
 * @see AuthorizationManager
 */
@Retention(RUNTIME)
@Target({ METHOD, TYPE })
@SecurityBindingType
@Documented
public @interface RolesAllowed {

    @Nonbinding
    String[] value() default {};

}
