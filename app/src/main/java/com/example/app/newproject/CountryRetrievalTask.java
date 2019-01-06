package com.example.app.newproject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.example.app.newproject.RoutesActivity.API_COUNTRY_URL;
import static com.example.app.newproject.RoutesActivity.API_KEY_AIRPORT;


public class CountryRetrievalTask implements Runnable {

    private String country;
    private String codeIso2Country="";

    public CountryRetrievalTask(String country){
        this.country=country;
    }

    @Override
    public void run() {
        String response="";
        URL  url=null;
        try {
            url = new URL(API_COUNTRY_URL + API_KEY_AIRPORT + "&nameCountry=" + country);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection urlConnection=null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            response=stringBuilder.toString();
            bufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            urlConnection.disconnect();
        }


        String codeIso2Country="";
        String nameCountry="";
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                codeIso2Country = jsonObject.getString("codeIso2Country");
                nameCountry=jsonObject.getString("nameCountry");

            }
            this.codeIso2Country=codeIso2Country;
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getCodeIso2Country(){
        return codeIso2Country;
    }
}
