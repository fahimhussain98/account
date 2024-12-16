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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.retrofit.RetrofitClient;

public class InstantPayAepsUserOnBoardActivity extends AppCompatActivity {

    ImageView imgBack;
    EditText etMobileNo, etPanNo, etEmailId, etAadharNo;
    AutoCompleteTextView spinnerConsent;
    ArrayList<String> consentArrayList;

    AppCompatButton btnSubmit;
    SharedPreferences sharedPreferences;
    String userId, deviceId, deviceInfo;
    String mobileNo, panNo, emailId, aadharNo, selectedConcent = "select";
    //////////////////////////////////////////////LOCATION
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    String lat = "0.0", longi = "0.0";
    //////////////////////////////////////////////LOCATION
    boolean isReadyForTransaction = false;
    String hash, otpReferenceId;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instant_pay_aeps_user_on_board);

        initViews();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(InstantPayAepsUserOnBoardActivity.this);
        mobileNo = sharedPreferences.getString("mobileno", null);
        panNo = sharedPreferences.getString("pancard", null);
        emailId = sharedPreferences.getString("email", null);
        aadharNo = sharedPreferences.getString("adharcard", null);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        etMobileNo.setText(mobileNo);
        etPanNo.setText(panNo);
        etEmailId.setText(emailId);
        etAadharNo.setText(aadharNo);

        consentArrayList = new ArrayList<>();
        consentArrayList.add("Yes");
        consentArrayList.add("No");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(InstantPayAepsUserOnBoardActivity.this, android.R.layout.simple_list_item_1, consentArrayList);
        spinnerConsent.setAdapter(adapter);
        spinnerConsent.setOnClickListener(view ->
        {
            spinnerConsent.showDropDown();
        });
        spinnerConsent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedConcent = consentArrayList.get(i);
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        imgBack.setOnClickListener(view ->
        {
            finish();
        });

        btnSubmit.setOnClickListener(view ->
        {
            if (checkInput()) {
                getLastLocation();
            } else {
                new AlertDialog.Builder(InstantPayAepsUserOnBoardActivity.this).setMessage("All fields are mandatory").show();
            }
        });


    }

    private void doUserOnboard() {
        final AlertDialog pDialog = new AlertDialog.Builder(InstantPayAepsUserOnBoardActivity.this).create();
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

        if (selectedConcent.equalsIgnoreCase("Yes"))
            selectedConcent = "Y";
        else
            selectedConcent = "N";

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().instantWtsUserOnboard(AUTH_KEY, userId, deviceId, deviceInfo,
                mobileNo, panNo, emailId, aadharNo, lat, longi, selectedConcent);
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
                            new androidx.appcompat.app.AlertDialog.Builder(InstantPayAepsUserOnBoardActivity.this)
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
                        new androidx.appcompat.app.AlertDialog.Builder(InstantPayAepsUserOnBoardActivity.this)
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
                    new androidx.appcompat.app.AlertDialog.Builder(InstantPayAepsUserOnBoardActivity.this)
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
                new androidx.appcompat.app.AlertDialog.Builder(InstantPayAepsUserOnBoardActivity.this)
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
        final androidx.appcompat.app.AlertDialog otpDialog = new androidx.appcompat.app.AlertDialog.Builder(InstantPayAepsUserOnBoardActivity.this).create();
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
        final AlertDialog pDialog = new AlertDialog.Builder(InstantPayAepsUserOnBoardActivity.this).create();
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
                        new androidx.appcompat.app.AlertDialog.Builder(InstantPayAepsUserOnBoardActivity.this)
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
                        new androidx.appcompat.app.AlertDialog.Builder(InstantPayAepsUserOnBoardActivity.this)
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
                    new androidx.appcompat.app.AlertDialog.Builder(InstantPayAepsUserOnBoardActivity.this)
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
                new androidx.appcompat.app.AlertDialog.Builder(InstantPayAepsUserOnBoardActivity.this)
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


    //  get location

    public boolean checkInput() {
        if (!TextUtils.isEmpty(etMobileNo.getText().toString())
                && !TextUtils.isEmpty(etPanNo.getText().toString()) && !TextUtils.isEmpty(etEmailId.getText().toString())
                && !TextUtils.isEmpty(etAadharNo.getText().toString())

                && !selectedConcent.equalsIgnoreCase("select")) {

            return true;
        } else {
            return false;
        }
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

                                doUserOnboard();
                            }
                        }
                );
            } else {
                Toast.makeText(InstantPayAepsUserOnBoardActivity.this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(InstantPayAepsUserOnBoardActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(InstantPayAepsUserOnBoardActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(InstantPayAepsUserOnBoardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(InstantPayAepsUserOnBoardActivity.this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    private void initViews() {
        imgBack = findViewById(R.id.imgBack);
        etMobileNo = findViewById(R.id.et_mobile_number);
        etPanNo = findViewById(R.id.et_pan_number);
        etEmailId = findViewById(R.id.et_email_id);
        etAadharNo = findViewById(R.id.et_aadhar_number);
        spinnerConsent = findViewById(R.id.spinner_consent);
        btnSubmit = findViewById(R.id.btn_submit);

    }

}