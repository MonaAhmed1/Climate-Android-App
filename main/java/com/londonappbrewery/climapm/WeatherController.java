package com.londonappbrewery.climapm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

import cz.msebera.android.httpclient.Header;


public class WeatherController extends AppCompatActivity {

    // Constants:
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    // App ID to use OpenWeather data
    final String APP_ID = "490dda6e150d90808d3a6da529f969d1";
    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 5000;
    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 1000;
    final int REQUEST_CODE=123;


    // TODO: Set LOCATION_PROVIDER here:
   String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;



    // Member Variables:
    TextView mCityLabel;
    ImageView mWeatherImage;
    TextView mTemperatureLabel;
    ImageButton changeCityButton;

    // TODO: Declare a LocationManager and a LocationListener here:
    LocationManager mLocationManager;
    LocationListener mLocationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);

        // Linking the elements in the layout to Java code
        mCityLabel = (TextView) findViewById(R.id.locationTV);
        mWeatherImage = (ImageView) findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel = (TextView) findViewById(R.id.tempTV);
        changeCityButton = (ImageButton) findViewById(R.id.changeCityButton);



        // TODO: Add an OnClickListener to the changeCityButton here:
        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(WeatherController.this,ChangeCityController.class);
               // startActivityForResult(intent,NEW_CITY_CODE);
                startActivity(intent);
            }
        });

    }


    // TODO: Add onResume() here:
@Override
    public void onResume(){
        super.onResume();
    Log.d("clima", "onResume:called ");
    Intent myIntent=getIntent();
    String city=myIntent.getStringExtra("City");
    if(city!=null){
    getWeatherForNewCity(city);}
    else {
        Log.d("clima", "onResume: getting weather for current location");
        getWeatherForCurrentLocation();
    }
  }


    // TODO: Add getWeatherForNewCity(String city) here:
    public void getWeatherForNewCity(String city){
       RequestParams params=new RequestParams();
       params.put("q",city);
       params.put("appid",APP_ID);
       letsDoSomeNetworking(params);
    }


    // TODO: Add getWeatherForCurrentLocation() here:
    public void getWeatherForCurrentLocation(){
        mLocationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        mLocationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
            Log.d("clima","onLocationChanged() callback");
            String longitut=String.valueOf(location.getLongitude());
            String Latitude=String.valueOf(location.getLatitude());

                Log.d("clima","longitut is:"+longitut);
                Log.d("clima","latitude is: "+Latitude);
                RequestParams params=new RequestParams();
                params.put("lat",Latitude);
                params.put("lon",longitut);
                params.put("appid",APP_ID);
                letsDoSomeNetworking(params);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d("clima","onProviderDisabled() callback");
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
        }
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER,MIN_TIME,MIN_DISTANCE,mLocationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                Log.d("clima","onRequestPermissionsResult: permission granted");
                getWeatherForCurrentLocation();
            }else Log.d("clima","onRequestPermissionsResult:permission denied");

        }

    }
    // TODO: Add letsDoSomeNetworking(RequestParams params) here:
    private void letsDoSomeNetworking(RequestParams params){
        AsyncHttpClient client =new AsyncHttpClient();
        client.get(WEATHER_URL,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                 Log.d("clima", "onSuccess: " + response.toString());

                WeatherDataModel result= WeatherDataModel.fromJson(response);
                 updateUI(result);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("clima", "onFailure: "+throwable.toString() );
                Log.d("clima", "status "+statusCode );
                Toast.makeText(WeatherController.this,"request failed",Toast.LENGTH_SHORT);
            }
        });
    }


    // TODO: Add updateUI() here:
    private void updateUI(WeatherDataModel weather){
        mCityLabel.setText(weather.getCity());
        mTemperatureLabel.setText(weather.getTemperature());
       int resourceID=getResources().getIdentifier(weather.getIconName(),"drawable",getPackageName());
        mWeatherImage.setImageResource(resourceID);
    }


    // TODO: Add onPause() here:


    @Override
    protected void onPause() {
        super.onPause();
        if (mLocationManager!=null){
            mLocationManager.removeUpdates(mLocationListener);
        }
    }
}
