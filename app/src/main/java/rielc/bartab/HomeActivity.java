package rielc.bartab;

import android.database.CursorJoiner;
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


public class HomeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    TextView mLatitudeText, mLongitudeText, mAddress;
    double latit, longit;
    Boolean mAddressRequested = true;
    int search_code;

    protected Location mLastLocation;

    private GoogleApiClient mGoogleApiClient;
    private AddressResultReceiver mResultReceiver;

    //Handles the results returned by the service
    class AddressResultReceiver extends ResultReceiver{
        public AddressResultReceiver(Handler handler)
        {
            super(handler);
        }

        protected void onReceiveResult(int resultCode, Bundle resultData)
        {
            //Sets the address string with either the result or error message
           mAddress.setText(resultData.getString(Constants.RESULT_DATA_KEY));
            if(resultCode == Constants.SUCCESS_RESULT)
            {
                showToast(getString(R.string.address_found));
            }
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mLatitudeText = (TextView)findViewById(R.id.latit);
        mLongitudeText = (TextView)findViewById(R.id.longit);
        mAddress = (TextView)findViewById(R.id.addr);

        //Listens for user to click "Find Me!" button
        Button find_me = (Button)findViewById(R.id.find_me_button);
        Button sign_out = (Button)findViewById(R.id.sign_out_button);
        Button search_dist = (Button)findViewById(R.id.search_dist);
        Button search_rate = (Button)findViewById(R.id.search_rate);
        Button search_wt = (Button)findViewById(R.id.search_wt);


        //builds GoogleApiClient and attempts to connect
        find_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                buildGoogleApiClient();
            }
        });

        //Returns user to Login screen
        //TODO: Actually log the user out of Google+, not just exit the app
        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        search_dist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if( !mLatitudeText.getText().equals("Latitude") && !mLongitudeText.getText().equals("Longitude") )
                {
                    search_code = 0;
                    runSearchIntent(search_code);

                }
                else
                {
                    //wait for the last location
                }
            }
        });

        search_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if( !mLatitudeText.getText().equals("Latitude") && !mLongitudeText.getText().equals("Longitude") )
                {
                    search_code = 1;
                    runSearchIntent(search_code);
                }
                else
                {
                    //wait for the last location
                }
            }
        });

        search_wt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if( !mLatitudeText.getText().equals("Latitude") && !mLongitudeText.getText().equals("Longitude") )
                {
                    search_code = 2;
                    runSearchIntent(search_code);
                }
                else
                {
                    //wait for the last location
                }
            }
        });
    }

    protected void runSearchIntent(int search_code)
    {
        Intent search_intent = new Intent(this, SearchActivity.class);
        search_intent.putExtra("LATITUDE_LOCATION", latit);
        search_intent.putExtra("LONGITUDE_LOCATION", longit);
        search_intent.putExtra("SEARCH_TYPE", search_code);
        startActivity(search_intent);
    }


    @Override
    public void onConnected(Bundle connectionHint)
    {
        //Gets last location using Latitude and Longitude
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation != null)
        {
            //outputs Latitude and Longitude
            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            latit = mLastLocation.getLatitude();
            longit = mLastLocation.getLongitude();

            if(!Geocoder.isPresent())
            {
                showToast(getString(R.string.no_geocoder_found));
                return;
            }

            if(mAddressRequested)
            {
                startIntentService();
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

    //Starts addressing intent service with information from the last known location
    protected void startIntentService()
    {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);

    }

    //Generic function for toasting messages to the UI
    //TODO: Global function?
    protected void showToast(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


    /*
    Function make sure GoogleApiClient is connected and will then start the service
    to find the user's location

    public void fetchAddressButtonHandler(View view) {
        // Only start the service to fetch the address if GoogleApiClient is
        // connected.
        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            startIntentService();
        }
        // If GoogleApiClient isn't connected, process the user's request by
        // setting mAddressRequested to true. Later, when GoogleApiClient connects,
        // launch the service to fetch the address. As far as the user is
        // concerned, pressing the Fetch Address button
        // immediately kicks off the process of getting the address.
        mAddressRequested = true;
        updateUIWidgets();
    }
    */

}

