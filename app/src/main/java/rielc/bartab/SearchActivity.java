package rielc.bartab;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import retrofit.Retrofit;

public class SearchActivity extends AppCompatActivity {

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

        /*
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("database url here")
                .build();
        */

    }

    protected void searchByDistance(double user_lat, double user_long)
    {
        //Build http request
        //send http request
        //process results
    }

    protected void searchByRating(double user_lat, double user_long)
    {

    }

    protected void searchByWaitTime(double user_lat, double user_long)
    {

    }

}
