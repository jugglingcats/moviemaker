package com.akirkpatrick.mm.rest;

import com.akirkpatrick.mm.model.Account;
import com.sun.jersey.api.core.InjectParam;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Type;

@Provider
@Component
public class UserProvider implements Injectable<Account>, InjectableProvider<User, Type> {
    @Context
    private HttpServletRequest request;

    @PersistenceContext
    private EntityManager em;

    @Override
    public Injectable getInjectable(ComponentContext ic, User user, Type type) {
        if (type.equals(Account.class)) {
            return this;
        }
        return null;
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.PerRequest;
    }

    @Override
    public Account getValue() {
        final Account account = (Account) request.getSession().getAttribute("mm.account");
        if (account == null) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        em.merge(account);
        return account;
    }

}
