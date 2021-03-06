/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dawson.ejb;

import com.dawson.entity.Notice;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Dawson
 */
@Stateless
public class NoticeFacade extends AbstractFacade<Notice> {

    @PersistenceContext(unitName = "CommentEndpointPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NoticeFacade() {
        super(Notice.class);
    }
    
}
