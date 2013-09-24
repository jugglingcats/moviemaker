package com.akirkpatrick.mm.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Project {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String name;

    @ElementCollection(targetClass=String.class)
    private List<String> frames=new ArrayList<String>();

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addFrame(String uid) {
        frames.add(uid);
    }

    public List<String> getFrames() {
        return frames;
    }
}
