package com.lyftoxi.lyftoxi;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.common.collect.Iterators;
import com.lyftoxi.lyftoxi.singletons.RideInfo;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.lyftoxi.lyftoxi.util.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateRouteActivity extends BaseActivity {


    private LatLng source, destination;
    private String sourceName, destinationName;
    private PlaceAutocompleteFragment autocompleteFragmentSource, autocompleteFragmentDestination;
    private SimpleDateFormat sdf,sdf1,sdf2;

    private Button saveRoute;
    private ImageButton changeDate, changeTime;
    private EditText  sourcePlaceholderText, destinationPlaceholderText;
    private TextView startDate, startTime;
    private static final int DATE_PICKER_ID = 1111;
    private static final int TIME_PICKER_ID = 1112;
    private RideInfo rideInfo = RideInfo.getInstance();

    private Calendar startDateTime = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_route);

        changeDate = (ImageButton)findViewById(R.id.createRouteDatePicker);
        changeTime = (ImageButton)findViewById(R.id.createRouteTimePicker);
        sdf  = new SimpleDateFormat(Constants.DATE_TIME_FORMAT_12HR_FORMAT);
        sdf1 = new SimpleDateFormat(Constants.SIMPLE_DATE_FORMAT);
        sdf2 = new SimpleDateFormat("h:mm a");
        sdf.setLenient(false);
        sdf1.setLenient(false);
        sdf2.setLenient(false);
        startDate = (TextView)findViewById(R.id.createRouteDate);
        startTime = (TextView)findViewById(R.id.createRouteTime);
        Log.i("Lyftoxi.info","startDate:"+startDate);
        Log.i("Lyftoxi.info","startTime:"+startTime);

        //startDate.setText(sdf.format(startDateTime.getTime()));

        saveRoute = (Button) findViewById(R.id.createRouteSave);
        saveRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isValidInputs())
                {
                    return;
                }

                rideInfo.setSource(source);
                rideInfo.setSourceName(sourceName);
                rideInfo.setDestination(destination);
                rideInfo.setDestinationName(destinationName);
                rideInfo.setRideOf(session.getUserDetails());
                try {
                    String startingTIme = startDate.getText().toString()+" "+startTime.getText().toString();
                    Log.d("lyftoxi.debug","startingTIme "+startingTIme);
                    rideInfo.setStarTime(sdf.parse(startingTIme));
                }
                catch (ParseException pex)
                {
                    Log.e("lyftoxi.error",pex.getMessage());
                }

                Log.d("lyftoxi.debug",rideInfo.toString());
                Intent otherRideDetails = new Intent(view.getContext(),OtherRideDetailsActivity.class);
                view.getContext().startActivity(otherRideDetails);
            }
        });


        changeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_PICKER_ID);
            }
        });

        changeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(TIME_PICKER_ID);
            }
        });

        autocompleteFragmentSource = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.createRouteSource);
        autocompleteFragmentSource.setHint(getString(R.string.source));
        ((EditText)autocompleteFragmentSource.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(20.0f);
        autocompleteFragmentSource.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.d("lyftoxi.debug", "Source: " + place.getName());
                Log.d("lyftoxi.debug", "Source:" + place.getLatLng().longitude + " ," + place.getLatLng().longitude);
                source = place.getLatLng();
                sourceName = place.getName().toString();
            }

            @Override
            public void onError(Status status) {
                Log.d("lyftoxi.debug", "An error occurred: " + status);
            }
        });


        autocompleteFragmentDestination = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.createRouteDestination);
        autocompleteFragmentDestination.setHint(getString(R.string.destination));
        ((EditText)autocompleteFragmentDestination.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(20.0f);
        autocompleteFragmentDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.d("lyftoxi.debug", "destination: " + place.getName());
                Log.d("lyftoxi.debug", "destination:"+ place.getLatLng().longitude+" ,"+place.getLatLng().longitude);
                destination = place.getLatLng();
                destinationName = place.getName().toString();
            }

            @Override
            public void onError(Status status) {
                Log.d("lyftoxi.debug", "An error occurred: " + status);
            }
        });

        sourcePlaceholderText = ((EditText)autocompleteFragmentSource.getView().findViewById(R.id.place_autocomplete_search_input));
        destinationPlaceholderText = ((EditText)autocompleteFragmentDestination.getView().findViewById(R.id.place_autocomplete_search_input));
    }

    private boolean isValidInputs() {

        if(null==startDate.getText() || startDate.getText().toString().trim().equals(""))
        {
            startDate.setError(getString(R.string.error_message_start_date_bkank));
            startDate.requestFocus();
            return false;
        }
        if(null==startTime.getText() || startTime.getText().toString().trim().equals(""))
        {
            startTime.setError(getString(R.string.error_message_start_time_bkank));
            startTime.requestFocus();
            return false;
        }
        try {
            //Date startDateTime = sdf1.parse(startDate.getText().toString())+sdf2.parse(startTime.getText().toString()));
            Date startDateTime = (Date)sdf.parse(startDate.getText().toString()+' '+startTime.getText().toString());
            Log.i("Lyftoxi.info","startDateTime:"+startDateTime);
            if((startDateTime.getTime() - new Date().getTime())/(60*1000)<15)
            {
                Log.e("lyftoxi.error","time diff in minutes "+(startDateTime.getTime() - new Date().getTime())/(60*1000));
                startDate.setError(getString(R.string.error_message_start_time_15_mins));
                startDate.requestFocus();
                return false;
            }

            if((startDateTime.getTime() - new Date().getTime())/(24*60*60*1000)>60)
            {
                Log.e("lyftoxi.error","time diff in days "+(startDateTime.getTime() - new Date().getTime())/(24*60*60*1000));
                startTime.setError(getString(R.string.error_message_start_time_120_days));
                startTime.requestFocus();
                return false;
            }
            Calendar myTime =  Calendar.getInstance();
            myTime.setTime(startDateTime);
            Log.d("lyftoxi.debug","Time Zone "+myTime.getTimeZone().getDisplayName());
            rideInfo.setStarTime(startDateTime);
        }
        catch (ParseException pex)
        {
            Log.e("lyftoxi.error",pex.getMessage());
            startDate.setError("Invalid Start Time");
            startDate.requestFocus();
            return false;
        }

        if(null==sourcePlaceholderText.getText() || sourcePlaceholderText.getText().toString().trim().equals(""))
        {
            Toast toast = Toast.makeText(this,"Source cannot be blank", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        if(null==destinationPlaceholderText.getText() || destinationPlaceholderText.getText().toString().trim().equals(""))
        {
            Toast toast = Toast.makeText(this,"Destination cannot be blank", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        if(null!=source && source.equals(destination))
        {
            Toast toast = Toast.makeText(this,"Source and Destination both cannot be blank", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id) {


        int year  = startDateTime.get(Calendar.YEAR);
        int month = startDateTime.get(Calendar.MONTH);
        int day   = startDateTime.get(Calendar.DAY_OF_MONTH);
        int hour  = startDateTime.get(Calendar.HOUR_OF_DAY);
        int minute  = startDateTime.get(Calendar.MINUTE);

        switch (id) {
            case DATE_PICKER_ID:
                startDate.setError(null);
                return new DatePickerDialog(this, pickerListener, year, month,day);
            case TIME_PICKER_ID:
                startTime.setError(null);
                return new TimePickerDialog(this, timePickerListener, hour, minute,
                false);
    }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            startDateTime.set(selectedYear,selectedMonth,selectedDay);
            startDate.setText(sdf1.format(startDateTime.getTime()));

        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {


        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            startDateTime.set(Calendar.HOUR_OF_DAY,hourOfDay);
            startDateTime.set(Calendar.MINUTE,minutes);
            //startDate.setText(sdf.format(startDateTime.getTime()));
            startTime.setText(sdf2.format(startDateTime.getTime()));
        }

    };

}
