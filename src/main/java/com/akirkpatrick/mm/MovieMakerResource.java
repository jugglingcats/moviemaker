package com.akirkpatrick.mm;

import com.akirkpatrick.mm.generator.MovieGenerator;
import com.akirkpatrick.mm.model.Account;
import com.akirkpatrick.mm.rest.User;
import com.akirkpatrick.mm.web.MovieMakerSession;
import com.akirkpatrick.mm.web.SessionHelper;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;

@Component
@Path("/mm")
public class MovieMakerResource {
    @Autowired
    private MovieMakerService service;

    @POST
    @Path("/post")
    @Consumes(MediaType.TEXT_PLAIN)
    public String acceptSingle(String base64data, @Context HttpServletRequest request) {
        try {
            MovieMakerSession mms= SessionHelper.getFrom(request);
            return service.store(base64data, mms);
        } catch (Exception e) {
            throw new MovieMakerException(e.getMessage());
        }
    }

    @GET
    @Path("/image/{id}")
    public StreamingOutput getImage(@PathParam("id") final String id) {
        return new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                FileUtils.copyFile(FileHelper.toFile(id), output);
            }
        };
    }

    @GET
    @Path("/download")
    @Produces("video/mp4")
    public StreamingOutput getMovie(@Context HttpServletRequest request) {
        System.out.println("Video generation requested");
        final MovieMakerSession mms=SessionHelper.getFrom(request);
        return new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                new MovieGenerator().create(FileHelper.toPaths(mms.getFrames()), output);
            }
        };
    }

    @GET
    @Path("/test")
    public String testMe(@User Account theAccount) {
        System.out.println(theAccount.getUsername());
        return "ok ok ok";
    }

    @POST
    @Path("/login")
    @Consumes("application/x-www-form-urlencoded")
    @Produces({"text/json", "text/xml"})
    public Account login(@FormParam("username") String username, @FormParam("password") String password,
                      @Context HttpServletRequest request) {
        Account account;
        try {
            account=service.authenticate(username, password);
        } catch (NoResultException e) {
            account=service.createAccount(username, password);
        }

        request.getSession().setAttribute("mm.account", account);
        return account;
    }
}
