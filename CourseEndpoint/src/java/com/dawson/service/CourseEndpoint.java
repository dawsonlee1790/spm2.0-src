/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dawson.service;

import com.dawson.ejb.CourseFacade;
import com.dawson.entity.Course;
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
@Path("course")
public class CourseEndpoint {

    @EJB
    CourseFacade courseFacade;

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
//    教务员、教师，可以创建课程
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @JWTTokenNeeded({Role.manager, Role.teacher})
    public Response register(Course entity) {
        try {
            if (entity.getId() == null || this.courseFacade.find(entity.getId()) != null) {
                System.out.println("请求数据出错");
                return Response.status(Status.BAD_REQUEST).entity("请求数据出错或课程id已经存在").build();
            } else {
                entity.setPlace(null);
                this.courseFacade.create(entity);
                return Response.ok().build();
            }
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    //管理员删除任意课程
    @DELETE
    @Path("{id}")
    @JWTTokenNeeded({Role.manager})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response delete(@PathParam("id") String id) {
        try {
            Course course = this.courseFacade.find(id);
            if (course == null) {
                return Response.status(Status.NOT_FOUND).build();
            } else {
                this.courseFacade.remove(course);
                return Response.ok().build();
            }
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    //教师删除属于自己的课程
    @DELETE
    @Path("{id}/teacher-delete")
    @JWTTokenNeeded({Role.teacher})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteBelongTo(@PathParam("id") String id) {
        try {
            Course course = this.courseFacade.find(id);
            if (course == null) {
                return Response.status(Status.NOT_FOUND).build();
            } else if (!course.getTeacherId().equals(this.authenticatedUser.get())) {
                return Response.status(Status.FORBIDDEN).entity("课程不属于该教师用户不能删除").build();
            } else {
                this.courseFacade.remove(course);
                return Response.ok().build();
            }
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    // 教务员用户实现修改任何课程信息功能
    @PUT
    @JWTTokenNeeded({Role.manager})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response update(Course entity) {
        try {
            if (entity.getId() == null && this.courseFacade.find(entity.getId()) == null) {
                return Response.status(Status.NOT_FOUND).entity("没有找到该课程").build();
            } else {
                this.courseFacade.edit(entity);
                return Response.ok().build();
            }
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    // 教师修改属于自己的除地点外课程信息
    @PUT
    @Path("teacher-edit")
    @JWTTokenNeeded({Role.teacher})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateBelongTo(Course entity) {
        try {
            if (entity.getId() == null) {
                return Response.status(Status.NOT_FOUND).entity("没有找到该课程").build();
            } else {
                Course course = this.courseFacade.find(entity.getId());
                if (course.getTeacherId()==null || !course.getTeacherId().equals(entity.getTeacherId())) {
                    return Response.status(Status.FORBIDDEN).entity("课程不属于该教师用户,不可以修改").build();
                } else {
                    //获取授权可以修改除地点和授课老师外的课程信息
//                    if (entity.getName() != null) {
//                        course.setName(entity.getName());
//                    }
//                    if (entity.getEndTime() != null) {
//                        course.setEndTime(entity.getEndTime());
//                    }
//                    if (entity.getStartTime() != null) {
//                        course.setStartTime(entity.getStartTime());
//                    }
                    if(course.getPlace()!=null){
                        entity.setPlace(course.getPlace());
                    }
                    if(course.getTeacherId()!=null){
                        entity.setTeacherId(course.getTeacherId());
                    }
                    this.courseFacade.edit(entity);
                    return Response.ok().build();
                }
            }
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    //用户可以查任何课程
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Course find(@PathParam("id") String id) {
        //隐去用户的密码
        Course course = this.courseFacade.find(id);
        return course;
    }

    //用户可以查所有课程
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Course> findAll() {
        //隐去用户的密码
        List<Course> courseAll = this.courseFacade.findAll();
        return courseAll;
    }


    //用户可以查所有课程数目
    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(this.courseFacade.count());
    }

}
