package com.example.tianbo.takefz10;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.text.DecimalFormat;

public class ForthActivity extends AppCompatActivity {

    TextView FoodNameTextView;
    TextView CalTextView;
    TextView FatTextView;
    TextView ProteinTextView;

    String FoodName;
    String ImageURL;
    String Cal;
    String Fat;
    String Protein;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forth);
        FoodNameTextView = (TextView) findViewById(R.id.FoodNameTextView);
        CalTextView = (TextView) findViewById(R.id.CalTextView);
        FatTextView = (TextView) findViewById(R.id.FatTextView);
        ProteinTextView = (TextView) findViewById(R.id.ProteinTextView);

        String ret1 = getIntent().getExtras().getString("ret1");
        String[] array = ret1.split("#");
        FoodName = array[0];
        ImageURL = array[1];
        Cal = array[2];
        Cal = Cal.substring(0,5);

        Fat = array[3];
        Protein = array[4];

        FoodNameTextView.setText(FoodName);
        CalTextView.setText(Cal + "Cal");
        FatTextView.setText(Fat + "g");
        ProteinTextView.setText(Protein + "g");

        new DownloadImageTask((ImageView) findViewById(R.id.imageView1))
                .execute(ImageURL);



    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
