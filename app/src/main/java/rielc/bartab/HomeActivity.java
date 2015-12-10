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

    private String myAddress = "Conner's House";
    private double latit, longit;
    private int searchCode;
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
        Button searchDist = (Button) findViewById(R.id.search_dist);
        Button searchRate = (Button) findViewById(R.id.search_rate);
        Button searchWt = (Button) findViewById(R.id.search_wt);
        Button submitRev = (Button) findViewById(R.id.submit_review);

        showToast("Getting your location...");
        //Try to connect to Google API
        buildGoogleApiClient();

        //Set buttons to visible once Google has connected and location has been found
        searchDist.setVisibility(View.VISIBLE);
        searchRate.setVisibility(View.VISIBLE);
        searchWt.setVisibility(View.VISIBLE);

        myAddress = "Conner's House";

        //Wait for buttons to be clicked to initialize search
        searchDist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latit != 0.0d && longit != 0.0d) {
                    searchCode = 0;
                    runSearchIntent(searchCode);

                } else {
                    showToast("Waiting to get your location...");
                }
            }
        });

        searchRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latit != 0.0d && longit != 0.0d) {
                    searchCode = 1;
                    runSearchIntent(searchCode);
                } else {
                    showToast("Waiting to get your location...");
                }
            }
        });

        searchWt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latit != 0.0d && longit != 0.0d) {
                    searchCode = 2;
                    runSearchIntent(searchCode);
                } else {
                    showToast("Waiting to get your location...");
                }
            }
        });

        submitRev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myAddress == null){
                    showToast("Sorry, cannot find address at this time.");
                }
                else {
                    runReviewIntent();
                }
            }
        });
    }

    protected void runSearchIntent(int search_code) {
        //sets up intent for searches with user's lat, long and search type
        Intent searchIntent = new Intent(this, SearchActivity.class);
        searchIntent.putExtra("LATITUDE_LOCATION", latit);
        searchIntent.putExtra("LONGITUDE_LOCATION", longit);
        searchIntent.putExtra("SEARCH_TYPE", search_code);
        startActivity(searchIntent);
    }

    protected void runReviewIntent(){
        //sets up intent for reviews with user's lat, long and their account info
        Intent reviewIntent = new Intent(this, ReviewActivity.class);
        reviewIntent.putExtra("LATITUDE_LOCATION", latit);
        reviewIntent.putExtra("LONGITUDE_LOCATION", longit);
        reviewIntent.putExtra("USER_ADDRESS", myAddress);
        reviewIntent.putExtra("USER_NAME", username);
        startActivity(reviewIntent);
    }


    @Override
    public void onConnected(Bundle connectionHint) {
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
                //attempts to get the user's address
                addresses = geocoder.getFromLocation(latit, longit, 1);
                myAddress = addresses.get(0).getFeatureName();
            }
            catch(IOException io) {
                //TODO: Add exception handling
            }
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        //TODO: Attempt to resolve connection based off a suspended connection
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        //TODO: Attempt to resolve connection based off a failed connection
    }

    protected synchronized void buildGoogleApiClient() {
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

