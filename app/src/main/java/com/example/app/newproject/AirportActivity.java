package com.example.app.newproject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.app.newproject.RoutesActivity.API_KEY_AIRPORT;
import static com.example.app.newproject.RoutesActivity.detailAirports;
import static com.example.app.newproject.RoutesActivity.responseView;


public class AirportActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    //departure Iata to retrieve arrival airports
    private String departureIata="";
    //departure + arrival Iata to view in list activity
    private ArrayList<String> routes=new ArrayList<String>();
    //arrival Iata airports
    static Set<String> arrivals = new HashSet<String>();
    //departure point to set marker on map
    static LatLng departureLatLng=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airport);

        final Spinner spinner = (Spinner) findViewById(R.id.spinner2);
        Button btn = (Button) findViewById(R.id.ok_button);
        final Button routesBtn=(Button) findViewById(R.id.routes_button);
        routesBtn.setEnabled(false);
        progressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);

        final List<String> ai=RoutesActivity.airports;

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,ai);

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setSelection(0);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routesBtn.setEnabled(true);
                String value=spinner.getSelectedItem().toString();
                String airport=value.substring(0,value.indexOf('('));
                departureIata=value.substring(value.indexOf('(')+1,value.indexOf(')'));

                int counter=0;
                String departureLat="";
                String departureLong="";
                for(String s:detailAirports){

                    if(s.contains(airport)){
                        departureLat=detailAirports.get(counter).substring(s.indexOf(':')+1,s.indexOf('/'));
                        departureLong=detailAirports.get(counter).substring(s.indexOf('/')+1);
                        departureLatLng=new LatLng(Double.parseDouble(departureLat),Double.parseDouble(departureLong));
                    }
                    counter++;
                }

            }
        });

        routesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RoutesTask(getApplicationContext()).execute();
            }
        });
    }



    class RoutesTask extends AsyncTask<Void,Void,String> {

        private Context context;
        private String sourceLocation;

        private RoutesTask(Context context){
            this.context=context.getApplicationContext();
        }


        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(Void... urls) {

            try {
                URL url= new URL("http://aviation-edge.com/v2/public/routes?key="+API_KEY_AIRPORT+"&departureIata=" + departureIata);

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
            progressBar.setVisibility(View.GONE);

            String departureTime="";
            String arrivalIata="";
            String arrivalTime="";
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    departureTime=jsonObject.getString("departureTime");
                    arrivalIata=jsonObject.getString("arrivalIata");
                    arrivalTime=jsonObject.getString("arrivalTime");

                    routes.add(departureIata+"("+departureTime+")"+" - "+arrivalIata+"("+arrivalTime+")");
                    arrivals.add(arrivalIata);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent intent=new Intent(context, RoutesListActivity.class);
            intent.putStringArrayListExtra("routes",routes);
            context.startActivity(intent);
        }
    }
}
