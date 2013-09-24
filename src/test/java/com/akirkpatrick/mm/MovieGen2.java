package com.akirkpatrick.mm;

import com.akirkpatrick.mm.generator.MovieGenerator;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: alfie
 * Date: 22/09/13
 * Time: 22:40
 * To change this template use File | Settings | File Templates.
 */
public class MovieGen2 {
    @Test
    public void basic() throws IOException {
        List<String> files=new ArrayList<String>();
        FileOutputStream fios=new FileOutputStream("target/test.mov");
        files.add("tmp/32c3f43f-f835-4bfe-b21b-eed473cbd9db.jpg");
        files.add("tmp/598ec9ad-cf86-44ae-88e3-a06571fce1ce.jpg");
        files.add("tmp/67a0685c-e048-41a2-817c-695cf564fe5c.jpg");
        files.add("tmp/95c5b128-cf01-430b-8149-cc62e13dabde.jpg");
        files.add("tmp/ce899f07-1312-45b6-a220-efd620d26ff5.jpg");
        files.add("tmp/f370d482-69bb-4aaf-a016-e6283e40839d.jpg");
        files.add("tmp/4f48bb31-f71b-4044-9c48-494802afbcb0.jpg");
        files.add("tmp/88f950f5-9fa0-46ed-8303-5e343f39f107.jpg");
        files.add("tmp/b04200dd-68ed-4141-be02-41cf01ec366a.jpg");
        files.add("tmp/fa3166dc-8708-4d31-9e7e-690ba5ffe58d.jpg");
        files.add("tmp/1bf4d04f-9a8f-462d-9616-8da2709a7660.jpg");
        files.add("tmp/5bc2ba6c-1ae9-4455-8032-bfd3ee243e74.jpg");
        files.add("tmp/9b613cb9-5b27-48a4-866e-051e6d633673.jpg");
        new MovieGenerator().create(files, fios);
        fios.close();
    }
}
