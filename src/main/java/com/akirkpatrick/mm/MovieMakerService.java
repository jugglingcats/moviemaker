package com.akirkpatrick.mm;

import com.akirkpatrick.mm.model.Account;
import com.akirkpatrick.mm.model.Project;
import com.sun.jersey.core.util.Base64;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class MovieMakerService {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public String store(String base64data, Account account, Long projectId) {
        UUID uuid = UUID.randomUUID();
        byte[] bytes = Base64.decode(base64data);
        try {
            File file = FileHelper.toFile(uuid);
            FileCopyUtils.copy(bytes, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Project project = getProject(account, projectId);
        project.addFrame(uuid.toString());

        return uuid.toString();
    }

    @Transactional(readOnly=true)
    public Project getProject(Account account, Long projectId) {
        Project project = em.find(Project.class, projectId);
        if ( project == null ) {
            throw new IllegalArgumentException("Project not found with id: "+projectId);
        }
        if ( !account.getId().equals(project.getAccount().getId()) ) {
            throw new IllegalArgumentException("Attempting to access a project not owned by account!");
        }
        return project;
    }

    @Transactional(readOnly=true)
    public Account authenticate(String username, String password) {
        Query query = em.createNamedQuery("Account.findByUsername");
        query.setParameter("username", username);
        return (Account) query.getSingleResult();
    }

    @Transactional
    public Account createAccount(String username, String password) {
        String digest = DigestUtils.md5DigestAsHex(password.getBytes());
        Account account=new Account();
        account.setUsername(username);
        account.setMd5Password(digest);
        em.persist(account);
        return account;
    }

    @Transactional
    public Project addProject(Account account, String name) {
        Project project=new Project();
        project.setName(name);
        project.setAccount(account);
        em.persist(project);
        account.getProjects().add(project);
        return project;
    }

    @Transactional(readOnly=true)
    public boolean isAccount(String username) {
        return em.createNamedQuery("Account.findByUsername")
                .setParameter("username", username)
                .getResultList().size() == 1;
    }

    @Transactional(readOnly=true)
    public Project findProject(Long projectId) {
        return em.find(Project.class, projectId);
    }
}
