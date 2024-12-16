package wts.com.accountpe.activities;


import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.retrofit.RetrofitClient;


public class WtsInstantUserOnboardActivity extends AppCompatActivity {

    EditText etMobileNo, etPanNo, etEmailId, etAadharNo, etPincode, etName, etAddress, etCompany;
    Spinner spinnerOldNew, spinnerConcent;
    ArrayList<String> oldNewArrayList,concentArrayList;

    AppCompatButton btnSubmit;
    SharedPreferences sharedPreferences;
    String userId, deviceId, deviceInfo;
    String mobileNo, panNo, emailId, aadharNo, pincode, name, address, company, selectedOldNew = "select", selectedConcent = "select";
    //////////////////////////////////////////////LOCATION
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    String lat = "0.0", longi = "0.0";
    //////////////////////////////////////////////LOCATION
    boolean isReadyForTransaction = false;
    String hash, otpReferenceId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wts_instant_user_onboard);
        initViews();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WtsInstantUserOnboardActivity.this);
        mobileNo = sharedPreferences.getString("mobileno", null);
        panNo = sharedPreferences.getString("pancard", null);
        emailId = sharedPreferences.getString("email", null);
        aadharNo = sharedPreferences.getString("adharcard", null);
        name = sharedPreferences.getString("username", null);
        address = sharedPreferences.getString("address", null);
        company = sharedPreferences.getString("firmName", null);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        etMobileNo.setText(mobileNo);
        etPanNo.setText(panNo);
        etEmailId.setText(emailId);
        etAadharNo.setText(aadharNo);
        etName.setText(name);
        etAddress.setText(address);
        etCompany.setText(company);

        HintSpinner<String> hintOldNewSpinner = new HintSpinner<>(
                spinnerOldNew,
                new HintAdapter<String>(WtsInstantUserOnboardActivity.this, "Select", oldNewArrayList),
                (position, itemAtPosition) -> selectedOldNew = oldNewArrayList.get(position));
        hintOldNewSpinner.init();

        HintSpinner<String> hinConcentSpinner = new HintSpinner<>(
                spinnerConcent,
                new HintAdapter<String>(WtsInstantUserOnboardActivity.this, "Concent", concentArrayList),
                (position, itemAtPosition) -> selectedConcent = concentArrayList.get(position));
        hinConcentSpinner.init();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        btnSubmit.setOnClickListener(v ->
        {
            isReadyForTransaction = true;
            if (!TextUtils.isEmpty(etMobileNo.getText().toString())
                    && !TextUtils.isEmpty(etPanNo.getText().toString()) && !TextUtils.isEmpty(etEmailId.getText().toString())
                    && !TextUtils.isEmpty(etAadharNo.getText().toString()) && !TextUtils.isEmpty(etName.getText().toString())
                    && !TextUtils.isEmpty(etAddress.getText().toString()) && !TextUtils.isEmpty(etPincode.getText().toString())
                    && !TextUtils.isEmpty(etCompany.getText().toString())
                    && !selectedConcent.equalsIgnoreCase("select") && !selectedOldNew.equalsIgnoreCase("select")) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Please select above fields.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @SuppressLint("SuspiciousIndentation")
    private void doUserOnboard() {
        final AlertDialog pDialog = new AlertDialog.Builder(WtsInstantUserOnboardActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        mobileNo = etMobileNo.getText().toString().trim();
        panNo = etPanNo.getText().toString().trim();
        emailId = etEmailId.getText().toString().trim();
        aadharNo = etAadharNo.getText().toString().trim();
        pincode = etPincode.getText().toString().trim();
        name = etName.getText().toString().trim();
        address = etAddress.getText().toString().trim();
        company = etCompany.getText().toString().trim();

        if (selectedOldNew.equalsIgnoreCase("Old"))
            selectedOldNew="1";
        else
        selectedOldNew="0";

        if (selectedConcent.equalsIgnoreCase("Yes"))
            selectedConcent="Y";
        else
            selectedConcent="N";

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().instantWtsUserOnboard(AUTH_KEY, userId, deviceId, deviceInfo,
                mobileNo, panNo, emailId, aadharNo, lat, longi, pincode, name, address, company, selectedOldNew, selectedConcent);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            hash = responseObject.getString("data");
                            otpReferenceId = responseObject.getString("status");
                            pDialog.dismiss();
                            showOtpDialog();
                        } else {
                            String message = responseObject.getString("data");
                            pDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(WtsInstantUserOnboardActivity.this)
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
                        new androidx.appcompat.app.AlertDialog.Builder(WtsInstantUserOnboardActivity.this)
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
                    new androidx.appcompat.app.AlertDialog.Builder(WtsInstantUserOnboardActivity.this)
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
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(WtsInstantUserOnboardActivity.this)
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

    @SuppressLint("SetTextI18n")
    private void showOtpDialog() {
        View addSenderDialogView = getLayoutInflater().inflate(R.layout.add_sender_otp_dialog_layout, null, false);
        final androidx.appcompat.app.AlertDialog otpDialog = new androidx.appcompat.app.AlertDialog.Builder(WtsInstantUserOnboardActivity.this).create();
        otpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        otpDialog.setCancelable(false);
        otpDialog.setView(addSenderDialogView);
        otpDialog.show();

        ImageView imgClose = addSenderDialogView.findViewById(R.id.img_close);
        TextView tvTitle = addSenderDialogView.findViewById(R.id.text_user_registration);
        final EditText etOtp = addSenderDialogView.findViewById(R.id.et_otp);
        Button btnCancel = addSenderDialogView.findViewById(R.id.btn_cancel);
        Button btnSubmit = addSenderDialogView.findViewById(R.id.btn_submit);
        Button btnResendOtp = addSenderDialogView.findViewById(R.id.btn_resend_otp);

        btnResendOtp.setVisibility(View.GONE);
        tvTitle.setText("OTP Verification");
        etOtp.setHint("OTP");

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpDialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpDialog.dismiss();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etOtp.getText())) {
                    String otp = etOtp.getText().toString().trim();
                    doOnboardVerification(otp);
                    otpDialog.dismiss();
                } else {
                    etOtp.setError("Required");
                }
            }
        });
    }

    private void doOnboardVerification(String otp) {
        final AlertDialog pDialog = new AlertDialog.Builder(WtsInstantUserOnboardActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().instantWtsUserOnboardVerify(AUTH_KEY, userId, deviceId, deviceInfo,
                otpReferenceId, hash, otp, mobileNo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject responseObject = null;
                    try {
                        responseObject = new JSONObject(String.valueOf(response.body()));
                        String message = responseObject.getString("data");
                        pDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(WtsInstantUserOnboardActivity.this)
                                .setMessage(message)
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                }).show();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(WtsInstantUserOnboardActivity.this)
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
                    new androidx.appcompat.app.AlertDialog.Builder(WtsInstantUserOnboardActivity.this)
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
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(WtsInstantUserOnboardActivity.this)
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

    private void initViews() {
        etMobileNo = findViewById(R.id.et_mobile_number);
        etPanNo = findViewById(R.id.et_pan_number);
        etEmailId = findViewById(R.id.et_email_id);
        etAadharNo = findViewById(R.id.et_aadhar_number);
        etPincode = findViewById(R.id.et_pin_code);
        etName = findViewById(R.id.et_name);
        etAddress = findViewById(R.id.et_address);
        etCompany = findViewById(R.id.et_company);
        spinnerOldNew = findViewById(R.id.spinner_old_new);
        spinnerConcent = findViewById(R.id.spinner_concent);
        btnSubmit = findViewById(R.id.btn_submit);

        oldNewArrayList=new ArrayList<>();
        oldNewArrayList.add("Old");
        oldNewArrayList.add("New");

        concentArrayList=new ArrayList<>();
        concentArrayList.add("Yes");
        concentArrayList.add("No");
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
                Toast.makeText(WtsInstantUserOnboardActivity.this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(WtsInstantUserOnboardActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(WtsInstantUserOnboardActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(WtsInstantUserOnboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(WtsInstantUserOnboardActivity.this);
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
}