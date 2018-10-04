package com.halycon.transactionserviceejbjaxrs.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class CustomExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception ex) {
        if (ex.getCause() instanceof InvalidFormatException)
            return Response.status(422).build();
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}
