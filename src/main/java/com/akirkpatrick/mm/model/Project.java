package com.akirkpatrick.mm.model;

import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

@Entity
@NamedQuery(name="Project.findExpired", query="select p from Project p where p.lastModified < :cutoff")
public class Project extends ProjectInfo {
    @ManyToOne
    private Account account;

    @ElementCollection(targetClass=String.class)
    @OrderColumn
    private List<String> frames=new ArrayList<String>();

    public void addFrame(String uid, Integer frameNum) {
        while ( frames.size() <= frameNum ) {
            frames.add(null);
        }
        frames.set(frameNum, uid);

        setLastModified(Calendar.getInstance());
    }

    public List<String> getFrames() {
        return Collections.unmodifiableList(frames);
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @JsonIgnore
    public Account getAccount() {
        return account;
    }

    public void removeFrame(Integer frameNum) {
        frames.remove(frameNum.intValue());
        setLastModified(Calendar.getInstance());
    }

}
