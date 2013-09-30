package com.akirkpatrick.mm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileHelper {

    public static File toFile(UUID uuid) {
        return toFile(uuid.toString());
    }

    public static File toFile(String id) {
        return new File(toPath(id));
    }

    private static String toPath(String id) {
        return "tmp/" + id + ".jpg";
    }

    public static List<String> toPaths(List<?> frames) {
        List<String> retval=new ArrayList<String>();
        for ( Object uuid : frames ) {
            retval.add(toPath(uuid.toString()));
        }
        return retval;
    }

    public static String toImageDownloadUrl(UUID uuid) {
        return "/rest/mm/image/"+uuid.toString();
    }

    public static void delete(String f) {
        new File(toPath(f)).delete();
    }
}
