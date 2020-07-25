package com.learn.demobatch.users.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    private Integer id;

    @Column
    private String name;

    @Column
    private String dept;

    @Column
    private Integer salary;
}
