package com.example.ashutosh.memorableplaces;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    LocationManager locationManager;
    LocationListener locationListener;
    private GoogleMap mMap;
    Marker marker;
    String stringToPass = "";
    ArrayList<String> Latlong = new ArrayList<String>();
    String openLatlong;
    LatLng placeLatlng;


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK,intent);
        intent.putExtra("sent",stringToPass);
        intent.putStringArrayListExtra("Latlong", Latlong);
        Log.i("msg",stringToPass);
        finish();
    }

    @Override //it will run the first time when permisiion would not be granted, when else block will not be run.
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        }

    }

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
        boolean flag = false;
        
        Intent mainIntent = getIntent();
        openLatlong = mainIntent.getStringExtra("openLatlong");
        
        if(openLatlong != null) {
            String[] placeLatlongs = openLatlong.split(" ");
            double lat = Double.parseDouble(placeLatlongs[0]);
            double lon = Double.parseDouble(placeLatlongs[1]);
            placeLatlng = new LatLng(lat, lon);
            flag = true;
        }

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                LatLng latLng = new LatLng(lat, lon);


                marker.setPosition(latLng);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        mMap = googleMap;
        LatLng temp = new LatLng(40,50);
        marker = mMap.addMarker(new MarkerOptions().position(temp).title("You are here"));
        if(flag) {
            Marker favMarker = mMap.addMarker(new MarkerOptions().position(placeLatlng).title("Fav place"));
            favMarker.setPosition(placeLatlng);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(placeLatlng));
        }



        // Add a marker in Sydney and move the camera
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 14, locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); //Location has all sort of information like area, altitude, latitude, longitude etc.
            LatLng pos = new LatLng(location.getLatitude(), location.getLongitude()); //Latlng has only latlong
            marker.setPosition(pos);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
            Log.i("msg","else");

        }




        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {
                Marker newMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("faviourite place"));

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                try{

                    List<Address> geocoderAddress = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if(geocoderAddress.get(0).getFeatureName() != null){
                        stringToPass += geocoderAddress.get(0).getFeatureName() + " ";
                    }
                    if(geocoderAddress.get(0).getSubAdminArea() != null){
                        stringToPass += geocoderAddress.get(0).getSubAdminArea() + " ";
                    }
                    if(geocoderAddress.get(0).getAdminArea() != null){
                        stringToPass += geocoderAddress.get(0).getAdminArea() + " ";
                    }
                    if(geocoderAddress.get(0).getCountryName() != null){
                        stringToPass += geocoderAddress.get(0).getCountryName() + ";";
                    }
                    Latlong.add(String.valueOf(latLng.latitude) + " " + String.valueOf(latLng.longitude));


                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        });


    }
}
