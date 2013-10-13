package com.akirkpatrick.mm.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Calendar;

@MappedSuperclass
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectInfo {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    protected static final int DEFAULT_FPS = 7;
    protected Calendar lastModified = Calendar.getInstance();
    protected String name;
    protected Integer fps = DEFAULT_FPS;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Calendar getLastModified() {
        return lastModified;
    }

    public void setLastModified(Calendar lastModified) {
        this.lastModified = lastModified;
    }

    public Integer getFps() {
        return fps == null ? DEFAULT_FPS : fps;
    }

    public void setFps(Integer fps) {
        this.fps = fps;
    }
}
