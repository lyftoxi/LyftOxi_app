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
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.common.collect.Iterators;
import com.lyftoxi.lyftoxi.singletons.RideInfo;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateRouteActivity extends BaseActivity {


    private LatLng source, destination;
    private String sourceName, destinationName;
    private PlaceAutocompleteFragment autocompleteFragmentSource, autocompleteFragmentDestination;
    private SimpleDateFormat sdf;

    private Button saveRoute;
    private ImageButton changeDate, changeTime;
    private EditText startDate,sourcePlaceholderText, destinationPlaceholderText;
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
        sdf  = new SimpleDateFormat("dd-MM-yyyy h:mm a");
        sdf.setLenient(false);
        startDate = (EditText)findViewById(R.id.createRouteDate);


        startDate.setText(sdf.format(startDateTime.getTime()));

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
                    String startingTIme = startDate.getText().toString();
                    Log.d("gog.debug","startingTIme "+startingTIme);
                    rideInfo.setStarTime(sdf.parse(startingTIme));
                }
                catch (ParseException pex)
                {
                    Log.e("gog.error",pex.getMessage());
                }

                Log.d("gog.debug",rideInfo.toString());
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
                Log.d("gog.debug", "Source: " + place.getName());
                Log.d("gog.debug", "Source:" + place.getLatLng().longitude + " ," + place.getLatLng().longitude);
                source = place.getLatLng();
                sourceName = place.getName().toString();
            }

            @Override
            public void onError(Status status) {
                Log.d("gog.debug", "An error occurred: " + status);
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
                Log.d("gog.debug", "destination: " + place.getName());
                Log.d("gog.debug", "destination:"+ place.getLatLng().longitude+" ,"+place.getLatLng().longitude);
                destination = place.getLatLng();
                destinationName = place.getName().toString();
            }

            @Override
            public void onError(Status status) {
                Log.d("gog.debug", "An error occurred: " + status);
            }
        });

        sourcePlaceholderText = ((EditText)autocompleteFragmentSource.getView().findViewById(R.id.place_autocomplete_search_input));
        destinationPlaceholderText = ((EditText)autocompleteFragmentDestination.getView().findViewById(R.id.place_autocomplete_search_input));
    }

    private boolean isValidInputs() {

        if(null==startDate.getText() || startDate.getText().toString().trim().equals(""))
        {
            startDate.setError("Start Time cannot be blank");
            startDate.requestFocus();
            return false;
        }
        try {
            Date startDateTime = sdf.parse(startDate.getText().toString());

            if((startDateTime.getTime() - new Date().getTime())/(60*1000)<15)
            {
                Log.e("gog.error","time diff in minutes "+(startDateTime.getTime() - new Date().getTime())/(60*1000));
                startDate.setError("Start time must be 15 mins more than current time.");
                startDate.requestFocus();
                return false;
            }

            if((startDateTime.getTime() - new Date().getTime())/(24*60*60*1000)>60)
            {
                Log.e("gog.error","time diff in days "+(startDateTime.getTime() - new Date().getTime())/(24*60*60*1000));
                startDate.setError("Start time must be within 120 days");
                startDate.requestFocus();
                return false;
            }
            rideInfo.setStarTime(startDateTime);
        }
        catch (ParseException pex)
        {
            Log.e("gog.error",pex.getMessage());
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
                return new DatePickerDialog(this, pickerListener, year, month,day);
            case TIME_PICKER_ID:
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
            startDate.setText(sdf.format(startDateTime.getTime()));

        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {


        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            startDateTime.set(Calendar.HOUR_OF_DAY,hourOfDay);
            startDateTime.set(Calendar.MINUTE,minutes);
            startDate.setText(sdf.format(startDateTime.getTime()));

        }

    };

}
