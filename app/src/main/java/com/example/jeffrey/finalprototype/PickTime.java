package com.example.jeffrey.finalprototype;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

/**
 * Created by Jeffrey on 2/16/2017.
 */

public class PickTime extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_time);

        Button pickTimeButton = (Button) findViewById(R.id.button);
        final TimePicker tPicker = (TimePicker) findViewById(R.id.timePicker);
        final Intent returnArrTime = new Intent(this, AddNewCommute.class);


        pickTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnArrTime.putExtra("hour", tPicker.getHour());
                returnArrTime.putExtra("minute", tPicker.getMinute());
                setResult(Activity.RESULT_OK, returnArrTime);
                finish();
            }
        });
    }
}
