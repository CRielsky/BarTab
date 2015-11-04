package rielc.bartab;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.location.Location;
import android.net.Uri;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class SearchActivity extends ListActivity {

    private final static String LOG_TAG = "SearchActivity";
    private double user_lat, user_long;
    private int sc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        user_lat = intent.getDoubleExtra("LATITUDE_LOCATION", user_lat);
        user_long = intent.getDoubleExtra("LONGITUDE_LOCATION", user_long);
        sc = intent.getIntExtra("SEARCH_TYPE", sc);

        String[] fromColumns = {"The Ruck \t 4.2 Miles", "Bombers Burrito Bar \t 6.9 Miles", "Browns Brewery \t 9.0 Miles"};
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_1, fromColumns);
        getListView().setAdapter(mAdapter);

    }

    protected String buildRequest() throws JSONException
    {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String searchJsonStr = null;

        try
        {
            Uri built_uri;

            if( sc == 0 )
            {
                built_uri = distanceRequest();
            }
            else if( sc == 1 )
            {
                built_uri = ratingRequest();
            }
            else
            {
                built_uri = waitTimeRequest();
            }
            URL url = new URL(built_uri.toString());
        }
        catch(IOException e)
        {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        }
        finally
        {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return null;
    }

    protected Uri distanceRequest()
    {
        Uri built_uri = Uri.parse(Constants.DATABASE_URL).buildUpon().build();
        return built_uri;
    }

    protected Uri ratingRequest()
    {
        Uri built_uri = Uri.parse(Constants.DATABASE_URL).buildUpon().build();
        return built_uri;
    }

    protected Uri waitTimeRequest()
    {
        Uri built_uri = Uri.parse(Constants.DATABASE_URL).buildUpon().build();
        return built_uri;
    }


    /*
    private double[] getMileages(double[] loc_latitudes, double[] loc_longitudes, int num_locs)
    {
        double distances[] = new double[num_locs];
        float results[] = new float[num_locs];

        for( int i = 0; i < num_locs; i++ )
        {
            android.location.Location.distanceBetween(user_lat, user_long, loc_latitudes[i], loc_longitudes[i], results);
            distances[i] = metersToMiles(results[0]);
        }

        return distances;
    }

    private double metersToMiles(float meters)
    {
        double result = meters * Constants.METERS_TO_MILES;
        return result;
    }
    */

}
