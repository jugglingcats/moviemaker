package com.akirkpatrick.mm;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class MovieMakerException extends WebApplicationException {
    public MovieMakerException(Exception e) {
        super(e, Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(e.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build());
    }
}
