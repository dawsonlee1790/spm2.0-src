package com.dawson.entity;

import com.dawson.entity.User;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2018-07-19T17:21:32")
@StaticMetamodel(Groups.class)
public class Groups_ { 

    public static volatile ListAttribute<Groups, User> userList;
    public static volatile SingularAttribute<Groups, String> description;
    public static volatile SingularAttribute<Groups, String> groupname;

}