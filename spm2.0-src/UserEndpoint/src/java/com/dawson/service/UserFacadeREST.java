/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dawson.service;

import com.dawson.ejb.GroupsFacade;
import com.dawson.ejb.UserFacade;
import com.dawson.entity.Groups;
import com.dawson.entity.User;
import com.dawson.filter.AuthenticatedUser;
import com.dawson.filter.JWTTokenNeeded;
import com.dawson.filter.Role;
import com.dawson.jaxrs.jwt.util.KeyGenerator;
import com.dawson.jaxrs.jwt.util.SHA256;
import com.dawson.json.Login;
import com.dawson.json.Payload;
import com.google.gson.Gson;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author Dawson
 */
@Path("user")
public class UserFacadeREST {

    @EJB
    UserFacade userFacade;

    @EJB
    GroupsFacade groupsFacade;

    @Context
    private UriInfo uriInfo;

    @Inject
    KeyGenerator keyGenerator;

    @Context
    SecurityContext sctx;

    @Inject
    @AuthenticatedUser
    Instance<String> authenticatedUser;

    //    GlassFish容器身份验证+配置JWT(Json Web Token)
    @OPTIONS
    @Path("login")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response login() {
        try {
            System.out.println("Authenticated user: " + sctx.getUserPrincipal().getName());
            String authenticatedUser = sctx.getUserPrincipal().getName();
            //编辑JWT(Json web token)的payload数据
//        {
//          "sub": "2015212101",
//          "iss": "http://localhost:8080/RESTful-User-Authorization/api/user/login",
//          "iat": 1531308470089,
//          "exp": 1531310270089,
//          "manager": false,
//          "teacher": true,
//          "student": false
//        }
            Date date = new Date();
            Payload payload = new Payload();
            payload.setSub(authenticatedUser);
            payload.setIss(uriInfo.getAbsolutePath().toString());
            payload.setIat(date.getTime());
            payload.setExp(date.getTime() + 30 * 60 * 1000);//加30分钟
            if (sctx.isUserInRole("manager")) {
                payload.setManager(Boolean.TRUE);
            } else if (sctx.isUserInRole("teacher")) {
                payload.setTeacher(Boolean.TRUE);
            } else if (sctx.isUserInRole("student")) {
                payload.setStudent(Boolean.TRUE);
            }
            String json = new Gson().toJson(payload);
            Key key = keyGenerator.generateKey();

            String jwtToken = Jwts.builder()
                    .setPayload(json)
                    .signWith(SignatureAlgorithm.HS512, key)
                    .compact();
            System.out.println("#### generating token for a key :" + jwtToken + "---" + key);
            return Response.ok("Bearer " + jwtToken).header(AUTHORIZATION, "Bearer " + jwtToken).build();
        } catch (Exception e) {
            return Response.status(Status.UNAUTHORIZED).build();
        }
    }
     
