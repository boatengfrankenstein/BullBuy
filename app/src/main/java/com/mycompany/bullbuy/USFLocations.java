package com.mycompany.bullbuy;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DateFormat;
import java.util.Date;

public class USFLocations extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener, GoogleApiClient.OnConnectionFailedListener, AdapterView.OnItemSelectedListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Marker mMarker;
    private GoogleApiClient mGoogleApiClient;
    private LatLng destLocation;
    private Marker destMarker = null;
    private Polyline route = null;

    protected LocationRequest mLocationRequest;
    protected Location mCurrentLocation;
    protected String mLastUpdateTime;

    TextView mLastUpdateTimeTextView;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usflocations);

        setUpMapIfNeeded();

        mLastUpdateTimeTextView = (TextView)findViewById(R.id.lastUpdateTime);
        spinner = (Spinner) findViewById(R.id.spinner_MapsActivity);
        ArrayAdapter<CharSequence> myAdapter = ArrayAdapter.createFromResource(this, R.array.locations, android.R.layout.simple_spinner_item);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);
        spinner.setOnItemSelectedListener(this);

        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new
                GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        createLocationRequest();
        startLocationUpdates();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new
                LatLng(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient).getLatitude(),
                LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient).getLongitude()), 15));
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected void createLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        startLocationUpdates();
    }

    protected void startLocationUpdates(){
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    public void onLocationChanged(Location location){
        mCurrentLocation = location;
        LatLng latLng = new LatLng(mCurrentLocation.getLatitude(),
                mCurrentLocation.getLongitude());
        if(mMarker == null) {
            mMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("You"));
        } else {
            mMarker.setPosition(latLng);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new
                LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), mMap.getCameraPosition().zoom));

        if(route != null && destMarker != null){
            LatLngBounds bounds = LatLngBounds.builder().include(new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude()))
                    .include(destLocation).build();
            //Google Developers LatLngBuilder
            route.remove();
            route = mMap.addPolyline(new PolylineOptions().add(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()),
                    new LatLng(destLocation.latitude, destLocation.longitude)).width(4).color(Color.GREEN));
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, (int)mMap.getCameraPosition().zoom));

        }

        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateMap();
    }

    private void updateMap(){
        if(mCurrentLocation != null){
            mLastUpdateTimeTextView.setText(mLastUpdateTime);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(destMarker != null) {
            destMarker.remove();
            destMarker = null;
        }
        if (route != null) {
            route.remove();
            route = null;
        }

        switch(parent.getSelectedItem().toString()){
            case "None":
                return;
            case "Library":
                Toast.makeText(this, "Library", Toast.LENGTH_SHORT).show();
                destLocation = new LatLng(28.0595525,-82.412246415);
                break;
            case "Marshall Student Center":
                Toast.makeText(this, "MSC", Toast.LENGTH_SHORT).show();
                destLocation = new LatLng(28.0639342,-82.4134536);
                break;
            case "Interdisciplinary Sciences":
                Toast.makeText(this, "ISA", Toast.LENGTH_SHORT).show();
                destLocation = new LatLng(28.0615119,-82.4165242);
                break;
            case "Business":
                Toast.makeText(this, "BSN", Toast.LENGTH_SHORT).show();
                destLocation = new LatLng(28.0583084,-82.40996);
                break;
            case "Cooper Hall":
                Toast.makeText(this, "Cooper Hall", Toast.LENGTH_SHORT).show();
                destLocation = new LatLng(28.0595977,-82.410524);
                break;
            case "CIS":
                Toast.makeText(this, "CIS", Toast.LENGTH_SHORT).show();
                destLocation = new LatLng(28.0586563,-82.4110936);
                break;
            case "Rec Center":
                Toast.makeText(this, "Rec Center", Toast.LENGTH_SHORT).show();
                destLocation = new LatLng(28.0602013,-82.4076104);
                break;
            case "USF Bookstore":
                Toast.makeText(this, "USF Bookstore", Toast.LENGTH_SHORT).show();
                destLocation = new LatLng(28.0634588,-82.4125229);
                break;
        }
        destMarker =  mMap.addMarker(new MarkerOptions().position(destLocation).title("Destination"));
        route = mMap.addPolyline(new PolylineOptions().add(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()),
                new LatLng(destLocation.latitude, destLocation.longitude)).width(4).color(Color.GREEN));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
