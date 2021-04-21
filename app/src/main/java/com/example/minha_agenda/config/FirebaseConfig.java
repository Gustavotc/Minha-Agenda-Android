package com.example.minha_agenda.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//Class to configure database returns
public final class FirebaseConfig {

    private static DatabaseReference firebaseReference;
    private static FirebaseAuth auth;

    //Returns a firebase reference
    public static DatabaseReference getFirebase() {

        if( firebaseReference == null){
            firebaseReference = FirebaseDatabase.getInstance().getReference();
        }
        return firebaseReference;
    }

    //Returns a firebase auth object
    public static FirebaseAuth getFirebaseAuth() {
        if( auth == null ) {
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }
}
