package com.lyftoxi.lyftoxi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lyftoxi.lyftoxi.exception.LyftoxiClientBusinessException;
import com.lyftoxi.lyftoxi.exception.LyftoxiClientException;
import com.lyftoxi.lyftoxi.singletons.CurrentUserInfo;
import com.lyftoxi.lyftoxi.util.Constants;
import com.lyftoxi.lyftoxi.util.HttpRestUtil;
import com.msg91.sendotp.library.Config;
import com.msg91.sendotp.library.SendOtpVerification;
import com.msg91.sendotp.library.Verification;
import com.msg91.sendotp.library.VerificationListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPasswordActivity extends BaseActivity implements VerificationListener {


    private EditText mobile, password, confirmPassword;
    private Button submitBtn;
    private Verification mVerification=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mobile =(EditText)findViewById(R.id.forgotPasswordMobile);
        password =(EditText)findViewById(R.id.forgotPasswordPassword);
        confirmPassword =(EditText)findViewById(R.id.forgotPasswordConfirmPassword);

        submitBtn = (Button)findViewById(R.id.forgotPasswordSubmit);
    }

    public void resetPassword(View view)
    {
        if(!isValidInputs())
        {
            return;
        }
       Config config = SendOtpVerification.config().context(getApplicationContext())
                .build();
        mVerification = SendOtpVerification.createSmsVerification(config, mobile.getText().toString(), this, "91");
        showProgress(true);
        mVerification.initiate();

        //new ResetPasswordTask().execute(mobile.getText().toString(), password.getText().toString());
    }


        private boolean isValidInputs()
    {
        if(mobile.getText().length()!=10)
        {
            mobile.setError("Mobile must be 10 digits only");
            mobile.requestFocus();
            return false;
        }

        if(null == password || password.getText().equals(""))
        {
            password.setError("Password is blank");
            password.requestFocus();
            return false;
        }

        if(password.getText().length()<6)
        {
            password.setError("Password must be 6 or more charecters");
            password.requestFocus();
            return false;
        }

        if(!password.getText().toString().equals(confirmPassword.getText().toString()))
        {
            confirmPassword.setError("Password and Retype password does not match");
            confirmPassword.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public void onInitiated(String response) {
        showProgress(false);
        Log.d("lyftoxi.debug", "Initialized!" + response);
        LayoutInflater li = LayoutInflater.from(this);
        View otpPopup = li.inflate(R.layout.otp_popup, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(otpPopup);
        final EditText otp = (EditText) otpPopup.findViewById(R.id.otp);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                if(null!=mVerification) {
                                    showProgress(true);
                                    mVerification.verify(otp.getText().toString());
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onInitiationFailed(Exception exception) {
        Log.e("lyftoxi.debug", "Verification initialization failed: " + exception.getMessage());
        exception.printStackTrace();
        showProgress(false);

    }

    @Override
    public void onVerified(String response) {
        Log.d("lyftoxi.debug", "Verified!\n" + response);
        showProgress(false);
        new ResetPasswordTask().execute(mobile.getText().toString(), password.getText().toString());
    }

    @Override
    public void onVerificationFailed(Exception exception) {
        Log.e("lyftoxi.debug", "Verification failed: " + exception.getMessage());
        showProgress(false);
    }

    private void startLoginActivity()
    {
        Intent loginIntent = new Intent(this,LoginActivity.class);
        Bundle b = new Bundle();
        b.putString("activityOnSuccess", MainActivity.class.getName());
        loginIntent.putExtras(b);
        startActivity(loginIntent);
    }


    public class ResetPasswordTask extends AsyncTask<String, Void, Boolean> {

        Gson gson = new GsonBuilder().setDateFormat(Constants.SIMPLE_DATE_FORMAT).create();
        private String newUserId;
        private String message;
        @Override
        protected void onPreExecute() {
            showProgress(true);
        }
        @Override
        protected Boolean doInBackground(String... params) {

            String mobileNumber = params[0];
            String newPassword = params[1];

            try {

                HttpRestUtil httpRestUtil = new HttpRestUtil(getApplicationContext());
                String response = httpRestUtil.httpPostSimple("userService/userForgotPassword?mobile="+mobileNumber+"&password="+newPassword);
                if(null!=response)
                {
                    return true;
                }

            }
            catch (IOException ioex)
            {
                Log.d("lyftoxi.debug","Error occurred in REST WS call url cannot be reached "+ioex.getMessage());
                message = "Service returned nothing";
            }
            catch (Exception ex)
            {
                Log.d("lyftoxi.debug","Error occurred in REST WS call "+ex.getMessage());
                message = "Something went wrong which should not. Contact support is this persists";
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);
            if (success) {
                    startLoginActivity();
            }
            else {
                Toast toast = Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG);
                toast.show();
            }
            finish();
        }

        @Override
        protected void onCancelled() {

        }
    }


}
