package com.lyftoxi.lyftoxi;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lyftoxi.lyftoxi.dao.Location;
import com.lyftoxi.lyftoxi.dao.Promo;
import com.lyftoxi.lyftoxi.dao.Ride;
import com.lyftoxi.lyftoxi.dao.TakeRide;
import com.lyftoxi.lyftoxi.exception.LyftoxiClientBusinessException;
import com.lyftoxi.lyftoxi.exception.LyftoxiClientException;
import com.lyftoxi.lyftoxi.singletons.CurrentUserInfo;
import com.lyftoxi.lyftoxi.singletons.CurrentUserInterestedRides;
import com.lyftoxi.lyftoxi.singletons.RideInfo;
import com.lyftoxi.lyftoxi.util.Constants;
import com.lyftoxi.lyftoxi.util.HttpRestUtil;
import com.lyftoxi.lyftoxi.util.LyftoxiFirebase;
import com.lyftoxi.lyftoxi.util.RoundImage;
import com.lyftoxi.lyftoxi.util.Util;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TakeRideDetailsActivity extends BaseActivity {

    private TextView takeRideDetailsPrice, takeRideDetailsSource, takeRideDetailsDestination,
            takeRideDetailsStartTime, takeRideDetailsCarBrand, takeRideDetailsCarModel, takeRideDetailsCarNumber,
            takeRideDetailsUserMessage, takeRideDetailsCarColor;

    private CheckBox takeRideDetailsRadioAc,takeRideDetailsRadioMusic,takeRideDetailsRadioSmoking,
            takeRideDetailsRadioAirbag, takeRideDetailsLuggage;

    private SimpleDateFormat sdf =  new SimpleDateFormat(Constants.DATE_TIME_FORMAT_12HR_FORMAT);

    private ImageButton takeRideDetailsCall, takeRideDetailSms, takeRideDetailsPaytm;

    private ImageView takeRideDetailsCarLogo;

    private PackageManager pm;

    private RideInfo seletctedRide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_ride_details);
        pm = getPackageManager();



        takeRideDetailsPrice = (TextView) findViewById(R.id.takeRideDetailsPrice);
        takeRideDetailsSource = (TextView) findViewById(R.id.takeRideDetailsSource);
        takeRideDetailsDestination = (TextView) findViewById(R.id.takeRideDetailsDestination);
        takeRideDetailsStartTime = (TextView) findViewById(R.id.takeRideDetailsStartTime);
        takeRideDetailsCarBrand = (TextView) findViewById(R.id.takeRideDetailsCarBrand);
        takeRideDetailsCarModel = (TextView) findViewById(R.id.takeRideDetailsCarModel);
        takeRideDetailsCarNumber = (TextView) findViewById(R.id.takeRideDetailsCarNumber);
        takeRideDetailsUserMessage = (TextView) findViewById(R.id.takeRideDetailsUserMessage);
        takeRideDetailsCarColor = (TextView) findViewById(R.id.takeRideDetailsCarColor);
        //takeRideDetailsPhone = (TextView) findViewById(R.id.takeRideDetailsPhone);

        takeRideDetailsCarLogo = (ImageView) findViewById(R.id.takeRideDetailsCarLogo);

        takeRideDetailsRadioAc = (CheckBox) findViewById(R.id.takeRideDetailsRadioAc);
        takeRideDetailsRadioAc.setEnabled(false);
        takeRideDetailsRadioMusic = (CheckBox) findViewById(R.id.takeRideDetailsRadioMusic);
        takeRideDetailsRadioMusic.setEnabled(false);
        takeRideDetailsRadioSmoking = (CheckBox) findViewById(R.id.takeRideDetailsRadioSmoking);
        takeRideDetailsRadioSmoking.setEnabled(false);
        takeRideDetailsRadioAirbag = (CheckBox) findViewById(R.id.takeRideDetailsRadioAirbag);
        takeRideDetailsRadioAirbag.setEnabled(false);
        takeRideDetailsLuggage = (CheckBox) findViewById(R.id.takeRideDetailsLuggage);
        takeRideDetailsLuggage.setEnabled(false);

        takeRideDetailsCall = (ImageButton) findViewById(R.id.takeRideDetailsCall);
        takeRideDetailSms = (ImageButton) findViewById(R.id.takeRideDetailsSms);
        takeRideDetailsPaytm = (ImageButton) findViewById(R.id.takeRideDetailsPaytm);

        // takeRideInterested = (Button) findViewById(R.id.takeRideInterested);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.sample_profile_pic);
        collapsingToolbarLayout.setBackground(new BitmapDrawable(getResources(), bm));

        seletctedRide = RideInfo.getInstance();
        if (null != seletctedRide) {

            downloadUserProfilePic(seletctedRide.getRideOf().getUID());
            getSupportActionBar().setTitle(seletctedRide.getRideOf().getName());
            takeRideDetailsPrice.setText(seletctedRide.getFare() + "");
            takeRideDetailsSource.setText(seletctedRide.getSourceName());
            takeRideDetailsDestination.setText(seletctedRide.getDestinationName());
            takeRideDetailsStartTime.setText(sdf.format(seletctedRide.getStarTime()));
            takeRideDetailsCarBrand.setText(seletctedRide.getCar().getCarBrand());
            takeRideDetailsCarModel.setText(seletctedRide.getCar().getCarModel());
            takeRideDetailsCarNumber.setText(seletctedRide.getCar().getCarNo());
            takeRideDetailsCarColor.setText(seletctedRide.getCar().getCarColor());
            takeRideDetailsUserMessage.setText(seletctedRide.getUserMessage());
            //takeRideDetailsPhone.setText(seletctedRide.getRideOf().getPhNo());


            takeRideDetailsRadioAc.setChecked(seletctedRide.getCar().isAcAvailable());
            takeRideDetailsRadioMusic.setChecked(seletctedRide.getCar().isMusicAvailable());
            takeRideDetailsRadioSmoking.setChecked(seletctedRide.getCar().isSmokingAllowed());
            takeRideDetailsRadioAirbag.setChecked(seletctedRide.getCar().isAirbagAvailable());
            takeRideDetailsLuggage.setChecked(seletctedRide.getCar().isLuggageAllowed());


            Bitmap carLogo;
            if(null!=seletctedRide.getCar().getCarBrand() && !seletctedRide.getCar().getCarBrand().trim().equals(""))
            {
                int id;
                if(seletctedRide.getCar().getCarBrand().contains("Rented"))
                {
                    id= getResources().getIdentifier("rented", "drawable",getPackageName());
                }
                else {
                    id = getResources().getIdentifier(Util.getResourceNameFromDisplayName(seletctedRide.getCar().getCarBrand()), "drawable", getPackageName());
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

            takeRideDetailsCarLogo.setImageBitmap(carLogo);

            takeRideDetailsCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + seletctedRide.getRideOf().getPhNo()));
                    startActivity(intent);
                }
            });

            takeRideDetailSms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StringBuffer sb =  new StringBuffer();
                    sb.append("Hi ");
                    sb.append(seletctedRide.getRideOf().getName());
                    sb.append(". I am interested in your ride from ");
                    sb.append(seletctedRide.getSourceName());
                    sb.append(" to ");
                    sb.append(seletctedRide.getDestinationName());
                    sb.append(" @ ");
                    sb.append(sdf.format(seletctedRide.getStarTime()));
                    sb.append(" shared by http://www.lyftoxi.com APP");

                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.putExtra("address", seletctedRide.getRideOf().getPhNo());
                    sendIntent.putExtra("sms_body", sb.toString());
                    sendIntent.setType("vnd.android-dir/mms-sms");
                    startActivity(sendIntent);
                }
            });

        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(appInstalledOrNot(Constants.APP_PACAKGE)) {
            if(null!=takeRideDetailsPaytm) {
                takeRideDetailsPaytm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*if(!openApp(Constants.APP_PACAKGE))
                        {
                            Snackbar.make(coordinatorLayout,"Could not open Paytm", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }*/
                        Promo promo = new Promo();
                        promo.setUserId(session.getUserDetails().getUID());
                        promo.setRideId(seletctedRide.getId());

                        new ApplyPromoTask().execute(promo);

                    }
                });
            }

        }
        else
        {
            Snackbar.make(coordinatorLayout,"Paytm is not installed", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Action", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.APP_LINK));
                                startActivity(myIntent);
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(view.getContext(), "No application can handle this request."
                                        + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }).show();
        }
    }


    private boolean appInstalledOrNot(String uri) {
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed ;
    }

    public boolean openApp(String packageName) {
        Intent i = pm.getLaunchIntentForPackage(packageName);
        if (i == null) {
            return false;
            //throw new PackageManager.NameNotFoundException();
        }
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        startActivity(i);
        return true;
    }

    private void downloadUserProfilePic(String userId)
    {
        if(null==userId || userId.trim().equals(""))
        {
            return;
        }
        final String profilePicFileName = userId+"_profile_pic.jpg";
        Log.d("gog.debug ","profilePicFileName "+profilePicFileName);
        StorageReference storageRef = LyftoxiFirebase.storageRef;
        StorageReference profileImageRef = storageRef.child("userProfilePics/"+profilePicFileName);
        // profileImageRef.getDownloadUrl();
        final long ONE_MEGABYTE = 500 * 500;
        profileImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                collapsingToolbarLayout.setBackground(new BitmapDrawable(getResources(),bitmap));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                Log.d("lyftoxi.debug","Firebase: profile pic download failed");
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.profile_pic_placeholder_large);
                collapsingToolbarLayout.setBackground(new BitmapDrawable(getResources(),bitmap));
            }
        });
    }


    public class ApplyPromoTask extends AsyncTask<Promo, Void, Boolean> {

        Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_TIME_FORMAT_WITH_TIME_ZONE).create();
        String errorMessage;


        @Override
        protected void onPreExecute() {
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Promo... params) {
            Promo promo = params[0];
            Object promoJson = gson.toJson(promo);
            try {
                HttpRestUtil httpRestUtil = new HttpRestUtil(getApplicationContext());
                String response;
                response = httpRestUtil.httpPost("promoService/promo", promoJson);
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
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            showProgress(false);
            Toast toast = null;
            if(result){
                toast = Toast.makeText(getApplicationContext(), "You will receive Promo amount in your PAYTM wallet", Toast.LENGTH_LONG);
            }else{
                toast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG);
            }
            toast.show();
            if(!openApp(Constants.APP_PACAKGE))
            {
                Snackbar.make(coordinatorLayout,"Could not open Paytm", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }


   /* private void startMyInterestedRideActivity()
    {
        Intent myInterestedRides = new Intent(this,MyInterestedRides.class);
        startActivity(myInterestedRides);
    }*/


}