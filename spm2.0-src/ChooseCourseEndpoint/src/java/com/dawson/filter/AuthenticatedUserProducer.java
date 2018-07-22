package com.dawson.filter;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Dawson
 */
@RequestScoped
public class AuthenticatedUserProducer {

    @Produces
    @AuthenticatedUser
    private String name;

    public void handleAuthenticationEvent(@Observes @AuthenticatedUser String username) {
        System.out.println("### CDI "+username);
        name = username;
        System.out.println("### CDI name "+name);
    }

}
