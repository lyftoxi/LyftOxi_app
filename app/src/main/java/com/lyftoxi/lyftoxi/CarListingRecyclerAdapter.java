package com.lyftoxi.lyftoxi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lyftoxi.lyftoxi.dao.Car;
import com.lyftoxi.lyftoxi.dao.User;
import com.lyftoxi.lyftoxi.exception.LyftoxiClientBusinessException;
import com.lyftoxi.lyftoxi.exception.LyftoxiClientException;
import com.lyftoxi.lyftoxi.singletons.CurrentUserInfo;
import com.lyftoxi.lyftoxi.singletons.RideInfo;
import com.lyftoxi.lyftoxi.util.Constants;
import com.lyftoxi.lyftoxi.util.HttpRestUtil;
import com.lyftoxi.lyftoxi.util.Util;

import java.io.IOException;
import java.util.List;

public class CarListingRecyclerAdapter extends RecyclerView.Adapter<CarListingRecyclerAdapter.CarViewHolder>{

    private List<CarInfo> cars;

    public class CarViewHolder extends RecyclerView.ViewHolder {

        ImageView carImage;
        TextView carBrand;
        TextView carNumber;
        TextView carModel;
        View progressBar;
        ImageButton editCarBtn;
        ImageButton deleteCarBtn;
        public CarViewHolder(View v) {
            super(v);
            carImage = (ImageView) v.findViewById(R.id.carListImage);
            carBrand = (TextView) v.findViewById(R.id.carListingBrand);
            carNumber = (TextView) v.findViewById(R.id.carListingNumber);
            carModel = (TextView) v.findViewById(R.id.carListingModel);
            progressBar = v.findViewById(R.id.carListingProgress);
            editCarBtn = (ImageButton) v.findViewById(R.id.carListingEditCar);
            deleteCarBtn = (ImageButton) v.findViewById(R.id.carListingDeleteCar);

        }
    }

    public CarListingRecyclerAdapter(List<CarInfo> cars)
    {
        this.cars = cars;
    }

    @Override
    public CarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.car_listing, parent, false);
        return new CarViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CarViewHolder holder, final int position) {
        Log.d("lyftoxi.debug","cars "+cars);
        final CarInfo carInfo = cars.get(position);
        if(null!=carInfo)
        {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RideInfo.getInstance().reset();
                    RideInfo.getInstance().setCar(carInfo);
                    Intent intent = new Intent(view.getContext(),CreateRouteActivity.class);
                    view.getContext().startActivity(intent);
                }
            });

            Log.d("lyftoxi.debug","car recycler "+carInfo.getCarNo());
            Bitmap bm;
            String carLogogName = Util.getResourceNameFromDisplayName(carInfo.getCarBrand());
            if(null!=carLogogName && !carLogogName.trim().equals(""))
            {
                int id = holder.carImage.getContext().getResources().getIdentifier(carLogogName,
                        "drawable", holder.carImage.getContext().getPackageName());
                bm = BitmapFactory.decodeResource(holder.carImage.getContext().getResources(),id);
                if(null==bm)
                {
                    bm = BitmapFactory.decodeResource(holder.carImage.getContext().getResources(),R.drawable.my_brand);
                }

            }
            else
            {
                bm = BitmapFactory.decodeResource(holder.carImage.getContext().getResources(),R.drawable.my_brand);
            }

            holder.carImage.setImageBitmap(bm);
            holder.editCarBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent addEditCarIntent = new Intent(view.getContext(),CarAddEdit.class);
                    addEditCarIntent.putExtra("carToEditNo", carInfo.getCarNo());
                    view.getContext().startActivity(addEditCarIntent);
                }
            });
            if(!carInfo.isShowDeleteButton())
            {
                holder.deleteCarBtn.setVisibility(View.GONE);
            }
            else
            {
                holder.deleteCarBtn.setVisibility(View.VISIBLE);
            }

            if(!carInfo.isShowProgress())
            {
                holder.progressBar.setVisibility(View.GONE);
            }
            else
            {
                holder.progressBar.setVisibility(View.VISIBLE);
            }
            holder.deleteCarBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    new AlertDialog.Builder(view.getContext())

                            .setMessage(R.string.car_delete_message)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    SaveCarDetailsTask saveCarDetailsTask =  new SaveCarDetailsTask();
                                    saveCarDetailsTask.context = view.getContext();
                                    saveCarDetailsTask.tmpCarinfo = carInfo;
                                    saveCarDetailsTask.execute();

                                }})
                            .setNegativeButton(android.R.string.no, null).show();

                }
            });

            holder.carBrand.setText(carInfo.getCarBrand());
            holder.carNumber.setText(carInfo.getCarNo());
            holder.carModel.setText(carInfo.getCarModel());

        }
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }

    public class SaveCarDetailsTask extends AsyncTask<Void, Void, Boolean> {
        CarInfo tmpCarinfo;
        private String carNumber;
        String errorMessage;
        Context context;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("lyftoxi.debug","position "+tmpCarinfo.getCarNo());
            tmpCarinfo.setShowDeleteButton(false);
            tmpCarinfo.setShowProgress(true);
            notifyDataSetChanged();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            carNumber = tmpCarinfo.getCarNo();
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

                HttpRestUtil httpRestUtil = new HttpRestUtil(context);
                String response = httpRestUtil.httpPut("userService/user",carInfoJson);
                if(null!=response)
                {
                    return true;
                }

            }catch (IOException ioex) {
                Log.e("lyftoxi.error","Error occurred in REST WS call url cannot be reached "+ioex.getMessage());
                errorMessage = "Service Unavailable";
            }
            catch (LyftoxiClientBusinessException e) {
                Log.e("lyftoxi.error","Business Exception occurred in REST WS call "+e.getMessage());
                errorMessage = e.getMessage();
            }
            catch (LyftoxiClientException e) {
                Log.e("lyftoxi.error","Error occurred in REST WS call "+e.getMessage());
                errorMessage = "Some thing wrong happened.Contact support";
            }
            catch (Exception e) {
                Log.e("lyftoxi.error","Something really went wrong "+e.getMessage());
                errorMessage = "OMG you got us a defect. Contact support with screenshot";
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            tmpCarinfo.setShowProgress(false);
            if(success)
            {

                for(CarInfo tmpCar : cars)
                {
                    if(carNumber.equals(tmpCar.getCarNo()))
                    {
                        cars.remove(tmpCar);
                        break;
                    }
                }
            }
            else
            {
                tmpCarinfo.setShowDeleteButton(true);
            }
            notifyDataSetChanged();

        }


        @Override
        protected void onCancelled() {

        }
    }
}
