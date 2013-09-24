package com.akirkpatrick.mm;

import com.akirkpatrick.mm.model.Account;
import com.akirkpatrick.mm.model.Project;
import com.akirkpatrick.mm.web.MovieMakerSession;
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

    public String store(String base64data, MovieMakerSession mms) {
        UUID uuid = UUID.randomUUID();
        byte[] bytes = Base64.decode(base64data);
        try {
            File file = FileHelper.toFile(uuid);
            FileCopyUtils.copy(bytes, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mms.addFrame(uuid);
        return FileHelper.toImageDownloadUrl(uuid);
    }

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
    public Project createProject(String name) {
        Project project=new Project();
        project.setName(name);
        em.persist(project);
        return project;
    }
}
