package com.example.minha_agenda.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.minha_agenda.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference firebaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseReference.child("teste").setValue(100);

    }
}