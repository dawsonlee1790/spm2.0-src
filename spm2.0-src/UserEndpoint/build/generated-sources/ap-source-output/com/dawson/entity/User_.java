package com.dawson.entity;

import com.dawson.entity.Groups;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2018-07-19T17:21:32")
@StaticMetamodel(User.class)
public class User_ { 

    public static volatile SingularAttribute<User, String> password;
    public static volatile ListAttribute<User, Groups> groupsList;
    public static volatile SingularAttribute<User, String> id;
    public static volatile SingularAttribute<User, String> username;

}