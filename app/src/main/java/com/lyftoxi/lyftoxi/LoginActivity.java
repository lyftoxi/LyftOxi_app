package com.lyftoxi.lyftoxi;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lyftoxi.lyftoxi.dao.User;
import com.lyftoxi.lyftoxi.singletons.CurrentUserInfo;
import com.lyftoxi.lyftoxi.util.HttpRestUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lyftoxi.lyftoxi.util.ImageUtil;
import com.lyftoxi.lyftoxi.util.RoundImage;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends Activity {

    private UserLoginTask mAuthTask = null;

    private TextView mMobileView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Class onSuccessActivity = null;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = new SessionManager(getApplicationContext());
        // Set up the login form.
        mMobileView = (TextView) findViewById(R.id.mobile);
       // populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mMobileSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mMobileSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        if(null!= getIntent().getExtras()) {
            Bundle b = getIntent().getExtras();
            String classNameStr = b.getString("activityOnSuccess");
            Log.d("gog.debug", "classNameReceived " + classNameStr);
            if (null != classNameStr) {
                try {
                    onSuccessActivity = Class.forName(classNameStr);
                } catch (ClassNotFoundException ex) {
                    Log.d("gog.debug", "ClassNotFound " + ex.getMessage());
                    ex.printStackTrace();
                    onSuccessActivity = null;
                }
            }
        }
    }

    /*private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }*/


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mMobileView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String mobile = mMobileView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mobile)) {
            mMobileView.setError(getString(R.string.error_field_required));
            focusView = mMobileView;
            cancel = true;
        } else if (!isMobileValid(mobile)) {
            mMobileView.setError(getString(R.string.error_invalid_email));
            focusView = mMobileView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(mobile, password);
            mAuthTask.execute((Void) null);
        }
    }

    private void downloadUserProfilePic()
    {

        final String profilePicFileName = session.getUserDetails().getUID()+"_profile_pic.jpg";
        Log.d("gog.debug ","profilePicFileName "+profilePicFileName);
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://lyftoxi-1321.appspot.com");
        //StorageReference profileImageRef = storageRef.child("userProfilePics/Screenshot_20160607-233643.png");
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

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                Log.d("gog.debug","Firebase: profile pic download failed");
            }
        });
    }

    private boolean isMobileValid(String mobile) {
        if(mobile.length()!=10)
        {
            return false;
        }
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

  /*  @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }
*/



    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mMobile;
        private final String mPassword;

        Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy").create();
        UserLoginTask(String email, String password) {
            mMobile = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            User userInfo = null;
            try {
                HttpRestUtil httpRestUtil = new HttpRestUtil(getApplicationContext());
                String response = httpRestUtil.httpPostSimple("userService/userLogin?mobile="+mMobile+"&password="+mPassword);
                userInfo = gson.fromJson(response, new TypeToken<User>() {}.getType());

            }catch (IOException ioex)
            {
                Log.d("gog.debug","Error occurred in REST WS call url cannot be reached "+ioex.getMessage());
            }
            catch (Exception ex)
            {
                Log.d("gog.debug","Error occurred in REST WS call "+ex.getMessage());
            }

            if(null!= userInfo)
            {
                //login successful

                session.createLoginSession(userInfo.getId(), userInfo.getName(),userInfo.getPhNo(),userInfo.getEmail());
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
            mAuthTask = null;
            showProgress(false);

            if (success) {
                downloadUserProfilePic();
                if(null!=onSuccessActivity) {
                    Log.d("gog.debug","onSuccessActivity Starting....");
                    Intent onSuccessIntent = new Intent(getApplicationContext(), onSuccessActivity);
                    startActivity(onSuccessIntent);
                }
                else {
                    Log.d("gog.debug","onSuccessActivity is null");
                    finish();
                }
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public void openSignUpActivity(View view)
    {
        Intent signUpIntent = new Intent(this, SignupActivity.class);
        startActivity(signUpIntent);
    }

    public void openForgotPasswordActivity(View view)
    {
        Intent forgotPasswordActivity = new Intent(this, ForgotPasswordActivity.class);
        startActivity(forgotPasswordActivity);
    }
}

