package com.akirkpatrick.mm;

import com.sun.jersey.core.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Component
@Path("/upload")
public class MovieMakerResource {
    @Autowired
    private MovieMakerService service;

    @POST
    @Path("/single")
    @Consumes(MediaType.TEXT_PLAIN)
    public void acceptSingle(String base64data, @Context HttpServletRequest request) {
        MovieMakerSession mms=SessionHelper.getFrom(request);
        service.store(base64data, mms);
    }

    @GET
    @Path("/download")
    public StreamingOutput getMovie(@Context HttpServletRequest request) {
        final MovieMakerSession mms=SessionHelper.getFrom(request);
        return new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                new MovieGenerator().create(FileHelper.toStrings(mms.getFrames()), output);
            }
        };
    }
}
