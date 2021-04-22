package com.example.minha_agenda.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minha_agenda.R;

import java.util.List;

public class AdapterContacts extends RecyclerView.Adapter<AdapterContacts.MyViewHolder> {

    private List<String> listNames;

    public AdapterContacts(List<String> list) {
        this.listNames = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View contactItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_contacts, parent, false);

        return new MyViewHolder(contactItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String names = listNames.get(position);
        holder.name.setText(names);

    }

    @Override
    public int getItemCount() {
        return listNames.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.textContact);
        }
    }
}
