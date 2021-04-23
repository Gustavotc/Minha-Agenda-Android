package com.example.minha_agenda.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.minha_agenda.R;
import com.example.minha_agenda.RecyclerItemClickListener;
import com.example.minha_agenda.adapter.AdapterContacts;
import com.example.minha_agenda.config.FirebaseConfig;
import com.example.minha_agenda.model.Contact;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.EventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private RecyclerView recyclerContacts;
    private ArrayList<String> contactsList;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerContacts;

    @Override
    protected void onStart() {
        super.onStart();
        firebase.addValueEventListener( valueEventListenerContacts );
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebase.removeEventListener( valueEventListenerContacts );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseConfig.getFirebaseAuth();
        recyclerContacts = findViewById(R.id.recyclerContacts);

        contactsList = new ArrayList<>();

        //Adapter configs
        final AdapterContacts adapter = new AdapterContacts( contactsList );

        //Get Database Contacts
        auth = FirebaseConfig.getFirebaseAuth();

        firebase = FirebaseConfig.getFirebase()
                .child("contacts")
                .child(auth.getUid());

        //RecyclerView configs
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getApplicationContext() );
        recyclerContacts.setLayoutManager( layoutManager );
        recyclerContacts.setHasFixedSize(true);
        recyclerContacts.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerContacts.setAdapter( adapter );

        //Listener to recover User contacts
        valueEventListenerContacts = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                contactsList.clear();

                //List Contacts
                for (DataSnapshot data: snapshot.getChildren()) {
                    Contact contact = data.getValue( Contact.class );
                    contactsList.add(contact.getName());
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        //Click Event
        recyclerContacts.addOnItemTouchListener(
            new RecyclerItemClickListener(
                    getApplicationContext(),
                    recyclerContacts,
                    new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            String name = contactsList.get( position );

                            Intent intent = new Intent(MainActivity.this, ContactInfoActivity.class);
                            intent.putExtra("name", name);
                            startActivity(intent);
                        }

                        @Override
                        public void onLongItemClick(View view, int position) {

                        }

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        }
                    }
            )
        );

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
}