        //    GlassFish容器身份验证+配置JWT(Json Web Token)
    @POST
    @Path("login2")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response login2(Login loginjson) {
        try {
            User u=this.userFacade.find(loginjson.getUsername());
            String s_password = new SHA256().getSHA256StrJava(loginjson.getPassword());
            if(!s_password.equals(u.getPassword())){
                throw new NotAuthorizedException("账号密码不正确");
            }
            
            String authenticatedUser=u.getId();
//            System.out.println("Authenticated user: " + sctx.getUserPrincipal().getName());
//            String authenticatedUser = sctx.getUserPrincipal().getName();
            //编辑JWT(Json web token)的payload数据
//        {
//          "sub": "2015212101",
//          "iss": "http://localhost:8080/RESTful-User-Authorization/api/user/login",
//          "iat": 1531308470089,
//          "exp": 1531310270089,
//          "manager": false,
//          "teacher": true,
//          "student": false
//        }
            Date date = new Date();
            Payload payload = new Payload();
            payload.setSub(authenticatedUser);
            payload.setIss(uriInfo.getAbsolutePath().toString());
            payload.setIat(date.getTime());
            payload.setExp(date.getTime() + 30 * 60 * 1000);//加30分钟
            
            Groups manager=this.groupsFacade.find("manager");
            Groups student=this.groupsFacade.find("student");
            Groups teacher=this.groupsFacade.find("teacher");
            
            if (u.getGroupsList().contains(manager)) {
                payload.setManager(Boolean.TRUE);
            } else if (u.getGroupsList().contains(teacher)) {
                payload.setTeacher(Boolean.TRUE);
            } else if (u.getGroupsList().contains(student)) {
                payload.setStudent(Boolean.TRUE);
            }
            
            String json = new Gson().toJson(payload);
            Key key = keyGenerator.generateKey();

            String jwtToken = Jwts.builder()
                    .setPayload(json)
                    .signWith(SignatureAlgorithm.HS512, key)
                    .compact();
            System.out.println("#### generating token for a key :" + jwtToken + "---" + key);
            return Response.ok("Bearer " + jwtToken).header(AUTHORIZATION, "Bearer " + jwtToken).build();
        } catch (Exception e) {
            return Response.status(Status.UNAUTHORIZED).build();
        }
    }
    
    
    
    
    

//    ============================
//    =  使用JWT验证的REST API   =
//    ============================
//    管理员进行用户注册，并分配权限
    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @JWTTokenNeeded({Role.manager})
    public Response register(User entity) {
        try {
            String password = entity.getPassword();
            //密码长度不能小于8位数
            if (password == null || password.length() < 8) {
                return Response.status(Status.BAD_REQUEST).entity("密码长度不能小于8位数").build();
            } else if (entity.getId() == null || this.userFacade.find(entity.getId()) != null) {
                return Response.status(Status.BAD_REQUEST).entity("ID为空或用户ID已经存在").build();
            } else {
                String s_password = new SHA256().getSHA256StrJava(password);
                entity.setPassword(s_password);
                userFacade.create(entity);
                return Response.ok().build();
            }
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    //删除任何用户,只有管理员有权限
    @DELETE
    @Path("{id}")
    @JWTTokenNeeded({Role.manager})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response delete(@PathParam("id") String id) {
        try {
            userFacade.remove(userFacade.find(id));
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    //管理员分配权限
    @PUT
    @Path("{id}/{group}")
    @JWTTokenNeeded({Role.manager})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response grouping(@PathParam("id") String id, @PathParam("group") String group) {
        try {
            if (!group.equals(Role.manager.toString()) && !group.equals(Role.student.toString()) && !group.equals(Role.teacher.toString())) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("身份{group}请填`manager`或`student`或`teacher`").build();
            } else if (this.userFacade.find(id) == null) {
                return Response.status(Status.BAD_REQUEST)
                        .entity("用户ID不存在").build();
            } else {
                User user = this.userFacade.find(id);
                List<User> userlist = new ArrayList<>();
                userlist.add(user);
                Groups groups = this.groupsFacade.find(group);
                groups.setUserList(userlist);
                return Response.ok().build();
            }
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    //管理员可以更改用户所有的信息
    @PUT
    @Path("{id}")
    @JWTTokenNeeded({Role.manager})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response update(@PathParam("id") String id, User entity) {
        try {
            if (userFacade.find(id) == null) {
                return Response.status(Status.NOT_FOUND).build();
            }
            String password = entity.getPassword();
            //密码长度不能小于8位数
            if (password == null || password.length() < 8) {
                return Response.status(Status.BAD_REQUEST).build();
            } else {
                String s_password = new SHA256().getSHA256StrJava(password);
                entity.setPassword(s_password);
                userFacade.edit(entity);
                return Response.ok().build();
            }
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    //用户可以更改自己的username和password
    //密码不可以小于8位数
    @PUT
    @Path("oneself/{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @JWTTokenNeeded
    public Response updateOneself(@PathParam("id") String id, User entity) {
        try {
            if (id == null ? authenticatedUser.get() != null : !id.equals(authenticatedUser.get())) {
                System.out.println("##### CDI   " + authenticatedUser.get());
                return Response.status(Status.FORBIDDEN).build();
            }

            User user = userFacade.find(id);
            if (user == null) {
                return Response.status(Status.NOT_FOUND).build();
            }
            user.setUsername(entity.getUsername());
            String password = entity.getPassword();
            //密码长度不能小于8位数
            if (password == null || password.length() <= 8) {
                return Response.status(Status.BAD_REQUEST).build();
            } else {
                String s_password = new SHA256().getSHA256StrJava(password);
                user.setPassword(s_password);
                userFacade.edit(user);
                return Response.ok().build();
            }
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    //管理员可以查任何用户
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @JWTTokenNeeded({Role.manager})
    public User find(@PathParam("id") String id) {
        //隐去用户的密码
        User user = userFacade.find(id);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    //管理员可以查所有用户
    @JWTTokenNeeded({Role.manager})
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<User> findAll() {
        //隐去用户的密码
        List<User> userAll = userFacade.findAll();
        for (User user : userAll) {
            user.setPassword(null);
        }
        return userAll;
    }

    //用户个人查自己的数据
    @JWTTokenNeeded
    @GET
    @Path("oneself/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findOneself(@PathParam("id") String id) {
        if (id == null ? authenticatedUser.get() != null : !id.equals(authenticatedUser.get())) {
            System.out.println("##### CDI   " + authenticatedUser.get());
            return Response.status(Status.FORBIDDEN).build();
        }
        //隐去用户的密码
        System.out.println("##### CDI   " + authenticatedUser.get());
        User user = userFacade.find(id);
        user.setPassword(null);
        return Response.ok(user).build();
    }

    //管理员可以查所有用户人数
    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    @JWTTokenNeeded({Role.manager})
    public String countREST() {
        return String.valueOf(userFacade.count());
    }

}
