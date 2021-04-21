package com.example.minha_agenda.model;

import com.example.minha_agenda.config.FirebaseConfig;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

//Clas to create an user model
public class User {

    private String id;
    private String email;
    private String password;

    public User(){

    }

    public void saveUserInfos() {
        DatabaseReference firebaseReference = FirebaseConfig.getFirebase();
        firebaseReference.child("users").child( getId() ).setValue( this );
    }

    @Exclude //Ignore when save in Database
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
