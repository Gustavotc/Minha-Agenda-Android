package com.example.minha_agenda.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import com.example.minha_agenda.R;
import com.example.minha_agenda.config.RecyclerItemClickListener;
import com.example.minha_agenda.adapter.AdapterContacts;
import com.example.minha_agenda.config.FirebaseConfig;
import com.example.minha_agenda.model.Contact;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

//Class to create the MainActivity
public class MainActivity extends AppCompatActivity {

    private FirebaseAuth userId;
    private RecyclerView recyclerContacts;
    private AutoCompleteTextView editSearch;
    private ArrayList<String> contactsList;
    private ArrayList<String> contactsIdList;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerContacts;
    private AdapterContacts adapter;

    @Override
    protected void onStart() {
        super.onStart();
        firebase.addValueEventListener( valueEventListenerContacts );

        ArrayAdapter<String> adapterNames = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, contactsList);

        editSearch.setAdapter(adapterNames);
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

        //Find components
        userId           = FirebaseConfig.getFirebaseAuth();
        recyclerContacts = findViewById(R.id.recyclerContacts);
        editSearch       = findViewById(R.id.editSearch);

        contactsList = new ArrayList<>();
        contactsIdList = new ArrayList<>();

        //Adapter configs
         adapter = new AdapterContacts( contactsList, contactsIdList );

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchContact(editSearch.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Get Database Contacts
        userId = FirebaseConfig.getFirebaseAuth();

        firebase = FirebaseConfig.getFirebase()
                .child("contacts")
                .child(userId.getUid());

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
                contactsIdList.clear();

                //List all Contacts
                for (DataSnapshot data: snapshot.getChildren()) {
                    Contact contact = data.getValue( Contact.class );
                    contactsIdList.add(data.getKey());
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
                            String id = contactsIdList.get( position );
                            Intent intent = new Intent(MainActivity.this, ContactInfoActivity.class);
                            intent.putExtra("id", id);
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
            case R.id.newOrderOption :
                orderByName();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //Function to disconnect user
    private void signOut() {
        userId.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    //Function to search contact by name
    private void searchContact(String name) {

    firebase.orderByChild("name").startAt(name).endAt(name+"\uf8ff").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            contactsList.clear();
            contactsIdList.clear();

            //List results
            for (DataSnapshot data: snapshot.getChildren()) {
                Contact contact = data.getValue( Contact.class );
                contactsIdList.add(data.getKey());
                contactsList.add(contact.getName());
            }

            adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
        }
    });

    }

    //Function to order contacts list by name
    private void orderByName() {

        final Query contactOrder = firebase.orderByChild("name");
        contactOrder.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactsList.clear();
                contactsIdList.clear();

                //List Contacts
                for (DataSnapshot data: snapshot.getChildren()) {
                    Contact contact = data.getValue( Contact.class );
                    contactsIdList.add(data.getKey());
                    contactsList.add(contact.getName());
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}