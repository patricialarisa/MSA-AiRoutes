package com.example.app.newproject;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.example.app.newproject.AirportActivity.arrivals;
import static com.example.app.newproject.RoutesActivity.API_AIRPORT_URL;
import static com.example.app.newproject.RoutesActivity.API_KEY_AIRPORT;
import static com.example.app.newproject.RoutesActivity.arrivalAirports;
import static com.example.app.newproject.RoutesActivity.responseView;


public class RoutesListActivity extends ListActivity {

    private Button mapBtn;
    private int arrivalsCounter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes_list);

        ArrayList<String> routes=getIntent().getStringArrayListExtra("routes");

        // Create the ArrayAdapter use the item row layout and the list data.
        ArrayAdapter<String> listDataAdapter = new ArrayAdapter<String>(this, R.layout.activity_routes_list_row, R.id.listRowTextView, routes);

        // Set this adapter to inner ListView object.
        this.setListAdapter(listDataAdapter);

        mapBtn=(Button) findViewById(R.id.map_button);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(String s : arrivals){
                    new RoutesListActivity.AirportNameRetrieveTask(getApplicationContext(),s).execute();
                }
            }
        });

    }



    class AirportNameRetrieveTask extends AsyncTask<Void,Void,String> {

        private Exception exception;
        private Context context;
        private String iata;


        private AirportNameRetrieveTask(Context context,String iata){
            this.context=context.getApplicationContext();
            this.iata=iata;
            arrivalsCounter++;
        }


        protected void onPreExecute() {
            //
        }

        protected String doInBackground(Void... urls) {

            try {
                URL url=new URL(API_AIRPORT_URL + API_KEY_AIRPORT + "&codeIataAirport=" + iata);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }


            String latitudeAirport="";
            String longitudeAirport="";
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    latitudeAirport = jsonObject.getString("latitudeAirport");
                    longitudeAirport=jsonObject.getString("longitudeAirport");

                    arrivalAirports.add(new LatLng(Double.parseDouble(latitudeAirport),Double.parseDouble(longitudeAirport)));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


             if(arrivalsCounter==arrivals.size()) {
                context.startActivity(new Intent(context, MapsActivity.class));
              }
        }
    }



}
