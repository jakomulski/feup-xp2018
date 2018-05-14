package com.asso.conference.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;


@Table(name = "User")
public class UserDBModel {

    @Column(name = "Id")
    public String id;
    @Column(name = "Username")
    public String username;
    @Column(name = "FirstName")
    public String firstName;
    @Column(name = "LastName")
    public String lastName;


    public UserDBModel() {
        super();
    }


}
