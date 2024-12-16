package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.retrofit.RetrofitClient;

public class CredoAepsKycActivity extends AppCompatActivity {

    SharedPreferences insuranceShp;
    String userid,deviceInfo,deviceId,userType,mobileNo,panNo,emailId,aadharNo,name,address;
    EditText etFirstName,etLastname,etMobile,etTitle,etCompanyName,etBankAccount,etBankIfsc,etEmail,etPincode,etAddress,etPanCard,etAadhaar,etCancelChequeNo;
    Button insuranceBtn;
    ImageView insuranceBackImg;
    TextView tvUploadPan,tvUploadAadharFront,tvUploadPassbook,tvUploadAadharBack;
    String pincode,firstName,lastName,companyName,bankAccountNum,bankIfsc,merchantTitle,cancelChequeNum;

    TextView tvDob;
    MaterialCardView insurance_dobCardView;
    String selectedDob = "";

    private static final int FILE_PERMISSION = 2;
    private  String selectedUplaod="";
    private String panImagePath = "";
    private String aadharFrontImagePath = "";
    private String aadharBackImagePath = "";
    private String passbookImagePath = "";


    private String panFileUrl = "NA";
    private String aadharFrontFileUrl = "NA";
    private String aadharBackFileUrl = "NA";
    private String passbookFileUrl = "NA";
    //////////////////////////////////////////////LOCATION
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    String lat = "0.0", longi = "0.0";
    //////////////////////////////////////////////LOCATION
    boolean isReadyForTransaction = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credo_aeps_kyc);
        initViews();

        insuranceShp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userid = insuranceShp.getString("userid", null);
        deviceId = insuranceShp.getString("deviceId", null);
        deviceInfo = insuranceShp.getString("deviceInfo", null);
        userType = insuranceShp.getString("usertype", null);
        mobileNo = insuranceShp.getString("mobileno", null);
        panNo = insuranceShp.getString("pancard", null);
        emailId = insuranceShp.getString("email", null);
        aadharNo = insuranceShp.getString("adharcard", null);
        name = insuranceShp.getString("username", null);
        address = insuranceShp.getString("address", null);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        insurance_dobCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                new DatePickerDialog(CredoAepsKycActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                        Calendar fromdate1 = Calendar.getInstance();
                        fromdate1.set(i, i1, i2);
                        tvDob.setText(simpleDateFormat.format(fromdate1.getTime()));
                        selectedDob = apiDateFormat.format(fromdate1.getTime());
                    }
                }, year, month, day).show();

            }
        });

        insuranceBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

       // getLastLocation();  //  comment by shauib 14 - 5 -24

        tvUploadPan.setOnClickListener(v ->
        {
            selectedUplaod = "PanImage";
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, FILE_PERMISSION);
        });


        tvUploadAadharFront.setOnClickListener(v ->
        {
            selectedUplaod = "AadharFrontImage";
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, FILE_PERMISSION);
        });

        tvUploadAadharBack.setOnClickListener(v ->
        {
            selectedUplaod = "AadharBackImage";
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, FILE_PERMISSION);
        });

        tvUploadPassbook.setOnClickListener(v ->
        {
            selectedUplaod = "PassbookImage";
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, FILE_PERMISSION);
        });



        insuranceBtn.setOnClickListener(v ->
        {
            isReadyForTransaction = true;
            if (!TextUtils.isEmpty(etMobile.getText().toString())
                    && !TextUtils.isEmpty(etPanCard.getText().toString()) && !TextUtils.isEmpty(etEmail.getText().toString())
                    && !TextUtils.isEmpty(etAadhaar.getText().toString()) && !TextUtils.isEmpty(etFirstName.getText().toString())
                    && !TextUtils.isEmpty(etAddress.getText().toString()) && !TextUtils.isEmpty(etPincode.getText().toString())
                    && !TextUtils.isEmpty(etCompanyName.getText().toString())
                     ) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Please select above fields.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        tvDob = findViewById(R.id.insuranceUserDob_tv);
        insurance_dobCardView = findViewById(R.id.insurance_dobCardView);

        etFirstName = findViewById(R.id.username_et);
        etLastname= findViewById(R.id.username_last_et);
        etMobile = findViewById(R.id.insurancemobile_et);
        etTitle = findViewById(R.id.username_title_et);
        etCompanyName = findViewById(R.id.username_companyname_et);
        etBankAccount = findViewById(R.id.username_bank_account_et);
        etBankIfsc = findViewById(R.id.username_bank_ifsc_et);
        etEmail = findViewById(R.id.insuranceemailId_et);
        etPincode = findViewById(R.id.insurancePincode_et);

        etAddress = findViewById(R.id.insuranceAddress_et);
        etPanCard = findViewById(R.id.pancard_et);
        etAadhaar = findViewById(R.id.aadharcard_et);
        etCancelChequeNo = findViewById(R.id.cancelcheque_et);
        insuranceBtn = findViewById(R.id.insuranceBtn);
        insuranceBackImg =findViewById(R.id.insuranceBackImg);
        tvUploadPan = findViewById(R.id.insurance_uploadPantv);
        tvUploadPassbook = findViewById(R.id.insurance_uploadPassbookTv);
        tvUploadAadharFront = findViewById(R.id.insurance_uploadAadharTv);
        tvUploadAadharBack = findViewById(R.id.insurance_uploadAadharBackTv);
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        task -> {
                            Location location = task.getResult();
                            if (location == null) {
                                requestNewLocationData();
                            } else {
                                lat = location.getLatitude() + "";
                                longi = location.getLongitude() + "";
                                if (isReadyForTransaction)
                                    doUserOnboard();
                            }
                        }
                );
            } else {
                Toast.makeText(CredoAepsKycActivity.this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private void doUserOnboard() {

            ProgressDialog pDialog = new ProgressDialog(CredoAepsKycActivity.this);
            pDialog.setCancelable(false);
            pDialog.setMessage("Please wait....");
            pDialog.show();

            mobileNo = etMobile.getText().toString().trim();
            panNo = etPanCard.getText().toString().trim();
            emailId = etEmail.getText().toString().trim();
            aadharNo = etAadhaar.getText().toString().trim();
            pincode = etPincode.getText().toString().trim();
            firstName = etFirstName.getText().toString().trim();
            lastName = etFirstName.getText().toString().trim();
            address = etAddress.getText().toString().trim();
            companyName = etCompanyName.getText().toString().trim();

             bankAccountNum = etBankAccount.getText().toString().trim();
            bankIfsc = etBankIfsc.getText().toString().trim();
            merchantTitle = etTitle.getText().toString().trim();
            cancelChequeNum = etCancelChequeNo.getText().toString().trim();



            Call<JsonObject> call = RetrofitClient.getInstance().getApi().credoAepsUserOnboardKyc(AUTH_KEY, userid,deviceId,deviceInfo,
                    firstName,lastName,mobileNo,selectedDob,address,pincode,panNo,panFileUrl,aadharNo,
                    aadharFrontFileUrl,aadharBackFileUrl,emailId,companyName,bankAccountNum,bankIfsc,merchantTitle,passbookFileUrl,
                    cancelChequeNum,lat,longi);

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                            String responseCode = responseObject.getString("statuscode");

                            if (responseCode.equalsIgnoreCase("TXN")) {

                                String message = responseObject.getString("data");

                                pDialog.dismiss();

                                new androidx.appcompat.app.AlertDialog.Builder(CredoAepsKycActivity.this)
                                        .setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                finish();
                                            }
                                        }).show();

                            } else {
                                String message = responseObject.getString("data");
                                pDialog.dismiss();
                                new androidx.appcompat.app.AlertDialog.Builder(CredoAepsKycActivity.this)
                                        .setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                finish();
                                            }
                                        }).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(CredoAepsKycActivity.this)
                                    .setMessage("Please try after sometime")
                                    .setCancelable(false)
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    }).show();
                        }
                    } else {
                        pDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(CredoAepsKycActivity.this)
                                .setMessage("Please try after sometime")
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                }).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    pDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(CredoAepsKycActivity.this)
                            .setMessage(t.getMessage())
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).show();
                }
            });



    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(CredoAepsKycActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(CredoAepsKycActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(CredoAepsKycActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(CredoAepsKycActivity.this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            lat = mLastLocation.getLatitude() + "";
            longi = mLastLocation.getLongitude() + "";
            if (isReadyForTransaction)
                doUserOnboard();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    ///////////

    public void checkPermission(String writePermission, String readPermission, int requestCode) {

        if (ContextCompat.checkSelfPermission(CredoAepsKycActivity.this, writePermission) == PackageManager.PERMISSION_DENIED
                && ContextCompat.checkSelfPermission(CredoAepsKycActivity.this, readPermission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(CredoAepsKycActivity.this, new String[]{writePermission, readPermission}, requestCode);
        } else {
            chooseImage();
        }
    }

    public void chooseImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (1 == requestCode && resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String mediaPath = cursor.getString(columnIndex);

                if (selectedUplaod.equalsIgnoreCase("PanImage"))
                {
                    panImagePath = mediaPath;
                }
                else if (selectedUplaod.equalsIgnoreCase("AadharFrontImage"))
                {
                    aadharFrontImagePath = mediaPath;
                }
                else if (selectedUplaod.equalsIgnoreCase("AadharBackImage"))
                {
                    aadharBackImagePath = mediaPath;
                }
                else if (selectedUplaod.equalsIgnoreCase("PassbookImage"))
                {
                    passbookImagePath = mediaPath;
                }


                File file = new File(mediaPath);
                serverUpload(file);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void serverUpload(File myfile) {


        ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading ....");
        pDialog.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
        pDialog.setCancelable(false);
        pDialog.show();

        RequestBody reqFile;
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        if (myfile.toString().contains(".pdf")) {
            reqFile = RequestBody.create(MediaType.parse("application/pdf"), myfile);
        } else if (myfile.toString().contains(".jpg") || myfile.toString().contains(".jpeg") || myfile.toString().contains(".png")) {
            reqFile = RequestBody.create(MediaType.parse("image/*"), myfile);
        } else {
            pDialog.dismiss();
            Toast.makeText(this, "Please select only image file.", Toast.LENGTH_SHORT).show();
            return;
        }
        MultipartBody.Part body = MultipartBody.Part.createFormData("myFile", timeStamp + myfile.getName(), reqFile);


        Call<JsonObject> call = RetrofitClient.getInstance().getApi().uploadfile(AUTH_KEY,body);
        call.enqueue(new Callback<JsonObject>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        String code = jsonObject.getString("statuscode");

                        if (code.equalsIgnoreCase("TXN")) {

                            if(selectedUplaod.equalsIgnoreCase("PanImage")){
                                tvUploadPan.setText("Pan File Uploaded");
                                panFileUrl = jsonObject.getString("data");
                                pDialog.dismiss();

                            }
                            else
                            if(selectedUplaod.equalsIgnoreCase("AadharFrontImage")){
                                tvUploadAadharFront.setText("Aaadhar Uploaded");
                                aadharFrontFileUrl = jsonObject.getString("data");
                                pDialog.dismiss();
                            }

                            else
                            if(selectedUplaod.equalsIgnoreCase("AadharBackImage")){
                                tvUploadAadharBack.setText("Aaadhar Uploaded");
                                aadharBackFileUrl = jsonObject.getString("data");
                                pDialog.dismiss();
                            }

                            else
                            if(selectedUplaod.equalsIgnoreCase("PassbookImage")){
                                tvUploadPassbook.setText("Uploaded");
                                passbookFileUrl = jsonObject.getString("data");
                                pDialog.dismiss();
                            }


                        } else if (code.equalsIgnoreCase("ERR")) {
                            showSnackbar("Try again.");
                            pDialog.dismiss();

                        } else {
                            pDialog.dismiss();
                            showSnackbar("Try again.");
                        }

                    } catch (JSONException e) {
                        pDialog.dismiss();
                        e.printStackTrace();
                        showSnackbar("Try again.");
                    }

                } else {
                    pDialog.dismiss();
                    showSnackbar("Try again.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                showSnackbar("Try again.");
            }
        });

    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.LifeInsuranceLayout), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}