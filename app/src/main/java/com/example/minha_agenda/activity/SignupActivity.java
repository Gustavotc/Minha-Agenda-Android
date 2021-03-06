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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

//Class to create the signup activity
public class SignupActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button btnSignup;
    private User user;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //SupportActionBar configs
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Cadastre-se");

        //Find components
        email       = findViewById(R.id.edit_signup_email);
        password    = findViewById(R.id.edit_signup_password);
        btnSignup   = findViewById(R.id.btnSignUp);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyFields();
            }
        });
    }

    //Function to verify if all fields were filled and create a new user object
    private void verifyFields() {
        user = new User();
        user.setEmail( email.getText().toString() );
        user.setPassword( password.getText().toString() );

        //Verify empty fields
        if (!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
            createUser();
        }
        else {
            Toast.makeText(SignupActivity.this, "Um ou mais campos est??o vazios", Toast.LENGTH_LONG).show();
        }
    }

    //Function to create a new user in the Database
    private void createUser() {
        auth = FirebaseConfig.getFirebaseAuth();
        auth.createUserWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SignupActivity.this, "Cadastro Realizado!", Toast.LENGTH_SHORT).show();

                    user.setId( task.getResult().getUser().getUid() );
                    user.saveUserInfos();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {

                    String exceptionError = "";

                    //Treat SignUp exceptions
                    try{
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        exceptionError = "A senha deve conter mais de 6 caracteres";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        exceptionError = "O email digitado ?? inv??lido";
                    } catch (FirebaseAuthUserCollisionException e) {
                       exceptionError = "Email j?? cadastrado";
                    } catch (Exception e) {
                        exceptionError = "Erro ao efetuar o cadastro";
                        e.printStackTrace();
                    }

                    Toast.makeText(SignupActivity.this, "Erro: " + exceptionError, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}