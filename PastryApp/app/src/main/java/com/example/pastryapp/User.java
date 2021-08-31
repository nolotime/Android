package com.example.pastryapp;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    String name, email, role, uid;
    Long timestamp;
    ArrayList<String> FavoredRecipes;

    public ArrayList<String> getFavoredRecipes() {
        return FavoredRecipes;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }
}
