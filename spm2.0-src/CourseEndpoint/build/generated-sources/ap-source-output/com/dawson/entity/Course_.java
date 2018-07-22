package com.dawson.entity;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2018-07-19T21:17:34")
@StaticMetamodel(Course.class)
public class Course_ { 

    public static volatile SingularAttribute<Course, String> teacherId;
    public static volatile SingularAttribute<Course, String> name;
    public static volatile SingularAttribute<Course, Date> startTime;
    public static volatile SingularAttribute<Course, String> id;
    public static volatile SingularAttribute<Course, Date> endTime;
    public static volatile SingularAttribute<Course, String> place;

}