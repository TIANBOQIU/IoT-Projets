package com.example.tianbo.takefz10;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    Button mCaptureBtn;
    Button uploadBtn;
    ImageView mImageView;
    Uri image_uri;
    Bitmap bitmap;
    String encodedImage;
    TextView resTextView;
    private static String ServerADDR = "http://54.145.179.157:1021/";
    private static String ServerADDR2 = "http://54.145.179.157:1203/";
    String response_from_server;
    Button showBtn;
    ProgressBar calorieProgressBar;
    ProgressBar fatProgressBar;
    ProgressBar proteinProgressBar;
    int MAX_CALORIE_PER_DAY = 2000;
    int MAX_FAT_PER_DAY = 60; // 44-77
    int MAX_PROTEIN_PER_DAY = 50; // 56 man 46 woman

    TextView CaloriePercentageTextView;
    TextView FatPercentageTextView;
    TextView ProteinPercentageTextView;

    String food_name1, food_name2, food_name3;
    String calorie1, calorie2, calorie3;
    String fat1, fat2, fat3;
    String protein1, protein2, protein3;

    Button recommendBtn;
    String recommend_choice = "chicken";

    double sum_calorie = 0, sum_fat = 0, sum_protein = 0;

    String recommend_food_name = "apple";
    String recommend_calorie = "200";
    String recommend_fat = "60";
    String recommend_protein = "50";

    String recommend_strings;

    Spinner recommendSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MAX_CALORIE_PER_DAY = (int)getIntent().getExtras().getDouble("recommendedCaloire");
        MAX_FAT_PER_DAY = (int) getIntent().getExtras().getDouble("recommendedFat");
        MAX_PROTEIN_PER_DAY = (int) getIntent().getExtras().getDouble("recommendedProtein");

        uploadBtn = (Button) findViewById(R.id.uploadBtn);
        resTextView = (TextView) findViewById(R.id.resTextView);

        mImageView = findViewById(R.id.image_view);
        mCaptureBtn = findViewById(R.id.capture_image_btn);
        showBtn = (Button) findViewById(R.id.showBtn);

        calorieProgressBar = (ProgressBar) findViewById(R.id.calorieProgressBar);
        calorieProgressBar.setMax(MAX_CALORIE_PER_DAY);
        calorieProgressBar.setProgress(0);

        fatProgressBar = (ProgressBar) findViewById(R.id.fatProgressBar);
        fatProgressBar.setMax(MAX_FAT_PER_DAY);
        fatProgressBar.setProgress(0);

        proteinProgressBar = (ProgressBar) findViewById(R.id.proteinProgressBar);
        proteinProgressBar.setMax(MAX_PROTEIN_PER_DAY);
        proteinProgressBar.setProgress(0);

        CaloriePercentageTextView = (TextView) findViewById(R.id.CaloriePercentageTextView);
        FatPercentageTextView = (TextView) findViewById(R.id.FatPercentageTextView);
        ProteinPercentageTextView = (TextView) findViewById(R.id.ProteinPercentageTextView);

        recommendBtn = (Button) findViewById(R.id.recommendBtn);

        recommendSpinner = (Spinner) findViewById(R.id.recommendSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.food_categories,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recommendSpinner.setAdapter(adapter);
        recommendSpinner.setOnItemSelectedListener(this);



        mCaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if system os is >= marshmallow, request runtime permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED){
                        // permission not enabled, request it
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        // show popup to request permission
                        requestPermissions(permission, PERMISSION_CODE);
                    }
                    else {
                        // permission already granted
                        openCamera();

                    }
                }
                else {
                    // system os < marshmallow
                    openCamera();
                }
            }
        });
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject postData = new JSONObject();



                try {
                    postData.put("image", encodedImage);
                    ///postData.put("address", address.getText().toString());

                    new SendDeviceDetails(ServerADDR, postData.toString()).execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        recommendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject postData = new JSONObject();



                try {
                    postData.put("calorie", (MAX_CALORIE_PER_DAY - sum_calorie)+"");
                    postData.put("choice", recommend_choice);
                    //postData.put("address", address.getText().toString());

                    new SendDeviceDetails2(ServerADDR2, postData.toString()).execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(),SecondActivity.class);
                startIntent.putExtra("ret1",response_from_server);
                startActivity(startIntent);
            }
        });

    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        // Camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);

    }

    // handling permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // this method is called, when user presses Allow or Deny from Permission Request Popup
        switch (requestCode) {
            case PERMISSION_CODE:{
                if(grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    // permission from popup was granted
                    openCamera();
                }
                else {
                    // permission from popup was denied
                    Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // called when image was captured from camera

        if (resultCode == RESULT_OK) {
            // set the image captured to our ImageView
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //mImageView.setImageURI(image_uri);

            // set to protrait orientation

            try {
                ExifInterface ei = new ExifInterface(image_uri.getPath());
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        bitmap = rotateImage(bitmap, 90);
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        bitmap =  rotateImage(bitmap, 180);
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        bitmap = rotateImage(bitmap, 270);
                    default:
                        bitmap = bitmap;

                }

            } catch (IOException e) {
                e.printStackTrace();
            }


            // get encoded image
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Bitmap image = bitmap;
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);


            mImageView.setImageBitmap(bitmap);
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        recommend_choice = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class SendDeviceDetails extends AsyncTask<String, Void, String> {
        String address;
        String jsondata;
        public SendDeviceDetails(String address, String jsondata){
            this.address = address;
            this.jsondata = jsondata;
        }
        @Override
        protected String doInBackground(String... params) {
            Log.e("TAG", ServerADDR);
            Log.e("TAG", encodedImage);


            String data = "";

            HttpURLConnection httpURLConnection = null;
            try {

                httpURLConnection = (HttpURLConnection) new URL(address).openConnection();
                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                //wr.writeBytes("PostData=" + jsondata);
                wr.writeBytes(jsondata);
                wr.flush();
                wr.close();

                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);

                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    data += current;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
            Log.e("TAG", data);
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("TAG", result); // this is expecting a response code to be sent from your server upon receiving the POST data
            //response_from_server = result;
            //calorieProgressBar.setMax(MAX_CALORIE_PER_DAY);
            String[] array = result.split("#");
            sum_calorie += Double.parseDouble(array[2]) + Double.parseDouble(array[6]) + Double.parseDouble(array[10]);
            sum_fat += Double.parseDouble(array[3]) + Double.parseDouble(array[7]) + Double.parseDouble(array[11]);
            sum_protein += Double.parseDouble(array[4]) + Double.parseDouble(array[8]) + Double.parseDouble(array[12]);

            //calorieProgressBar.setProgress(600);
            calorieProgressBar.setProgress((int)sum_calorie);
            fatProgressBar.setProgress((int)sum_fat);
            proteinProgressBar.setProgress((int)sum_protein);
            String result_formatted = "Complete!#"+array[1] + ":\n" + "Cal:" + array[2] + "\nfat(g):" + array[3] + "\nprotein(g):" + array[4] + "#"
                    +array[5] + ":\n" + "Cal:" + array[6] + "\nfat(g):" + array[7] + "\nprotein(g):" + array[8] + "#"
                    +array[9] + ":\n" + "Cal:" + array[10] + "\nfat(g):" + array[11] + "\nprotein(g):" + array[12];
            response_from_server = result_formatted;
            Double percentageCalorieIntake = sum_calorie / MAX_CALORIE_PER_DAY * 100;
            Double percentageFatIntake = sum_fat / MAX_FAT_PER_DAY * 100;
            Double percentageProteinIntake = sum_protein / MAX_PROTEIN_PER_DAY * 100;
            CaloriePercentageTextView.setText(String.format("%.1f",percentageCalorieIntake) + "%");
            FatPercentageTextView.setText(String.format("%.1f",percentageFatIntake) + "%");
            ProteinPercentageTextView.setText(String.format("%.1f",percentageProteinIntake)+"%");


            resTextView.setText(array[0]);
        }
    }

    private class SendDeviceDetails2 extends AsyncTask<String, Void, String> {
        String address;
        String jsondata;
        public SendDeviceDetails2(String address, String jsondata){
            this.address = address;
            this.jsondata = jsondata;
        }
        @Override
        protected String doInBackground(String... params) {
            Log.e("TAG", ServerADDR2);
            //Log.e("TAG", MAX_CALORIE_PER_DAY);


            String data = "";

            HttpURLConnection httpURLConnection = null;
            try {

                httpURLConnection = (HttpURLConnection) new URL(address).openConnection();
                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                //wr.writeBytes("PostData=" + jsondata);
                wr.writeBytes(jsondata);
                wr.flush();
                wr.close();

                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);

                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    data += current;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
            Log.e("TAG", data);
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("TAG", result); // this is expecting a response code to be sent from your server upon receiving the POST data
            //response_from_server = result;
            recommend_strings = result;
            Intent intent4 = new Intent(getApplicationContext(),ForthActivity.class);
            intent4.putExtra("ret1",recommend_strings);
            startActivity(intent4);





            //resTextView.setText(recommend_strings);
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        return rotatedImg;
    }
}
