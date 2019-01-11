package com.example.app.newproject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.example.app.newproject.AirportActivity.arrivals;

public class RoutesActivity extends AppCompatActivity {

    public static String API_KEY_AIRPORT="";
    public static final String API_AIRPORT_URL="https://aviation-edge.com/v2/public/airportDatabase?key=";
    public static final String API_COUNTRY_URL="https://aviation-edge.com/v2/public/countryDatabase?key=";


    private EditText sourceLocationText;
    private Button aiportBtn;
    private Button mapBtn;

    //airports name+codeIata to help for the retrieval of arrival airports
    static List<String> airports;
    //possible departure airports with latitude and logitude for map
    static LinkedList<String> detailAirports=new LinkedList<>();
    //arrival airports for a given departure location(for map - latitude+longitude)
    static ArrayList<LatLng> arrivalAirports=new ArrayList<>();
    //country Iso to retrieve possible departure airports
    private String countryIso="";
    static TextView responseView;

    private int arrivalsCounter=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        sourceLocationText = (EditText) findViewById(R.id.source_text);
        responseView= (TextView) findViewById(R.id.responseView);
        // progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Button queryButton = (Button) findViewById(R.id.list_button);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sourceLocation = sourceLocationText.getText().toString().trim();
                 new RetrieveFeedTask(getApplicationContext()).execute();
            }
        });

        aiportBtn=(Button) findViewById(R.id.ais);
        aiportBtn.setActivated(false);
        aiportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AirportRetrieveTask(getApplicationContext()).execute();
            }
        });

        mapBtn=(Button) findViewById(R.id.map_button);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(String s : arrivals){
                    new AirportNameRetrieveTask(getApplicationContext(),s).execute();
                }
            }
        });

    }

//    public void startMyActivity(){
//        //comment for no error
////        Intent intent = new Intent(this, RoutesListActivity.class);
////        startActivity(intent);
//    }

    class RetrieveFeedTask extends AsyncTask<Void,Void,String> {

        private Exception exception;

        private Context context;
        private String sourceLocation;

        private RetrieveFeedTask(Context context){
            this.context=context.getApplicationContext();
        }


        protected void onPreExecute() {
            //progressBar.setVisibility(View.VISIBLE);
            //responseView.setText("");
        }

        protected String doInBackground(Void... urls) {
            String sourceLocation = sourceLocationText.getText().toString();
//            String destinationLocation = destinationLocationText.getText().toString();
            // Do some validation here

            try {
                URL url= new URL(API_COUNTRY_URL + API_KEY_AIRPORT + "&nameCountry=" + sourceLocation);

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

            } catch (JSONException e) {
                e.printStackTrace();
            }

            countryIso=codeIso2Country;
            aiportBtn.setActivated(true);
        }
    }




    class AirportRetrieveTask extends AsyncTask<Void,Void,String> {

        private Exception exception;

        private Context context;


        private AirportRetrieveTask(Context context){
            this.context=context.getApplicationContext();
        }


        protected void onPreExecute() {
            //progressBar.setVisibility(View.VISIBLE);
            //responseView.setText("");
        }

        protected String doInBackground(Void... urls) {


            try {
                URL url= new URL(API_AIRPORT_URL + API_KEY_AIRPORT + "&codeIso2Country=" + countryIso);
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


            airports=new LinkedList<String>();
            String nameAirport="";
            String codeIata="";
            String departureLat="";
            String departureLong="";
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    codeIata = jsonObject.getString("codeIataAirport");
                    nameAirport=jsonObject.getString("nameAirport");
                    departureLat=jsonObject.getString("latitudeAirport");
                    departureLong=jsonObject.getString("longitudeAirport");

                    //airport name+codeIata to display in list
                    airports.add(nameAirport+"("+codeIata+")");
                    detailAirports.add(nameAirport+":"+departureLat+"/"+departureLong);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

              context.startActivity(new Intent(context, AirportActivity.class));
        }
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
            //progressBar.setVisibility(View.VISIBLE);
            //responseView.setText("");
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
            //progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
            responseView.setText(response);

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

           // if(arrivalsCounter==arrivals.size()-5) {
                context.startActivity(new Intent(context, MapsActivity.class));


          //  }
        }
    }
}
