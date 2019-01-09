package com.example.abhishekaryan.googlemaptutorial;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAP-DUBUG";
    private static final int ERROR_DIALOG_REQUEST=9001;
    private static final int PERMISSION_REQUEST_CODE = 101;
    private Button mapBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpPermissions();

        if(isServiceOk()){

            init();
        }
    }

    private void init() {


        mapBtn=(Button)findViewById(R.id.activity_main_map_btn);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent=new Intent(MainActivity.this,MapActivity.class);
                startActivity(intent);


            }
        });


    }


    public Boolean isServiceOk(){

        Log.d(TAG, "isServiceOk: checking google srvices version");

        int available= GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if(available== ConnectionResult.SUCCESS){

            Log.d(TAG, "isServiceOk: All okay google play service is working");

            return true;
        }

        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){

            Log.d(TAG, "isServiceOk: An error occured but we can fix it");

            Dialog dialog=GoogleApiAvailability.getInstance().getErrorDialog(this,available,ERROR_DIALOG_REQUEST);
            dialog.show();

        }

        else {

            Toast.makeText(this,"you can't make map request",Toast.LENGTH_SHORT).show();
        }

        return false;

    }


    private void setUpPermissions() {


        String[] permission=new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE
                };


        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                !=PackageManager.PERMISSION_GRANTED){


            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {


                Toast.makeText(this, "ACCESS_FINE_LOCATION " +
                        "permission need to give man at any cost", Toast.LENGTH_SHORT).show();
            }
            else {


                ActivityCompat.requestPermissions(this, permission, PERMISSION_REQUEST_CODE);
            }


        }

        else {

            Log.d(TAG, "checking Permision: All okay");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){


            case PERMISSION_REQUEST_CODE:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "ACCESS_FINE_LOCATION permission granted", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(this, "ACCESS_FINE_LOCATION permission denied", Toast.LENGTH_SHORT).show();
                }

                if (grantResults.length > 0
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "ACCESS_COARSE_LOCATION permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Toast.makeText(this, "ACCESS_COARSE_LOCATION permission denied", Toast.LENGTH_SHORT).show();
                }

                return;
        }
    }


}


