package com.akirkpatrick.mm;

import com.sun.jersey.core.util.Base64;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class MovieMakerService {
    public void store(String base64data, MovieMakerSession mms) {
        UUID uuid = UUID.randomUUID();
        byte[] bytes = Base64.decode(base64data);
        try {
            FileCopyUtils.copy(bytes, new File("tmp/"+uuid.toString()+".jpg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mms.addFrame(uuid);
    }
}
