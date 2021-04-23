package com.example.minha_agenda.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.minha_agenda.R;
import com.example.minha_agenda.config.FirebaseConfig;
import com.example.minha_agenda.model.CEP;
import com.example.minha_agenda.model.Contact;
import com.example.minha_agenda.service.HTTPService;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

//Class to add a new contact to the Agenda
public class NewContactActivity extends AppCompatActivity {

    private FirebaseAuth userAuth;
    private EditText editName;
    private EditText editPhone;
    private EditText editEmail;
    private EditText editCep;
    private EditText editAdress;
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
        editAdress = findViewById(R.id.editNewContactAdress);
        btnAdd = findViewById(R.id.btnNewContact);

        //Set edit text masks
        setMasks();

        editCep.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length() == 9) {
                    searchCep(s.toString());
                }
                else {
                    editAdress.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = editName.getText().toString();
                String phone = editPhone.getText().toString();
                String email = editEmail.getText().toString();
                String cep = editCep.getText().toString();
                String adress = editAdress.getText().toString();

                //Create a new contact object
                final Contact contact = new Contact();
                contact.setName(name);
                contact.setPhone(phone);
                contact.setEmail(email);
                contact.setCep(cep);
                contact.setAdress(adress);

                //Validations
                //Check if all informations were given
                if( !name.isEmpty() && !phone.isEmpty() && !email.isEmpty() && !cep.isEmpty() && !adress.isEmpty()) {

                    firebase = FirebaseConfig.getFirebase().child("contacts").child(userAuth.getUid()).push();

                    firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            //Case the contact name already exists
                            if( snapshot.getValue() != null ) {
                                Toast.makeText(NewContactActivity.this, "Contato já cadastrado", Toast.LENGTH_LONG).show();
                            }
                            else  { //Case it's a new contact name
                                firebase.setValue( contact ); //Save the new contact in the database
                                finish();
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

    private void searchCep(String cep) {

        cep = cep.replaceAll("(-)", "");

      HTTPService service = new HTTPService(cep);
        try {
            CEP info = service.execute().get();

            if(info.getLocalidade() != null){
                editAdress.setText( info.getLogradouro().equals("") ? info.getLocalidade() : info.getLogradouro()  );
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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