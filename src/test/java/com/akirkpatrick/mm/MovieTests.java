package com.akirkpatrick.mm;

import com.akirkpatrick.mm.generator.MovieGenerator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: alfie
 * Date: 09/08/13
 * Time: 10:56
 * To change this template use File | Settings | File Templates.
 */
public class MovieTests {

    @Test
    public void testbasic() {
        List<String> files=new ArrayList<String>();
        files.add("tmp/107bed06-2595-472a-8edf-f871fdd8e912.jpg");
        files.add("tmp/x612cfb7a-0a72-4363-a988-0661e2fd1f13.jpg");
        new MovieGenerator().create(files, 10, null);
    }
}
