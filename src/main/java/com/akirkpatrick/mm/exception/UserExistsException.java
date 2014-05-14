package com.akirkpatrick.mm.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class UserExistsException extends WebApplicationException {
    public UserExistsException() {
        super(Response.status(Response.Status.BAD_REQUEST)
                .entity("An account with this username already exists")
                .type(MediaType.TEXT_PLAIN)
                .build());
    }
}
