package com.example.chena2247.googlemaps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location myLocation;
    private LocationManager locationManager;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private boolean canGetLocation = false;
    private boolean getMyLocationOneTime;
    private double latitude, longitude;

    private static final long MIN_TIME_BW_UPDATES = 1000*5;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 0.0f;
    private static final int MY_LOC_ZOOM_FACTOR = 17;

    private EditText LocationSearch;

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

        // Add a marker in Sydney and move the camera
        LatLng palo = new LatLng(37, -122);
        mMap.addMarker(new MarkerOptions().position(palo).title("Birthplace"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(palo));
/*
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("MyMapsApp", "Failed FINE permission check");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("MyMapsApp", "Failed COARSE permission check");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }

        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                || (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            mMap.setMyLocationEnabled(true);
        }*/

        locationSearch = (EditText) findViewById(R.id.editText_addr);
        getMyLocationOneTime = false;
        getLocation();
    }
        //onSearch()
    public void onSearch(View v) {
        String location = locationSearch.getText().toString();

        List<Address> addressList = null;
        List<Address> addressListzip = null;

        //Use LocationManager for user location
        //Implement the LocationListener interface to setup location services
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = service.getBestProvider(criteria, false);

        Log.d("MyMapsApp", "onSearch: location = " + location);
        Log.d("MyMapsApp", "onSearch: provider = " + provider);

        LatLng userLocation = null;

        try {
            if (locationManager != null) {
                Log.d("MyMapsApp", "onSearch: locationManager is not null ");

                if (myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
                    userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    Log.d("MyMapsApp", "onSearch:using NETWORK_PROVIDER userLocation is: " + myLocation.getLatitude() + " " + myLocation.getLongitude());
                    Toast.makeText(this, "UserLoc" + myLocation.getLatitude() + myLocation.getLongitude(), Toast.LENGTH_SHORT);
                }
                else if (myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
                    Log.d("MyMapsApp", "onSearch: locationManager is not null ");
                    userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    Log.d("MyMapsApp", "onSearch:using NETWORK_PROVIDER userLocation is: " + myLocation.getLatitude() + " " + myLocation.getLongitude());
                    Toast.makeText(this, "UserLoc" + myLocation.getLatitude() + myLocation.getLongitude(), Toast.LENGTH_SHORT);
                }
                else {
                    Log.d("MyMapsApp", "onSearch: locationManager is null from getLastKnownLocation");
                }
            }
        } catch (SecurityException | IllegalArgumentException e) {
            Log.d("MyMapsApp", "onSearch: Exception getLastKnownLocation");
            Toast.makeText(this, "onSearch: Exception getLastKnownLocation", Toast.LENGTH_SHORT);
        }

        //Get the location if it exists
        if (!location.matches("")){
            Log.d("myMapsApp","onSearch:location field is populated");
            Geocoder geocoder = new Geocoder(this, Locale.US);

            try {
                //Get a list of the addresses
                addressList = geocoder.getFromLocationName(location, 100,
                        userLocation.latitude - (5.0 / 60),
                        userLocation.longitude - (5.0 / 60),
                        userLocation.latitude + (5.0 / 60),
                        userLocation.longitude + (5.0 / 60));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!addressList.isEmpty()) {
                Log.d("MyMapsApp", "onSearch: AddressList size is: " + addressList.size());
                for (int i=0; i<addressList.size(); i++) {
                    Address address = addressList.get(i);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    //Place a marker on the map
                    mMap.addMarker(new MarkerOptions().position(latLng).title(i + ": " + address.getSubThoroughfare() + address.getSubThoroughfare()));
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                }
            }
        }
    }

    public void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            //get GPS status, is providerEnabled returns true if user has enabled gps
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGPSEnabled)
                Log.d("myMapsApp","getLocation: GPS is enabled");
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isNetworkEnabled)
                Log.d("myMapsApp","getLocation: Network is enabled");

            if (!isGPSEnabled && !isNetworkEnabled) {
                Log.d("myMapsApp", "getLocation: no provider is enabled");
            }
            else {
                if (isNetworkEnabled) {
                    //Request location updates
                    if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                        && (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);
                }
                if (isGPSEnabled) {
                    //locationManager request for GPS_PROVIDER
                }
            }
        }
        catch (Exception e) {
            Log.d("myMapsApp","getLocation: exception in getLocation");
            e.printStackTrace();
        }
    }

    LocationListener locationListenerNetwork = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            dropAmarker (LocationManager.NETWORK_PROVIDER);

            if (getMyLocationOneTime == false) {
                locationManager.removeUpdates(this);
                locationManager.removeUpdates(locationListenerGps);
                getMyLocationOneTime = true;
            }
            else {

            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("myMapsApp","getLocation: exception in getLocation");

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    LocationListener locationListenerGps = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            dropAmarker (LocationManager.GPS_PROVIDER);

            //if doing one time remove updates to both gps and network
            //else do nothing
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //switch (i)
                //case LocationProvider.AVAILABLE:
                //printout log.d and, or toast message
                // break;
                //case LocationProvider.OUT_OF_SERVICE
                //enable network updates
                // breakl
                // case LocationProvider.TEMPORARILY_UNAVAILABLE;
                // enable both network and gps
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    public void dropAmarker (String provider) {
        //if(locationManager != null)
            // if (checkSelfPermission fails)
                // return
            // myLocation = locationManager.getLastKnownLocation(provider);
        //LatLng userLocation = null;
        // if(myLocation == null) print log or toast message
        // else
            //userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            //CameraUpdate update = CameraUpdateFactory.newLatLngZoom(userLocation, MY_LOC_ZOOM_FACTOR);
            //if (provider == LocationManager.GPS_PROVIDER)
                //add circle for the marker with 2 outer rings
                //mMap.addCircle(new CircleOptions())
                    //.center(userLocation)
                    //.radius(1)
                    //strokeColor(Color.RED)
                    //strokeWidth(2)
                    //fillColor(Color.RED)
            //else add circle for the marker with 2 outer rings (blue)
            //mMap.anim

    }

    public void changeView (View view) {

    }
}
