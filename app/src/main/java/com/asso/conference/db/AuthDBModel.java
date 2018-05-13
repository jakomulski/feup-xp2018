package com.asso.conference.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

@Table(name = "Auth")
public class AuthDBModel extends Model {

    @Column(name = "Id")
    public Integer id;
    @Column(name = "Username")
    public String username;
    @Column(name = "FirstName")
    public String firstName;
    @Column(name = "LastName")
    public String lastName;
    @Column(name = "Key")
    public String key;

    public AuthDBModel() {
        super();
    }

    public AuthDBModel(String username, String key) {
        super();
        this.username = username;
        this.key = key;
    }

    public static boolean keyExists() {
        return new Select().from(AuthDBModel.class).exists();
    }

    public static AuthDBModel getFirst() {
        return new Select().from(AuthDBModel.class).executeSingle();
    }

    public static void updateToken(String token) {
        new Update(AuthDBModel.class).set("Key = ?",token).execute();
    }

//    public static void insert(String token) {
//        new Update(AuthDBModel.class).set("Token = ?",token).execute();
//    }


}
