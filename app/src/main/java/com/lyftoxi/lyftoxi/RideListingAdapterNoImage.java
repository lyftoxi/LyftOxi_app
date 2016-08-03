package com.lyftoxi.lyftoxi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lyftoxi.lyftoxi.dao.Location;
import com.lyftoxi.lyftoxi.dao.TakeRide;
import com.lyftoxi.lyftoxi.exception.LyftoxiClientBusinessException;
import com.lyftoxi.lyftoxi.exception.LyftoxiClientException;
import com.lyftoxi.lyftoxi.singletons.CurrentUserInfo;
import com.lyftoxi.lyftoxi.singletons.RideInfo;
import com.lyftoxi.lyftoxi.util.Constants;
import com.lyftoxi.lyftoxi.util.HttpRestUtil;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class RideListingAdapterNoImage extends ArrayAdapter<RideListingInfo>{

    List<RideListingInfo> rides;

    SessionManager session;

    //private View v;

    public RideListingAdapterNoImage(Context context, int textViewResourceId, List<RideListingInfo> rides){
        super(context, textViewResourceId, rides);
        this.rides=rides;
        this.session = new SessionManager(context);
    }

    public RideListingInfo getItem(int position){

        return rides.get(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //v = inflater.inflate(R.layout.ride_listing_no_user_image, null);
            v = inflater.inflate(R.layout.ride_listing_no_user_image, parent, false);
        }

        final RideListingInfo i = rides.get(position);
        final SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_TIME_FORMAT_12HR_FORMAT);
        final DecimalFormat df = new DecimalFormat("##.######");

        if (i != null) {


            TextView from = (TextView) v.findViewById(R.id.rideListingNoImageFrom);
            TextView to = (TextView) v.findViewById(R.id.rideListingNoImageTo);
            TextView fare = (TextView) v.findViewById(R.id.rideListingNoImageFare);
            TextView startingTime = (TextView) v.findViewById(R.id.rideListingNoImageStartingTime);
            final TextView cancelled = (TextView)v.findViewById(R.id.rideListingNoImageCancelled);
            final ImageButton cancelRideBtn = (ImageButton) v.findViewById(R.id.rideListingNoImageCancel);
            final View progressBar = v.findViewById(R.id.rideListingNoImageProgress);
            TextView interestedUserCount = (TextView) v.findViewById(R.id.rideListingNoImageInterestedUserCount);
            final ImageButton editRide = (ImageButton) v.findViewById(R.id.rideListingNoImageEdit);

            from.setText(i.getSourceName());
            to.setText(i.getDestinationName());
            String fareStr = " "+i.getFare();
            fare.setText(fareStr);
            startingTime.setText(sdf.format(i.getStarTime().getTime()));
            if(i.getStarTime().after(new Date()))
            {
                if("C".equals(i.getStatus()))
                {
                    cancelled.setVisibility(View.VISIBLE);
                    cancelRideBtn.setVisibility(View.GONE);
                    editRide.setVisibility(View.GONE);
                }
                else{
                    cancelled.setVisibility(View.GONE);
                    cancelRideBtn.setVisibility(View.VISIBLE);
                    editRide.setVisibility(View.VISIBLE);
                }
            }
            else
            {
                cancelRideBtn.setVisibility(View.GONE);
                editRide.setVisibility(View.GONE);
                if("C".equals(i.getStatus())) {
                    cancelled.setVisibility(View.VISIBLE);
                } else {
                    cancelled.setVisibility(View.GONE);
                }
            }
            interestedUserCount.setText(i.getInterestedUserCount()+" ");

            editRide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RideInfo rideInfo = RideInfo.getInstance();
                    rideInfo.reset();
                    rideInfo = RideInfo.getInstance();
                    rideInfo.setId(i.getId());
                    rideInfo.setSource(i.getSource());
                    rideInfo.setSourceName(i.getSourceName());
                    rideInfo.setDestination(i.getDestination());
                    rideInfo.setDestinationName(i.getDestinationName());
                    rideInfo.setRideOf(i.getRideOf());
                    rideInfo.setStarTime(i.getStarTime());
                    rideInfo.setFare(i.getFare());
                    rideInfo.setUserMessage(i.getUserMessage());
                    rideInfo.setCar(i.getCar());
                    rideInfo.setStatus(i.getStatus());
                    rideInfo.setInterestedUserCount(i.getInterestedUserCount());

                    Intent otherRideDetailsActivity = new Intent(view.getContext(),OtherRideDetailsActivity.class);
                    view.getContext().startActivity(otherRideDetailsActivity);
                }
            });




        cancelRideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new AlertDialog.Builder(view.getContext())

                        .setMessage(R.string.cancel_ride)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                DeleteRideTask deleteRide = new DeleteRideTask();
                                deleteRide.progressBar = progressBar;
                                deleteRide.cancelledText = cancelled;
                                deleteRide.cancelBtn = cancelRideBtn;
                                deleteRide.editBtn = editRide;
                                deleteRide.execute(i.getId());
                            }})
                        .setNegativeButton(android.R.string.no, null).show();

            }
        });
        }


        return v;

    }



    public class DeleteRideTask extends AsyncTask<String, Void,Boolean>
    {
        String errorMessage;
        Gson gson = new GsonBuilder().setDateFormat(Constants.SIMPLE_DATE_FORMAT).create();
        View progressBar;
        TextView cancelledText;
        ImageButton cancelBtn, editBtn;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.GONE);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String rideId = params[0];
            try {

                HttpRestUtil httpRestUtil = new HttpRestUtil(getContext());
                String response = httpRestUtil.httpDelete("shareRideService/ride?id="+rideId);
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

            progressBar.setVisibility(View.GONE);
            Toast toast;
            if (success) {
                toast = Toast.makeText(getContext(), "Cancelled ride successful", Toast.LENGTH_LONG);
                cancelledText.setVisibility(View.VISIBLE);
                editBtn.setVisibility(View.GONE);
                cancelBtn.setVisibility(View.GONE);
               // RideListingAdapterNoImage.this.notifyDataSetChanged();

            } else {
                toast = Toast.makeText(getContext(), "Canceling ride failed. Try Again", Toast.LENGTH_LONG);
                cancelledText.setVisibility(View.GONE);
                editBtn.setVisibility(View.VISIBLE);
                cancelBtn.setVisibility(View.VISIBLE);

            }
            toast.show();
            //finish();
            //startHomeActivity();

        }
    }

}
