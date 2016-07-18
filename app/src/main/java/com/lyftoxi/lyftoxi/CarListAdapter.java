package com.lyftoxi.lyftoxi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.lyftoxi.lyftoxi.dao.Car;
import com.lyftoxi.lyftoxi.dao.User;
import com.lyftoxi.lyftoxi.singletons.CurrentUserInfo;
import com.lyftoxi.lyftoxi.util.Constants;
import com.lyftoxi.lyftoxi.util.HttpRestUtil;
import com.lyftoxi.lyftoxi.util.RoundImage;
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
import java.util.List;


public class CarListAdapter extends ArrayAdapter<CarInfo> {

    private List<CarInfo> cars;

    public CarListAdapter(Context context, int resource, List<CarInfo> cars) {
        super(context, resource, cars);
        this.cars = cars;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.car_listing, parent, false);
        }

        final CarInfo carInfo = cars.get(position);
        if (carInfo != null) {
            Log.d("lyftoxi.debug","car adapter "+carInfo.getCarNo());
            ImageView carImage = (ImageView) v.findViewById(R.id.carListImage);
            TextView carBrand = (TextView) v.findViewById(R.id.carListingBrand);
            TextView carNumber = (TextView) v.findViewById(R.id.carListingNumber);
            TextView carModel = (TextView) v.findViewById(R.id.carListingModel);

            Bitmap bm;
            String carLogogName = Util.getResourceNameFromDisplayName(carInfo.getCarBrand());
            if(null!=carLogogName && !carLogogName.trim().equals(""))
            {
                int id = v.getResources().getIdentifier(carLogogName, "drawable", v.getContext().getPackageName());
                bm = BitmapFactory.decodeResource(v.getResources(),id);
                if(null==bm)
                {
                    bm = BitmapFactory.decodeResource(v.getResources(),R.drawable.my_brand);
                }

            }
            else
            {
                bm = BitmapFactory.decodeResource(v.getResources(),R.drawable.my_brand);
            }


           // RoundImage roundedImage = new RoundImage(bm);
            carImage.setImageBitmap(bm);

            final View progressBar = v.findViewById(R.id.carListingProgress);

            ImageButton editCarBtn = (ImageButton) v.findViewById(R.id.carListingEditCar);
            editCarBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent addEditCarIntent = new Intent(view.getContext(),CarAddEdit.class);
                    addEditCarIntent.putExtra("carToEditNo", carInfo.getCarNo());
                    view.getContext().startActivity(addEditCarIntent);
                }
            });

            final ImageButton deleteCarBtn = (ImageButton) v.findViewById(R.id.carListingDeleteCar);
            deleteCarBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(view.getContext())

                            .setMessage(R.string.car_delete_message)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    SaveCarDetailsTask saveCarDetailsTask =  new SaveCarDetailsTask();
                                    saveCarDetailsTask.progressBar = progressBar;
                                    saveCarDetailsTask.deleteBtn = deleteCarBtn;
                                    saveCarDetailsTask.execute(carInfo.getCarNo());
                                }})
                            .setNegativeButton(android.R.string.no, null).show();

                }
            });

            carBrand.setText(carInfo.getCarBrand());
            carNumber.setText(carInfo.getCarNo());
            carModel.setText(carInfo.getCarModel());

        }

        return v;
    }

    public CarInfo getItem(int position){

        return cars.get(position);
    }

    public class SaveCarDetailsTask extends AsyncTask<String, Void, Boolean> {
        View progressBar;
        ImageButton deleteBtn;
        private String carNumber;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            deleteBtn.setVisibility(View.GONE);
        }

        @Override
        protected Boolean doInBackground(String... params) {

            carNumber = params[0];
            if(null == carNumber)
            {
                return false;
            }
            CurrentUserInfo currentUser = CurrentUserInfo.getInstance();
            User user = new User();

            user.setId(currentUser.getId());
            user.setCarDetails(currentUser.getCarDetails());
            user.setAddresses(currentUser.getAddresses());
            for(Car tmpCar : user.getCarDetails()) {
                if (carNumber.equals(tmpCar.getCarNo())) {
                    user.getCarDetails().remove(tmpCar);
                    break;
                }
            }

            Log.d("lyftoxi.debug","user "+user);
            Gson gson = new GsonBuilder().setDateFormat(Constants.SIMPLE_DATE_FORMAT).create();
            Object carInfoJson = gson.toJson(user);

            Log.d("lyftoxi.debug","user Info "+carInfoJson.toString());
            try {

                HttpRestUtil httpRestUtil = new HttpRestUtil(getContext());
                String response = httpRestUtil.httpPut("userService/user",carInfoJson);
                if(null!=response)
                {
                    return true;
                }

            }catch (IOException ioex)
            {
                Log.d("lyftoxi.debug","Error occurred in REST WS call url cannot be reached "+ioex.getMessage());
            }
            catch (Exception ex)
            {
                Log.d("lyftoxi.debug","Error occurred in REST WS call "+ex.getMessage());
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(success)
            {
                progressBar.setVisibility(View.GONE);
                for(CarInfo tmpCar : cars)
                {
                    if(carNumber.equals(tmpCar.getCarNo()))
                    {
                        cars.remove(tmpCar);
                        break;
                    }
                }
                CarListAdapter.this.notifyDataSetChanged();
            }
            else
            {
                progressBar.setVisibility(View.GONE);
                deleteBtn.setVisibility(View.VISIBLE);
            }
        }


        @Override
        protected void onCancelled() {

        }
    }

    }
