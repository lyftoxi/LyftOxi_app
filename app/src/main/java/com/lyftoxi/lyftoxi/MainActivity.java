package com.lyftoxi.lyftoxi;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.reflect.TypeToken;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lyftoxi.lyftoxi.dao.TakeRide;
import com.lyftoxi.lyftoxi.singletons.CurrentUserInfo;
import com.lyftoxi.lyftoxi.singletons.CurrentUserInterestedRides;
import com.lyftoxi.lyftoxi.util.GPSTracker;
import com.lyftoxi.lyftoxi.util.HttpRestUtil;
import com.lyftoxi.lyftoxi.util.ImageUtil;
import com.lyftoxi.lyftoxi.util.LyftoxiFirebase;
import com.lyftoxi.lyftoxi.util.RoundImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;


public class MainActivity extends BaseActivity {

    private LatLng currentLocation;
    private ImageView logo;
    private FrameLayout splashScreen,  welcomeView;
    private static final int ANIMATION_DURATION=2000;
    private AnimationDrawable frameAnimation = null;
    private static boolean showSplashScreen=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isScrollable=false;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        session = new SessionManager(this);
        welcomeView = (FrameLayout)findViewById(R.id.welcomeView);
        splashScreen = (FrameLayout)findViewById(R.id.splashScreen);

        if(showSplashScreen) {
            logo = (ImageView) findViewById(R.id.mainLyftoxiLogoAnim);
            logo.setBackgroundResource(R.drawable.lyftoxi_logo_anim);
            AnimationDrawable frameAnimation = (AnimationDrawable) logo.getBackground();
            frameAnimation.setOneShot(true);
            frameAnimation.start();
            logoAnimation();
            crossfade();
            showSplashScreen=false;
        }
        else
        {
            splashScreen.setVisibility(View.GONE);
            welcomeView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        showSplashScreen=true;
        Log.i("gog.debug", "On Destroy .....");
    }


