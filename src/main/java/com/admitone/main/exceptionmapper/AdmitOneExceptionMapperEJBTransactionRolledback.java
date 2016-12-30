package com.admitone.main.exceptionmapper;

// Java
import java.util.List;

// Java
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.admitone.utils.ErrorCodes;
import com.admitone.utils.NetworkUtils;

import lombok.extern.slf4j.Slf4j;


@Provider
@Slf4j
public class AdmitOneExceptionMapperEJBTransactionRolledback implements ExceptionMapper<javax.ejb.EJBTransactionRolledbackException> {
    public Response toResponse(javax.ejb.EJBTransactionRolledbackException e) {
        log.error(ExceptionUtils.getMessage(e), e);

        final List<Throwable> list = ExceptionUtils.getThrowableList(e);
        final boolean bConstraintViolation = list.stream().anyMatch(t -> t instanceof org.hibernate.exception.ConstraintViolationException);

        final ErrorCodes.ADMITONE_HTTP_ERROR_CODES code = (bConstraintViolation) ? ErrorCodes.ADMITONE_HTTP_ERROR_CODES.CONSTRAINT_VIOLATION : ErrorCodes.ADMITONE_HTTP_ERROR_CODES.UNEXPECTED_ERROR;

        // If there was a constraint violation on the server
        // find the native exception string.
        String nativeMessage = "";
        if(bConstraintViolation) {
            // The PSQLException actually thrown by the system is in a JDBC driver which will almost certainly
            // differ from a maven retrieved JAR file so the instanceof will fail. It is safer to match strings. 
            final Throwable item = list.stream().filter(t -> t.toString().startsWith("org.postgresql.util.PSQLException", 0)).findFirst().get();
            if(item != null) {
                nativeMessage = item.getMessage();
            } 
        }
        
        return NetworkUtils.errorResponse(Response.Status.INTERNAL_SERVER_ERROR, ExceptionUtils.getMessage(e), nativeMessage, code);

    }
}
