package com.example.minha_agenda.service;

import android.os.AsyncTask;

import com.example.minha_agenda.model.CEP;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class HTTPService extends AsyncTask<Void, Void, CEP> {

    private String cep;

    public HTTPService(String cep) {
        this.cep = cep;
    }

    @Override
    protected CEP doInBackground(Void... voids) {
        StringBuilder answer = new StringBuilder();

        try {
            URL url = new URL("https://viacep.com.br/ws/" + this.cep + "/json/");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(5000);
            connection.connect();

            Scanner scanner = new Scanner(url.openStream());

            while (scanner.hasNext()){
                answer.append(scanner.nextLine());
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Gson().fromJson(answer.toString(), CEP.class);
    }
}
