package rielc.bartab;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

public class SearchActivity extends ListActivity {

    private final static String LOG_TAG = "SearchActivity";
    private double user_lat, user_long;
    private String user_lat_str;
    private String user_long_str;
    private String mile_length = "5";
    private int sc;
    private ArrayAdapter<String> mSearchAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Get information from Home screen intent
        Intent intent = getIntent();
        user_lat = intent.getDoubleExtra("LATITUDE_LOCATION", user_lat);
        user_long = intent.getDoubleExtra("LONGITUDE_LOCATION", user_long);
        sc = intent.getIntExtra("SEARCH_TYPE", sc);

        user_lat_str = String.valueOf(user_lat);
        user_long_str = String.valueOf(user_long);

        //Call thread to query database
        mSearchAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        FetchSearchData searchResults = new FetchSearchData();
        searchResults.execute();
        getListView().setAdapter(mSearchAdapter);

    }

    //Class for querying database and parsing the result returned
    public class FetchSearchData extends AsyncTask<String, Void, String[]>
    {
        @Override
        protected String[] doInBackground(String... params)
        {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String searchJsonStr = null;

            try
            {
                //build query based off search type
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

                //setup url connection and establish request type
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                searchJsonStr = buffer.toString();
            }
            catch(IOException e)
            {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the search data, there's no point in attemping
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
            try {
                return getSearchDataFromJson(searchJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            System.out.println("Here");
            return null;
        }


        private String[] getSearchDataFromJson(String searchJsonStr)
                throws JSONException
        {
            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LOCATIONS = "locations";
            final String OWM_NAME = "location_name";
            final String OWM_LATITUDE = "latitude";
            final String OWM_LONGITUDE = "longitude";
            final String OWM_WAITTIME = "avg_wait_time";
            final String OWM_ATMOSPHERE = "avg_atmosphere";
            final String OWM_DISTANCE = "dist";

            JSONObject searchJson = new JSONObject(searchJsonStr);
            JSONArray searchArray = searchJson.getJSONArray(OWM_LOCATIONS);

            String[] resultStrs = new String[searchArray.length()];
            for(int i = 0; i < searchArray.length(); i++)
            {
                //extract relevant JSON information
                String loc_name = searchArray.getJSONObject(i).getString(OWM_NAME);
                double latit = searchArray.getJSONObject(i).getDouble(OWM_LATITUDE);
                double longit = searchArray.getJSONObject(i).getDouble(OWM_LONGITUDE);
                int avg_wt = searchArray.getJSONObject(i).getInt(OWM_WAITTIME);
                int avg_at = searchArray.getJSONObject(i).getInt(OWM_ATMOSPHERE);

                //format list view entries for search results pages
                if( sc == 0 )
                {
                    double dist = searchArray.getJSONObject(i).getDouble(OWM_DISTANCE);
                    dist = Math.round(dist * 100);
                    dist = dist/100;
                    String add_str = loc_name + "\t\t" + Double.toString(dist) + " miles";
                    resultStrs[i] = add_str;
                }
                else if( sc == 1 )
                {
                    String add_str = loc_name + "\t\t" + Integer.toString(avg_at) + "/5";
                    resultStrs[i] = add_str;
                }
                else if( sc == 2 )
                {
                    String add_str = loc_name + "\t\t" + Integer.toString(avg_wt) + " minutes";
                    resultStrs[i] = add_str;
                }
                else
                {
                    resultStrs[i] = "  ";
                }
            }
            return resultStrs;
        }

        protected Uri distanceRequest()
        {
            Uri built_uri = Uri.parse(Constants.SEARCH_DIST_URL).buildUpon()
                    .appendQueryParameter("lat", user_lat_str)
                    .appendQueryParameter("lon",user_long_str)
                    .appendQueryParameter("len", mile_length)
                    .build();
            return built_uri;
        }

        protected Uri ratingRequest()
        {
            Uri built_uri = Uri.parse(Constants.SEARCH_RATE_URL).buildUpon()
                    .appendQueryParameter("lat", user_lat_str)
                    .appendQueryParameter("lon", user_long_str)
                    .appendQueryParameter("len", mile_length)
                    .build();
            return built_uri;
        }

        protected Uri waitTimeRequest()
        {
            Uri built_uri = Uri.parse(Constants.SEARCH_WT_URL).buildUpon()
                    .appendQueryParameter("lat", user_lat_str)
                    .appendQueryParameter("lon", user_long_str)
                    .appendQueryParameter("len", mile_length)
                    .build();
            return built_uri;
        }

        @Override
        protected void onPostExecute(String[] result)
        {
            if (result != null)
            {
                for(String resultlet : result)
                {
                    mSearchAdapter.add(resultlet);
                }
            }
        }

    }
}
