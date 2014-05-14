package com.akirkpatrick.mm.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class InvalidUsernameOrPasswordException extends WebApplicationException {
    public InvalidUsernameOrPasswordException() {
        super(Response.status(Response.Status.BAD_REQUEST)
                .entity("Invalid username or password")
                .type(MediaType.TEXT_PLAIN)
                .build());
    }
}
