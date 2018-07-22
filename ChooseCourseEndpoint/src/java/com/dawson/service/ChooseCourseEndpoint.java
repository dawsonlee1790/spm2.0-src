



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dawson.service;

import com.dawson.ejb.StudentCourseFacade;
import com.dawson.entity.StudentCourse;
import com.dawson.entity.StudentCoursePK;
import com.dawson.filter.AuthenticatedUser;
import com.dawson.filter.JWTTokenNeeded;
import com.dawson.filter.Role;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author Dawson
 */
@Path("choose-course")
public class ChooseCourseEndpoint {

    @EJB
    StudentCourseFacade studentCourseFacade;

    @Context
    private UriInfo uriInfo;

    @Context
    SecurityContext sctx;

    @Inject
    @AuthenticatedUser
    Instance<String> authenticatedUser;

    private StudentCoursePK getPrimaryKey(PathSegment pathSegment) {
        /*
         * pathSemgent represents a URI path segment and any associated matrix parameters.
         * URI path part is supposed to be in form of 'somePath;studentId=studentIdValue;courseId=courseIdValue'.
         * Here 'somePath' is a result of getPath() method invocation and
         * it is ignored in the following code.
         * Matrix parameters are used as field names to build a primary key instance.
         */
        com.dawson.entity.StudentCoursePK key = new com.dawson.entity.StudentCoursePK();
        javax.ws.rs.core.MultivaluedMap<String, String> map = pathSegment.getMatrixParameters();
        java.util.List<String> studentId = map.get("studentId");
        if (studentId != null && !studentId.isEmpty()) {
            key.setStudentId(studentId.get(0));
        }
        java.util.List<String> courseId = map.get("courseId");
        if (courseId != null && !courseId.isEmpty()) {
            key.setCourseId(courseId.get(0));
        }
        return key;
    }

//    ============================
//    =  使用JWT验证的REST API   =
//    ============================
//    学生可以选课
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @JWTTokenNeeded({Role.student})
    public Response register(StudentCourse entity) {
        try {
            if (entity.getStudentCoursePK() == null || this.studentCourseFacade.find(entity.getStudentCoursePK()) != null) {
                System.out.println("请求数据出错");
                return Response.status(Status.BAD_REQUEST).entity("请求数据出错或学生已经选择该课程").build();
            } else if (!entity.getStudentCoursePK().getStudentId().equals(this.authenticatedUser.get())) {
                System.out.println("entity.getStudentCoursePK().getStudentId():  " + entity.getStudentCoursePK().getStudentId());
                System.out.println("authenticatedUser:   " + authenticatedUser.get());
                return Response.status(Status.FORBIDDEN).entity("不可以为别的学生用户选课").build();
            } else {
                this.studentCourseFacade.create(entity);
                return Response.ok().build();
            }
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    //学生取消选课
    @DELETE
    @Path("{id}")
    @JWTTokenNeeded({Role.student})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteBelongTo(@PathParam("id") PathSegment id) {
        try {
            StudentCoursePK key = getPrimaryKey(id);
            StudentCourse studentCourse = this.studentCourseFacade.find(key);
            if (studentCourse == null) {
                return Response.status(Status.NOT_FOUND).build();
            } else if (!studentCourse.getStudentCoursePK().getStudentId().equals(this.authenticatedUser.get())) {
                return Response.status(Status.FORBIDDEN).entity("不可以为别的学生用户取消选课").build();
            } else {
                this.studentCourseFacade.remove(studentCourse);
                return Response.ok().build();
            }
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    //用户查询某学生选课列表
    @GET
    @Path("student/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<StudentCourse> findByStudent(@PathParam("id") String studentid) {
        //隐去用户的密码
        List<StudentCourse> coursesOfStudent = new ArrayList<>();
        for (StudentCourse item : this.studentCourseFacade.findAll()) {
            if (item.getStudentCoursePK().getStudentId().equals(studentid)) {
                coursesOfStudent.add(item);
            }
        }
        return coursesOfStudent;
    }

    //用户查询某课程的学生列表
    @GET
    @Path("course/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<StudentCourse> findByCourse(@PathParam("id") String courseid) {
        //隐去用户的密码
        List<StudentCourse> studentOfCourse = new ArrayList<>();
        for (StudentCourse item : this.studentCourseFacade.findAll()) {
            if (item.getStudentCoursePK().getCourseId().equals(courseid)) {
                studentOfCourse.add(item);
            }
        }
        return studentOfCourse;
    }

    //用户可以查所有学生的选课信息
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<StudentCourse> findAll() {
        //隐去用户的密码
        
        List<StudentCourse> all = this.studentCourseFacade.findAll();
        return all;
    }

}
