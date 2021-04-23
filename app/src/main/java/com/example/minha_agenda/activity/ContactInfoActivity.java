package com.example.minha_agenda.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minha_agenda.R;
import com.example.minha_agenda.config.FirebaseConfig;
import com.example.minha_agenda.model.Contact;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ContactInfoActivity extends AppCompatActivity {

    private EditText editName;
    private EditText editPhone;
    private EditText editEmail;
    private EditText editCep;
    private EditText editAdress;
    private Button btnEdit;

    private DatabaseReference firebase;
    private FirebaseAuth auth;

    private String contactId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);

        getSupportActionBar().setTitle("Informações do contato");

        //Recover contact name from user selection
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
             this.contactId = extras.getString("id");
             Log.i("ContactKey", "Recebido: " + contactId);
        }

        editName = findViewById(R.id.editInfoName);
        editPhone = findViewById(R.id.editInfoPhone);
        editEmail = findViewById(R.id.editInfoEmail);
        editCep = findViewById(R.id.editInfoCep);
        editAdress = findViewById(R.id.editInfoAdress);
        btnEdit = findViewById(R.id.btnEditContact);

        //Get database user auth
        auth = FirebaseConfig.getFirebaseAuth();

        //Get database instance, based on the contact name
        firebase = FirebaseConfig.getFirebase()
                .child("contacts")
                .child(auth.getUid())
                .child(contactId);

        //Get Database Contact Infos and show in EditTexts, disabled for edition
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Contact contact = snapshot.getValue(Contact.class);
                editName.setText(contact.getName());
                editPhone.setText(contact.getPhone());
                editEmail.setText(contact.getEmail());
                editCep.setText(contact.getCep());
                editAdress.setText(contact.getAdress());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact contact = new Contact();
                contact.setName(editName.getText().toString());
                contact.setPhone(editPhone.getText().toString());
                contact.setEmail(editEmail.getText().toString());
                contact.setCep(editCep.getText().toString());
                contact.setAdress(editAdress.getText().toString());

                if(!contact.getName().isEmpty() && !contact.getPhone().isEmpty() && !contact.getEmail().isEmpty()
                        && !contact.getCep().isEmpty() && !contact.getAdress().isEmpty()){
                    firebase.setValue( contact ).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ContactInfoActivity.this, "Contato atualizado", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ContactInfoActivity.this, "Erro ao atualizar contato", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Show edit icon
        getMenuInflater().inflate(R.menu.contact_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        //Allows editions and shows Edit Contact button
        switch (item.getItemId()) {
            case R.id.editContactButton:
                editName.setEnabled(true);
                editPhone.setEnabled(true);
                editEmail.setEnabled(true);
                editCep.setEnabled(true);
                editAdress.setEnabled(true);
                btnEdit.setVisibility(View.VISIBLE);
                btnEdit.setEnabled(true);
                setMasks();
                break;
            case R.id.deleteContactButton:
                firebase.removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        finish();
                        Toast.makeText(ContactInfoActivity.this, "Contato removido", Toast.LENGTH_LONG).show();
                    }
                });
        }

        return super.onOptionsItemSelected(item);
    }

    private void setMasks() {

        //Phone mask
        SimpleMaskFormatter phone = new SimpleMaskFormatter("(NN) NNNNN-NNNN");
        MaskTextWatcher maskPhone = new MaskTextWatcher(editPhone, phone);
        editPhone.addTextChangedListener(maskPhone);

        //CEP mask
        SimpleMaskFormatter cep = new SimpleMaskFormatter("NNNNN-NNN");
        MaskTextWatcher maskCep = new MaskTextWatcher(editCep, cep);
        editCep.addTextChangedListener(maskCep);
    }
}