package com.example.minha_agenda.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.minha_agenda.R;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().hide();
    }
}