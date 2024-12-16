package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.retrofit.RetrofitClient;


public class CredoOnboardActivity extends AppCompatActivity {
    EditText  etPincode, etAddress, etDeviceImei,etDeviceSerial,etDeviceSModel;

    AppCompatButton btnSubmit;
    SharedPreferences sharedPreferences;
    String userId, deviceId, deviceInfo;
    String mobileNo, panNo, emailId, aadharNo, pincode, name, address, company,deviceImei,deviceSerial,deviceModel;
    //////////////////////////////////////////////LOCATION
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    String lat = "0.0", longi = "0.0";
    //////////////////////////////////////////////LOCATION
    boolean isReadyForTransaction = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credo_onboard);
        initViews();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CredoOnboardActivity.this);
        panNo = sharedPreferences.getString("pancard", null);
        emailId = sharedPreferences.getString("email", null);
        mobileNo = sharedPreferences.getString("mobileno", null);
        aadharNo = sharedPreferences.getString("adharcard", null);
        name = sharedPreferences.getString("username", null);
        address = sharedPreferences.getString("address", null);
        company = sharedPreferences.getString("firmName", null);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        etAddress.setText(address);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
       // getLastLocation();

        btnSubmit.setOnClickListener(v ->
        {
            isReadyForTransaction = true;
            if (!TextUtils.isEmpty(etAddress.getText().toString()) && !TextUtils.isEmpty(etPincode.getText().toString())

                    ) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Please select above fields.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void doUserOnboard() {

        ProgressDialog pDialog = new ProgressDialog(CredoOnboardActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Please wait....");
        pDialog.show();


        pincode = etPincode.getText().toString().trim();
        address = etAddress.getText().toString().trim();
        deviceImei = etDeviceImei.getText().toString().trim();
        deviceSerial = etDeviceSerial.getText().toString().trim();
        deviceModel = etDeviceSModel.getText().toString().trim();



        Call<JsonObject> call = RetrofitClient.getInstance().getApi().credoAepsUserOnboard(AUTH_KEY, userId, deviceId, deviceInfo,
                deviceModel,deviceSerial,deviceImei,address,pincode,lat,longi );
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
                            new androidx.appcompat.app.AlertDialog.Builder(CredoOnboardActivity.this)
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
                            new androidx.appcompat.app.AlertDialog.Builder(CredoOnboardActivity.this)
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
                        new androidx.appcompat.app.AlertDialog.Builder(CredoOnboardActivity.this)
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
                    new androidx.appcompat.app.AlertDialog.Builder(CredoOnboardActivity.this)
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
                new androidx.appcompat.app.AlertDialog.Builder(CredoOnboardActivity.this)
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




        etPincode = findViewById(R.id.et_pin_code);

        etAddress = findViewById(R.id.et_address);
        etDeviceImei = findViewById(R.id.et_deviceimei);

        etDeviceSerial = findViewById(R.id.et_deviceserial);
        etDeviceSModel = findViewById(R.id.et_devicemodel);



        btnSubmit = findViewById(R.id.btn_submit);




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
                Toast.makeText(CredoOnboardActivity.this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(CredoOnboardActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(CredoOnboardActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(CredoOnboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(CredoOnboardActivity.this);
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