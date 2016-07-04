package com.lyftoxi.lyftoxi;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lyftoxi.lyftoxi.dao.Location;
import com.lyftoxi.lyftoxi.dao.Ride;
import com.lyftoxi.lyftoxi.dao.TakeRide;
import com.lyftoxi.lyftoxi.dao.User;
import com.lyftoxi.lyftoxi.singletons.CurrentUserInfo;
import com.lyftoxi.lyftoxi.singletons.CurrentUserInterestedRides;
import com.lyftoxi.lyftoxi.singletons.RideInfo;
import com.lyftoxi.lyftoxi.util.GPSTracker;
import com.lyftoxi.lyftoxi.util.HttpRestUtil;
import com.lyftoxi.lyftoxi.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class TakeRideActivity extends BaseActivity {


    //private SessionManager session;
    private List<RideListingInfo> rides = new ArrayList<RideListingInfo>();
    private ListView rideListing;
    private View advancedSearchLayout;
    private ImageButton toggleAdvancedSearch, takeARideSearch;
    private TextView noRidesFound;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private boolean showAdvancedSearch=false;


    private LatLng source;
    private LatLng destination;
    private LatLng currentLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isScrollable=false;
        setContentView(R.layout.activity_take_ride);

        final GetRidesBasedOnLocationTask getRidesBasedOnLocationTask = new GetRidesBasedOnLocationTask();
        // check if GPS enabled
        GPSTracker gps = new GPSTracker(this);
        if(gps.canGetLocation()){
            currentLocation = new LatLng(gps.getLatitude(),gps.getLongitude());
        }else{
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("GPS is settings");
            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog,int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    //getRidesBasedOnLocationTask.execute();
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    getRidesBasedOnLocationTask.execute();
                }
            });
            alertDialog.show();
        }
        if(null!=currentLocation) {
            Toast locationToast = Toast.makeText(this, "Latitide :" + currentLocation.latitude + " \n Longitude :" + currentLocation.longitude, Toast.LENGTH_LONG);
            locationToast.show();
            getRidesBasedOnLocationTask.execute();
        }

        rideListing = (ListView) findViewById(R.id.takeARiderideList);
        noRidesFound =(TextView) findViewById(R.id.takeRideEmptyList);
        advancedSearchLayout =  findViewById(R.id.takeARideAdvancedSearch);
        toggleAdvancedSearch = (ImageButton) findViewById(R.id.toggleAdvancedSearch);
        takeARideSearch = (ImageButton) findViewById(R.id.takeARideSearch);

        takeARideSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetRidesBasedOnLocationTask().execute();
            }
        });

        setSupportActionBar(toolbar);
        toggleAdvancedSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAdvancedSearch();
            }
        });

        if(null!= getIntent().getExtras()) {
            Bundle b = getIntent().getExtras();
            double latitude = b.getDouble("currentLocationLatitude");
            double longitude = b.getDouble("currentLocationLongitude");

            currentLocation = new LatLng(latitude,longitude);
        }


        PlaceAutocompleteFragment autocompleteFragmentSource = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_take_a_ride_source);
        EditText sourceTextInput = ((EditText)autocompleteFragmentSource.getView().findViewById(R.id.place_autocomplete_search_input));
        sourceTextInput.setTextSize(20.0f);
        autocompleteFragmentSource.setHint(getString(R.string.source));

        autocompleteFragmentSource.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.d("gog.debug", "Source: " + place.getName());
                Log.d("gog.debug", "Source:" + place.getLatLng().longitude + " ," + place.getLatLng().longitude);
                source = place.getLatLng();
            }

            @Override
            public void onError(Status status) {
                Log.d("gog.debug", "An error occurred: " + status);
            }
        });

        PlaceAutocompleteFragment autocompleteFragmentDestination = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_take_a_ride_destination);
        EditText destinationTextInput = ((EditText)autocompleteFragmentDestination.getView().findViewById(R.id.place_autocomplete_search_input));
        destinationTextInput.setTextSize(20.0f);
        autocompleteFragmentDestination.setHint(getString(R.string.destination));
        autocompleteFragmentDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.d("gog.debug", "destination: " + place.getName());
                Log.d("gog.debug", "destination:"+ place.getLatLng().longitude+" ,"+place.getLatLng().longitude);
                destination = place.getLatLng();
            }

            @Override
            public void onError(Status status) {
                Log.d("gog.debug", "An error occurred: " + status);
            }
        });



    }

    public void displayAdvancedSearch()
    {
        showAdvancedSearch = !showAdvancedSearch;

        advancedSearchLayout.setVisibility(showAdvancedSearch ? View.VISIBLE : View.GONE);
        if(showAdvancedSearch) {
            toggleAdvancedSearch.setImageResource(R.drawable.ic_expand_less_white_18dp);
        }else {
            toggleAdvancedSearch.setImageResource(R.drawable.ic_expand_more_white_18dp);
        }
    }

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_take_ride, menu);
        return true;
    }
*/
   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/


/*
    private void showProgress(boolean show)
    {
        rideListing.setVisibility(show ? View.GONE : View.VISIBLE);
        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }
*/


    private void setRideListView(){
        RideListingAdapter rideListingAdapter = new RideListingAdapter(this,R.layout.ride_listing,rides);
        rideListing.setAdapter(rideListingAdapter);
        rideListing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d("gog.debug", "clicked ride position " + position);
                if (!session.isLoggedIn()) {
                    Intent loginIntent = new Intent(view.getContext(), LoginActivity.class);
                    Bundle b = new Bundle();
                    b.putString("activityOnSuccess", TakeRideActivity.class.getName());
                    loginIntent.putExtras(b);
                    startActivity(loginIntent);
                }
                else
                {

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
            if(null==source)
            {
                source = currentLocation;
            }

        }

        @Override
        protected Boolean doInBackground(LatLng... params) {
            String sourceStr = null;
            String destinationStr = null;
            if(null!=source) {
            StringBuffer sb = new StringBuffer();
                sb.append(String.format("%.6f",source.longitude));
                sb.append(",");
                sb.append(String.format("%.6f",source.latitude));
             sourceStr =  sb.toString();
            }
            if(null!=destination) {
                StringBuffer sb = new StringBuffer();
                sb.append(String.format("%.6f",destination.longitude));
                sb.append(",");
                sb.append(String.format("%.6f",destination.latitude));
                destinationStr = sb.toString();
            }
            Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy'T'HH:mm").create();
            HttpRestUtil httpRestUtil = new HttpRestUtil(getApplicationContext());
            StringBuffer url = new StringBuffer();
            url.append("shareRideService/rides/locationTime");
            if(null!=sourceStr)
            {
                if(url.toString().contains("?"))
                {
                    url.append("&") ;
                }
                else
                {
                    url.append("?") ;
                }
                url.append("source=");
                url.append(sourceStr);
            }
            if(null!=destinationStr)
            {
                if(url.toString().contains("?"))
                {
                   url.append("&") ;
                }
                else
                {
                    url.append("?") ;
                }
                url.append("destination=");
                url.append(destinationStr);
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
                ride.setInterested(false);
                Log.d("gog.debug","Interested rides "+CurrentUserInterestedRides.getInstance().getRides());
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
