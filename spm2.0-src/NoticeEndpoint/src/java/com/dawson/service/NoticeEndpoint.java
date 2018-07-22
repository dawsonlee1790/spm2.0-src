/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dawson.service;

import com.dawson.ejb.NoticeFacade;
import com.dawson.entity.Notice;
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
@Path("notice")
public class NoticeEndpoint {

    @EJB
    NoticeFacade noticeFacade;

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
//    教务员、教师，可以创建通知
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @JWTTokenNeeded({Role.manager, Role.teacher})
    public Response register(Notice entity) {
        try {
            if (entity.getId() == null || this.noticeFacade.find(entity.getId()) != null) {
                System.out.println("请求数据出错");
                return Response.status(Status.BAD_REQUEST).entity("请求数据出错或通知id已经存在").build();
            } else {
                this.noticeFacade.create(entity);
                return Response.ok().build();
            }
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    //管理员删除任意通知
    @DELETE
    @Path("{id}")
    @JWTTokenNeeded({Role.manager})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response delete(@PathParam("id") String id) {
        try {
            Notice course = this.noticeFacade.find(id);
            if (course == null) {
                return Response.status(Status.NOT_FOUND).build();
            } else {
                this.noticeFacade.remove(course);
                return Response.ok().build();
            }
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    //教师删除属于自己的通知
    @DELETE
    @Path("{id}/teacher-delete")
    @JWTTokenNeeded({Role.teacher})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteBelongTo(@PathParam("id") String id) {
        try {
            Notice notice = this.noticeFacade.find(id);
            if (notice == null) {
                return Response.status(Status.NOT_FOUND).build();
            } else if (!notice.getAuthor().equals(this.authenticatedUser.get())) {
                return Response.status(Status.FORBIDDEN).entity("课程不属于该教师用户不能删除").build();
            } else {
                this.noticeFacade.remove(notice);
                return Response.ok().build();
            }
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    // 教务员用户实现修改任何通知信息功能
    @PUT
    @JWTTokenNeeded({Role.manager})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response update(Notice entity) {
        try {
            if (entity.getId() == null && this.noticeFacade.find(entity.getId()) == null) {
                return Response.status(Status.NOT_FOUND).entity("没有找到该课程").build();
            } else {
                this.noticeFacade.edit(entity);
                return Response.ok().build();
            }
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    // 教师修改属于自己的通知信息
    @PUT
    @Path("teacher-edit")
    @JWTTokenNeeded({Role.teacher})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateBelongTo(Notice entity) {
        try {
            if (entity.getId() == null) {
                return Response.status(Status.NOT_FOUND).entity("没有找到该课程").build();
            } else {
                Notice notice = this.noticeFacade.find(entity.getId());
                if (notice.getAuthor()==null || !notice.getAuthor().equals(entity.getAuthor())) {
                    return Response.status(Status.FORBIDDEN).entity("课程不属于该教师用户,不可以修改").build();
                } else {
                    this.noticeFacade.edit(entity);
                    return Response.ok().build();
                }
            }
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    //用户可以查任何通知
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Notice find(@PathParam("id") String id) {
        //隐去用户的密码
        Notice notice = this.noticeFacade.find(id);
        return notice;
    }

    //用户可以查所有通知
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Notice> findAll() {
        //隐去用户的密码
        List<Notice> noticeAll = this.noticeFacade.findAll();
        return noticeAll;
    }


    //用户可以查所有课程数目
    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(this.noticeFacade.count());
    }

}
