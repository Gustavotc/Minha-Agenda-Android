package com.example.minha_agenda.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.minha_agenda.R;
import com.example.minha_agenda.config.FirebaseConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseConfig.getFirebaseAuth();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch ( item.getItemId() ) {
            case R.id.logoutOption :
                signOut();
                break;
            case R.id.newContactOption :
                startActivity(new Intent(MainActivity.this, NewContactActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        auth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    private void openAddContact(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        //Dialog configs
        alertDialog.setTitle("Novo contato");
        alertDialog.setMessage("Nome do usuário");
        alertDialog.setCancelable(false);

        EditText editName = new EditText(MainActivity.this);
        alertDialog.setView(editName);

        alertDialog.setMessage("Telefone do usuário");
        EditText editPhone = new EditText(MainActivity.this);
        alertDialog.setView(editPhone);

        //Buttons configs
        alertDialog.setPositiveButton("Cadastrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.create();
        alertDialog.show();
    }
}