package com.lyftoxi.lyftoxi;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lyftoxi.lyftoxi.dao.User;
import com.lyftoxi.lyftoxi.exception.LyftoxiClientBusinessException;
import com.lyftoxi.lyftoxi.exception.LyftoxiClientException;
import com.lyftoxi.lyftoxi.singletons.CurrentUserInfo;
import com.lyftoxi.lyftoxi.singletons.CurrentUserInterestedRides;
import com.lyftoxi.lyftoxi.singletons.RideInfo;
import com.lyftoxi.lyftoxi.util.ConnectionDetector;
import com.lyftoxi.lyftoxi.util.Constants;
import com.lyftoxi.lyftoxi.util.HttpRestUtil;
import com.lyftoxi.lyftoxi.util.ImageUtil;
import com.lyftoxi.lyftoxi.util.RoundImage;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class BaseActivity extends AppCompatActivity {

   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }
*/
    private UserInfo loggedInUser;
    Toolbar toolbar;
    DrawerLayout fullView;
    SessionManager session;
    private View progressView;
    FrameLayout activityContainer;
    protected ImageButton navProfilePicBtn;
    protected CollapsingToolbarLayout collapsingToolbarLayout;
    protected boolean isScrollable = true;
    protected CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorlayout);
        ConnectionDetector connectionDetector =  new ConnectionDetector(getApplicationContext());
        if(!connectionDetector.isConnectingToInternet())
        {

            showAlertDialog(this, getString(R.string.no_internet_connection),
                    getString(R.string.no_internet_connection_please_try_again), false);
        }

    }
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon((status) ? android.R.drawable.presence_online : android.R.drawable.presence_offline);
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Reconnect and Try", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                    Intent main = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(main);
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if(session != null &&(
                session.getPromoDialogCode()==null ||
                !session.getPromoDialogCode().equals(Constants.CURRENT_PROMO_CODE))){
            Log.d("lyftoxi.debug","CURRENT PROMO CODE "+session.getPromoDialogCode());
            showPromoDialog();
        }
    }



    private void showPromoDialog()
    {
        AlertDialog.Builder promoDialog = new AlertDialog.Builder(this);
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.promo_dialog_layout, null);
        promoDialog.setView(view);
        promoDialog.setNeutralButton("Got it!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int sumthin) {
                    session.setPromoDialogCode(Constants.CURRENT_PROMO_CODE);
            }
        });
        promoDialog.show();
    }

    @Override
    public void setContentView(int layoutResID)
    {

        if(isScrollable) {
            fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
            activityContainer = (FrameLayout) fullView.findViewById(R.id.content_frame);
        }
        else
        {
            fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base_noscroll, null);
            activityContainer = (FrameLayout) fullView.findViewById(R.id.content_frame_not_scrollable);
        }
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(fullView);



        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);
        setTitle("Activity Title");

        setToolBar();
        session = new SessionManager(this);
        navProfilePicBtn = (ImageButton)findViewById(R.id.navProfilePicBtn);
        refreshProfileImage();

        TextView navName = (TextView) findViewById(R.id.navUserName);
        TextView navMobile = (TextView) findViewById(R.id.navMobile);


        List<NavDrawerItem> navList = new ArrayList<>();


        if(session.isLoggedIn())
        {
            navList.add(new NavDrawerItem(getString(R.string.home),
                    ContextCompat.getDrawable(this, R.drawable.ic_home_black_24dp)));
            navList.add(new NavDrawerItem(getString(R.string.take),
                    ContextCompat.getDrawable(this, R.drawable.ic_search_black_24dp)));
            navList.add(new NavDrawerItem(getString(R.string.share_your_ride),
                    ContextCompat.getDrawable(this, R.drawable.ic_share_black_24dp)));
/*            navList.add(new NavDrawerItem(getString(R.string.my_suggested_rides),
                    ContextCompat.getDrawable(this, R.drawable.ic_share_black_18dp)));*/
            navList.add(new NavDrawerItem(getString(R.string.my_shared_rides),
                    ContextCompat.getDrawable(this, R.drawable.ic_drive_eta_black_24dp)));
            navList.add(new NavDrawerItem(getString(R.string.my_interested_rides),
                    ContextCompat.getDrawable(this, R.drawable.ic_drive_eta_black_24dp)));
            navList.add(new NavDrawerItem(getString(R.string.title_activity_help),
                    ContextCompat.getDrawable(this, R.drawable.ic_live_help_black_24dp)));
            navList.add(new NavDrawerItem(getString(R.string.log_out),
                    ContextCompat.getDrawable(this, R.drawable.ic_power_settings_new_black_24dp)));

            this.loggedInUser = session.getUserDetails();
            navName.setText(this.loggedInUser.getName());
            navMobile.setText(this.loggedInUser.getPhNo());
        }
        else
        {
            navList.add(new NavDrawerItem(getString(R.string.home),
                    ContextCompat.getDrawable(this, R.drawable.ic_home_black_24dp)));
            navList.add(new NavDrawerItem(getString(R.string.take),
                    ContextCompat.getDrawable(this, R.drawable.ic_search_black_24dp)));
            navList.add(new NavDrawerItem(getString(R.string.share_your_ride),
                    ContextCompat.getDrawable(this, R.drawable.ic_share_black_24dp)));
            navList.add(new NavDrawerItem(getString(R.string.title_activity_help),
                    ContextCompat.getDrawable(this, R.drawable.ic_live_help_black_24dp)));
            navList.add(new NavDrawerItem(getString(R.string.action_sign_in),
                    ContextCompat.getDrawable(this, R.drawable.ic_perm_identity_black_24dp)));
        }


        ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new NavListAdapter(this,R.layout.drawer_list_item,navList));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener(this));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Log.d("lyftoxi.debug","base activity check user info");
        //Is user logged in?
        if(session.isLoggedIn())
        {
            Log.d("lyftoxi.debug","base activity user logged in");
            //Is user data populated?
            if(null == CurrentUserInfo.getInstance().getId())
            {
                Log.d("lyftoxi.debug","base activity user logged in but id is null");
                new UserDetailsTask().execute((Void) null);
            }
        }

