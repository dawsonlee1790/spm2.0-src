/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dawson.service;

import com.dawson.ejb.TestFacade;
import com.dawson.entity.Test;
import com.dawson.filter.AuthenticatedUser;
import com.dawson.filter.JWTTokenNeeded;
import com.dawson.filter.Role;
import com.dawson.jaxrs.jwt.util.UUID;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author Dawson
 */
@Path("test")
public class TestEndpoint {

    @EJB
    TestFacade testFacade;

    @Context
    private UriInfo uriInfo;

    @Inject
    UUID uuid;

    @Context
    SecurityContext sctx;

    @Inject
    @AuthenticatedUser
    Instance<String> authenticatedUser;

//    ============================
//    =  使用JWT验证的REST API   =
//    ============================
//  教师，可以创建自己课程的题目
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @JWTTokenNeeded({Role.teacher})
    public Response register(Test entity) {
        try {
            if (entity.getId() == null || this.testFacade.find(entity.getId()) != null) {
                System.out.println("请求数据出错");
                return Response.status(Status.BAD_REQUEST).entity("请求数据出错或课程id已经存在").build();
            } else {
                this.testFacade.create(entity);
                return Response.ok().build();
            }
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    //教师可以删除自己课程的题目
    @DELETE
    @Path("{id}")
    @JWTTokenNeeded({Role.teacher})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response delete(@PathParam("id") String id) {
        try {
            Test course = this.testFacade.find(id);
            if (course == null) {
                return Response.status(Status.NOT_FOUND).build();
            } else if (!course.getAuthorId().equals(this.authenticatedUser.get())) {
                return Response.status(Status.FORBIDDEN).entity("该题目不属于您，您没有权限删除").build();
            } else {
                this.testFacade.remove(course);
                return Response.ok().build();
            }
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    // 教师可以编辑自己课程的题目
    @PUT
    @JWTTokenNeeded({Role.teacher})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response update(Test entity) {
        try {
            if (entity.getId() == null && this.testFacade.find(entity.getId()) == null) {
                return Response.status(Status.NOT_FOUND).entity("没有找到该课程").build();
            } else if (!entity.getAuthorId().equals(this.authenticatedUser.get())) {
                return Response.status(Status.FORBIDDEN).entity("该题目不属于您，您没有权限修改").build();
            } else {
                this.testFacade.edit(entity);
                return Response.ok().build();
            }
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    //用户可以查询任意题目
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Test find(@PathParam("id") String id) {
        //隐去用户的密码
        Test test = this.testFacade.find(id);
        return test;
    }

    //用户可以查所有测试题目
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Test> findAll() {
        //隐去用户的密码
        List<Test> testAll = this.testFacade.findAll();
        return testAll;
    }

    //用户可以查所有题目的数目
    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(this.testFacade.count());
    }

}
