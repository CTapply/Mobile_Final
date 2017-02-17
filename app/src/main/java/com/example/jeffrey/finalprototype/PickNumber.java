package com.example.jeffrey.finalprototype;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeffrey on 2/16/2017.
 */

public class PickNumber extends AppCompatActivity {

    int val = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_number);

        NumberPicker numPicker = (NumberPicker) findViewById(R.id.numberPicker);
        numPicker.setMinValue(0);
        numPicker.setMaxValue(180);
        numPicker.setWrapSelectorWheel(true);
        numPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Display the newly selected number from picker
                val = newVal;
            }
        });

        final Intent returnPrepTime = new Intent(this, AddNewCommute.class);
        final Button pickPrepTimeButton = (Button) findViewById(R.id.button2);
        pickPrepTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnPrepTime.putExtra("mins", val);
                setResult(Activity.RESULT_OK, returnPrepTime);
                finish();
            }
        });


//        List<String> vals = new ArrayList<String>();
//        for(int i = 0; i < 36; i += 5){
//            vals.add(Integer.toString(i));
//        }
//        String[] values = new String[vals.size()];
//        vals.toArray(values);
//        numPicker.setDisplayedValues(values);

    }
}
