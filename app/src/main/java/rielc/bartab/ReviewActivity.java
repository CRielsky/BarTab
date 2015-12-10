package rielc.bartab;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class ReviewActivity extends AppCompatActivity {

    private double latit, longit;
    private int waitTime, rating;
    private String address, username;
    private boolean wtFin = false;
    private boolean rtFin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        //get data for review that was acquired in login activity and home activity
        Intent intent = getIntent();
        latit = intent.getDoubleExtra("LATITUDE_LOCATION", latit);
        longit = intent.getDoubleExtra("LONGITUDE_LOCATION", longit);
        address = intent.getStringExtra("USER_ADDRESS");
        username = intent.getStringExtra("USER_NAME");

        //create a drop down menu for restaurant wait times
        Spinner dropdown_wt = (Spinner)findViewById(R.id.wt_drop);
        //for now leave the increments in fields of 10 minutes each
        final String[] items_wt = new String[]{"0", "10", "20", "30", "40", "50", "60"};
        ArrayAdapter<String> adapter_wt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items_wt);
        dropdown_wt.setAdapter(adapter_wt);
        dropdown_wt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //on a time selected, set the wait time variable
                waitTime = Integer.parseInt(items_wt[position]);
                wtFin = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //on nothing selected, do nothing
            }
        });

        //create a drop down menu for restaurant ratings
        //allow ratings from 1 to 5 "stars"
        Spinner dropdown_rt = (Spinner)findViewById(R.id.rt_drop);
        final String[] items_rt = new String[]{"1", "2", "3", "4", "5"};
        ArrayAdapter<String> adapter_rt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items_rt);
        dropdown_rt.setAdapter(adapter_rt);
        dropdown_rt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //on a rating selected, set the rating variable
                rating = Integer.parseInt(items_rt[position]);
                rtFin = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //on nothing selected, do nothing
            }
        });

        //wait until a rating and waiting time has been selected and then post the review
        Button submit_rev = (Button) findViewById(R.id.submit);
        submit_rev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rtFin && wtFin ){
                    PostReview rev = new PostReview();
                    rev.execute();
                }
                else{
                    Toast.makeText(getBaseContext(), "You need to fill out the review first!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //class to run separate thread to PUT review
    class PostReview extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... voids) {
            int response = 0;
            HttpURLConnection urlConnection = null;
            OutputStream printout;
            JSONObject review = createJSONObj();

            try {
                //build url connection
                URL url = new URL(Constants.POST_REVIEW_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestMethod("PUT");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Host", "69.204.137.215:3000");
                urlConnection.connect();

                //change json object to bytes
                byte[] data = review.toString().getBytes("UTF-8");
                printout = urlConnection.getOutputStream();
                printout.write(data);
                printout.flush();
                printout.close();
                response = urlConnection.getResponseCode();
                System.out.println(response);

            }
            catch(MalformedURLException e) {
                e.printStackTrace();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            finally{
                //disconnect the connection at the end
                urlConnection.disconnect();
            }

            if( response == 200 ) {
                //if good response code, return this message
                return "Review was submitted successfully!";
            }
            else{
                return "Error submitting review, please try again!";
            }
        }

        protected JSONObject createJSONObj() {
            JSONObject newReview = new JSONObject();
            try{
                //create json object with review parameters
                newReview.put("user", username);
                newReview.put("location_name", address);
                newReview.put("latitude", latit);
                newReview.put("longitude", longit);
                newReview.put("wait_time", waitTime);
                newReview.put("atmosphere", rating);
            }
            catch(JSONException e) {
                e.printStackTrace();
            }
            return newReview;
        }

        protected void onPostExecute(String msg) {
            //alert user to the result of the review posting
            Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
        }
    }

}
