package com.lyftoxi.lyftoxi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    private RecyclerView shareRideCarRecyclerView;
    static final int OWN_CAR=0;
    static final int RENTED_CAR=1;
    private Button addCarBtn;
    private Dialog dialog;
    private CarListingRecyclerAdapter mAdapter;
    private List<CarInfo> carInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_ride);
        if(!session.isLoggedIn())
        {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            Bundle b = new Bundle();
            b.putString("activityOnSuccess", ShareRideActivity.class.getName());
            loginIntent.putExtras(b);
            startActivity(loginIntent);
            finish();
        }
        List<Car> cars = CurrentUserInfo.getInstance().getCarDetails();
        addCarBtn = (Button)findViewById(R.id.sharingRideAddCar);
        addCarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addEditCarIntent = new Intent(view.getContext(),CarAddEdit.class);
                startActivity(addEditCarIntent);
            }
        });

        /*carListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("lyftoxi.debug","Item Clicked "+i);
                CarInfo selectedCar = (CarInfo)adapterView.getItemAtPosition(i);
                RideInfo.getInstance().reset();
                RideInfo.getInstance().setCar(selectedCar);
                Intent intent = new Intent(view.getContext(),CreateRouteActivity.class);
                startActivity(intent);
            }
        });*/
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
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.select_car_type_layout);
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
        carInfos = new ArrayList<CarInfo>();
        for(Car car : cars) {
            CarInfo carInfo = new CarInfo();
            Log.d("lyftoxi.debug","car sharing ride "+car.getCarNo());
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
        if(null!=dialog) {
            dialog.dismiss();
        }
        shareRideCarRecyclerView = (RecyclerView) findViewById(R.id.shareRideCarRecyclerView);
        mAdapter = new CarListingRecyclerAdapter(carInfos);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        shareRideCarRecyclerView.setLayoutManager(mLayoutManager);
        shareRideCarRecyclerView.setItemAnimator(new DefaultItemAnimator());
        shareRideCarRecyclerView.setAdapter(mAdapter);
        shareRideCarRecyclerView.setNestedScrollingEnabled(false);
        mAdapter.notifyDataSetChanged();
    }

    private void showRentedCars()
    {
        RideInfo.reset();
        CarInfo rentedCar =  new CarInfo();
        rentedCar.setCarBrand("(Rented)");
        rentedCar.setCarModel("Car");
        rentedCar.setCarNo("");
        RideInfo.getInstance().setCar(rentedCar);
        Intent rentedCarIntent = new Intent(this,RentedCarActivity.class);
        startActivity(rentedCarIntent);
    }

}
