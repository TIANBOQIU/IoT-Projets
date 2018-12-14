package com.example.tianbo.takefz10;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {
    TextView mServerTextView;
    TextView part1TextView;
    TextView part2TextView;
    TextView part3TextView;

    String ret1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        mServerTextView = (TextView) findViewById(R.id.mServerTextView);
        part1TextView = (TextView) findViewById(R.id.part1TextView);
        part2TextView = (TextView) findViewById(R.id.part2TextView);
        part3TextView = (TextView) findViewById(R.id.part3TextView);

        if(getIntent().hasExtra("ret1")) {
            ret1 = getIntent().getExtras().getString("ret1");
            String[] array = ret1.split("#");
            part1TextView.setText(array[1]);
            part2TextView.setText(array[2]);
            part3TextView.setText(array[3]);
            mServerTextView.setText(ret1);
        }
    }
}
