package com.admitone.main.exceptionmapper;

// Java
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.admitone.utils.ErrorCodes;
import com.admitone.utils.NetworkUtils;

import lombok.extern.slf4j.Slf4j;



/** <p> 
    Maps any uncaught system exception from RESTEASY to a human
    readable small error message. 
    JSR311 https://jsr311.java.net/nonav/javadoc/javax/ws/rs/ext/ExceptionMapper.html
    http://docs.jboss.org/resteasy/docs/1.1.GA/userguide/html/ExceptionHandling.html
    </p>

 *
 * <a href="mailto:sagneta@gmail.com">Steve 'Crash' Agneta</a>
 *
 */

@Provider
@Slf4j
public class AdmitOneExceptionMapperExceptionsAll implements ExceptionMapper<java.lang.Exception> {
    public Response toResponse(java.lang.Exception e) {
        log.error(ExceptionUtils.getMessage(e), e);
        return NetworkUtils.errorResponse(Response.Status.INTERNAL_SERVER_ERROR, ExceptionUtils.getMessage(e), ErrorCodes.ADMITONE_HTTP_ERROR_CODES.UNEXPECTED_ERROR);

    }
}
