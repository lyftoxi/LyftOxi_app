package com.lyftoxi.lyftoxi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lyftoxi.lyftoxi.dao.TakeRide;
import com.lyftoxi.lyftoxi.exception.LyftoxiClientBusinessException;
import com.lyftoxi.lyftoxi.exception.LyftoxiClientException;
import com.lyftoxi.lyftoxi.singletons.RideInfo;
import com.lyftoxi.lyftoxi.util.Constants;
import com.lyftoxi.lyftoxi.util.HttpRestUtil;
import com.lyftoxi.lyftoxi.util.Util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MyRideDetails extends BaseActivity {

    private TextView myRideDetailsPrice, myRideDetailsSource, myRideDetailsDestination,
            myRideDetailsStartTime, myRideDetailsCarBrand, myRideDetailsCarModel, myRideDetailsCarNumber,
            myRideDetailsUserMessage, myRideDetailsCarColor, myRideDetailsInterestedUserListEmpty;

    private CheckBox myRideDetailsRadioAc,myRideDetailsRadioMusic,myRideDetailsRadioSmoking,
            myRideDetailsRadioAirbag, myRideDetailsLuggage;

    private SimpleDateFormat sdf =  new SimpleDateFormat(Constants.DATE_TIME_FORMAT_12HR_FORMAT);
    private ImageView myRideDetailsCarLogo;
    private RideInfo seletctedRide;
    private List<UserInfo> interestedUsers =  new ArrayList<UserInfo>();
    private RecyclerView myRideDetailsInterestedUserRecycler;
    private UserListingRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isScrollable=true;
        setContentView(R.layout.activity_my_ride_details);

        myRideDetailsPrice = (TextView) findViewById(R.id.myRideDetailsPrice);
        myRideDetailsSource = (TextView) findViewById(R.id.myRideDetailsSource);
        myRideDetailsDestination = (TextView) findViewById(R.id.myRideDetailsDestination);
        myRideDetailsStartTime = (TextView) findViewById(R.id.myRideDetailsStartTime);
        myRideDetailsCarBrand = (TextView) findViewById(R.id.myRideDetailsCarBrand);
        myRideDetailsCarModel = (TextView) findViewById(R.id.myRideDetailsCarModel);
        myRideDetailsCarNumber = (TextView) findViewById(R.id.myRideDetailsCarNumber);
        myRideDetailsUserMessage = (TextView) findViewById(R.id.myRideDetailsUserMessage);
        myRideDetailsCarColor = (TextView) findViewById(R.id.myRideDetailsCarColor);
        myRideDetailsInterestedUserListEmpty = (TextView) findViewById(R.id.myRideDetailsInterestedUserListEmpty);

        myRideDetailsCarLogo = (ImageView) findViewById(R.id.myRideDetailsCarLogo);

        myRideDetailsRadioAc = (CheckBox) findViewById(R.id.myRideDetailsRadioAc);
        myRideDetailsRadioAc.setEnabled(false);
        myRideDetailsRadioMusic = (CheckBox) findViewById(R.id.myRideDetailsRadioMusic);
        myRideDetailsRadioMusic.setEnabled(false);
        myRideDetailsRadioSmoking = (CheckBox) findViewById(R.id.myRideDetailsRadioSmoking);
        myRideDetailsRadioSmoking.setEnabled(false);
        myRideDetailsRadioAirbag = (CheckBox) findViewById(R.id.myRideDetailsRadioAirbag);
        myRideDetailsRadioAirbag.setEnabled(false);
        myRideDetailsLuggage = (CheckBox) findViewById(R.id.myRideDetailsLuggage);
        myRideDetailsLuggage.setEnabled(false);


        seletctedRide = RideInfo.getInstance();
        if (null != seletctedRide) {

            myRideDetailsPrice.setText(seletctedRide.getFare() + "");
            myRideDetailsSource.setText(seletctedRide.getSourceName());
            myRideDetailsDestination.setText(seletctedRide.getDestinationName());
            myRideDetailsStartTime.setText(sdf.format(seletctedRide.getStarTime()));
            myRideDetailsCarBrand.setText(seletctedRide.getCar().getCarBrand());
            myRideDetailsCarModel.setText(seletctedRide.getCar().getCarModel());
            myRideDetailsCarNumber.setText(seletctedRide.getCar().getCarNo());
            myRideDetailsCarColor.setText(seletctedRide.getCar().getCarColor());
            myRideDetailsUserMessage.setText(seletctedRide.getUserMessage());

            myRideDetailsRadioAc.setChecked(seletctedRide.getCar().isAcAvailable());
            myRideDetailsRadioMusic.setChecked(seletctedRide.getCar().isMusicAvailable());
            myRideDetailsRadioSmoking.setChecked(seletctedRide.getCar().isSmokingAllowed());
            myRideDetailsRadioAirbag.setChecked(seletctedRide.getCar().isAirbagAvailable());
            myRideDetailsLuggage.setChecked(seletctedRide.getCar().isLuggageAllowed());

            myRideDetailsInterestedUserRecycler = (RecyclerView) findViewById(R.id.myRideDetailsInterestedUserRecycler);

            Bitmap carLogo;
            if (null != seletctedRide.getCar().getCarBrand() && !seletctedRide.getCar().getCarBrand().trim().equals("")) {
                int id;
                if (seletctedRide.getCar().getCarBrand().contains("Rented")) {
                    id = getResources().getIdentifier("rented", "drawable", getPackageName());
                } else {
                    id = getResources().getIdentifier(Util.getResourceNameFromDisplayName(seletctedRide.getCar().getCarBrand()), "drawable", getPackageName());
                }
                carLogo = BitmapFactory.decodeResource(getResources(), id);
                if (null == carLogo) {
                    carLogo = BitmapFactory.decodeResource(getResources(), R.drawable.my_brand);
                }

            } else {
                carLogo = BitmapFactory.decodeResource(getResources(), R.drawable.my_brand);
            }

            myRideDetailsCarLogo.setImageBitmap(carLogo);

            myRideDetailsInterestedUserListEmpty.setText(getString(R.string.not_one_interested));
            mAdapter = new UserListingRecyclerAdapter(interestedUsers);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            myRideDetailsInterestedUserRecycler.setLayoutManager(mLayoutManager);
            myRideDetailsInterestedUserRecycler.setItemAnimator(new DefaultItemAnimator());
            myRideDetailsInterestedUserRecycler.setAdapter(mAdapter);
            myRideDetailsInterestedUserRecycler.setNestedScrollingEnabled(false);
            new GetInterestedUsers().execute(seletctedRide.getId());
        }

    }
    private class GetInterestedUsers extends AsyncTask<String, Void, Boolean>
    {
        String errorMessage;
        @Override
        protected void onPreExecute()
        {
            showProgress(true);
        }
        @Override
        protected Boolean doInBackground(String... params) {
            String rideId = params[0];
            Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_TIME_FORMAT_WITH_TIME_ZONE).create();
            HttpRestUtil httpRestUtil = new HttpRestUtil(getApplicationContext());
            try {
                String response = httpRestUtil.httpGet("takeRideService/rides/shareRideObjId?id="+rideId);
                if(null!=response)
                {
                    List<TakeRide> ridesReceived = gson.fromJson(response, new TypeToken<List<TakeRide>>() {}.getType());
                    if(null!=ridesReceived)
                    {
                        for(TakeRide tmpRide : ridesReceived)
                        {
                            UserInfo interestedUserInfo = new UserInfo();
                            interestedUserInfo.setUID(tmpRide.getInterestedUserObjId());
                            interestedUserInfo.setName(tmpRide.getInterestedUserName());
                            interestedUserInfo.setPhNo(tmpRide.getInterestedUserMobileNo());
                            interestedUsers.add(interestedUserInfo);
                        }
                        return true;
                    }
                    return false;
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
        protected void onPostExecute(Boolean result)
        {
            showProgress(false);
            if(result)
            {
                myRideDetailsInterestedUserListEmpty.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();
            }
            else
            {
                myRideDetailsInterestedUserListEmpty.setVisibility(View.VISIBLE);
            }

        }
    }


}
