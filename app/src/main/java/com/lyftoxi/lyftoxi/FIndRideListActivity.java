package com.lyftoxi.lyftoxi;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lyftoxi.lyftoxi.dao.Ride;
import com.lyftoxi.lyftoxi.dao.TakeRide;
import com.lyftoxi.lyftoxi.singletons.CurrentUserInterestedRides;
import com.lyftoxi.lyftoxi.singletons.RideInfo;
import com.lyftoxi.lyftoxi.util.HttpRestUtil;
import com.lyftoxi.lyftoxi.util.Util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FindRideListActivity extends BaseActivity {

    private List<RideListingInfo> rides = new ArrayList<RideListingInfo>();
    private TextView noRidesFound;
    private ListView rideListing;
    private String searchRequestParams;
    private FloatingActionButton filterBtn;

    private final ArrayList selectedItems=new ArrayList();
    private  final CharSequence[] filterOptions = {"ac","music","luggage","smoking", "airBag"};
    private final boolean[] checkedItems = new boolean[5];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isScrollable=false;
        setContentView(R.layout.activity_find_ride_list);
        noRidesFound = (TextView)findViewById(R.id.findRidesEmptyList);
        rideListing = (ListView)findViewById(R.id.findRidesList);
        filterBtn = (FloatingActionButton)findViewById(R.id.findRidesFilter);
        final StringBuffer filterOptionParams = new StringBuffer();

        if(null!= getIntent().getExtras()) {
            Bundle b = getIntent().getExtras();
            searchRequestParams = b.getString("searchRequestParams");
        }

        setSelectedItems();


        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Rides with")
                .setMultiChoiceItems(filterOptions, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            //selectedItems.add(indexSelected);
                            checkedItems[indexSelected]=true;
                        } else if (selectedItems.contains(indexSelected)) {
                            //selectedItems.remove(Integer.valueOf(indexSelected));
                            checkedItems[indexSelected]=false;
                        }
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        filterOptionParams.setLength(0);
                        for(int i=0; i<filterOptions.length ; i++)
                        {
                            if(checkedItems[i])
                            {
                                filterOptionParams.append("&"+filterOptions[i]+"=true");
                            }
                        }
                       Log.d("gog.debug","selected options "+filterOptionParams.toString());
                        String tmpUrl = searchRequestParams.substring(0,searchRequestParams.indexOf("dateTime")+25);
                        searchRequestParams = tmpUrl+filterOptionParams.toString();
                        Log.d("gog.debug","final url "+searchRequestParams);
                        new GetRidesBasedOnLocationTask().execute();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }).create();

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });


        if(null!= getIntent().getExtras()) {
            Bundle b = getIntent().getExtras();
            searchRequestParams = b.getString("searchRequestParams");
        }
        new GetRidesBasedOnLocationTask().execute();
    }


    private void setSelectedItems()
    {
        if(searchRequestParams.contains("ac="))
        {
            checkedItems[0]=true;
        }
        if(searchRequestParams.contains("music="))
        {
            checkedItems[1]=true;
        }
        if(searchRequestParams.contains("luggage="))
        {
            checkedItems[2]=true;
        }
        if(searchRequestParams.contains("smoking="))
        {
            checkedItems[3]=true;
        }
        if(searchRequestParams.contains("airBag="))
        {
            checkedItems[4]=true;
        }
    }



    private void setRideListView(){
        RideListingAdapter rideListingAdapter = new RideListingAdapter(this,R.layout.ride_listing,rides);
        rideListing.setAdapter(rideListingAdapter);
        rideListing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d("gog.debug", "clicked ride position " + position);

                RideListingInfo selectedRide = (RideListingInfo)adapterView.getItemAtPosition(position);
                RideInfo ride = RideInfo.getInstance();
                ride.setId(selectedRide.getId());
                ride.setCar(selectedRide.getCar());
                ride.setUserMessage(selectedRide.getUserMessage());
                ride.setFare(selectedRide.getFare());
                ride.setSourceName(selectedRide.getSourceName());
                ride.setSource(selectedRide.getSource());
                ride.setDestinationName(selectedRide.getDestinationName());
                ride.setDestination(selectedRide.getDestination());
                ride.setStarTime(selectedRide.getStarTime());
                ride.setRideOf(selectedRide.getRideOf());
                Log.d("gog.debug","Interested in take ride "+selectedRide.isInterested());
                ride.setInterested(selectedRide.isInterested());
                Log.d("gog.debug","Interested in take ride "+ride.isInterested());
                ride.setStatus(selectedRide.getStatus());


                if (!session.isLoggedIn()) {
                    Intent loginIntent = new Intent(view.getContext(), LoginActivity.class);
                    Bundle b = new Bundle();
                    b.putString("activityOnSuccess", TakeRideDetailsActivity.class.getName());
                    loginIntent.putExtras(b);
                    startActivity(loginIntent);
                }
                else
                {
                    Intent takeRideDetailsIntent = new Intent(view.getContext(), TakeRideDetailsActivity.class);
                    startActivity(takeRideDetailsIntent);
                }
            }
        });
    }

    private class GetRidesBasedOnLocationTask extends AsyncTask<LatLng, Void, Boolean>
    {

        List<Ride> ridesReceived = null;
        @Override
        protected void onPreExecute()
        {
            showProgress(true);

        }


        private void fetchInterestedRides()
        {
            Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy'T'HH:mm").create();
            HttpRestUtil httpRestUtil = new HttpRestUtil(getApplicationContext());
            StringBuffer url = new StringBuffer();
            url.append("takeRideService/rides/interestedUser");
            url.append("?id=");
            url.append(session.getUserDetails().getUID());

            Log.d("gog.debug","URL "+url.toString());
            try {
                String response = httpRestUtil.httpGet(url.toString());
                if(null!=response)
                {
                    List<TakeRide> interestedRides = gson.fromJson(response, new TypeToken<List<TakeRide>>() {}.getType());
                    CurrentUserInterestedRides.reset();
                    CurrentUserInterestedRides.getInstance().getRides().addAll(interestedRides);

                }


            }catch (IOException ioex)
            {
                Log.d("gog.debug","Error occurred in REST WS call url cannot be reached "+ioex.getMessage());
            }
            catch (Exception ex)
            {
                Log.d("gog.debug","Error occurred in REST WS call "+ex.getMessage());
            }
        }

        @Override
        protected Boolean doInBackground(LatLng... params) {

            if(session.isLoggedIn())
            {
                fetchInterestedRides();
            }

            Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy'T'HH:mm").create();
            HttpRestUtil httpRestUtil = new HttpRestUtil(getApplicationContext());
            StringBuffer url = new StringBuffer();
            url.append("shareRideService/rides/locationTime");
            if(null!=searchRequestParams)
            {
                url.append(searchRequestParams);
            }
            Log.d("gog.debug","URL "+url.toString());
            try {
                String response = httpRestUtil.httpGet(url.toString());
                if(null!=response)
                {
                    ridesReceived = gson.fromJson(response, new TypeToken<List<Ride>>() {}.getType());
                    return true;
                }


            }catch (IOException ioex)
            {
                Log.d("gog.debug","Error occurred in REST WS call url cannot be reached "+ioex.getMessage());
            }
            catch (Exception ex)
            {
                Log.d("gog.debug","Error occurred in REST WS call "+ex.getMessage());
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
                    ride.setStatus(tmpRide.getRideStatus());
                    ride.setInterested(false);
                    Log.d("gog.debug","Interested rides "+ CurrentUserInterestedRides.getInstance().getRides());
                    for(TakeRide tmpInterestedRide : CurrentUserInterestedRides.getInstance().getRides())
                    {
                        Log.d("gog.debug","tmpInterestedRide "+tmpInterestedRide.getShareRideObjId());
                        Log.d("gog.debug","tmpRide "+tmpRide.getId());
                        if (tmpInterestedRide.getShareRideObjId().equals(tmpRide.getId())) {
                            ride.setInterested(true);
                            break;
                        }
                    }
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
