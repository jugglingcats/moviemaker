
package com.akirkpatrick.classifieds.resources;

import com.akirkpatrick.classifieds.service.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/** Example resource class hosted at the URI path "/myresource"
 */
@Component
@Path("/myresource")
public class MyResource {
    @Autowired
    private MyService service;

    /** Method processing HTTP GET requests, producing "text/plain" MIME media
     * type.
     * @return String that will be send back as a response of type "text/plain".
     */
    @GET
    @Path("/test")
    @Produces("text/plain")
    public String getIt() {
        return service.getMessage();
    }
}
