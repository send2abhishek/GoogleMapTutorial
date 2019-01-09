package com.example.abhishekaryan.googlemaptutorial;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        OnCompleteListener, GoogleApiClient.OnConnectionFailedListener {


    private static final float DEFAULT_ZOOM = 15f;
    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private AutoCompleteTextView srchText;
    private ImageView gpsIcon;
    private PlaceAutocompleteAdapter adapter;
    private GoogleApiClient mGoogleApiClient;
    protected GeoDataClient mGeoDataClient;
    PlaceInfo placeInfo;
    private static final LatLngBounds LAT_LNG_BOUNDS=new LatLngBounds(
            new LatLng(-40,-178),new LatLng(71,136)
    );


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);
        gpsIcon=(ImageView)findViewById(R.id.activity_map_gps_icon);
        gpsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceCurrentLocation();
            }
        });
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this);
        // Construct a PlaceDetectionClient.

        adapter=new PlaceAutocompleteAdapter(this,mGeoDataClient,LAT_LNG_BOUNDS,null);

        srchText=(AutoCompleteTextView)findViewById(R.id.activity_map_edit_srch_text);
        srchText.setAdapter(adapter);
        srchText.setOnItemClickListener(mautoCompleteListener);
        srchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER){

                    locateYourLocation();
                    return true;
                }
                return false;
            }
        });

        intiMap();

    }

    private void locateYourLocation() {

        String search=srchText.getText().toString();
        Geocoder geocoder=new Geocoder(this);
        List<Address> resultContainer=new ArrayList<>();

        try {
            resultContainer=geocoder.getFromLocationName(search,1);
            if(resultContainer.size() > 0) {
                Address address = resultContainer.get(0);

                moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));

                Log.d("AbhishekLocation",address.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        hideKeyBord();

    }

    private void intiMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;

        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        getDeviceCurrentLocation();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this,"Please provide the permission of locations ",Toast.LENGTH_SHORT).show();

            return;
        }
        else {
            this.googleMap.setMyLocationEnabled(true);
            googleMap.setMapType(MAP_TYPE_NORMAL);
        }
    }

    private void getDeviceCurrentLocation() {

        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this);

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                ==PackageManager.PERMISSION_GRANTED) {
            try {
                Task location = fusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(this,"Current location not avaliable ",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onComplete(@NonNull Task task) {






        if(task.isSuccessful()){

            Location location=(Location)task.getResult();
            moveCamera(new LatLng(location.getLatitude(),location.getLongitude()),DEFAULT_ZOOM,"MyLocation");

    }
    else {

            Toast.makeText(this,"Unable to get current locations",Toast.LENGTH_SHORT).show();
        }
}

    private void moveCamera(LatLng latLng, float defaultZoom,String title) {


        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,defaultZoom));

        MarkerOptions options=new MarkerOptions().position(latLng).title(title);

        if(!title.equals("MyLocation")){
            googleMap.addMarker(options);
        }

        hideKeyBord();

    }

    private void hideKeyBord() {

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }



    /*
     -----------------------Google place API autocomplete suggestions ----------------------
     */


    private AdapterView.OnItemClickListener mautoCompleteListener= new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            hideKeyBord();
            final AutocompletePrediction item=adapter.getItem(position);
            final String placeId=item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult=Places.GeoDataApi.getPlaceById(mGoogleApiClient,placeId);
            placeResult.setResultCallback(mUdpdatePlaceDetailCallback);

        }
    };


    private ResultCallback<PlaceBuffer> mUdpdatePlaceDetailCallback=new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {

            if(!places.getStatus().isSuccess()){

                Toast.makeText(MapActivity.this,"LOcation not found",Toast.LENGTH_SHORT).show();
                places.release();
                return;
            }

            final Place place=places.get(0);

            try {
                placeInfo=new PlaceInfo();
                placeInfo.setName(place.getName().toString());
            } catch (Exception e) {
                e.printStackTrace();

                Toast.makeText(MapActivity.this,"LOcation Exception"+ e.getMessage(),Toast.LENGTH_SHORT).show();
            }


            moveCamera(new LatLng(place.getViewport().getCenter().latitude,
                    place.getViewport().getCenter().longitude
                    ),DEFAULT_ZOOM,placeInfo.getName());

            places.release();


        }
    };



}
