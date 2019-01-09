package com.example.app.newproject;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.app.newproject.AirportActivity.departureLatLng;
import static com.example.app.newproject.RoutesActivity.arrivalAirports;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        List<LatLng> ll=new ArrayList<LatLng>();
//        List<LatLng> ss=new ArrayList<LatLng>();
//
//        if(Geocoder.isPresent()){
//            try {
//                String location = "Cluj";
//                String anotherLocation="Sydney";
//                Geocoder gc = new Geocoder(this);
//                List<Address> addresses= gc.getFromLocationName(location, 5); // get the found Address Objects
//
//                 ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available
//                for(Address a : addresses){
//                    if(a.hasLatitude() && a.hasLongitude()){
//                        ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
//                    }
//                }
//
//                Geocoder gc2 = new Geocoder(this);
//                List<Address> addresses2= gc2.getFromLocationName(anotherLocation, 5); // get the found Address Objects
//
//                ss = new ArrayList<LatLng>(addresses2.size()); // A list to save the coordinates if they are available
//                for(Address a : addresses2){
//                    if(a.hasLatitude() && a.hasLongitude()){
//                        ss.add(new LatLng(a.getLatitude(), a.getLongitude()));
//                    }
//                }
//            } catch (IOException e) {
//                // handle the exception
//            }
//        }

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        LatLng sydney=ll.get(0);
//        LatLng b=ss.get(0);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));


        for(LatLng l:arrivalAirports) {
            Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                    .clickable(false)
                    .add(departureLatLng, l));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(departureLatLng));
    }
}
