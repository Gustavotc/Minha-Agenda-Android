package com.example.minha_agenda.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.minha_agenda.R;
import com.example.minha_agenda.config.FirebaseConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth user = FirebaseAuth.getInstance();

    private DatabaseReference firebaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseReference = FirebaseConfig.getFirebase();
        firebaseReference.child("pontos").setValue("100");

        getSupportActionBar().hide();
    }

        public void openCreateAcc(View view){

            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity( intent );

        }

        /*
        user.createUserWithEmailAndPassword("gtchinalia@gmail.com", "gu12345").addOnCompleteListener(
                LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if ( task.isSuccessful() ){
                            Log.i("CreateUser", "Sucesso ao criar usuário");
                        }
                        else {
                            Log.i("CreateUser", "Erro ao criar usuário");
                        }
                    }
                }
        );
    } */
}