package com.example.minha_agenda.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minha_agenda.R;

import java.util.List;

//Class to create a custom adapter for contacts recycler view
public class AdapterContacts extends RecyclerView.Adapter<AdapterContacts.MyViewHolder> {

    private List<String> listNames; //List to storage contact names
    private List<String> listId;    //List to storage contact ids

    //Constructor to receive the parameters
    public AdapterContacts(List<String> list, List<String> listId) {
        this.listNames = list;
        this.listId = listId;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Inflate the view with the adapter
        View contactItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_contacts, parent, false);

        return new MyViewHolder(contactItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        //Set contact name and implicit Id for each contact
        String names = listNames.get(position);
        holder.name.setText(names);
        holder.id = listId.get(position);
    }

    @Override
    public int getItemCount() {
        return listNames.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;   //Contact name
        String id;      //Contact id

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.textContact);
        }
    }
}
