/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dawson.ejb;

import com.dawson.entity.StudentCourse;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Dawson
 */
@Stateless
public class StudentCourseFacade extends AbstractFacade<StudentCourse> {

    @PersistenceContext(unitName = "ChooseCourseEndpointPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public StudentCourseFacade() {
        super(StudentCourse.class);
    }
    
}