    private String getSecretKey() {
        MessageDigest md = null;
        try {
            PackageInfo info = this.getPackageManager().getPackageInfo(
                    this.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        //Log.i("SecretKey = ", Base64.encodeToString(md.digest(), Base64.DEFAULT));
        return Base64.encodeToString(md.digest(), Base64.DEFAULT);
    }


    private void downloadUserProfilePic()
    {
        final String profilePicFileName = session.getUserDetails().getUID()+"_profile_pic.jpg";
        Log.d("gog.debug ","profilePicFileName "+profilePicFileName);
        StorageReference storageRef = LyftoxiFirebase.storageRef;
        StorageReference profileImageRef = storageRef.child("userProfilePics/"+profilePicFileName);

       // profileImageRef.getDownloadUrl();
        final long ONE_MEGABYTE = 1024 * 1024;
        profileImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                /*RoundImage roundedImage = new RoundImage(bm);
                profileImage.setImageDrawable(roundedImage);*/

                Log.d("gog.debug"," downloaded profile pic");
                ImageUtil imageUtil = new ImageUtil();
                String filePath = imageUtil.saveToInternalStorage(getBaseContext(),bitmap,"user_avatar.jpg");
                CurrentUserInfo.getInstance().setProfilePicPath(filePath);
               /* RoundImage roundedImage = new RoundImage(bitmap);
                profileImage.setImageDrawable(roundedImage);*/
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                Log.d("gog.debug","Firebase: profile pic download failed");
                Bitmap bm = BitmapFactory.decodeResource(getResources(),R.drawable.sample_profile_pic);
               /* RoundImage roundedImage = new RoundImage(bm);
                profileImage.setImageDrawable(roundedImage);*/
            }
        });
    }


  /* @Override
    public void onWindowFocusChanged (boolean hasFocus)
    {
        GPSTracker gps = new GPSTracker(this);

        // check if GPS enabled
        if(gps.canGetLocation()){
            currentLocation = new LatLng(gps.getLatitude(),gps.getLongitude());
        }else{
            gps.showSettingsAlert(this);
        }
        if(null!=currentLocation) {
            Toast locationToast = Toast.makeText(this, "Latitide :" + currentLocation.latitude + " \n Longitude :" + currentLocation.longitude, Toast.LENGTH_LONG);
            locationToast.show();
        }

    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void takeRide(View view)
    {
        Intent findRideIntent = new Intent(this, FindRideActivity.class);
        startActivity(findRideIntent);
    }


    public void shareRide(View view)
    {
        Log.d("gog.debug","isLoggedIn "+session.isLoggedIn());
        Intent shareRideIntent = new Intent(this, ShareRideActivity.class);
        startActivity(shareRideIntent);

    }


    public void help(View view)
    {
        Log.d("gog.debug","isLoggedIn "+session.isLoggedIn());
        Intent helpIntent = new Intent(this, HelpActivity.class);
        startActivity(helpIntent);

    }

    private void logoAnimation()
    {
        welcomeView.setVisibility(View.GONE);
        if(null!=logo) {
            frameAnimation = (AnimationDrawable) logo.getBackground();
            frameAnimation.setOneShot(true);
            frameAnimation.start();
        }

        welcomeView.setVisibility(View.VISIBLE);
    }

    private void crossfade() {


        welcomeView.setAlpha(0f);
        welcomeView.setVisibility(View.VISIBLE);
        welcomeView.animate()
                .alpha(1f)
                .setDuration(ANIMATION_DURATION)
                .setListener(null);

        splashScreen.animate()
                .alpha(0f)
                .setDuration(ANIMATION_DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        splashScreen.setVisibility(View.GONE);
                    }
                });
    }

    private class GetMyInterestedRides extends AsyncTask<LatLng, Void, Boolean>
    {

        List<TakeRide> ridesReceived = null;
       /* @Override
        protected void onPreExecute()
        {
            showProgress(true);
        }*/

        @Override
        protected Boolean doInBackground(LatLng... params) {

            String sourceStr = null;
            String destinationStr = null;
            Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy'T'HH:mm").create();
            HttpRestUtil httpRestUtil = new HttpRestUtil(getApplicationContext());
            StringBuffer url = new StringBuffer();
            url.append("takeRideService/rides/interestedUser");
            url.append("?id=");
            url.append(session.getUserDetails().getUID());

            Log.d("gog.debug","URL "+url.toString());
            try {
                String response = httpRestUtil.httpGet(url.toString());
                if(null!=response)
                {
                    ridesReceived = gson.fromJson(response, new TypeToken<List<TakeRide>>() {}.getType());
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
        protected void onPostExecute(Boolean result)
        {

            if(null!=ridesReceived && ridesReceived.size()>0)
            {
                CurrentUserInterestedRides.getInstance().getRides().clear();
                for(TakeRide tmpRide : ridesReceived)
                {
                    RideListingInfo ride = new RideListingInfo();

                    ride.setFare(tmpRide.getFare());
                    ride.setSourceName(tmpRide.getSource().getName());
                    ride.setSource(new LatLng(tmpRide.getSource().getLatitude(),tmpRide.getSource().getLongitude()));
                    ride.setDestinationName(tmpRide.getDestination().getName());
                    ride.setDestination(new LatLng(tmpRide.getDestination().getLatitude(),tmpRide.getDestination().getLongitude()));
                    ride.setStarTime(tmpRide.getRideTime());
                    UserInfo rideOf = new UserInfo();
                    rideOf.setUID(tmpRide.getOwnerObjId());
                    rideOf.setName(tmpRide.getOwnerName());
                    rideOf.setPhNo(tmpRide.getOwnerMobileNo());
                    ride.setRideOf(rideOf);
                    ride.setInterested(true);

                    CurrentUserInterestedRides.getInstance().getRides().add(tmpRide);
                }
            }
            //web service fetched data before logo animation complete
            //hence wiat for 2 seconds.
            /*if(null!=frameAnimation && frameAnimation.isRunning()) {
               try {
                   Thread.sleep(2000);
               }catch(InterruptedException iex)
               {
                   Log.e("gog.debug","waiting thread stopper "+iex.getMessage());
               }
            }*/
            crossfade();
        }
    }


}
