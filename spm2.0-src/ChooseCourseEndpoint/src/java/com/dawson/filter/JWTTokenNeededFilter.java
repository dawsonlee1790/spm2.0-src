package com.dawson.filter;

import io.jsonwebtoken.Jwts;
import com.dawson.jaxrs.jwt.util.KeyGenerator;
import io.jsonwebtoken.Claims;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import javax.enterprise.event.Event;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;

/**
 * @author Dawson
 */
@Provider
@JWTTokenNeeded
@Priority(Priorities.AUTHENTICATION)
public class JWTTokenNeededFilter implements ContainerRequestFilter {

    // ======================================
    // =          Injection Points          =
    // ======================================
    @Inject
    private KeyGenerator keyGenerator;

    @Inject
    private Logger logger;

    @Context
    private ResourceInfo resourceInfo;

    @Inject
    @AuthenticatedUser
    Event<String> userAuthenticatedEvent;

    // ======================================
    // =          Business methods          =
    // ======================================
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Get the HTTP Authorization header from the request
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        logger.info("#### authorizationHeader : " + authorizationHeader);

        // Check if the HTTP Authorization header is present and formatted correctly
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.severe("#### invalid authorizationHeader : " + authorizationHeader);
            throw new NotAuthorizedException("Authorization header must be provided");
        }

        // Extract the token from the HTTP Authorization header
        String token = authorizationHeader.substring("Bearer ".length()).trim();

        // Validate the token
        Key key = keyGenerator.generateKey();
        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        userAuthenticatedEvent.fire(claims.getSubject());
        logger.info("#### valid token :  " +claims.getSubject()+ "     "+ token);

        // Get the resource class which matches with the requested URL
        // Extract the roles declared by it
        Class<?> resourceClass = resourceInfo.getResourceClass();
        List<Role> classRoles = extractRoles(resourceClass);

        // Get the resource method which matches with the requested URL
        // Extract the roles declared by it
        Method resourceMethod = resourceInfo.getResourceMethod();
        List<Role> methodRoles = extractRoles(resourceMethod);
        try {
            // Check if the user is allowed to execute the method
            // The method annotations override the class annotations
            if (methodRoles.isEmpty()) {
                checkPermissions(classRoles, claims);
            } else {
                checkPermissions(methodRoles, claims);
            }
        } catch (Exception e) {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
        }

    }

    // Extract the roles from the annotated element
    private List<Role> extractRoles(AnnotatedElement annotatedElement) {
        if (annotatedElement == null) {
            return new ArrayList<Role>();
        } else {
            JWTTokenNeeded secured = annotatedElement.getAnnotation(JWTTokenNeeded.class);
            if (secured == null) {
                return new ArrayList<Role>();
            } else {
                Role[] allowedRoles = secured.value();
                return Arrays.asList(allowedRoles);
            }
        }
    }

    private void checkPermissions(List<Role> allowedRoles, Claims claims) throws Exception {
        // Check if the user contains one of the allowed roles
        // Throw an Exception if the user has not permission to execute the method
        boolean flag = false;
        if (allowedRoles.isEmpty()) {
            //注解为空则默认人人都可以操作
            flag = true;
        } else {
            //否则只允许限定角色操作

            if ((boolean) claims.get("manager") && allowedRoles.contains(Role.manager)) {
                flag = true;
            } else if ((boolean) claims.get("student") && allowedRoles.contains(Role.student)) {
                flag = true;
            } else if ((boolean) claims.get("teacher") && allowedRoles.contains(Role.teacher)) {
                flag = true;
            }
        }

        //如果得不到允许，则抛出异常
        if (!flag) {
            logger.info("manager：  " + claims.get("manager"));
            logger.info("student：  " + claims.get("student"));
            logger.info("teacher：  " + claims.get("teacher"));
            logger.info("allowedRoles：  " + allowedRoles);
            logger.info("you hava not permission to requst this api");
            throw new NotAuthorizedException("you hava not permission to requst this api");
        } else {
            logger.info("you have  permission to requst this api");
        }
    }

}
