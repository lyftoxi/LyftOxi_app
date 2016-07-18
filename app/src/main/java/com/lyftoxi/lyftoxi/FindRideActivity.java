package com.lyftoxi.lyftoxi;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.lyftoxi.lyftoxi.singletons.RideInfo;
import com.lyftoxi.lyftoxi.util.Constants;
import com.lyftoxi.lyftoxi.util.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FindRideActivity extends BaseActivity {


    private LatLng source, destination;
    private PlaceAutocompleteFragment autocompleteFragmentSource, autocompleteFragmentDestination;
    private SimpleDateFormat sdf;

    private CheckBox ac,music,airBag,luggage,smoking;
    private Button saveRoute;
    private ImageButton changeDate;
    private EditText startDate, sourcePlaceholderText, destinationPlaceholderText;
    private static final int DATE_PICKER_ID = 1111;
    private RideInfo rideInfo = RideInfo.getInstance();


    private Calendar startDateTime = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_ride);

        ac = (CheckBox)findViewById(R.id.findRideRadioAc);
        luggage = (CheckBox)findViewById(R.id.findRideRadioLuggage);
        airBag = (CheckBox)findViewById(R.id.findRideRadioAirbag);
        music = (CheckBox)findViewById(R.id.findRideRadioMusic);
        smoking = (CheckBox)findViewById(R.id.findRideRadioSmoking);

        changeDate = (ImageButton)findViewById(R.id.findRideDatePicker);
        sdf  = new SimpleDateFormat(Constants.SIMPLE_DATE_FORMAT);
        sdf.setLenient(false);
        startDate = (EditText)findViewById(R.id.findRideDate);


        //startDate.setText(sdf.format(startDateTime.getTime()));

        saveRoute = (Button) findViewById(R.id.findRideSubmit);
        saveRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isValidInputs())
                {
                    return;
                }


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
                StringBuffer urlParamaters = new StringBuffer();
                urlParamaters.append("");
                if(null!=sourceStr)
                {
                    Util.addParamToUrl(urlParamaters,"source",sourceStr);
                }
                if(null!=destinationStr)
                {
                    Util.addParamToUrl(urlParamaters,"destination",destinationStr);
                }
                if(null!=startDate.getText())
                {
                   // Util.addParamToUrl(urlParamaters,"dateTime",startDate.getText().toString());
                    try {
                        Log.d("lyftoxi.debug"," date---> "+new SimpleDateFormat(Constants.DATE_TIME_FORMAT_WITH_TIME_ZONE).format(sdf.parse(startDate.getText().toString())));
                        Util.addParamToUrl(urlParamaters,"dateTime",new SimpleDateFormat(Constants.DATE_TIME_FORMAT_WITH_TIME_ZONE).format(sdf.parse(startDate.getText().toString())));
                    }catch(ParseException pe)
                    {
                        pe.printStackTrace();
                    }
                }

                if(ac.isChecked())
                {
                    Util.addParamToUrl(urlParamaters,"ac","true");
                }
                if(airBag.isChecked())
                {
                    Util.addParamToUrl(urlParamaters,"airBag","true");
                }
                if(music.isChecked())
                {
                    Util.addParamToUrl(urlParamaters,"music","true");
                }
                if(luggage.isChecked())
                {
                    Util.addParamToUrl(urlParamaters,"luggage","true");
                }
                if(smoking.isChecked())
                {
                    Util.addParamToUrl(urlParamaters,"smoking","true");
                }

                Intent findRideList = new Intent(view.getContext(),FindRideListActivity.class);
                Bundle b = new Bundle();
                b.putString("searchRequestParams", urlParamaters.toString());
                findRideList.putExtras(b);
                view.getContext().startActivity(findRideList);
            }
        });


        changeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_PICKER_ID);
            }
        });

        autocompleteFragmentSource = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.findRideSource);
        autocompleteFragmentSource.setHint(getString(R.string.source));
        ((EditText)autocompleteFragmentSource.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(20.0f);
        autocompleteFragmentSource.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.d("lyftoxi.debug", "Source: " + place.getName());
                Log.d("lyftoxi.debug", "Source:" + place.getLatLng().longitude + " ," + place.getLatLng().longitude);
                source = place.getLatLng();
            }

            @Override
            public void onError(Status status) {
                Log.d("lyftoxi.debug", "An error occurred: " + status);
            }
        });


        autocompleteFragmentDestination = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.findRideDestination);
        autocompleteFragmentDestination.setHint(getString(R.string.destination));
        ((EditText)autocompleteFragmentDestination.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(20.0f);
        autocompleteFragmentDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.d("lyftoxi.debug", "destination: " + place.getName());
                Log.d("lyftoxi.debug", "destination:"+ place.getLatLng().longitude+" ,"+place.getLatLng().longitude);
                destination = place.getLatLng();
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
            startDate.setError("Start Time cannot be blank");
            startDate.requestFocus();
            return false;
        }
        try {
            Date startDateTime = sdf.parse(startDate.getText().toString());

            if((new Date().getTime() - startDateTime.getTime())/(60*60*1000)>24)
            {
                Log.e("gog.error","time diff in hours "+(startDateTime.getTime() - new Date().getTime())/(60*60*1000));
                startDate.setError("Start time must at least current date");
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
            rideInfo.setStarTime(sdf.parse(startDate.getText().toString()));
        }
        catch (ParseException pex)
        {
            Log.e("gog.error",pex.getMessage());
            startDate.setError("Invalid Start Time");
            startDate.requestFocus();
            return false;
        }




        if((null==sourcePlaceholderText.getText() || sourcePlaceholderText.getText().toString().trim().equals("")) &&
                (null==destinationPlaceholderText.getText() || destinationPlaceholderText.getText().toString().trim().equals("")))
        {

            Toast toast = Toast.makeText(this,"Source and Destination both cannot be blank", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        if(null!=source && source.equals(destination))
        {

            Toast toast = Toast.makeText(this,"Source and Destination cannot be same", Toast.LENGTH_SHORT);
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

        switch (id) {
            case DATE_PICKER_ID:
                return new DatePickerDialog(this, pickerListener, year, month,day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            startDateTime.set(selectedYear,selectedMonth,selectedDay);
            startDate.setText(sdf.format(startDateTime.getTime()));

        }
    };



}
