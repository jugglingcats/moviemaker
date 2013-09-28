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

    @ManyToOne
    private Account account;

    @ElementCollection(targetClass=String.class)
    private List<String> frames=new ArrayList<String>();

    public Long getId() {
        return id;
    }

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

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }
}
