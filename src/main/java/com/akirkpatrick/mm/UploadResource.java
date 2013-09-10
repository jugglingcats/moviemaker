package com.akirkpatrick.mm;

import com.sun.jersey.core.util.Base64;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

@Path("/upload")
public class UploadResource {
    @POST
    @Path("/single")
    @Consumes(MediaType.TEXT_PLAIN)
    public void acceptSingle(String base64data) {
        byte[] bytes = Base64.decode(base64data);
        System.out.println(bytes.length);
    }
}
