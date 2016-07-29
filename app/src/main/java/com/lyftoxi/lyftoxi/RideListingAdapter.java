package com.lyftoxi.lyftoxi;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.core.AndroidSupport;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lyftoxi.lyftoxi.dao.Location;
import com.lyftoxi.lyftoxi.dao.Ride;
import com.lyftoxi.lyftoxi.dao.TakeRide;
import com.lyftoxi.lyftoxi.singletons.CurrentUserInfo;
import com.lyftoxi.lyftoxi.singletons.CurrentUserInterestedRides;
import com.lyftoxi.lyftoxi.util.Constants;
import com.lyftoxi.lyftoxi.util.HttpRestUtil;
import com.lyftoxi.lyftoxi.util.LyftoxiFirebase;
import com.lyftoxi.lyftoxi.util.RoundImage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;


public class RideListingAdapter extends ArrayAdapter<RideListingInfo>{


    List<RideListingInfo> rides;

    SessionManager session;

    private View v;

    private LruCache<String, Bitmap> mMemoryCache;

    static class ViewHolder {
        ImageView userImage;
        TextView from;
        TextView to;
        TextView fare;
        TextView startingTime;
        ImageButton interestedButton;
        TextView cancelled ;
        View progressBar;

    }

    public RideListingAdapter(Context context, int textViewResourceId, List<RideListingInfo> rides){
        super(context, textViewResourceId, rides);
        this.rides=rides;
        this.session = new SessionManager(context);

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public RideListingInfo getItem(int position){

        return rides.get(position);
    }

    //private  ViewHolder holder;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        v = convertView;
        ViewHolder holder;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.ride_listing, parent,false);
            holder = new ViewHolder();
            holder.userImage = (ImageView) v.findViewById(R.id.rideListingUserImage);
            holder.from = (TextView) v.findViewById(R.id.rideListingFrom);
            holder.to = (TextView) v.findViewById(R.id.rideListingTo);
            holder.fare = (TextView) v.findViewById(R.id.rideListingFare);
            holder.startingTime = (TextView) v.findViewById(R.id.rideListingStartingTime);
            holder.interestedButton = (ImageButton) v.findViewById(R.id.rideListingBtnInterested);
            holder.cancelled = (TextView) v.findViewById(R.id.rideListingCancelled);
            holder.progressBar = v.findViewById(R.id.rideListingProgress);
            v.setTag(holder);
        }
        else{
            holder = (ViewHolder) v.getTag();
        }

        final RideListingInfo i = rides.get(position);
        final SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_TIME_FORMAT_12HR_FORMAT);
        final DecimalFormat df = new DecimalFormat("##.######");


        if (i.isShowProgress()) {
            holder.progressBar.setVisibility(View.VISIBLE);
            Log.d("lyftoxi.debug","show progress..............");
        } else {
            holder.progressBar.setVisibility(View.GONE);
            Log.d("lyftoxi.debug","hide progress..............");
        }

        if (i != null) {


            Bitmap bm = BitmapFactory.decodeResource(v.getResources(), R.drawable.sample_profile_pic);
            RoundImage roundedImage = new RoundImage(bm);
            holder.userImage.setImageDrawable(roundedImage);


            downloadUserProfilePic(i.getRideOf().getUID(), v);

            Log.d("lyftoxi.debug", "ride status " + i.getStatus());
            if ("C".equals(i.getStatus())) {
                holder.cancelled.setVisibility(View.VISIBLE);
                holder.interestedButton.setVisibility(View.GONE);
            } else {
                holder.cancelled.setVisibility(View.GONE);
                holder.interestedButton.setVisibility(View.VISIBLE);
            }


            if (null != holder.interestedButton) {
                if (!session.isLoggedIn() || null == i.isInterested()) {
                    holder.interestedButton.setVisibility(View.INVISIBLE);
                } else {
                    if (i.isInterested()) {
                        //holder.interestedButton.setImageResource(android.R.drawable.btn_star_big_on);
                        holder.interestedButton.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                        holder.interestedButton.setColorFilter(v.getResources().getColor(R.color.interestedOn));
                    } else {
                        //holder.interestedButton.setImageResource(android.R.drawable.btn_star_big_off);
                        holder.interestedButton.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                        holder.interestedButton.setColorFilter(v.getResources().getColor(R.color.interestedOff));
                    }
                }



                holder.interestedButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Log.d("lyftoxi.debug", "interested " + i.isInterested());
                        if (i.isInterested()) {
                            RemoveInterestedRide removeInterestedRide = new RemoveInterestedRide();
                            removeInterestedRide.position = position;
                            removeInterestedRide.execute(i.getId(), CurrentUserInfo.getInstance().getId());
                            return;
                        }

                        TakeRide interestedRide = new TakeRide();
                        interestedRide.setOwnerObjId(i.getRideOf().getUID());
                        interestedRide.setOwnerName(i.getRideOf().getName());
                        interestedRide.setOwnerMobileNo(i.getRideOf().getPhNo());
                        interestedRide.setShareRideObjId(i.getId());
                        interestedRide.setRideTime(i.getStarTime());
                        interestedRide.setFare(i.getFare());
                        interestedRide.setInterestedUserObjId(CurrentUserInfo.getInstance().getId());
                        interestedRide.setInterestedUserName(CurrentUserInfo.getInstance().getName());
                        interestedRide.setInterestedUserMobileNo(CurrentUserInfo.getInstance().getPhNo());
                        Location source = new Location();
                        source.setName(i.getSourceName());
                        source.setLatitude(Double.valueOf(df.format(i.getSource().latitude)));
                        source.setLongitude(Double.valueOf(df.format(i.getSource().longitude)));
                        interestedRide.setSource(source);
                        Location destination = new Location();
                        destination.setName(i.getDestinationName());
                        destination.setLatitude(Double.valueOf(df.format(i.getDestination().latitude)));
                        destination.setLongitude(Double.valueOf(df.format(i.getDestination().longitude)));
                        interestedRide.setDestination(destination);

                        AddInterestedRide addInterestedRide = new AddInterestedRide();
                        addInterestedRide.position = position;
                        addInterestedRide.execute(interestedRide);
                    }
                });
            }

            holder.from.setText(i.getSourceName());
            holder.to.setText(i.getDestinationName());

            String fareStr = " "+ i.getFare();
            holder.fare.setText(fareStr);
            holder.startingTime.setText(sdf.format(i.getStarTime().getTime()));

        }
        return v;

    }


    private void downloadUserProfilePic(String userId, final View v)
    {
        if(null==userId || userId.trim().equals(""))
        {
            return;
        }
        final String profilePicFileName = userId+"_profile_pic.jpg";
        final String profilePicThumbFileName = userId+"_profile_pic_thumb.jpg";
        final ImageView rideListingUserImage = (ImageView)v.findViewById(R.id.rideListingUserImage);
        Bitmap bm = getBitmapFromMemCache(profilePicFileName);
        if (bm != null) {
            RoundImage roundedImage = new RoundImage(bm);
            rideListingUserImage.setImageDrawable(roundedImage);
        } else {

            Log.d("gog.debug ","profilePicFileName "+profilePicFileName);
            final StorageReference storageRef = LyftoxiFirebase.storageRef;
            StorageReference profileImageThumbRef = storageRef.child("userProfilePicThumbs/"+profilePicThumbFileName);
            final long SIZE = 100 * 100;
            profileImageThumbRef.getBytes(SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {

                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
                    RoundImage roundedImage = new RoundImage(bitmap);
                    addBitmapToMemoryCache(profilePicFileName, bitmap);
                    rideListingUserImage.setImageDrawable(roundedImage);
                    notifyDataSetChanged();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception exception) {
                    Log.d("lyftoxi.debug","Firebase: profile pic thumnail download failed");
                    StorageReference profileImageRef = storageRef.child("userProfilePics/"+profilePicFileName);
                    final long ONE_MEGABYTE = 500 * 500;
                    profileImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {

                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
                            RoundImage roundedImage = new RoundImage(bitmap);
                            addBitmapToMemoryCache(profilePicFileName, bitmap);
                            rideListingUserImage.setImageDrawable(roundedImage);
                            notifyDataSetChanged();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception exception) {
                            Log.d("lyftoxi.debug","Firebase: profile pic download failed");

                        }
                    });

                }
            });
        }

    }



    public class AddInterestedRide extends AsyncTask<TakeRide, Void, Boolean> {

        TakeRide  interestedRide;
        int position;



        Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_TIME_FORMAT_WITH_TIME_ZONE).create();
        @Override
        protected void onPreExecute() {
            //progressBar.setVisibility(View.VISIBLE);
            rides.get(position).setShowProgress(true);
            notifyDataSetChanged();
        }

        @Override
        protected Boolean doInBackground(TakeRide... params) {
            interestedRide = params[0];
            Object interestedRideInfoJson = gson.toJson(interestedRide);

            try {
                HttpRestUtil httpRestUtil = new HttpRestUtil(getContext());
                String response = httpRestUtil.httpPost("takeRideService/ride",interestedRideInfoJson);
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
            //progressBar.setVisibility(View.GONE);
            rides.get(position).setShowProgress(false);
            if (success) {
                // startMyInterestedRideActivity();
                //interestedButton.setImageResource(android.R.drawable.btn_star_big_on);
                rides.get(position).setInterested(true);
                CurrentUserInterestedRides.getInstance().getRides().add(interestedRide);
                Toast toast =  Toast.makeText(RideListingAdapter.this.getContext(),RideListingAdapter.this.getContext().getResources().getString(R.string.marked_interested),Toast.LENGTH_SHORT);
                toast.show();
            }
            else
            {
                rides.get(position).setInterested(false);
            }
            notifyDataSetChanged();
        }
        @Override
        protected void onCancelled() {

        }

    }

    public class RemoveInterestedRide extends AsyncTask<String, Void, Boolean> {

        ////View progressBar;
        int position;
        ImageButton interestedButton;
        String rideId, userId;
        Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_TIME_FORMAT_WITH_TIME_ZONE).create();
        @Override
        protected void onPreExecute() {
            //progressBar.setVisibility(View.VISIBLE);
            rides.get(position).setShowProgress(true);
            notifyDataSetChanged();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            rideId = params[0];
            userId = params[1];


            try {
                HttpRestUtil httpRestUtil = new HttpRestUtil(getContext());
                String response = httpRestUtil.httpDelete("takeRideService/ride/rideIdUserId?rideId="+rideId+"&userId="+userId);
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
            //progressBar.setVisibility(View.GONE);
            rides.get(position).setShowProgress(false);
            if (success) {
                // startMyInterestedRideActivity();
                rides.get(position).setInterested(false);
                for(int i=0; i<CurrentUserInterestedRides.getInstance().getRides().size(); i++)
                {
                    if(CurrentUserInterestedRides.getInstance().getRides().get(i).getShareRideObjId().equals(rideId))
                    {
                        CurrentUserInterestedRides.getInstance().getRides().remove(i);
                    }
                }
                //interestedButton.setImageResource(android.R.drawable.btn_star_big_off);
                Toast toast =  Toast.makeText(RideListingAdapter.this.getContext(),RideListingAdapter.this.getContext().getResources().getString(R.string.marked_uninterested),Toast.LENGTH_SHORT);
                toast.show();
            }
            else
            {
                rides.get(position).setInterested(true);
            }
            notifyDataSetChanged();
        }
        @Override
        protected void onCancelled() {

        }

    }

}
