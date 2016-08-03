package com.lyftoxi.lyftoxi;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lyftoxi.lyftoxi.dao.Ride;
import com.lyftoxi.lyftoxi.dao.TakeRide;
import com.lyftoxi.lyftoxi.exception.LyftoxiClientBusinessException;
import com.lyftoxi.lyftoxi.exception.LyftoxiClientException;
import com.lyftoxi.lyftoxi.singletons.CurrentUserInterestedRides;
import com.lyftoxi.lyftoxi.singletons.RideInfo;
import com.lyftoxi.lyftoxi.util.Constants;
import com.lyftoxi.lyftoxi.util.HttpRestUtil;
import com.lyftoxi.lyftoxi.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyInterestedRides extends BaseActivity {

    private ListView rideListing;
   // private View progressView;
    private List<RideListingInfo> rides = new ArrayList<RideListingInfo>();
    private TextView noRidesFound;
    private RideListingInfo selectedRide;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isScrollable=false;
        setContentView(R.layout.activity_my_interested_rides);
        rideListing = (ListView) findViewById(R.id.interestedRideList);
       // progressView = (View) findViewById(R.id.interestedRides_progress);
        noRidesFound =(TextView) findViewById(R.id.interestedRideEmptyList);
        new GetMyInterestedRides().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showProgress(false);
    }

    private void setRideListView(){
        RideListingAdapter rideListingAdapter = new RideListingAdapter(this,R.layout.ride_listing,rides);
        rideListing.setAdapter(rideListingAdapter);
        rideListing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d("lyftoxi.debug", "clicked ride position " + position);

                    selectedRide = (RideListingInfo)adapterView.getItemAtPosition(position);
                Log.d("lyftoxi.debug", "selected ride id "+selectedRide.getId());
                    new GetRideDetails().execute(selectedRide.getId());

            }
        });

    }


    private void openTakeRideDetails()
    {
        Intent takeRideDetailsIntent = new Intent(this, TakeRideDetailsActivity.class);
        startActivity(takeRideDetailsIntent);
    }


    private class GetRideDetails extends AsyncTask<String, Void, Boolean> {

        Ride ridesReceived = null;
        String errorMessage;

        @Override
        protected void onPreExecute() {
            Log.d("lyftoxi.debug"," getting ride details");
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String rideId = params[0];
            Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy'T'HH:mm Z").create();
            HttpRestUtil httpRestUtil = new HttpRestUtil(getApplicationContext());
            StringBuffer url = new StringBuffer();
            url.append("shareRideService/ride");
            url.append("?id=");
            url.append(rideId);

            Log.d("lyftoxi.debug","URL "+url.toString());
            try {
                String response = httpRestUtil.httpGet(url.toString());
                if(null!=response)
                {
                    ridesReceived = gson.fromJson(response, new TypeToken<Ride>() {}.getType());
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
        protected void onPostExecute(Boolean result)
        {
            RideInfo ride = RideInfo.getInstance();
            ride.reset();
            ride = RideInfo.getInstance();
            ride.setId(ridesReceived.getId());
            ride.setCar(Util.convertCarToCarInfo(ridesReceived.getCar()));
            ride.setUserMessage(ridesReceived.getComment());
            ride.setFare(ridesReceived.getFare());
            ride.setSourceName(ridesReceived.getSource().getName());
            ride.setSource(new LatLng(ridesReceived.getSource().getLatitude(),ridesReceived.getSource().getLongitude()));
            ride.setDestinationName(ridesReceived.getDestination().getName());
            ride.setDestination(new LatLng(ridesReceived.getDestination().getLatitude(),ridesReceived.getDestination().getLongitude()));
            ride.setStarTime(ridesReceived.getStartTime());
            ride.setRideOf(Util.convertUserToUserInfo(ridesReceived.getRideOwner()));
            ride.setInterested(false);
            ride.setStatus(ridesReceived.getRideStatus());
            Log.d("lyftoxi.debug","Interested rides "+CurrentUserInterestedRides.getInstance().getRides());
            for(TakeRide tmpInterestedRide : CurrentUserInterestedRides.getInstance().getRides())
            {
                Log.d("lyftoxi.debug","tmpInterestedRide "+tmpInterestedRide.getShareRideObjId());
                Log.d("lyftoxi.debug","tmpRide "+ridesReceived.getId());
                if (tmpInterestedRide.getShareRideObjId().equals(ridesReceived.getId())) {
                    ride.setInterested(true);
                    break;
                }
            }
            //showProgress(false);
            openTakeRideDetails();
        }

    }
        private class GetMyInterestedRides extends AsyncTask<LatLng, Void, Boolean>
    {
        String errorMessage;
        List<TakeRide> ridesReceived = null;
        @Override
        protected void onPreExecute()
        {
            Log.d("lyftoxi.debug"," getting my interested rides");
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(LatLng... params) {
            Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_TIME_FORMAT_WITH_TIME_ZONE).create();
            HttpRestUtil httpRestUtil = new HttpRestUtil(getApplicationContext());
            StringBuffer url = new StringBuffer();
            url.append("takeRideService/rides/interestedUser");
            url.append("?id=");
            url.append(session.getUserDetails().getUID());

            Log.d("lyftoxi.debug","URL "+url.toString());
            try {
                String response = httpRestUtil.httpGet(url.toString());
                if(null!=response)
                {
                    ridesReceived = gson.fromJson(response, new TypeToken<List<TakeRide>>() {}.getType());
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
        protected void onPostExecute(Boolean result)
        {
            rides.clear();
            if(null!=ridesReceived)
            {
                for(TakeRide tmpRide : ridesReceived)
                {
                    RideListingInfo ride = new RideListingInfo();

;                   ride.setId(tmpRide.getShareRideObjId());
                    ride.setFare(tmpRide.getFare());
                    ride.setSourceName(tmpRide.getSource().getName());
                    ride.setSource(new LatLng(tmpRide.getSource().getLatitude(),tmpRide.getSource().getLongitude()));
                    ride.setDestinationName(tmpRide.getDestination().getName());
                    ride.setDestination(new LatLng(tmpRide.getDestination().getLatitude(),tmpRide.getDestination().getLongitude()));
                    ride.setStarTime(tmpRide.getRideTime());
                    UserInfo rideOf = new UserInfo();
                    rideOf.setUID(tmpRide.getOwnerObjId());
                    rideOf.setName(tmpRide.getOwnerName());
                    rideOf.setPhNo(tmpRide.getOwnerMobileNo());
                    ride.setRideOf(rideOf);
                    ride.setStatus(tmpRide.getShareRideStatus());
                    ride.setInterested(true);

                    CurrentUserInterestedRides.getInstance().getRides().add(tmpRide);
                    rides.add(ride);
                    noRidesFound.setVisibility(View.GONE);
                    setRideListView();


                }
            }
            if(ridesReceived==null || ridesReceived.size()==0)
            {
                noRidesFound.setVisibility(View.VISIBLE);
            }
            showProgress(false);
        }
    }

}
