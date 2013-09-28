package com.akirkpatrick.mm;

import com.akirkpatrick.mm.generator.MovieGenerator;
import com.akirkpatrick.mm.model.Account;
import com.akirkpatrick.mm.model.Project;
import com.akirkpatrick.mm.rest.User;
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
import java.util.List;

@Component
@Path("/mm")
public class MovieMakerResource {
    @Autowired
    private MovieMakerService service;

    @POST
    @Path("/post/{projectId}")
    @Consumes(MediaType.TEXT_PLAIN)
    public String addImageToProject(@User Account account, @PathParam("projectId") Long projectId, String base64data) {
        try {
            return service.store(base64data, account, projectId);
        } catch (Exception e) {
            throw new MovieMakerException(e);
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
    @Path("/download/{projectId}")
    @Produces("video/quicktime")
    public StreamingOutput getMovie(@User Account account, @PathParam("projectId") Long projectId) {
        final Project project=service.getProject(account, projectId);
        return new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                new MovieGenerator().create(FileHelper.toPaths(project.getFrames()), output);
            }
        };
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

        request.getSession().setAttribute("mm.account", account.getId());
        return account;
    }

    @POST
    @Path("/logout")
    public String logout(@Context HttpServletRequest request) {
        request.getSession().invalidate();
        return "true";
    }

    @GET
    @Path("/project/{projectId}")
    @Produces({"text/json", "text/xml"})
    public Project project(@User Account account, @PathParam("projectId") Long projectId) {
        return service.findProject(projectId);
    }

    @GET
    @Path("/project/list")
    @Produces({"text/json", "text/xml"})
    public List<Project> project(@User Account account) {
        return account.getProjects();
    }

    @POST
    @Path("/project")
    @Produces({"text/json", "text/xml"})
    @Consumes({"application/json", "text/json", "text/xml"})
    public Project projectCreateOrUpdate(@User Account account, Project project) {
        if ( project.getId() != null ) {
            throw new IllegalArgumentException("Update of project not allowed yet!");
        }
        return service.addProject(account, project.getName());
    }

    @GET
    @Path("/account")
    @Produces({"text/json", "text/xml"})
    public Account account(@User Account account) {
        return account;
    }

}
