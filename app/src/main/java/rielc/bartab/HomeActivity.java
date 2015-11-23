package rielc.bartab;

import android.database.CursorJoiner;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class HomeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private String mAddress = "Conner's House";
    private double latit, longit;
    private Boolean mAddressRequested = true;
    private int search_code;
    private boolean googleConnected = false;
    private String username;

    protected Location mLastLocation;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent = getIntent();
        username = intent.getStringExtra("USER_NAME");

        //Establishes buttons for searches
        Button search_dist = (Button) findViewById(R.id.search_dist);
        Button search_rate = (Button) findViewById(R.id.search_rate);
        Button search_wt = (Button) findViewById(R.id.search_wt);
        Button submit_rev = (Button) findViewById(R.id.submit_review);

        showToast("Getting your location...");
        //Try to connect to Google API
        buildGoogleApiClient();

        //Set buttons to visible once Google has connected and location has been found
        search_dist.setVisibility(View.VISIBLE);
        search_rate.setVisibility(View.VISIBLE);
        search_wt.setVisibility(View.VISIBLE);

        mAddress = "Conner's House";

        //Wait for buttons to be clicked to initialize search
        search_dist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latit != 0.0d && longit != 0.0d) {
                    search_code = 0;
                    runSearchIntent(search_code);

                } else {
                    showToast("Waiting to get your location...");
                }
            }
        });

        search_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latit != 0.0d && longit != 0.0d) {
                    search_code = 1;
                    runSearchIntent(search_code);
                } else {
                    showToast("Waiting to get your location...");
                }
            }
        });

        search_wt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latit != 0.0d && longit != 0.0d) {
                    search_code = 2;
                    runSearchIntent(search_code);
                } else {
                    showToast("Waiting to get your location...");
                }
            }
        });

        submit_rev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAddress == null){
                    showToast("Sorry, cannot find address at this time.");
                }
                else {
                    runReviewIntent();
                }
            }
        });
    }

    protected void runSearchIntent(int search_code) {
        //setups intent for searches with user's lat, long and search type
        Intent search_intent = new Intent(this, SearchActivity.class);
        search_intent.putExtra("LATITUDE_LOCATION", latit);
        search_intent.putExtra("LONGITUDE_LOCATION", longit);
        search_intent.putExtra("SEARCH_TYPE", search_code);
        startActivity(search_intent);
    }

    protected void runReviewIntent(){
        Intent review_intent = new Intent(this, ReviewActivity.class);
        review_intent.putExtra("LATITUDE_LOCATION", latit);
        review_intent.putExtra("LONGITUDE_LOCATION", longit);
        review_intent.putExtra("USER_ADDRESS", mAddress);
        review_intent.putExtra("USER_NAME", username);
        startActivity(review_intent);
    }


    @Override
    public void onConnected(Bundle connectionHint)
    {
        //Gets last location using Latitude and Longitude
        googleConnected = true;
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation != null)
        {
            latit = mLastLocation.getLatitude();
            longit = mLastLocation.getLongitude();
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(latit, longit, 1);
                mAddress = addresses.get(0).getFeatureName();
            }
            catch(IOException io) {

            }


        }



    }

    @Override
    public void onConnectionSuspended(int cause)
    {
        //TODO: Attempt to resolve connection based off a suspended connection
    }

    @Override
    public void onConnectionFailed(ConnectionResult result)
    {
        //TODO: Attempt to resolve connection based off a failed connection
    }

    protected synchronized void buildGoogleApiClient()
    {
        //builds GoogleApiClient for locations
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    //Generic function for toasting messages to the UI
    //TODO: Global function?
    protected void showToast(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

}

