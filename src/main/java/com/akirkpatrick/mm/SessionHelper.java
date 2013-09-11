package com.akirkpatrick.mm;

import javax.servlet.http.HttpServletRequest;

public class SessionHelper {
    public static MovieMakerSession getFrom(HttpServletRequest request) {
        MovieMakerSession session= (MovieMakerSession) request.getSession().getAttribute("mm");
        if ( session == null ) {
            session=new MovieMakerSession();
            request.getSession().setAttribute("mm", session);
        }
        return session;
    }
}
