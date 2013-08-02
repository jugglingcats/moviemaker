package com.akirkpatrick.classifieds.service;

import com.akirkpatrick.classifieds.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created with IntelliJ IDEA.
 * User: alfie
 * Date: 31/07/13
 * Time: 23:39
 * To change this template use File | Settings | File Templates.
 */
@Component
public class MyService {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public String getMessage() {
        Item i=new Item();
        em.persist(i);

        return "Hi there Alfie!";
    }
}
