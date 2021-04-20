package com.example.minha_agenda.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class FirebaseConfig {

    private static DatabaseReference firebaseReference;

    public static DatabaseReference getFirebase() {

        if( firebaseReference == null){
            firebaseReference = FirebaseDatabase.getInstance().getReference();
        }

        return firebaseReference;
    }
}
