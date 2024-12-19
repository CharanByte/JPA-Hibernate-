package com.xworkz.project.entity;


import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Data
@RequiredArgsConstructor
@Entity
@Table(name="signUp_table")
@NamedQuery(name = "getPassword",query = "select a.password from SignupEntity a where a.name=:setName")
public class SignupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "user_name")
    private String name;
    @Column(name = "user_email")
    private String email;
    @Column(name = "user_password")
    private String password;
    @Column(name = "user_phoneNo")
    private String phoneNo;
    @Column(name = "user_altEmail")
    private String altEmail;
    @Column(name = "user_altPhhoneNo")
    private String altPhhoneNo;
    @Column(name = "user_location")
    private String location;
}
