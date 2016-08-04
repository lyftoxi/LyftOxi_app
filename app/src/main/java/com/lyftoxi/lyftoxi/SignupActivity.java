package com.lyftoxi.lyftoxi;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lyftoxi.lyftoxi.dao.User;
import com.lyftoxi.lyftoxi.exception.LyftoxiClientBusinessException;
import com.lyftoxi.lyftoxi.exception.LyftoxiClientException;
import com.lyftoxi.lyftoxi.singletons.CurrentUserInfo;
import com.lyftoxi.lyftoxi.util.Constants;
import com.lyftoxi.lyftoxi.util.HttpRestUtil;
import com.lyftoxi.lyftoxi.util.ImageUtil;
import com.lyftoxi.lyftoxi.util.LyftoxiFirebase;
import com.lyftoxi.lyftoxi.util.PasswordUtil;
import com.lyftoxi.lyftoxi.util.Util;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.msg91.sendotp.library.Config;
import com.msg91.sendotp.library.SendOtpVerification;
import com.msg91.sendotp.library.Verification;
import com.msg91.sendotp.library.VerificationListener;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignupActivity extends BaseActivity implements VerificationListener {


   private EditText mobile;
    private EditText password ;
    private EditText confirmPassword ;
    private EditText email;
    private EditText name;
    private EditText dob;
    private RadioButton signupRadioMale, signupRadioFemale;
    private CheckBox tncAccept;

    private SimpleDateFormat sdf =  new SimpleDateFormat(Constants.SIMPLE_DATE_FORMAT);
    private User userInfo = new User();
    private UserLoginTask mSignUpTask = null;
    private Uri mImageCaptureUri;
    private FloatingActionButton profilePicBtn;
    private ImageButton changeDob;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    private  Verification mVerification=null;
    private ImageUtil imageUtil;
    private Bitmap profilePic, profilePicThumb;
    private Calendar dobValue = Calendar.getInstance();
    private static final int DATE_PICKER_ID = 1111;

    private AlertDialog chooseImageSourceDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        sdf.setLenient(false);

        mobile = (EditText)findViewById(R.id.signupMobile);
        password = (EditText)findViewById(R.id.signupPassword);
        confirmPassword = (EditText)findViewById(R.id.signupConfirmPassword);
        email = (EditText)findViewById(R.id.signupEmail);
        name = (EditText)findViewById(R.id.signupName);
        dob = (EditText)findViewById(R.id.signupDob);
        signupRadioMale = (RadioButton)findViewById(R.id.signupRadioMale);
        signupRadioFemale = (RadioButton)findViewById(R.id.signupRadioFemale);
        tncAccept =(CheckBox) findViewById(R.id.signupTnCaccept);

        signupRadioMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(signupRadioMale.isChecked())
                {
                    userInfo.setGender("M");
                }
            }
        });

        signupRadioFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(signupRadioFemale.isChecked())
                {
                    userInfo.setGender("F");
                }
            }
        });

        tncAccept.setText(Html.fromHtml("Accept&nbsp;<a href=\"http://www.lyftoxi.com/terms-and-conditions.html\">"+getString(R.string.tnc)+"</a>"));
        tncAccept.setMovementMethod(LinkMovementMethod.getInstance());

        imageUtil = new ImageUtil();

        final String [] items           = new String [] {"From Camera", "From SD Card"};
        ArrayAdapter<String> adapter  = new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,items);
        AlertDialog.Builder builder     = new AlertDialog.Builder(this);

        builder.setTitle("Select Image");
        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialog, int item ) {
                if (item == 0) {
                    Intent intent    = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file        = new File(Environment.getExternalStorageDirectory(),
                            "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                    mImageCaptureUri = Uri.fromFile(file);

                    try {
                        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                        intent.putExtra("return-data", true);

                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    dialog.cancel();
                } else {
                    Intent intent = new Intent();

                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                }
            }
        } );

        chooseImageSourceDialog = builder.create();
        profilePicBtn = (FloatingActionButton) findViewById(R.id.signupProfilePic);
        profilePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(v.getContext(),
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(SignupActivity.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(SignupActivity.this);
                        alert.setTitle("Explanation");
                        alert.setMessage(getString(R.string.ext_storage_write_permission_explanation_1));
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(SignupActivity.this,
                                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                            }
                        });
                        alert.show();

                    } else {
                        ActivityCompat.requestPermissions(SignupActivity.this,
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    }
                }
                else {

                    chooseImageSourceDialog.show();
                }
            }
        });

        changeDob = (ImageButton)findViewById(R.id.signupDatePicker);
        changeDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_PICKER_ID);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    chooseImageSourceDialog.show();

                } else {

                    AlertDialog.Builder alert = new AlertDialog.Builder(SignupActivity.this);
                    alert.setTitle("Explanation");
                    alert.setMessage(getString(R.string.ext_storage_write_permission_explanation_2));
                    alert.setPositiveButton("OK",null);
                    alert.show();
                }
                return;
            }

        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {


        int year  = dobValue.get(Calendar.YEAR);
        int month = dobValue.get(Calendar.MONTH);
        int day   = dobValue.get(Calendar.DAY_OF_MONTH);
        int hour  = dobValue.get(Calendar.HOUR_OF_DAY);
        int minute  = dobValue.get(Calendar.MINUTE);

        switch (id) {
            case DATE_PICKER_ID:
                return new DatePickerDialog(this, pickerListener, year, month,day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            dobValue.set(selectedYear,selectedMonth,selectedDay);
            dob.setText(sdf.format(dobValue.getTime()));

        }
    };




    public void signUp(View view)
    {




       if(!isValidInputs())
       {
           Log.d("lyftoxi.debug","error in field validation");
           return;
       }

        userInfo.setUserStatus("A");
        userInfo.setEmail(email.getText().toString());
        userInfo.setName(name.getText().toString());
        userInfo.setPhNo(mobile.getText().toString());
        userInfo.setPassword(PasswordUtil.encryptPassword(password.getText().toString()));
        try {
            userInfo.setDob(sdf.parse(dob.getText().toString()));
        }catch(ParseException pe)
        {
            Log.d("lyftoxi.debug","invalid date "+dob.getText().toString());
            dob.setError("Invalid date. Must be "+Constants.SIMPLE_DATE_FORMAT);
            dob.requestFocus();
            return;
        }

        Config config = SendOtpVerification.config().context(getApplicationContext())
                .build();
        mVerification = SendOtpVerification.createSmsVerification(config, userInfo.getPhNo(), this, "91");
        showProgress(true);
        mVerification.initiate();

    }

    /*public void selectSex(View view)
    {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_male:
                if (checked)
                    userInfo.setSex("M");
                break;
            case R.id.radio_female:
                if (checked)
                    userInfo.setSex("F");
                break;
        }
    }*/

    public String getDataDir(final Context context){
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).applicationInfo.dataDir;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    if (resultCode == RESULT_OK) {

            Bitmap bitmap = null;
            String path = "";

            if (requestCode == PICK_FROM_FILE) {
                mImageCaptureUri = data.getData();
                Log.d("lyftoxi.debug", " path " + mImageCaptureUri.getPath());
                path = getRealPathFromURI(mImageCaptureUri);


                if (path == null) {
                    //This is a temporary work arround
                    showProgress(false);
                    Toast.makeText(this,"Please use Gallery to select image. We are working in fixing this",Toast.LENGTH_LONG).show();
                    return;
                    /*path = mImageCaptureUri.getPath(); //from File Manager
                    Log.d("lyftoxi.debug", "real path from file manager" + path.toString());*/
                }

                if (path != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(path,options);
                    options.inSampleSize = imageUtil.calculateInSampleSize(options, 400, 400);
                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeFile(path,options);
                    Log.d("lyftoxi.debug", "real path 1" + path.toString());

                }
            } else {
                path = mImageCaptureUri.getPath();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path,options);
                options.inSampleSize = imageUtil.calculateInSampleSize(options, 400, 400);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeFile(path,options);

            }
            int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
            bitmap = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
            profilePic = ThumbnailUtils.extractThumbnail(bitmap, 400, 200);
            profilePicThumb = ThumbnailUtils.extractThumbnail(bitmap, 50, 50);
            collapsingToolbarLayout.setBackground(new BitmapDrawable(getResources(),profilePic));
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String [] proj      = {MediaStore.Images.Media.DATA};
        String res = null;
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;

    }


    private boolean isValidInputs()
    {
        if(!tncAccept.isChecked())
        {
            tncAccept.setError("Please accept the Terms and Conditions");
            tncAccept.requestFocus();
            return false;
        }

        if(null == userInfo){    return false; }


        if(mobile.getText().length()!=10)
        {
            mobile.setError("Mobile must be 10 digits only");
            mobile.requestFocus();
            return false;
        }

        if(null == password || password.getText().toString().equals(""))
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

        if(password.getText().toString().contains(" "))
        {
            password.setError("Password cannot have spaces");
            password.requestFocus();
            return false;
        }

        if(!password.getText().toString().equals(confirmPassword.getText().toString()))
        {
            confirmPassword.setError("Password and Retype password does not match");
            confirmPassword.requestFocus();
            return false;
        }

        Pattern pattern = Pattern.compile(Constants.EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email.getText().toString());
        if(!matcher.matches())
        {
            email.setError("Invalid Email");
            email.requestFocus();
            return false;
        }

        if(null==name.getText() || name.getText().toString().trim().equals(""))
        {
            name.setError("Name cannot be blank");
            name.requestFocus();
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
        new UserLoginTask().execute((Void) null);
    }

    @Override
    public void onVerificationFailed(Exception exception) {
        Log.e("lyftoxi.debug", "Verification failed: " + exception.getMessage());
        showProgress(false);
    }


    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        Gson gson = new GsonBuilder().setDateFormat(Constants.SIMPLE_DATE_FORMAT).create();
        private String newUserId;
        String errorMessage;

        @Override
        protected void onPreExecute() {
            showProgress(true);
        }
        @Override
        protected Boolean doInBackground(Void... params) {
           if(null == userInfo)
           {
               return false;
           }
            Object userInfoJson = gson.toJson(userInfo);
            try {

                HttpRestUtil httpRestUtil = new HttpRestUtil(getApplicationContext());
                String response = httpRestUtil.httpPost("userService/user",userInfoJson);
                if(null!=response)
                {
                    newUserId = response;
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


            Toast toast;
            if (success) {

                if(null!=profilePic && null!=profilePicThumb) {

                    final ByteArrayOutputStream fullStream = new ByteArrayOutputStream();
                    profilePic.compress(Bitmap.CompressFormat.JPEG, 100, fullStream);

                    ByteArrayOutputStream thumbStream = new ByteArrayOutputStream();
                    profilePicThumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbStream);

                    final StorageMetadata metadata = new StorageMetadata.Builder()
                            .setContentType("image/jpg")
                            .build();
                    final String profilePicFileName = newUserId + "_profile_pic.jpg";
                    final String profilePicThumbsFileName = newUserId + "_profile_pic_thumb.jpg";

                    StorageReference storageRef = LyftoxiFirebase.storageRef;
                    UploadTask thumbUploadTask = storageRef.child("userProfilePicThumbs/" + profilePicThumbsFileName).putBytes(thumbStream.toByteArray(), metadata);
                    thumbUploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception exception) {
                            showProgress(false);
                            collapsingToolbarLayout.setBackground(getResources().getDrawable(R.drawable.profile_pic_placeholder_large));
                            Toast toast = Toast.makeText(getApplicationContext(), "Image upload Failed. Try Again", Toast.LENGTH_LONG);
                            toast.show();
                            exception.printStackTrace();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            showProgress(false);
                            String filePath = imageUtil.saveToInternalStorage(getBaseContext(), profilePicThumb, profilePicThumbsFileName);
                            CurrentUserInfo.getInstance().setProfilePicPath(filePath);
                            collapsingToolbarLayout.setBackground(new BitmapDrawable(getResources(), profilePicThumb));
                            refreshProfileImage();
                            Toast toast = Toast.makeText(getApplicationContext(), "Sign Up Successful", Toast.LENGTH_LONG);
                            toast.show();

                            StorageReference storageRef = LyftoxiFirebase.storageRef;
                            UploadTask uploadTaskFullPic = storageRef.child("userProfilePics/" + profilePicFileName).putBytes(fullStream.toByteArray(), metadata);
                            uploadTaskFullPic.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception exception) {
                                    //showProgress(false);
                                    exception.printStackTrace();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String filePath = imageUtil.saveToInternalStorage(getApplicationContext(), profilePic, profilePicFileName);
                                    CurrentUserInfo.getInstance().setProfilePicPath(filePath);
                                    collapsingToolbarLayout.setBackground(new BitmapDrawable(getResources(), profilePic));
                                    refreshProfileImage();
                                    //showProgress(false);
                                }
                            });


                        }
                    });
                }
                else{
                    showProgress(false);
                    toast = Toast.makeText(getApplicationContext(), "Sign Up Successful. Please upload profile picture from Edit profile", Toast.LENGTH_LONG);
                    toast.show();
                }


            } else {
                showProgress(false);
               toast = Toast.makeText(getApplicationContext(), "Sign Up Failed. Try Again", Toast.LENGTH_LONG);
                toast.show();

            }

            finish();

        }

        @Override
        protected void onCancelled() {

        }
    }





}
