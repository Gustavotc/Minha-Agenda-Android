package com.example.minha_agenda.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.minha_agenda.R;
import com.example.minha_agenda.config.FirebaseConfig;
import com.example.minha_agenda.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button btnLogin;
    private User user;
    private  FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Verify if the user is logged
        verifyUserLogin();

        email    = findViewById(R.id.edit_login_email);
        password = findViewById(R.id.edit_login_password);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user = new User();
                user.setEmail( email.getText().toString() );
                user.setPassword( password.getText().toString() );

                //Verify empty fields
                if (!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
                    validateLogin();
                }
                else {
                    Toast.makeText(LoginActivity.this, "Um ou mais campos estão vazios", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //Function to Validade user email and password
    private void validateLogin() {

        auth = FirebaseConfig.getFirebaseAuth();
        auth.signInWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    openMainScreen();
                    Toast.makeText(LoginActivity.this, "Sucesso ao fazer login !", Toast.LENGTH_SHORT).show();
                }
                else  {

                    String exceptionError = "";

                    //Treat Login exceptions
                    try{
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        exceptionError = "Email não cadastrado";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        exceptionError = "Senha inválida";
                    } catch (Exception e) {
                        exceptionError = "Erro ao efetuar login";
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this, "Erro: " + exceptionError, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void verifyUserLogin() {

        auth = FirebaseConfig.getFirebaseAuth();

        if( auth.getCurrentUser() != null ) {
            openMainScreen();
        }
    }

    //Function to open Main page
    private void openMainScreen() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //Function to open Sign-up page
    public void openCreateAcc(View view){

        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity( intent );
    }

}