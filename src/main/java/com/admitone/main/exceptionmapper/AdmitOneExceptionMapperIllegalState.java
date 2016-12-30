package com.admitone.main.exceptionmapper;

// Java
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.admitone.utils.ErrorCodes;
import com.admitone.utils.NetworkUtils;


/** <p> 
    Maps any uncaught system exception from RESTEASY to a human
    readable small error message. 
    JSR311 https://jsr311.java.net/nonav/javadoc/javax/ws/rs/ext/ExceptionMapper.html
    http://docs.jboss.org/resteasy/docs/1.1.GA/userguide/html/ExceptionHandling.html
    </p>

 *
 * <a href="mailto:Stephen.Agneta@bjondinc.com">Steve 'Crash' Agneta</a>
 *
 */

@Provider
public class AdmitOneExceptionMapperIllegalState implements ExceptionMapper<java.lang.IllegalStateException> {



    public Response toResponse(java.lang.IllegalStateException exception) {
        return NetworkUtils.errorResponse(Response.Status.INTERNAL_SERVER_ERROR, ExceptionUtils.getMessage(exception), ErrorCodes.ADMITONE_HTTP_ERROR_CODES.UNEXPECTED_ERROR);
    }
}


