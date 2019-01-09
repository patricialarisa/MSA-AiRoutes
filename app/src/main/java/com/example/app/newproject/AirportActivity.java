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
import static com.example.app.newproject.RoutesActivity.arrivalAirports;
import static com.example.app.newproject.RoutesActivity.detailAirports;
import static com.example.app.newproject.RoutesActivity.responseView;

public class AirportActivity extends AppCompatActivity {

    static String airport="";
    static String departureIata="";
    static ArrayList<String> routes=new ArrayList<String>();
    static Set<String> arrivals = new HashSet<String>();
    static LatLng departureLatLng=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airport);

        // Get reference of widgets from XML layout
        final Spinner spinner = (Spinner) findViewById(R.id.spinner2);
        Button btn = (Button) findViewById(R.id.ok_button);
        Button routesBtn=(Button) findViewById(R.id.routes_button);
        final TextView r=(TextView) findViewById(R.id.responseAirport);

        // Initializing a String Array
//        String[] plants = new String[]{
//                "Black birch",
//                "European weeping birch"
//        };
//
//        final List<String> plantsList = new ArrayList<>(Arrays.asList(plants));
        final List<String> ai=RoutesActivity.airports;

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,ai);

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value=spinner.getSelectedItem().toString();
                airport=value.substring(0,value.indexOf('('));
                departureIata=value.substring(value.indexOf('(')+1,value.indexOf(')'));
//                int index=spinner.getSelectedItemPosition();
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

               // r.setText(arrivalAirports.get(0).latitude+"");
               // startActivity(new Intent(AirportActivity.this,RoutesActivity.class));
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

        private Exception exception;

        private Context context;
        private String sourceLocation;

        private RoutesTask(Context context){
            this.context=context.getApplicationContext();
        }


        protected void onPreExecute() {
            //progressBar.setVisibility(View.VISIBLE);
            //responseView.setText("");
        }

        protected String doInBackground(Void... urls) {
//            String sourceLocation = sourceLocationText.getText().toString();
//            String destinationLocation = destinationLocationText.getText().toString();
            // Do some validation here

            try {
                URL url=null;
//                String spinnerText=String.valueOf(spinner.getSelectedItem());
//                if("Airport".equals(spinnerText)){
//                    url = new URL(API_AIRPORT_URL + API_KEY_AIRPORT + "&nameAirport=" + sourceLocation);
//                }else if ("Country".equals(spinnerText)){
                url = new URL("http://aviation-edge.com/v2/public/routes?key="+API_KEY_AIRPORT+"&departureIata=" + departureIata);
//                }
                //URL url = new URL(API_AIRPORT_URL + API_KEY_AIRPORT + "&nameAirport=" + sourceLocation);
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
            //progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
            responseView.setText(response);



            String departureTime="";
            String arrivalIata="";
            String arrivalTime="";
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length()-5; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    departureTime=jsonObject.getString("departureTime");
                    arrivalIata=jsonObject.getString("arrivalIata");
                    arrivalTime=jsonObject.getString("arrivalTime");

                    routes.add(departureIata+" - "+arrivalIata);
                    arrivals.add(arrivalIata);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


              context.startActivity(new Intent(context, RoutesListActivity.class));
        }
    }
}
