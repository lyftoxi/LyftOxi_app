package com.lyftoxi.lyftoxi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lyftoxi.lyftoxi.dao.Car;
import com.lyftoxi.lyftoxi.dao.Location;
import com.lyftoxi.lyftoxi.dao.Ride;
import com.lyftoxi.lyftoxi.dao.User;
import com.lyftoxi.lyftoxi.exception.LyftoxiClientBusinessException;
import com.lyftoxi.lyftoxi.exception.LyftoxiClientException;
import com.lyftoxi.lyftoxi.singletons.CurrentUserInfo;
import com.lyftoxi.lyftoxi.singletons.RideInfo;
import com.lyftoxi.lyftoxi.util.Constants;
import com.lyftoxi.lyftoxi.util.HttpRestUtil;
import com.lyftoxi.lyftoxi.util.ImageUtil;
import com.lyftoxi.lyftoxi.util.Util;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ConfirmRideActivity extends BaseActivity {

    private TextView confirmRideDetailsPrice, confirmRideDetailsSource, confirmRideDetailsDestination,
            confirmRideDetailsStartTime, confirmRideDetailsCarBrand, confirmRideDetailsCarModel, confirmRideDetailsCarNumber,
            confirmRideDetailsUserMessage, confirmRideDetailsCarColor,confirmRideDetailsPhone;

    private CheckBox confirmRideDetailsRadioAc,confirmRideDetailsRadioMusic,confirmRideDetailsRadioSmoking,
            confirmRideDetailsRadioAirbag, confirmRideDetailsLuggage;

    private SimpleDateFormat sdf =  new SimpleDateFormat(Constants.DATE_TIME_FORMAT_12HR_FORMAT);

    private Button confirmRideDetailsPublish;

    private ImageView confirmRideDetailsCarLogo;

    private ImageUtil imageUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_ride);
        boolean showConfirmButton = true;
        if(null!= getIntent().getExtras()) {
            Bundle b = getIntent().getExtras();
            showConfirmButton = b.getBoolean("showConfirmButton");
        }
        imageUtil = new ImageUtil();
        confirmRideDetailsPrice = (TextView) findViewById(R.id.confirmRideDetailsPrice);
        confirmRideDetailsSource = (TextView) findViewById(R.id.confirmRideDetailsSource);
        confirmRideDetailsDestination = (TextView) findViewById(R.id.confirmRideDetailsDestination);
        confirmRideDetailsStartTime = (TextView) findViewById(R.id.confirmRideDetailsStartTime);
        confirmRideDetailsCarBrand = (TextView) findViewById(R.id.confirmRideDetailsCarBrand);
        confirmRideDetailsCarModel = (TextView) findViewById(R.id.confirmRideDetailsCarModel);
        confirmRideDetailsCarNumber = (TextView) findViewById(R.id.confirmRideDetailsCarNumber);
        confirmRideDetailsUserMessage = (TextView) findViewById(R.id.confirmRideDetailsUserMessage);
        //confirmRideDetailsPhone = (TextView) findViewById(R.id.confirmRideDetailsPhone);
        confirmRideDetailsCarColor = (TextView) findViewById(R.id.confirmRideDetailsCarColor);

        confirmRideDetailsCarLogo = (ImageView) findViewById(R.id.confirmRideDetailsCarLogo);

        confirmRideDetailsRadioAc = (CheckBox) findViewById(R.id.confirmRideDetailsRadioAc);
        confirmRideDetailsRadioAc.setEnabled(false);
        confirmRideDetailsRadioMusic = (CheckBox) findViewById(R.id.confirmRideDetailsRadioMusic);
        confirmRideDetailsRadioMusic.setEnabled(false);
        confirmRideDetailsRadioSmoking = (CheckBox) findViewById(R.id.confirmRideDetailsRadioSmoking);
        confirmRideDetailsRadioSmoking.setEnabled(false);
        confirmRideDetailsRadioAirbag = (CheckBox) findViewById(R.id.confirmRideDetailsRadioAirbag);
        confirmRideDetailsRadioAirbag.setEnabled(false);
        confirmRideDetailsLuggage = (CheckBox) findViewById(R.id.confirmRideDetailsLuggage);
        confirmRideDetailsLuggage.setEnabled(false);

        confirmRideDetailsPublish = (Button) findViewById(R.id.confirmRideDetailsPublish);

        RideInfo ride = RideInfo.getInstance();
        if (null != ride) {

            getSupportActionBar().setTitle(ride.getRideOf().getName());
            /*if(null!=CurrentUserInfo.getInstance().getProfilePicPath())
            {
                Bitmap bm = imageUtil.loadImageFromStorage(CurrentUserInfo.getInstance().getProfilePicPath());
                collapsingToolbarLayout.setBackground(new BitmapDrawable(imageUtil.getProfilePic(this)));
            }*/
            collapsingToolbarLayout.setBackground(new BitmapDrawable(imageUtil.getProfilePic(this)));
            confirmRideDetailsPrice.setText(ride.getFare() + "");
            confirmRideDetailsSource.setText(ride.getSourceName());
            confirmRideDetailsDestination.setText(ride.getDestinationName());
            confirmRideDetailsStartTime.setText(sdf.format(ride.getStarTime()));
            confirmRideDetailsCarBrand.setText(ride.getCar().getCarBrand());
            confirmRideDetailsCarModel.setText(ride.getCar().getCarModel());
            confirmRideDetailsCarNumber.setText(ride.getCar().getCarNo());
            confirmRideDetailsCarColor.setText(ride.getCar().getCarColor());
            confirmRideDetailsUserMessage.setText(ride.getUserMessage());
            //confirmRideDetailsPhone.setText(ride.getRideOf().getPhNo());

            Bitmap carLogo;
            if(null!=ride.getCar().getCarBrand() && !ride.getCar().getCarBrand().trim().equals(""))
            {
                int id;
                if(ride.getCar().getCarBrand().contains("Rented"))
                {
                    id= getResources().getIdentifier("rented", "drawable",getPackageName());
                }
                else {
                    id = getResources().getIdentifier(Util.getResourceNameFromDisplayName(ride.getCar().getCarBrand()), "drawable", getPackageName());
                }
                carLogo = BitmapFactory.decodeResource(getResources(),id);
                if(null==carLogo)
                {
                    carLogo = BitmapFactory.decodeResource(getResources(),R.drawable.my_brand);
                }

            }
            else
            {
                carLogo = BitmapFactory.decodeResource(getResources(),R.drawable.my_brand);
            }

            confirmRideDetailsCarLogo.setImageBitmap(carLogo);

            confirmRideDetailsRadioAc.setChecked(ride.getCar().isAcAvailable());
            confirmRideDetailsRadioMusic.setChecked(ride.getCar().isMusicAvailable());
            confirmRideDetailsRadioSmoking.setChecked(ride.getCar().isSmokingAllowed());
            confirmRideDetailsRadioAirbag.setChecked(ride.getCar().isAirbagAvailable());
            confirmRideDetailsLuggage.setChecked(ride.getCar().isLuggageAllowed());

            if(showConfirmButton) {
                confirmRideDetailsPublish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SaveRideDetailsTask saveRideDetailsTask = new SaveRideDetailsTask();
                        saveRideDetailsTask.execute();
                    }
                });
            }
            else
            {
                confirmRideDetailsPublish.setVisibility(View.GONE);
            }



        }
    }

    private void startMySharedRideActivity()
    {
        Intent mySharedRides = new Intent(this,MySharedRides.class);
        startActivity(mySharedRides);
    }

    public class SaveRideDetailsTask extends AsyncTask<Void, Void, Boolean> {

        Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_TIME_FORMAT_WITH_TIME_ZONE).create();
        String errorMessage;
        @Override
        protected void onPreExecute() {
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            DecimalFormat df = new DecimalFormat("##.######");
            boolean isRideIdAvailable = false;
            Ride ride = new Ride();
            RideInfo rideInfo = RideInfo.getInstance();
            if(null!=rideInfo.getId())
            {
                Log.d("lyftoxi.debug","rideId "+rideInfo.getId());
                ride.setId(rideInfo.getId());
                isRideIdAvailable=true;
            }
            ride.setCar(Util.convertCarInfoToCar(rideInfo.getCar()));
            ride.setComment(rideInfo.getUserMessage());
            ride.setFare(rideInfo.getFare());
            List<String> locations = new ArrayList<String>();
            Location source = new Location();
            source.setName(rideInfo.getSourceName());
            source.setLatitude(Double.valueOf(df.format(rideInfo.getSource().latitude)));
            source.setLongitude(Double.valueOf(df.format(rideInfo.getSource().longitude)));
            ride.setSource(source);
            Location destination = new Location();
            destination.setName(rideInfo.getDestinationName());
            destination.setLatitude(Double.valueOf(df.format(rideInfo.getDestination().latitude)));
            destination.setLongitude(Double.valueOf(df.format(rideInfo.getDestination().longitude)));
            ride.setDestination(destination);
            ride.setStartTime(rideInfo.getStarTime());
            ride.setRideOwner(Util.convertUserInfoToUser(CurrentUserInfo.getInstance()));
            ride.setInterestedUserCount(rideInfo.getInterestedUserCount());

            Object rideInfoJson = gson.toJson(ride);
            try {
                HttpRestUtil httpRestUtil = new HttpRestUtil(getApplicationContext());
                String response;
                if(isRideIdAvailable)
                {
                    Log.d("lyftoxi.debug","executing put");
                    response = httpRestUtil.httpPut("shareRideService/ride", rideInfoJson);
                }else {
                    Log.d("lyftoxi.debug","executing post");
                    response = httpRestUtil.httpPost("shareRideService/ride", rideInfoJson);
                }
                if(null!=response)
                {
                    return true;
                }


            }catch (IOException ioex) {
                Log.e("lyftoxi.error","Error occurred in REST WS call url cannot be reached "+ioex.getMessage());
                errorMessage = "Service Unavailable";
            }
            catch (LyftoxiClientBusinessException e) {
                Log.e("lyftoxi.error","Business Exception occurred in REST WS call "+e.getMessage());
                errorMessage = e.getMessage();
            }
            catch (LyftoxiClientException e) {
                Log.e("lyftoxi.error","Error occurred in REST WS call "+e.getMessage());
                errorMessage = "Some thing wrong happened.Contact support";
            }
            catch (Exception e) {
                Log.e("lyftoxi.error","Something really went wrong "+e.getMessage());
                errorMessage = "OMG you got us a defect. Contact support with screenshot";
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);
            Toast toast;
            if (success) {
                toast = Toast.makeText(getApplicationContext(), "Ride saved successfully", Toast.LENGTH_LONG);
                toast.show();
                finish();
                startMySharedRideActivity();


            } else {
                toast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG);
                toast.show();
                finish();
            }


        }


        @Override
        protected void onCancelled() {

        }



    }
}
