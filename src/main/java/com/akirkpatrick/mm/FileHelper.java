package com.akirkpatrick.mm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

public class FileHelper {

    public static File fromUUID(UUID uuid) {
        return new File("tmp/"+uuid.toString()+".jpg");
    }

    public static List<String> toStrings(List<UUID> frames) {
        List<String> retval=new ArrayList<String>();
        for ( UUID uuid : frames ) {
            retval.add(uuid.toString());
        }
        return retval;
    }
}
