package com.lyftoxi.lyftoxi;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.lyftoxi.lyftoxi.exception.LyftoxiClientBusinessException;
import com.lyftoxi.lyftoxi.exception.LyftoxiClientException;
import com.lyftoxi.lyftoxi.singletons.RideInfo;
import com.lyftoxi.lyftoxi.util.Constants;
import com.lyftoxi.lyftoxi.util.HttpRestUtil;
import com.lyftoxi.lyftoxi.util.Util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MySharedRides extends BaseActivity {

    private ListView rideListing;
   // private View progressView;
    private List<RideListingInfo> rides = new ArrayList<RideListingInfo>();
    private TextView noRidesFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isScrollable=false;
        setContentView(R.layout.activity_my_shared_rides);
        rideListing = (ListView) findViewById(R.id.sharedRideList);
        //progressView = (View) findViewById(R.id.sharedRides_progress);
        noRidesFound =(TextView) findViewById(R.id.sharedRideEmptyList);
        new GetMySharedRides().execute();
    }

    private void setRideListView(){
        final SimpleDateFormat sdf  = new SimpleDateFormat(Constants.SIMPLE_DATE_FORMAT);
        RideListingAdapterNoImage rideListingAdapter = new RideListingAdapterNoImage(this,R.layout.ride_listing_no_user_image,rides);
        rideListing.setAdapter(rideListingAdapter);
        rideListing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d("lyftoxi.debug", "clicked ride position " + position);
                RideListingInfo selectedRide = (RideListingInfo)adapterView.getItemAtPosition(position);
                RideInfo rideInfo = RideInfo.getInstance();
                rideInfo.reset();
                rideInfo = RideInfo.getInstance();
                rideInfo.setId(selectedRide.getId());
                rideInfo.setSource(selectedRide.getSource());
                rideInfo.setSourceName(selectedRide.getSourceName());
                rideInfo.setDestination(selectedRide.getDestination());
                rideInfo.setDestinationName(selectedRide.getDestinationName());
                rideInfo.setRideOf(session.getUserDetails());
                rideInfo.setStarTime(selectedRide.getStarTime());
                rideInfo.setFare(selectedRide.getFare());
                rideInfo.setUserMessage(selectedRide.getUserMessage());
                rideInfo.setCar(selectedRide.getCar());
                rideInfo.setStatus(selectedRide.getStatus());
                rideInfo.setInterestedUserCount(selectedRide.getInterestedUserCount());
                Log.d("lyftoxi.debug",rideInfo.toString());

                Intent myRideDetails = new Intent(view.getContext(),MyRideDetails.class);
                view.getContext().startActivity(myRideDetails);

            }
        });
    }


    private class GetMySharedRides extends AsyncTask<LatLng, Void, Boolean>
    {
        String errorMessage;
        List<Ride> ridesReceived = null;
        @Override
        protected void onPreExecute()
        {
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(LatLng... params) {
            String sourceStr = null;
            String destinationStr = null;
            Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_TIME_FORMAT_WITH_TIME_ZONE).create();
            HttpRestUtil httpRestUtil = new HttpRestUtil(getApplicationContext());
            StringBuffer url = new StringBuffer();
            url.append("shareRideService/rides/ownerId");
            url.append("?id=");
            url.append(session.getUserDetails().getUID());

            Log.d("lyftoxi.debug","URL "+url.toString());
            try {
                String response = httpRestUtil.httpGet(url.toString());
                if(null!=response)
                {
                    ridesReceived = gson.fromJson(response, new TypeToken<List<Ride>>() {}.getType());
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
                for(Ride tmpRide : ridesReceived)
                {
                    RideListingInfo ride = new RideListingInfo();
                    ride.setId(tmpRide.getId());
                    ride.setCar(Util.convertCarToCarInfo(tmpRide.getCar()));
                    ride.setUserMessage(tmpRide.getComment());
                    ride.setFare(tmpRide.getFare());
                    ride.setSourceName(tmpRide.getSource().getName());
                    ride.setSource(new LatLng(tmpRide.getSource().getLatitude(),tmpRide.getSource().getLongitude()));
                    ride.setDestinationName(tmpRide.getDestination().getName());
                    ride.setDestination(new LatLng(tmpRide.getDestination().getLatitude(),tmpRide.getDestination().getLongitude()));
                    ride.setStarTime(tmpRide.getStartTime());
                    ride.setRideOf(Util.convertUserToUserInfo(tmpRide.getRideOwner()));
                    ride.setInterested(null);
                    ride.setStatus(tmpRide.getRideStatus());
                    ride.setInterestedUserCount(tmpRide.getInterestedUserCount());

                    rides.add(ride);
                }
                noRidesFound.setVisibility(View.GONE);
                setRideListView();
            }
            if(ridesReceived==null || ridesReceived.size()==0)
            {
                noRidesFound.setVisibility(View.VISIBLE);
            }
            showProgress(false);
        }
    }

}
