package com.akirkpatrick.mm.model;

import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NamedQuery(name="Account.findByUsername", query="select a from Account a where a.username=:username")
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private String username;
    private String md5Password;

    @OneToMany(mappedBy="account")
    private List<Project> projects=new ArrayList<Project>();

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setMd5Password(String md5Password) {
        this.md5Password = md5Password;
    }

    public String getMd5Password() {
        return md5Password;
    }

    public Project addProject(Project project) {
        projects.add(project);
        return project;
    }

    @JsonIgnore
    public List<Project> getProjects() {
        return projects;
    }

    public Long getId() {
        return id;
    }
}
