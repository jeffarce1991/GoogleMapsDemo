package com.jeff.googlemaps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    int longitude = -1;
    int latitude = -1;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MapsActivity.this, "PERMISSION GRANTED", Toast.LENGTH_SHORT).show();

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }

        }
    }

    @SuppressLint("MissingPermission")
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
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);



        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("Location", location.toString());
                Toast.makeText(MapsActivity.this, "lat: "+location.getLatitude() +", long: "+location.getLongitude(), Toast.LENGTH_SHORT).show();
                LatLng yourLocation = new LatLng(location.getLatitude(),  location.getLongitude());

                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(yourLocation).title("Your here!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(yourLocation,20));
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                try {

                    List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
                    if (listAddresses != null && listAddresses.size() >0){

                        Log.i("PlaceInfo",listAddresses.get(0).toString());

                    }
                } catch (IOException e) {

                    e.printStackTrace();

                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        // If device is running SDK < 23

        if (Build.VERSION.SDK_INT < 23){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        }else{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                /// ask for permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }else {
                /// permission granted already
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (lastKnownLocation != null) {
                    LatLng yourLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(yourLocation).title("Your here!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(yourLocation, 20));

                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                    try {

                        List<Address> listAddresses = geocoder.getFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), 1);
                        if (listAddresses != null && listAddresses.size() > 0) {
                            Log.i("PlaceInfo", listAddresses.get(0).toString());

                            String address = "";

                            if (listAddresses.get(0).getSubThoroughfare() != null) {
                                address += listAddresses.get(0).getSubThoroughfare() + " ";
                            }
                            if (listAddresses.get(0).getThoroughfare() != null) {
                                address += listAddresses.get(0).getThoroughfare() + ", ";
                            }
                            if (listAddresses.get(0).getLocality() != null) {
                                address += listAddresses.get(0).getLocality() + ", ";
                            }
                            if (listAddresses.get(0).getPostalCode() != null) {
                                address += listAddresses.get(0).getPostalCode() + ", ";
                            }
                            if (listAddresses.get(0).getCountryName() != null) {
                                address += listAddresses.get(0).getCountryName();
                            }
                            Toast.makeText(this, address, Toast.LENGTH_SHORT).show();

                        }
                    } catch (IOException e) {

                        e.printStackTrace();

                    }


//                    Toast.makeText(this, "True", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Device Location has no signal.", Toast.LENGTH_SHORT).show();


                }
            }


            }
        // Add a marker in Sydney and move the camera
        //LatLng upraxis = new LatLng(14.558251,  121.018505);
        //mMap.addMarker(new MarkerOptions().position(upraxis).title("I work here!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(upraxis,17));


        }
    }
