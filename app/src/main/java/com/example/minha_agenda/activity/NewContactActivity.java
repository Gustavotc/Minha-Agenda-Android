package com.example.minha_agenda.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.minha_agenda.R;
import com.example.minha_agenda.config.FirebaseConfig;
import com.example.minha_agenda.model.CEP;
import com.example.minha_agenda.model.Contact;
import com.example.minha_agenda.config.HTTPService;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.concurrent.ExecutionException;

//Class to add a new contact to the Agenda
public class NewContactActivity extends AppCompatActivity {

    private FirebaseAuth userAuth;
    private EditText editName;
    private EditText editPhone;
    private EditText editEmail;
    private EditText editCep;
    private EditText editAdress;
    private Button btnNewPhone;
    private Button btnNewAddress;
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

        //Find components
        editName        = findViewById(R.id.editNewContactName);
        editPhone       = findViewById(R.id.editNewContactPhone);
        editEmail       = findViewById(R.id.editNewContactEmail);
        editCep         = findViewById(R.id.editNewContactCep);
        editAdress      = findViewById(R.id.editNewContactAdress);
        btnAdd          = findViewById(R.id.btnNewContact);
        btnNewPhone     = findViewById(R.id.btnNewContactPhone);
        btnNewAddress   = findViewById(R.id.btnNewContactAddress);


        //Todo multiple contacts and phones implementation
        btnNewPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layout = (LinearLayout) findViewById(R.id.newContactView);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,  LinearLayout.LayoutParams.WRAP_CONTENT );
                EditText newPhone = new EditText(getApplicationContext());
                newPhone.setLayoutParams(params);
                newPhone.setHint("Novo telefone");
                layout.addView(newPhone);
            }
        });

        btnNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layout = (LinearLayout) findViewById(R.id.newContactView);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,  LinearLayout.LayoutParams.WRAP_CONTENT );
                EditText newAddress = new EditText(getApplicationContext());
                newAddress.setLayoutParams(params);
                newAddress.setHint("Novo endereço");
                layout.addView(newAddress);
            }
        });


        //Set edit text masks
        setMasks();

        //Automatically search the address when user type 8 digits in CEP field
        editCep.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() == 9) { //Length must be 9 because of the mask extra symbol
                    searchCep(s.toString());
                } else {
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
                addContact();
            }
        });
    }

    //Function to add a new Contact
    private void addContact() {

        //Get user inputs
        String name = editName.getText().toString();
        String phone = editPhone.getText().toString();
        String email = editEmail.getText().toString();
        String cep = editCep.getText().toString();
        String address = editAdress.getText().toString();

        //Create a new contact object
        final Contact contact = new Contact();
        contact.setName(name);
        contact.setPhone(phone);
        contact.setEmail(email);
        contact.setCep(cep);
        contact.setAdress(address);

        //Validations
        //Check if all informations were given
        if (!name.isEmpty() && !phone.isEmpty() && !email.isEmpty() && !cep.isEmpty() && !address.isEmpty()) {

            //Creates a firebase instance based on the user and generates a new identifier for each contact
            firebase = FirebaseConfig.getFirebase().child("contacts").child(userAuth.getUid()).push();

            firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    //Case the contact name already exists
                    if (snapshot.getValue() != null) {
                        Toast.makeText(NewContactActivity.this, "Contato já cadastrado", Toast.LENGTH_LONG).show();
                    } else { //Case it's a new contact name
                        firebase.setValue(contact); //Save the new contact in the database
                        finish(); //Close the current activity
                        Toast.makeText(NewContactActivity.this, "Contato adicionado com sucesso", Toast.LENGTH_LONG).show();
                        showNotification(contact.getName()); //Notify user
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

        } else { //Case user didn't fill any text field
            Toast.makeText(NewContactActivity.this, "Preencha todos os campos", Toast.LENGTH_LONG).show();
        }
    }

    //Function to search for an address based on the provided CEP
    private void searchCep(String cep) {

        cep = cep.replaceAll("(-)", ""); //Remove unwanted characters

        //Creates a HTTP connection
        HTTPService service = new HTTPService(cep);
        try {
            CEP info = service.execute().get();

            //Shows the result case it exists
            if (info.getLocalidade() != null) {
                //If there's no street for the Cep, shows the the city
                editAdress.setText(info.getLogradouro().equals("") ? info.getLocalidade() : info.getLogradouro());
            }

        } catch (ExecutionException e) { //Treats exceptions
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Function to set masks in Phone and Cep editFields
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

    //Function to show a notification when a new contact is added
    private void showNotification(String name) {
        createNotificationChannel();

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "ID_01")
                .setSmallIcon(R.drawable.ic_person_24)
                .setContentTitle("Novo Contato")
                .setContentText("Você adicionou " + name + " aos seus contatos!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }

    //Function to create a notification channel to prevent errors
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Chanel_Name";
            String description = ("Channel_Description");
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("ID_01", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}