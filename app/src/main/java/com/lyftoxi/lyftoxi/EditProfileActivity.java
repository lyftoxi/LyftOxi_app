package com.lyftoxi.lyftoxi;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
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
import com.lyftoxi.lyftoxi.dao.User;
import com.lyftoxi.lyftoxi.dao.UserAddress;
import com.lyftoxi.lyftoxi.singletons.CurrentUserInfo;
import com.lyftoxi.lyftoxi.util.Constants;
import com.lyftoxi.lyftoxi.util.HttpRestUtil;
import com.lyftoxi.lyftoxi.util.ImageUtil;
import com.lyftoxi.lyftoxi.util.LyftoxiFirebase;
import com.msg91.sendotp.library.Config;
import com.msg91.sendotp.library.SendOtpVerification;
import com.msg91.sendotp.library.Verification;
import com.msg91.sendotp.library.VerificationListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditProfileActivity extends BaseActivity implements VerificationListener {


    private EditText editProfileMobile, editProfileName, editProfileEmail, editProfileDob,
                editProfileAddressLine1, editProfileAddressLine2, editProfileCity, editProfileState, editProfilePin;
    private RadioButton editProfileRadioMale, editProfileRadioFemale;
    private View appBarEditProfile;
    private Button save;
    private ImageButton changeDob;
    private FloatingActionButton editProfilePicBtn;
    private Uri mImageCaptureUri;
    private User user = new User();
    private SimpleDateFormat sdf =  new SimpleDateFormat(Constants.SIMPLE_DATE_FORMAT);
    private Verification mVerification=null;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    private ImageUtil imageUtil;
    private Calendar dob = Calendar.getInstance();
    private static final int DATE_PICKER_ID = 1111;

    AlertDialog chooseImageSourceDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        imageUtil = new ImageUtil();

        editProfileMobile =(EditText)findViewById(R.id.editProfileMobile);
        editProfileName =(EditText)findViewById(R.id.editProfileName);
        editProfileEmail =(EditText)findViewById(R.id.editProfileEmail);
        editProfileDob =(EditText)findViewById(R.id.editProfileDob);
        editProfileAddressLine1 =(EditText)findViewById(R.id.editProfileAddressLine1);
        editProfileAddressLine2 =(EditText)findViewById(R.id.editProfileAddressLine2);
        editProfileCity =(EditText)findViewById(R.id.editProfileCity);
        editProfileState =(EditText)findViewById(R.id.editProfileState);
        editProfilePin =(EditText)findViewById(R.id.editProfilePin);
        editProfileRadioMale =(RadioButton)findViewById(R.id.editProfileRadioMale);
        editProfileRadioFemale =(RadioButton)findViewById(R.id.editProfileRadioFemale);

        changeDob = (ImageButton)findViewById(R.id.editProfileDatePicker);

       /* Bitmap profilePic;
        if(session.isLoggedIn() && null!=CurrentUserInfo.getInstance().getProfilePicPath()) {
            Log.d("lyftoxi.debug","profile pic path "+CurrentUserInfo.getInstance().getProfilePicPath());
            profilePic = imageUtil.loadImageFromStorage(CurrentUserInfo.getInstance().getProfilePicPath());
        }
        else
        {
            Log.d("lyftoxi.debug","using default pic");
            profilePic = BitmapFactory.decodeResource(getResources(),R.drawable.profile_pic_placeholder_large);
        }*/
        collapsingToolbarLayout.setBackground(new BitmapDrawable(imageUtil.getProfilePic(this)));
        //collapsingToolbarLayout.setBackground(new BitmapDrawable(getResources(), profilePic));

        save =(Button)findViewById(R.id.editProfileSave);

        editProfilePicBtn = (FloatingActionButton)findViewById(R.id.editProfilePic);



        final CurrentUserInfo currentUser = CurrentUserInfo.getInstance();
        if(null!=currentUser && null!= currentUser.getId())
        {
            editProfileMobile.setText(currentUser.getPhNo());
            editProfileName.setText(currentUser.getName());
            editProfileEmail.setText(currentUser.getEmail());
            dob.setTime(currentUser.getDob());
            editProfileDob.setText(sdf.format(currentUser.getDob()));
            if(null!=currentUser.getAddresses() && currentUser.getAddresses().size()>0) {
                editProfileAddressLine1.setText(currentUser.getAddresses().get(0).getAddrLine1());
                editProfileAddressLine2.setText(currentUser.getAddresses().get(0).getAddrLine2());
                editProfileCity.setText(currentUser.getAddresses().get(0).getCity());
                editProfileState.setText(currentUser.getAddresses().get(0).getState());
                editProfilePin.setText(currentUser.getAddresses().get(0).getPinCode());
            }

            if("M".equalsIgnoreCase(currentUser.getGender()))
            {
                editProfileRadioMale.setChecked(true);
            }
            else if("F".equalsIgnoreCase(currentUser.getGender()))
            {
                editProfileRadioFemale.setChecked(true);
            }

            editProfileRadioMale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(editProfileRadioMale.isChecked())
                    {
                        user.setGender("M");
                    }
                }
            });

            editProfileRadioFemale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(editProfileRadioFemale.isChecked())
                    {
                        user.setGender("F");
                    }
                }
            });

        }
        else
        {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            Bundle b = new Bundle();
            b.putString("activityOnSuccess", EditProfileActivity.class.getName());
            loginIntent.putExtras(b);
            startActivity(loginIntent);
        }


        changeDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_PICKER_ID);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.setId(CurrentUserInfo.getInstance().getId());
                boolean mobileNumberNotChanged = true;
                if(!isValidInputs())
                {
                    Log.d("lyftoxi.debug","error in field validation");
                    return;
                }
                if(!CurrentUserInfo.getInstance().getPhNo().equalsIgnoreCase(editProfileMobile.getText().toString()))
                {
                    mobileNumberNotChanged = false;
                }
                user.setPhNo(editProfileMobile.getText().toString());
                user.setName(editProfileName.getText().toString());
                user.setEmail(editProfileEmail.getText().toString());
                try {
                    user.setDob(sdf.parse(editProfileDob.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                    UserAddress userAddress = new UserAddress();
                    userAddress.setAddrLine1(editProfileAddressLine1.getText().toString());
                    userAddress.setAddrLine2(editProfileAddressLine2.getText().toString());
                    userAddress.setCity(editProfileCity.getText().toString());
                    userAddress.setState(editProfileState.getText().toString());
                    userAddress.setPinCode(editProfilePin.getText().toString());

                    List<UserAddress> addresses = new ArrayList<UserAddress>();
                    addresses.add(userAddress);
                    user.setAddresses(addresses);

                if(editProfileRadioMale.isChecked())
                {
                    user.setGender("M");
                }
                if(editProfileRadioFemale.isChecked())
                {
                    user.setGender("F");
                }


                if(mobileNumberNotChanged) {
                    new UpdateUserInfoTask().execute(false);
                }
                else{
                    initiateOTPVerification();
                }
            }
        });

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

        editProfilePicBtn = (FloatingActionButton) findViewById(R.id.editProfilePic);
        editProfilePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(v.getContext(),
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(EditProfileActivity.this);
                        alert.setTitle("Explanation");
                        alert.setMessage(getString(R.string.ext_storage_write_permission_explanation_1));
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(EditProfileActivity.this,
                                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                            }
                        });
                        alert.show();

                    } else {
                        ActivityCompat.requestPermissions(EditProfileActivity.this,
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    }
                }
                else {

                    chooseImageSourceDialog.show();
                }
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

                    AlertDialog.Builder alert = new AlertDialog.Builder(EditProfileActivity.this);
                    alert.setTitle("Explanation");
                    alert.setMessage(getString(R.string.ext_storage_write_permission_explanation_2));
                    alert.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", EditProfileActivity.this.getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    });
                    alert.show();
                }
                return;
            }

        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {


        int year  = dob.get(Calendar.YEAR);
        int month = dob.get(Calendar.MONTH);
        int day   = dob.get(Calendar.DAY_OF_MONTH);
        int hour  = dob.get(Calendar.HOUR_OF_DAY);
        int minute  = dob.get(Calendar.MINUTE);

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

            dob.set(selectedYear,selectedMonth,selectedDay);
            editProfileDob.setText(sdf.format(dob.getTime()));

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            showProgress(true);

            Bitmap bitmap = null;
            String path = "";

            if (requestCode == PICK_FROM_FILE) {
                mImageCaptureUri = data.getData();
                Log.d("lyftoxi.debug", " path " + mImageCaptureUri.getPath());
                path = getRealPathFromURI(mImageCaptureUri); //from Gallery


                if (path == null) {
                    //This is a temporary work arround
                    showProgress(false);
                    Toast.makeText(this,"Please use Gallery to select image. We are working in fixing this",Toast.LENGTH_LONG).show();
                    return;
                    /*path = mImageCaptureUri.getPath(); //from File Manager
                    Log.d("lyftoxi.debug", "real path from file manager" + path.toString());*/
                }

                if (path != null) {
                    Log.d("lyftoxi.debug", "real path from something else" + path.toString());
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(path,options);
                    options.inSampleSize = imageUtil.calculateInSampleSize(options, 400, 400);
                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeFile(path,options);
                    //bitmap = BitmapFactory.decodeFile(path);
                    Log.d("lyftoxi.debug", "real path 1" + path.toString());

                }
            } else {
                path = mImageCaptureUri.getPath();
                Log.d("lyftoxi.debug", "path from camera" + path.toString());
               // bitmap = BitmapFactory.decodeFile(path);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path,options);
                options.inSampleSize = imageUtil.calculateInSampleSize(options, 400, 400);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeFile(path,options);

            }
            int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth())); // scaling captured image
            bitmap = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
            //final Bitmap thumbBitmap = ThumbnailUtils.extractThumbnail(bitmap, 400, 400);
            final Bitmap fullBitmap = ThumbnailUtils.extractThumbnail(bitmap, 400, 200);
            final Bitmap thumbBitmap = ThumbnailUtils.extractThumbnail(bitmap, 50, 50);

            ByteArrayOutputStream thumbStream = new ByteArrayOutputStream();
            thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, thumbStream);

            final ByteArrayOutputStream fullStream = new ByteArrayOutputStream();
            fullBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fullStream);

            final String profilePicFileName = CurrentUserInfo.getInstance().getId()+"_profile_pic.jpg";
            final String profilePicThumbFileName = CurrentUserInfo.getInstance().getId()+"_profile_pic_thumb.jpg";

            final StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpg")
                    .build();

            StorageReference storageRef = LyftoxiFirebase.storageRef;

            UploadTask uploadTaskThumbPic = storageRef.child("userProfilePicThumbs/"+profilePicThumbFileName).putBytes(thumbStream.toByteArray(),metadata);
            uploadTaskThumbPic.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception exception) {
                    showProgress(false);
                    exception.printStackTrace();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   String filePath =  imageUtil.saveToInternalStorage(getApplicationContext(),thumbBitmap,profilePicThumbFileName);
                    //String filePath = imageUtil.saveToInternalStorage(getBaseContext(),fullBitmap,"user_avatar.jpg");
                    CurrentUserInfo.getInstance().setProfilePicPath(filePath);
                    collapsingToolbarLayout.setBackground(new BitmapDrawable(getResources(),thumbBitmap));
                    refreshProfileImage();

                    StorageReference storageRef = LyftoxiFirebase.storageRef;
                    UploadTask uploadTaskFullPic = storageRef.child("userProfilePics/"+profilePicFileName).putBytes(fullStream.toByteArray(),metadata);
                    uploadTaskFullPic.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception exception) {
                            //showProgress(false);
                            exception.printStackTrace();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //String filePath =  imageUtil.saveToInternalStorage(getApplicationContext(),fullBitmap,profilePicFileName);
                            String filePath = imageUtil.saveToInternalStorage(getBaseContext(),fullBitmap,"user_avatar.jpg");
                            CurrentUserInfo.getInstance().setProfilePicPath(filePath);
                            collapsingToolbarLayout.setBackground(new BitmapDrawable(getResources(),fullBitmap));
                            refreshProfileImage();

                           // showProgress(false);
                        }
                    });

                    showProgress(false);
                }
            });


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
        if(null==res)
        {

        }
        return res;

    }


    private void initiateOTPVerification()
    {
        showProgress(true);
        Config config = SendOtpVerification.config().context(getApplicationContext())
                .build();
        mVerification = SendOtpVerification.createSmsVerification(config, user.getPhNo(), this, "91");
        mVerification.initiate();
    }

    private boolean isValidInputs()
    {

        Pattern pattern = Pattern.compile(Constants.EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(editProfileEmail.getText().toString());
        if(!matcher.matches())
        {
            editProfileEmail.setError("Invalid Email");
            editProfileEmail.requestFocus();
            return false;
        }

        pattern = Pattern.compile(Constants.MOBIL_PATTERN);
        matcher = pattern.matcher(editProfileMobile.getText().toString());
        if(!matcher.matches())
        {
            editProfileMobile.setError("Mobile must be 10 digits only");
            editProfileMobile.requestFocus();
            return false;
        }

        if(null==editProfileName.getText() || editProfileName.getText().toString().trim().equals(""))
        {
            editProfileName.setError("Name cannot be blank");
            editProfileName.requestFocus();
            return false;
        }

        if(null!=editProfilePin.getText() && !("".equals(editProfilePin.getText().toString().trim()))) {
            pattern = Pattern.compile(Constants.PIN_PATTERN);
            matcher = pattern.matcher(editProfilePin.getText().toString());
            if (!matcher.matches()) {
                editProfilePin.setError("PIN must be 6 digits only");
                editProfilePin.requestFocus();
                return false;
            }
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
        new UpdateUserInfoTask().execute(true);
    }

    @Override
    public void onVerificationFailed(Exception exception) {
        Log.e("lyftoxi.debug", "Verification failed: " + exception.getMessage());
        showProgress(false);
    }

    public class UpdateUserInfoTask extends AsyncTask<Boolean, Void, Boolean> {
        Gson gson = new GsonBuilder().setDateFormat(Constants.SIMPLE_DATE_FORMAT).create();

        @Override
        protected void onPreExecute() {
            showProgress(true);
        }
        @Override
        protected Boolean doInBackground(Boolean... params) {
            Boolean isMobileNumberChanged = false;
            String url = "userService/user";
            if(params.length==1)
            {
                isMobileNumberChanged = params[0];
                if(isMobileNumberChanged) {
                    url = url + "?mobileNoChanged=true";
                }
            }
            if(null == user)
            {
                return false;
            }
            Object userInfoJson = gson.toJson(user);
            try {

                HttpRestUtil httpRestUtil = new HttpRestUtil(getApplicationContext());
                String response = httpRestUtil.httpPut(url,userInfoJson);
                if(null!=response)
                {
                    return true;
                }

            }catch (IOException ioex)
            {
                Log.d("lyftoxi.debug","Error occurred in REST WS call url cannot be reached "+ioex.getMessage());
                ioex.printStackTrace();
            }
            catch (Exception ex)
            {
                Log.d("lyftoxi.debug","Error occurred in REST WS call "+ex.getMessage());
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            showProgress(false);
            Toast toast;
            if (success) {

                session.createLoginSession(user.getId(), user.getName(),user.getPhNo(),user.getEmail(),user.getGender());

                CurrentUserInfo currentUser = CurrentUserInfo.getInstance();

                currentUser.setPhNo(user.getPhNo());
                currentUser.setName(user.getName());
                currentUser.setEmail(user.getEmail());
                currentUser.setDob(user.getDob());
                currentUser.setGender(user.getGender());
                currentUser.setAddresses(user.getAddresses());
                toast = Toast.makeText(getApplicationContext(), "Updating profile successful", Toast.LENGTH_LONG);
               // startHomeActivity();

            } else {
                toast = Toast.makeText(getApplicationContext(), "Updating profile Failed. Try Again", Toast.LENGTH_LONG);

            }
            toast.show();
            //finish();


        }


        @Override
        protected void onCancelled() {

        }
    }
}
