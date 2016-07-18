package com.lyftoxi.lyftoxi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.lyftoxi.lyftoxi.singletons.RideInfo;

import java.text.SimpleDateFormat;

public class OtherRideDetailsActivity extends BaseActivity {

    private TextView userMessage, price;

    /*via,source, destination,carModel,carBrand, carNumber, userMessage, price, startingTime ;*/
    private CheckBox acAvailable, airbagAvailable, musicAvailable, smokingAllowed, luggageAllowed;
    private Button otherRideDetailsDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_ride_details);

        userMessage = (TextView)findViewById(R.id.otherRideDetailsUserMessage);
        price = (TextView)findViewById(R.id.otherRideDetailsPrice);

        acAvailable = (CheckBox) findViewById(R.id.otherRideDetailsRadioAc);
        airbagAvailable = (CheckBox) findViewById(R.id.otherRideDetailsRadioAirbag);
        musicAvailable = (CheckBox) findViewById(R.id.otherRideDetailsRadioMusic);
        smokingAllowed = (CheckBox) findViewById(R.id.otherRideDetailsRadioSmoking);
        luggageAllowed = (CheckBox) findViewById(R.id.otherRideDetailsRadioLuggage);



        RideInfo rideInfo = RideInfo.getInstance();

        getSupportActionBar().setTitle("Ride of "+rideInfo.getRideOf().getName());
        //source.setText(rideInfo.getSourceName());
        /*if(null != rideInfo.getViaName()) {
            via.setText(rideInfo.getViaName());
        }
        else
        {
            via.setVisibility(View.GONE);
            TextView secondPipe = (TextView)findViewById(R.id.otherRideDetailsSecondPipe);
            secondPipe.setVisibility(View.GONE);
        }*/
        /*destination.setText(rideInfo.getDestinationName());
        carBrand.setText(rideInfo.getCar().getCarBrand());
        carModel.setText(rideInfo.getCar().getCarModel());
        carNumber.setText(rideInfo.getCar().getCarNo());
        startingTime.setText(sdf.format(rideInfo.getStarTime()));*/

        if(rideInfo.getFare()>0) {
            price.setText("" + rideInfo.getFare());
        }

        acAvailable.setChecked(rideInfo.getCar().isAcAvailable());
        airbagAvailable.setChecked(rideInfo.getCar().isAirbagAvailable());
        musicAvailable.setChecked(rideInfo.getCar().isMusicAvailable());
        smokingAllowed.setChecked(rideInfo.getCar().isSmokingAllowed());
        luggageAllowed.setChecked(rideInfo.getCar().isLuggageAllowed());

        if(!rideInfo.getCar().getCarBrand().contains("Rented"))
        {
            if(!rideInfo.getCar().isAcAvailable()){acAvailable.setEnabled(false);}
            if(!rideInfo.getCar().isAirbagAvailable()){airbagAvailable.setEnabled(false);}
            if(!rideInfo.getCar().isMusicAvailable()){musicAvailable.setEnabled(false);}
            if(!rideInfo.getCar().isSmokingAllowed()){smokingAllowed.setEnabled(false);}
            if(!rideInfo.getCar().isLuggageAllowed()){luggageAllowed.setEnabled(false);}
        }

        //price.setText(rideInfo.getFare()+"");
        userMessage.setText(rideInfo.getUserMessage());


        otherRideDetailsDetails = (Button)findViewById(R.id.otherRideDetailsSave);

        otherRideDetailsDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isValidInputs())
                {
                    return;
                }
                RideInfo.getInstance().setUserMessage(userMessage.getText().toString());
                RideInfo.getInstance().setFare(Integer.parseInt(price.getText().toString()));
                if(acAvailable.isChecked())
                {
                    RideInfo.getInstance().getCar().setAcAvailable(true);
                }
                else
                {
                    RideInfo.getInstance().getCar().setAcAvailable(false);
                }
                if(smokingAllowed.isChecked())
                {
                    RideInfo.getInstance().getCar().setSmokingAllowed(true);
                }
                else
                {
                    RideInfo.getInstance().getCar().setSmokingAllowed(false);
                }
                if(musicAvailable.isChecked())
                {
                    RideInfo.getInstance().getCar().setMusicAvailable(true);
                }
                else
                {
                    RideInfo.getInstance().getCar().setMusicAvailable(false);
                }
                if(airbagAvailable.isChecked())
                {
                    RideInfo.getInstance().getCar().setAirbagAvailable(true);
                }
                else
                {
                    RideInfo.getInstance().getCar().setAirbagAvailable(false);
                }
                if(luggageAllowed.isChecked())
                {
                    RideInfo.getInstance().getCar().setLuggageAllowed(true);
                }
                else
                {
                    RideInfo.getInstance().getCar().setLuggageAllowed(false);
                }

                Intent confirmRide = new Intent(view.getContext(),ConfirmRideActivity.class);
                startActivity(confirmRide);

            }
        });
    }

    private boolean isValidInputs() {
        if(null==price.getText() || price.getText().toString().trim().equals(""))
        {
            price.setError("Fare cannot be blank");
            price.requestFocus();
            return false;
        }
        try {
            if(Integer.parseInt(price.getText().toString())<0)
            {
                price.setError("Fare should not be less than 0.");
                price.requestFocus();
            }
        }catch(NumberFormatException nex)
        {
            Log.e("gog.error",nex.getMessage());
            price.setError("Fare should be numbers only e.g. 20");
            price.requestFocus();
        }
        return true;
    }
}