/*        ImageButton editProfile = (ImageButton)findViewById(R.id.navEditProfile);*/
        navProfilePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editProfileActivity = new Intent(view.getContext(),EditProfileActivity.class);
                startActivity(editProfileActivity);
            }
        });

        progressView = (View) findViewById(R.id.baseActivityProgress);


    }

    protected void setToolBar()
    {
        //toolbar = (Toolbar)findViewById(R.id.appBarDefault);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }


    public void refreshProfileImage()
    {
        Log.d("lyftoxi.debug","executing profile image refresh...");
        Bitmap profilePic = null;
        if(session.isLoggedIn())
        {
            ImageUtil imageUtil = new ImageUtil();
            profilePic = imageUtil.getProfilePic(this);
            profilePic = ThumbnailUtils.extractThumbnail(profilePic, 100, 100);
        }
        else
        {
            Log.d("lyftoxi.debug","using default pic");
            profilePic = BitmapFactory.decodeResource(getResources(),R.drawable.sample_profile_pic);
        }
        /*Bitmap profilePic;
        if(session.isLoggedIn() && null!=CurrentUserInfo.getInstance().getProfilePicPath()) {
            Log.d("lyftoxi.debug","profile pic path "+CurrentUserInfo.getInstance().getProfilePicPath());

        }
        else
        {
            Log.d("lyftoxi.debug","using default pic");
           profilePic = BitmapFactory.decodeResource(getResources(),R.drawable.sample_profile_pic);
        }*/
        if(null!=profilePic) {
            navProfilePicBtn.setImageDrawable(new RoundImage(profilePic));
        }
    }


    protected void showProgress(boolean show)
    {
        activityContainer.setVisibility(show ? View.GONE : View.VISIBLE);
        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public class UserDetailsTask extends AsyncTask<Void, Void, Boolean> {

        String errorMessage;
        @Override
        protected Boolean doInBackground(Void... params) {

            User userInfo = null;
            String relativeUrl = "userService/user?id=" + session.getUserDetails().getUID();
            Gson gson = new GsonBuilder().setDateFormat(Constants.SIMPLE_DATE_FORMAT).create();
            HttpRestUtil httpRestUtil = new HttpRestUtil(getApplicationContext());
            try {
                String response = httpRestUtil.httpGet(relativeUrl);
                userInfo = gson.fromJson(response, new TypeToken<User>() {}.getType());
            } catch (IOException ioex) {
                errorMessage ="Error occurred in REST WS call url cannot be reached ";
                Toast.makeText(getApplicationContext(),"Service Unavailable",Toast.LENGTH_LONG).show();
            }
            catch (LyftoxiClientBusinessException e) {
                Log.e("lyftoxi.error","Business Exception occurred in REST WS call "+e.getMessage());
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
            catch (LyftoxiClientException e) {
                Log.e("lyftoxi.error","Error occurred in REST WS call "+e.getMessage());
                Toast.makeText(getApplicationContext(),"Some thing wrong happened.Contact support",Toast.LENGTH_LONG).show();
            }
            catch (Exception e) {
                Log.e("lyftoxi.error","Something really went wrong "+e.getMessage());
                Toast.makeText(getApplicationContext(),"OMG you got us a defect. Contact support with screenshot",Toast.LENGTH_LONG).show();
            }

            if(null!= userInfo)
            {
                //login successful
                session.createLoginSession(userInfo.getId(), userInfo.getName(),userInfo.getPhNo(),userInfo.getEmail(), userInfo.getGender());
                CurrentUserInfo currentUserInfo = CurrentUserInfo.getInstance();
                currentUserInfo.setId(userInfo.getId());
                currentUserInfo.setName(userInfo.getName());
                currentUserInfo.setGender(userInfo.getGender());
                currentUserInfo.setEmail(userInfo.getEmail());
                currentUserInfo.setPhNo(userInfo.getPhNo());
                currentUserInfo.setAddresses(userInfo.getAddresses());
                currentUserInfo.setCarDetails(userInfo.getCarDetails());
                currentUserInfo.setDob(userInfo.getDob());
                return true;
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {



        }

        @Override
        protected void onCancelled() {

        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        Activity callingActivity;

        public DrawerItemClickListener(Activity callingActivity)
        {
            this.callingActivity = callingActivity;
        }

        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {

            Intent contentIntent = null;

            if(session.isLoggedIn()) {
                switch (position) {
                    case 0:
                        contentIntent = new Intent(this.callingActivity, MainActivity.class);
                        break;
                    case 1:
                        contentIntent = new Intent(this.callingActivity, FindRideActivity.class);
                        break;
                    case 2:
                        contentIntent = new Intent(this.callingActivity, ShareRideActivity.class);
                        break;
                    case 3:
                        contentIntent = new Intent(this.callingActivity, MySharedRides.class);
                        break;
                    case 4:
                        contentIntent = new Intent(this.callingActivity, MyInterestedRides.class);
                        break;
                    case 5:
                        contentIntent = new Intent(this.callingActivity, HelpActivity.class);
                        break;
                    case 6:
                        session.logoutUser();
                        CurrentUserInfo.reset();
                        CurrentUserInterestedRides.reset();
                        RideInfo.reset();
                        refreshProfileImage();
                        contentIntent = new Intent(this.callingActivity, MainActivity.class);
                        contentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        break;
                    default:
                        contentIntent = new Intent(this.callingActivity, MainActivity.class);
                        break;
                }
            }
            else
            {
                switch (position) {
                    case 0:
                        contentIntent = new Intent(this.callingActivity, MainActivity.class);
                        break;
                    case 1:
                        contentIntent = new Intent(this.callingActivity, FindRideActivity.class);
                        break;
                    case 2:
                        contentIntent = new Intent(this.callingActivity, ShareRideActivity.class);
                        break;
                    case 3:
                        contentIntent = new Intent(this.callingActivity, HelpActivity.class);
                        break;
                    case 4:
                        contentIntent = new Intent(this.callingActivity, LoginActivity.class);
                        Bundle b = new Bundle();
                        b.putString("activityOnSuccess", MainActivity.class.getName());
                        contentIntent.putExtras(b);
                        break;
                    default:
                        contentIntent = new Intent(this.callingActivity, MainActivity.class);
                        break;
                }
            }

            startActivity(contentIntent);

        }
    }

    @Override
    public void setTitle(CharSequence title) {

    }

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
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

        if(id == android.R.id.home){
            fullView.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
