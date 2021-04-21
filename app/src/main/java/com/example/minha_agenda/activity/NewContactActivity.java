package com.example.minha_agenda.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.minha_agenda.R;
import com.example.minha_agenda.config.FirebaseConfig;
import com.example.minha_agenda.model.Contact;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

//Class to add a new contact to the Agenda
public class NewContactActivity extends AppCompatActivity {

    private FirebaseAuth userAuth;
    private EditText editName;
    private EditText editPhone;
    private EditText editEmail;
    private EditText editCep;
    private Button btnAdd;
    private DatabaseReference firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        //Appbar tittle
        getSupportActionBar().setTitle("Novo contato");

        //Gets auth instance
        userAuth = FirebaseConfig.getFirebaseAuth();

        //Get user inputs
        editName = findViewById(R.id.editNewContactName);
        editPhone = findViewById(R.id.editNewContactPhone);
        editEmail = findViewById(R.id.editNewContactEmail);
        editCep = findViewById(R.id.editNewContactCep);
        btnAdd = findViewById(R.id.btnNewContact);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = editName.getText().toString();
                String phone = editPhone.getText().toString();
                String email = editEmail.getText().toString();
                String cep = editCep.getText().toString();

                //Create a new contact object
                final Contact contact = new Contact();
                contact.setName(name);
                contact.setPhone(phone);
                contact.setEmail(email);
                contact.setCep(cep);

                //Validations
                //Check if all informations were given
                if( !name.isEmpty() && !phone.isEmpty() && !email.isEmpty() && !cep.isEmpty() ) {

                    firebase = FirebaseConfig.getFirebase().child("contacts").child(userAuth.getUid()).child(name);

                    firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            //Case the contact name already exists
                            if( snapshot.getValue() != null ) {
                                Toast.makeText(NewContactActivity.this, "Contato já cadastrado", Toast.LENGTH_LONG).show();
                            }
                            else  { //Case it's a new contact name
                                firebase.setValue( contact ); //Save the new contact in the database
                                Toast.makeText(NewContactActivity.this, "Contato adicionado com sucesso", Toast.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else{ //Case user didn't fill any text field
                    Toast.makeText(NewContactActivity.this, "Preencha todos os campos", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}