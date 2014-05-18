package com.akirkpatrick.mm;

import com.akirkpatrick.mm.exception.InvalidUsernameOrPasswordException;
import com.akirkpatrick.mm.exception.MovieMakerException;
import com.akirkpatrick.mm.exception.UserExistsException;
import com.akirkpatrick.mm.generator.MovieGenerator;
import com.akirkpatrick.mm.model.Account;
import com.akirkpatrick.mm.model.Project;
import com.akirkpatrick.mm.model.ProjectInfo;
import com.akirkpatrick.mm.rest.User;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @DELETE
    @Path("/delete/{projectId}/{frameNum}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_XML})
    public String deleteFrame(@User Account account, @PathParam("projectId") Long projectId, @PathParam("frameNum") Integer frameNum) {
        service.deleteFrame(account, projectId, frameNum);
        return "true";
    }

    @DELETE
    @Path("/project/{projectId}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_XML})
    public String deleteProject(@User Account account, @PathParam("projectId") Long projectId) {
        service.deleteProject(account, projectId);
        return "true";
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
    public StreamingOutput getMovie(@User Account account, @PathParam("projectId") Long projectId,
                                    @Context HttpServletResponse response) {

        final Project project=service.getProject(account, projectId);
        response.addHeader("Content-Disposition", "attachment; filename=\""+ project.getName() +"\"");
        return new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                new MovieGenerator().create(FileHelper.toPaths(project.getFrames()), project.getFps(), output);
            }
        };
    }

    @POST
    @Path("/login")
    @Consumes("application/x-www-form-urlencoded")
    @Produces({"text/json", "text/xml"})
    public Account login(@FormParam("username") String username, @FormParam("password") String password, @Context HttpServletRequest request) {
        Account account;
        try {
            account=service.authenticate(username, password);
        } catch (NoResultException e) {
            throw new InvalidUsernameOrPasswordException();
        }

        request.getSession().setAttribute("mm.account", account.getId());
        return account;
    }

    @POST
    @Path("/register")
    @Consumes("application/x-www-form-urlencoded")
    @Produces({"text/json", "text/xml"})
    public Account register(@FormParam("username") String username, @FormParam("password") String password, @Context HttpServletRequest request) {
        if ( service.findAccount(username) != null ) {
            throw new UserExistsException();
        }
        Account account=service.createAccount(username, password);

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

    @POST
    @Path("/project")
    @Produces({"text/json", "text/xml"})
    @Consumes({"application/json", "text/json", "text/xml"})
    public Project projectCreateOrUpdate(@User Account account, ProjectInfo projectInfo) {
        if ( projectInfo.getId() != null ) {
            return service.updateProjectInfo(projectInfo);
        }
        return service.addProject(account, projectInfo.getName());
    }

    @GET
    @Path("/project/list")
    @Produces({"text/json", "text/xml"})
    public List<Project> project(@User Account account) {
        return account.getProjects();
    }

    @GET
    @Path("/project/list/{accountId}")
    @Produces({"text/json", "text/xml"})
    public List<Project> projectsForAccount(@User Account account, @PathParam("accountId") Long accountId) throws IllegalAccessException {
        if ( !account.getUsername().equals("admin") ) {
            throw new IllegalAccessException("Only admin can list user projects!");
        }

        Account other=service.findAccount(accountId);
        return other.getProjects();
    }

    @GET
    @Path("/account/list")
    @Produces({"text/json", "text/xml"})
    public List<Account> listAccounts(@User Account account) throws IllegalAccessException {
        if ( !account.getUsername().equals("admin") ) {
            throw new IllegalAccessException("Only admin can list users!");
        }
        return service.listAccounts();
    }


    @GET
    @Path("/account")
    @Produces({"text/json", "text/xml"})
    public Account account(@User(required = false) Account account) {
        return account;
    }

}
