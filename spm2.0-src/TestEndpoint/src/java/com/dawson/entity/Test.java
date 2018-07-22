/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dawson.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Dawson
 */
@Entity
@Table(name = "test")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Test.findAll", query = "SELECT t FROM Test t")
    , @NamedQuery(name = "Test.findById", query = "SELECT t FROM Test t WHERE t.id = :id")
    , @NamedQuery(name = "Test.findByQuestion", query = "SELECT t FROM Test t WHERE t.question = :question")
    , @NamedQuery(name = "Test.findByA", query = "SELECT t FROM Test t WHERE t.a = :a")
    , @NamedQuery(name = "Test.findByB", query = "SELECT t FROM Test t WHERE t.b = :b")
    , @NamedQuery(name = "Test.findByC", query = "SELECT t FROM Test t WHERE t.c = :c")
    , @NamedQuery(name = "Test.findByD", query = "SELECT t FROM Test t WHERE t.d = :d")
    , @NamedQuery(name = "Test.findByAnswer", query = "SELECT t FROM Test t WHERE t.answer = :answer")
    , @NamedQuery(name = "Test.findByDescription", query = "SELECT t FROM Test t WHERE t.description = :description")
    , @NamedQuery(name = "Test.findByCourseId", query = "SELECT t FROM Test t WHERE t.courseId = :courseId")
    , @NamedQuery(name = "Test.findByAuthorId", query = "SELECT t FROM Test t WHERE t.authorId = :authorId")})
public class Test implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Size(max = 450)
    @Column(name = "question")
    private String question;
    @Size(max = 450)
    @Column(name = "a")
    private String a;
    @Size(max = 450)
    @Column(name = "b")
    private String b;
    @Size(max = 450)
    @Column(name = "c")
    private String c;
    @Size(max = 450)
    @Column(name = "d")
    private String d;
    @Size(max = 45)
    @Column(name = "answer")
    private String answer;
    @Size(max = 450)
    @Column(name = "description")
    private String description;
    @Size(max = 45)
    @Column(name = "course_id")
    private String courseId;
    @Size(max = 450)
    @Column(name = "author_id")
    private String authorId;

    public Test() {
    }

    public Test(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Test)) {
            return false;
        }
        Test other = (Test) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.dawson.entity.Test[ id=" + id + " ]";
    }
    
}
