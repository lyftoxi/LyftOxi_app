package com.lyftoxi.lyftoxi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lyftoxi.lyftoxi.dao.Car;
import com.lyftoxi.lyftoxi.singletons.CurrentUserInfo;
import com.lyftoxi.lyftoxi.singletons.RideInfo;

import java.util.ArrayList;
import java.util.List;


public class ShareRideActivity extends BaseActivity {

    private int carType;
    private ListView carListView;

    static final int OWN_CAR=0;
    static final int RENTED_CAR=1;

    private Button addCarBtn;

    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isScrollable=false;
        setContentView(R.layout.activity_share_ride);

        if(!session.isLoggedIn())
        {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            Bundle b = new Bundle();
            b.putString("activityOnSuccess", ShareRideActivity.class.getName());
            loginIntent.putExtras(b);
            startActivity(loginIntent);
        }
        addCarBtn = (Button)findViewById(R.id.sharingRideAddCar);
        addCarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addEditCarIntent = new Intent(view.getContext(),CarAddEdit.class);
                startActivity(addEditCarIntent);
            }
        });

        carListView = (ListView) findViewById(R.id.shareRideCarList);
        carListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("gog.debug","Item Clicked "+i);
                CarInfo selectedCar = (CarInfo)adapterView.getItemAtPosition(i);
                RideInfo.getInstance().reset();
                RideInfo.getInstance().setCar(selectedCar);
                Intent intent = new Intent(view.getContext(),CreateRouteActivity.class);
                startActivity(intent);
            }
        });

        if(null!= getIntent().getExtras()) {
            Bundle b = getIntent().getExtras();
            showCarsByType(b.getInt("carType"));
        }
        else{
            selectCarType(this);
        }




    }


    private void selectCarType(final Context context)
    {
        /*final String [] items           = new String [] {getString(R.string.own_cars), getString(R.string.rented_cars), getString(R.string.back)};
        ArrayAdapter<String> adapter  = new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,items);
        AlertDialog.Builder builder     = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.select_car_type));
        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialog, int item ) {
                carType = item;
                showCarsByType(carType);
            }
        } );

        final AlertDialog dialog = builder.create();*/
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.select_car_type_layout);
        //dialog.setTitle(getString(R.string.select_car_type));


        Button ownCars = (Button) dialog.findViewById(R.id.selectCarTypeOwnCar);
        Button rentedCars = (Button) dialog.findViewById(R.id.selectCarTypeRentedCar);
        Button back = (Button) dialog.findViewById(R.id.selectCarTypeBack);

        ownCars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCarsByType(OWN_CAR);
            }
        });

        rentedCars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCarsByType(RENTED_CAR);
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }


    private void showCarsByType(int carType)
    {
        switch(carType)
        {
            case OWN_CAR:
                showOwnCars();
                break;
            case RENTED_CAR:
                showRentedCars();
                break;
            default:dialog.dismiss();
                finish();

        }
    }


    private void showOwnCars()
    {
        CarListAdapter carListAdapter;
        getSupportActionBar().setTitle(getText(R.string.own_cars));
        List<Car> cars = CurrentUserInfo.getInstance().getCarDetails();
        List<CarInfo> carInfos = new ArrayList<CarInfo>();
        for(Car car : cars) {
            CarInfo carInfo = new CarInfo();
            Log.d("gog.debug","car sharing ride "+car.getCarNo());
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

            carInfos.add(carInfo);
        }
        carListAdapter = new CarListAdapter(this,R.layout.car_listing, carInfos);
        carListView.setAdapter(carListAdapter);
        if(null!=dialog) {
            dialog.dismiss();
        }
    }

    private void showRentedCars()
    {
        RideInfo rideInfo = RideInfo.getInstance();
        CarInfo rentedCar =  new CarInfo();
        rentedCar.setCarBrand("(Rented)");
        rentedCar.setCarModel("Car");
        rentedCar.setCarNo("");
        rideInfo.setCar(rentedCar);

        Intent rentedCarIntent = new Intent(this,RentedCarActivity.class);
        startActivity(rentedCarIntent);
    }





}
