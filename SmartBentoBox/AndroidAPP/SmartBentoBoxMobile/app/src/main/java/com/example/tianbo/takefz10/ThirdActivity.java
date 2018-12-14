package com.example.tianbo.takefz10;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ThirdActivity extends AppCompatActivity {

    EditText TesterWeightEditTest;
    EditText TesterHeightEditTest;
    EditText TesterAgeEditTest;
    EditText TesterGenderEditTest;

    TextView RecommendCalorieTextView;
    TextView RecommendFatTextView;
    TextView RecommendProteinTextView;

    double recommendedCalorie = 2000;
    double recommendedFat = 60;
    double recommendedProtein = 50;
    Button Calculate_Btn;
    Button Next_Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        TesterWeightEditTest = (EditText) findViewById(R.id.TesterWeightEditText);
        TesterHeightEditTest = (EditText) findViewById(R.id.TesterHeightEditText);
        TesterAgeEditTest = (EditText) findViewById(R.id.TesterAgeEditText);
        TesterGenderEditTest = (EditText) findViewById(R.id.TesterGenderEditText);

        RecommendCalorieTextView = (TextView) findViewById(R.id.RecommendCalorieTextView);
        RecommendFatTextView = (TextView) findViewById(R.id.RecommendFatTextView);
        RecommendProteinTextView = (TextView) findViewById(R.id.RecommendProteinTextView);
        Calculate_Btn = (Button) findViewById(R.id.Calculate_Btn);
        Next_Btn = (Button) findViewById(R.id.Next_Btn);


        Calculate_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double weight = Double.parseDouble(TesterWeightEditTest.getText().toString());
                double height = Double.parseDouble(TesterHeightEditTest.getText().toString());
                double age = Double.parseDouble(TesterAgeEditTest.getText().toString());
                int gender = Integer.parseInt(TesterGenderEditTest.getText().toString());

                if (gender == 0) {
                    recommendedCalorie = 66.5 + 13.8 * weight + 5.0 * height - 6.8 * age;
                }
                else {
                    recommendedCalorie = 655.1 + 9.6 * weight + 1.9 * height - 4.7 * age;
                }

                recommendedFat = recommendedCalorie * 0.3 / 9;
                recommendedProtein = weight * 0.9;
                RecommendCalorieTextView.setText(recommendedCalorie + "Cal");
                RecommendFatTextView.setText(recommendedFat + "g");
                RecommendProteinTextView.setText(recommendedProtein+"g");
            }

        });

        Next_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(),MainActivity.class);
                startIntent.putExtra("recommendedCaloire",recommendedCalorie);
                startIntent.putExtra("recommendedFat",recommendedFat);
                startIntent.putExtra("recommendedProtein",recommendedProtein);
                startActivity(startIntent);
            }
        });






    }
}
