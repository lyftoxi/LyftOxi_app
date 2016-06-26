package com.lyftoxi.lyftoxi;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lyftoxi.lyftoxi.dao.Car;
import com.lyftoxi.lyftoxi.dao.User;
import com.lyftoxi.lyftoxi.singletons.CurrentUserInfo;
import com.lyftoxi.lyftoxi.util.HttpRestUtil;
import com.lyftoxi.lyftoxi.util.Util;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CarAddEdit extends BaseActivity {

    private CarInfo carInfo;
    private EditText otherBrand, model,number,color;
    private Spinner brand;
    private CheckBox smokingAllowed, luggageAllowed, acAvailable, airbagAvailable, musicAvailable;

    private Button saveBtn;

    private String[] brands;

    private static final String  CAR_NUMBER_PATTERN = "^[A-Z]{2}\\s[0-9]{2}\\s[A-Z]{2}\\s[0-9]{4}$";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_add_edit);


        brand = (Spinner) findViewById(R.id.carAddEditCarBrand);

        otherBrand= (EditText) findViewById(R.id.carAddEditCarBrandOther);
        model= (EditText) findViewById(R.id.carAddEditCarModel);
        number= (EditText) findViewById(R.id.carAddEditCarNumber);
        color= (EditText) findViewById(R.id.carAddEditCarColor);
        airbagAvailable = (CheckBox) findViewById(R.id.carAddEditRadioAirbag);
        smokingAllowed = (CheckBox) findViewById(R.id.carAddEditRadioSmoking);
        acAvailable = (CheckBox) findViewById(R.id.carAddEditRadioAc);
        musicAvailable = (CheckBox) findViewById(R.id.carAddEditRadioMusic);
        luggageAllowed = (CheckBox) findViewById(R.id.carAddEditRadioLuggage);

        brands =  getResources().getStringArray(R.array.car_brands);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.car_brands, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brand.setAdapter(adapter);

        brand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedBrand = adapterView.getItemAtPosition(position).toString();
               if("Other".equalsIgnoreCase(selectedBrand))
               {
                   otherBrand.setVisibility(View.VISIBLE);
                   carInfo.setCarBrand("Other");
               }
                else
               {
                   otherBrand.setVisibility(View.GONE);
                   carInfo.setCarBrand(selectedBrand);
               }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        saveBtn =(Button) findViewById(R.id.carAddEditSave);

        airbagAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                    carInfo.setAirbagAvailable(true);
                }
                else if (!((CheckBox) view).isChecked()) {
                    carInfo.setAirbagAvailable(false);
                }
            }
        });

        acAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                    carInfo.setAcAvailable(true);
                }
                else  if (!((CheckBox) view).isChecked()){
                    carInfo.setAcAvailable(false);
                }
            }
        });

        smokingAllowed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                    carInfo.setSmokingAllowed(true);
                }
                else if (!((CheckBox) view).isChecked())
                {
                    carInfo.setSmokingAllowed(false);
                }
            }
        });

        musicAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                    carInfo.setMusicAvailable(true);
                }
                else if (!((CheckBox) view).isChecked())
                {
                    carInfo.setMusicAvailable(false);
                }
            }
        });

        luggageAllowed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                    carInfo.setLuggageAllowed(true);
                }
                else if (!((CheckBox) view).isChecked())
                {
                    carInfo.setLuggageAllowed(false);
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isValidInputs())
                {
                    return;
                }

                if(carInfo.getCarBrand().equalsIgnoreCase("Other"))
                {
                    carInfo.setCarBrand(otherBrand.getText().toString());
                }
                carInfo.setCarNo(number.getText().toString());
                carInfo.setCarModel(model.getText().toString());
                carInfo.setCarColor(color.getText().toString());
                carInfo.setSmokingAllowed(smokingAllowed.isChecked());
                carInfo.setAirbagAvailable(airbagAvailable.isChecked());
                carInfo.setAcAvailable(acAvailable.isChecked());
                carInfo.setMusicAvailable(musicAvailable.isChecked());
                carInfo.setLuggageAllowed(luggageAllowed.isChecked());
                SaveCarDetailsTask saveCarDetailsTask = new SaveCarDetailsTask();
                saveCarDetailsTask.execute((Void) null);

            }
        });


        Bundle extras = getIntent().getExtras();
        brand.setSelection(0);
        //Editing Car
        if(null!=extras)
        {
            String carNumber = extras.getString("carToEditNo");
            if(null!= carNumber)
            {
                this.carInfo = getCarInfo(carNumber);
                if(null!=carInfo)
                {

                    if(null!=brands)
                    {
                        boolean isOtherBrand = true;
                            for (int i = 0; i < brands.length; i++) {
                                if (brands[i].equalsIgnoreCase(carInfo.getCarBrand())) {
                                    brand.setSelection(i);
                                    isOtherBrand=false;
                                    otherBrand.setVisibility(View.GONE);
                                    break;
                                }
                            }
                        if(isOtherBrand)
                        {
                            brand.setSelection(brands.length -1);
                            otherBrand.setVisibility(View.VISIBLE);
                            otherBrand.setText(carInfo.getCarBrand());
                        }
                    }


                    model.setText(carInfo.getCarModel());
                    number.setText(carInfo.getCarNo());
                    color.setText(carInfo.getCarColor());
                    acAvailable.setChecked(carInfo.isAcAvailable());
                    smokingAllowed.setChecked(carInfo.isSmokingAllowed());
                    airbagAvailable.setChecked(carInfo.isAirbagAvailable());
                    musicAvailable.setChecked(carInfo.isMusicAvailable());
                    luggageAllowed.setChecked(carInfo.isLuggageAllowed());


                }
            }
        }
        //Adding new car
        if(null==this.carInfo)
        {
            this.carInfo =  new CarInfo();
        }

    }



    private CarInfo getCarInfo(String carNumber)
    {
        CarInfo carInfo = null;

        Car car = CurrentUserInfo.getInstance().getCarByNumber(carNumber);
        if(null!=car)
        {
            carInfo = new CarInfo();
            carInfo.setAirbagAvailable(car.isAirbagAvailable());
            carInfo.setMusicAvailable(car.isMusicAvailable());
            carInfo.setAcAvailable(car.isAcAvailable());
            carInfo.setLuggageAllowed(car.isLuggageAllowed());
            carInfo.setSmokingAllowed(car.isSmokingAllowed());
            carInfo.setCarBrand(car.getCarBrand());
            carInfo.setCarModel(car.getCarModel());
            carInfo.setCarNo(car.getCarNo());
            carInfo.setNoOfSeat(car.getNoOfSeat());
            carInfo.setCarColor(car.getCarColor());

        }


        return carInfo;
    }

    private boolean isValidInputs() {

        /*if(null==brand.getText() || brand.getText().toString().trim().equals(""))
        {
            brand.setError("Brand cannot be blank");
            brand.requestFocus();
            return false;
        }*/

        if(carInfo!=null && carInfo.getCarBrand().equalsIgnoreCase("NA") )
        {
            brand.requestFocus();
            return false;
        }

        if(carInfo!=null && carInfo.getCarBrand().equalsIgnoreCase("Other") )
        {
            if(null==otherBrand.getText() || otherBrand.getText().toString().trim().equals(""))
            {
                otherBrand.setError("Other Brand cannot be blank");
                otherBrand.requestFocus();
                return false;
            }
        }

        if(null==model.getText() || model.getText().toString().trim().equals(""))
        {
            model.setError("Model cannot be blank");
            model.requestFocus();
            return false;
        }

        if(null==number.getText() || number.getText().toString().trim().equals(""))
        {
            number.setError("Number cannot be blank");
            number.requestFocus();
            return false;
        }

        Pattern pattern = Pattern.compile(CAR_NUMBER_PATTERN);
        Matcher matcher = pattern.matcher(number.getText().toString());
        if(!matcher.matches())
        {
            number.setError("Invalid Number");
            number.requestFocus();
            return false;
        }

        for(Car tmpCar :CurrentUserInfo.getInstance().getCarDetails())
        {
            if(tmpCar.getCarNo().equals(number))
            {
                number.setError("Car with this number already exists");
                number.requestFocus();
                return false;
            }
        }

        if(color==color.getText() || color.getText().toString().trim().equals(""))
        {
            color.setError("Color cannot be blank");
            color.requestFocus();
            return false;
        }

        return true;
    }

    private void startShareRideActrivity()
    {
        Intent shareRide =  new Intent(this,ShareRideActivity.class);
        Bundle b = new Bundle();
        b.putInt("carType", ShareRideActivity.OWN_CAR);
        shareRide.putExtras(b);
        startActivity(shareRide);
    }

    public class SaveCarDetailsTask extends AsyncTask<Void, Void, Boolean> {

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy").create();
        User user = new User();

        @Override
        protected void onPreExecute() {
            showProgress(true);
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            if(null == carInfo)
            {
                return false;
            }
            CurrentUserInfo currentUser = CurrentUserInfo.getInstance();

            user.setId(currentUser.getId());
            Log.d("gog.debug","address "+currentUser.getAddresses());
           // user.setAddresses(currentUser.getAddresses());
            user.setCarDetails(currentUser.getCarDetails());
            boolean newCar= true;
            for(Car tmpCar : user.getCarDetails())
            {
                if(carInfo.getCarNo().equals(tmpCar.getCarNo()))
                {

                    tmpCar.setAirbagAvailable(carInfo.isAirbagAvailable());
                    tmpCar.setMusicAvailable(carInfo.isMusicAvailable());
                    tmpCar.setAcAvailable(carInfo.isAcAvailable());
                    tmpCar.setLuggageAllowed(carInfo.isLuggageAllowed());
                    tmpCar.setSmokingAllowed(carInfo.isSmokingAllowed());
                    tmpCar.setCarBrand(carInfo.getCarBrand());
                    tmpCar.setCarModel(carInfo.getCarModel());
                    tmpCar.setCarNo(carInfo.getCarNo());
                    tmpCar.setNoOfSeat(carInfo.getNoOfSeat());
                    tmpCar.setCarColor(carInfo.getCarColor());
                    newCar = false;
                    break;
                }
            }

            if(newCar)
            {
                Car car = new Car();
                car.setAirbagAvailable(carInfo.isAirbagAvailable());
                car.setMusicAvailable(carInfo.isMusicAvailable());
                car.setAcAvailable(carInfo.isAcAvailable());
                car.setLuggageAllowed(carInfo.isLuggageAllowed());
                car.setSmokingAllowed(carInfo.isSmokingAllowed());
                car.setCarBrand(carInfo.getCarBrand());
                car.setCarModel(carInfo.getCarModel());
                car.setCarNo(carInfo.getCarNo());
                car.setNoOfSeat(carInfo.getNoOfSeat());
                car.setCarColor(carInfo.getCarColor());

                user.getCarDetails().add(car);
            }
            Object carInfoJson = gson.toJson(user);
            try {

                HttpRestUtil httpRestUtil = new HttpRestUtil(getApplicationContext());
                String response = httpRestUtil.httpPut("userService/user",carInfoJson);
                if(null!=response)
                {
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
        protected void onPostExecute(final Boolean success) {
            showProgress(false);
            Toast toast;
            if (success) {
                toast = Toast.makeText(getApplicationContext(), "Car saved successfully", Toast.LENGTH_LONG);
                CurrentUserInfo.getInstance().setCarDetails(user.getCarDetails());
                startShareRideActrivity();

            } else {
                toast = Toast.makeText(getApplicationContext(), "Car saving failed. Try Again", Toast.LENGTH_LONG);

            }
            toast.show();
            finish();

        }


        @Override
        protected void onCancelled() {

        }
    }

}
