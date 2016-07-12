package com.lyftoxi.lyftoxi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.lyftoxi.lyftoxi.dao.Car;
import com.lyftoxi.lyftoxi.singletons.CurrentUserInfo;
import com.lyftoxi.lyftoxi.singletons.RideInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RentedCarActivity extends BaseActivity {

    private CarInfo carInfo;
    private Spinner brand;
    private EditText number, otherBrand, model, color;
    private Button saveBtn;

    private static final String  CAR_NUMBER_PATTERN = "^[A-Z]{2}[\\s|.|-]*[0-9]+[\\s|.|-]*[A-Z]*[\\s|.|-]*[0-9]{4}$";

    private String[] brands;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rented_car);

        carInfo = RideInfo.getInstance().getCar();

        brand = (Spinner) findViewById(R.id.rentedCarBrand);
        otherBrand = (EditText) findViewById(R.id.rentedCarBrandOther);
        model = (EditText) findViewById(R.id.rentedCarModel);
        number = (EditText) findViewById(R.id.rentedCarNumber);
        color = (EditText) findViewById(R.id.rentedCarColor);

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

        saveBtn =(Button) findViewById(R.id.rentedCarSave);
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
                if(!carInfo.getCarBrand().contains("Rented")) {
                    carInfo.setCarBrand("(Rented) " + carInfo.getCarBrand());
                }
                if(null!=model.getText() && !model.getText().toString().trim().equals(""))
                {
                    carInfo.setCarModel(model.getText().toString());
                }
                if(null!=number.getText() && !number.getText().toString().trim().equals("")) {
                    carInfo.setCarNo(number.getText().toString());
                }
                if(null!=color.getText() && !color.getText().toString().trim().equals("")) {
                    carInfo.setCarColor(color.getText().toString());
                }
                RideInfo.getInstance().setCar(carInfo);

                Intent createRideIntent = new Intent(view.getContext(),CreateRouteActivity.class);
                startActivity(createRideIntent);

            }
        });

        brand.setSelection(brands.length -1);
        otherBrand.setVisibility(View.VISIBLE);
        otherBrand.setText(carInfo.getCarBrand());

    }


    private boolean isValidInputs() {

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

        if(null!=number.getText() && !number.getText().toString().trim().equals(""))
        {
            Pattern pattern = Pattern.compile(CAR_NUMBER_PATTERN);
            Matcher matcher = pattern.matcher(number.getText().toString());
            if(!matcher.matches())
            {
                number.setError("Invalid Number");
                number.requestFocus();
                return false;
            }
        }
        return true;
    }

}
