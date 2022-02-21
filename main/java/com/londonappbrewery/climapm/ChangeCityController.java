package com.londonappbrewery.climapm;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class ChangeCityController extends AppCompatActivity {
    //declare varaibles
    ImageButton mButton;
    TextView mTextView;
    EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_city_layout);
        mButton= (ImageButton) findViewById(R.id.backButton);
        mTextView= (TextView)findViewById(R.id.getWeatherTV);
        mEditText= findViewById(R.id.queryET);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String NewCity=mEditText.getText().toString();
                Intent NewcityIntent=new Intent(ChangeCityController.this,WeatherController.class);
                NewcityIntent.putExtra("City",NewCity);
               // setResult(Activity.RESULT_OK,NewcityIntent);
                startActivity(NewcityIntent);
                finish();
                return false;
            }
        });

    }
}
