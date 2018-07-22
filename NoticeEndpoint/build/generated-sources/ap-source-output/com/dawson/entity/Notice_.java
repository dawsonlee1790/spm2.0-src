package com.dawson.entity;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2018-07-18T09:47:03")
@StaticMetamodel(Notice.class)
public class Notice_ { 

    public static volatile SingularAttribute<Notice, Date> date;
    public static volatile SingularAttribute<Notice, String> author;
    public static volatile SingularAttribute<Notice, String> id;
    public static volatile SingularAttribute<Notice, String> message;

}