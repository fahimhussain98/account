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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.retrofit.RetrofitClient;


public class InstantPayAddBankDetailActivity extends AppCompatActivity {
    TextView tvBankName;
    EditText etAccountHolderName,etAccountNumber,etIfscCode;
    Button btnAddDetails, btnVerifyDetails;
    String userId,deviceId,deviceInfo, mobileNo, address, userName, pincode;
    SharedPreferences sharedPreferences;

    ArrayList<String> bankNameArrayList, ifscArrayList;
    String selectedBankName="select";

    //////////////////////////////////////////////LOCATION
    String pan;
    int LOCATION_PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    String lat = "0.0", longi = "0.0";
    //////////////////////////////////////////////LOCATION


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instant_pay_add_bank_detail);

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(InstantPayAddBankDetailActivity.this);
        userId= sharedPreferences.getString("userid",null);
        deviceId=sharedPreferences.getString("deviceId",null);
        deviceInfo= sharedPreferences.getString("deviceInfo",null);
        mobileNo= sharedPreferences.getString("mobileno",null);
        address= sharedPreferences.getString("address",null);
        userName= sharedPreferences.getString("username",null);

        initViews();
        getBanks();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(InstantPayAddBankDetailActivity.this);

        btnVerifyDetails.setOnClickListener(view ->
        {
            getLastLocation();
        });

        btnAddDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInputs())
                {

                    addBankDetails();
                }
                else
                {
                    Toast.makeText(InstantPayAddBankDetailActivity.this, "All Fields Are Mandatory!!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void verifyAddBankDetails() {
        ProgressDialog progressDialog =new ProgressDialog(InstantPayAddBankDetailActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        String accountHolderName=etAccountHolderName.getText().toString().trim();
        String accountHolderNumber=etAccountNumber.getText().toString().trim();
        String ifsc=etIfscCode.getText().toString().trim();
        Call<JsonObject> call= RetrofitClient.getInstance().getApi().verifyInstantPayAddBankDetails(AUTH_KEY,userId,deviceId,deviceInfo,"NA",selectedBankName,accountHolderName,"NA",mobileNo,ifsc,accountHolderNumber,mobileNo,address,"NA","NA",userName,"ifs",lat,longi);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful())
                {
                    try {
                        JSONObject responseObject=new JSONObject(String.valueOf(response.body()));
                        String statusCode = responseObject.getString("statuscode");

                        if (statusCode.equalsIgnoreCase("TXN"))
                        {
                            progressDialog.dismiss();
                            String verifiedName=responseObject.getString("data");
                            etAccountHolderName.setText(verifiedName);

                            btnAddDetails.setVisibility(View.VISIBLE);
                            btnVerifyDetails.setVisibility(View.GONE);
                            tvBankName.setEnabled(false);
                            etAccountHolderName.setEnabled(false);
                            etAccountNumber.setEnabled(false);
                            etIfscCode.setEnabled(false);

                        }
                        else
                        {
                            progressDialog.dismiss();
                            String errorMessage=responseObject.getString("data");
                            new AlertDialog.Builder(InstantPayAddBankDetailActivity.this)
                                    .setMessage(errorMessage)
                                    .setPositiveButton("OK",null)
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        new AlertDialog.Builder(InstantPayAddBankDetailActivity.this)
                                .setMessage("Something went wrong.")
                                .setPositiveButton("OK",null)
                                .show();
                    }
                }
                else
                {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(InstantPayAddBankDetailActivity.this)
                            .setMessage("Something went wrong.")
                            .setPositiveButton("OK",null)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(InstantPayAddBankDetailActivity.this)
                        .setMessage(t.getMessage())
                        .setPositiveButton("OK",null)
                        .show();
            }
        });
    }

    private void addBankDetails() {
        ProgressDialog progressDialog =new ProgressDialog(InstantPayAddBankDetailActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        String accountHolderName=etAccountHolderName.getText().toString().trim();
        String accountHolderNumber=etAccountNumber.getText().toString().trim();
        String ifsc=etIfscCode.getText().toString().trim();
        Call<JsonObject> call= RetrofitClient.getInstance().getApi().addbankDetails(AUTH_KEY,userId,deviceId,deviceInfo,selectedBankName,
                accountHolderNumber,accountHolderName,ifsc);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful())
                {
                    try {
                        JSONObject responseObject=new JSONObject(String.valueOf(response.body()));
                        String message=responseObject.getString("data");
                        progressDialog.dismiss();
                        new AlertDialog.Builder(InstantPayAddBankDetailActivity.this)
                                .setCancelable(false)
                                .setMessage(message)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                })
                                .show();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        new AlertDialog.Builder(InstantPayAddBankDetailActivity.this)
                                .setMessage("Something went wrong.")
                                .setPositiveButton("OK",null)
                                .show();
                    }
                }
                else
                {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(InstantPayAddBankDetailActivity.this)
                            .setMessage("Something went wrong.")
                            .setPositiveButton("OK",null)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(InstantPayAddBankDetailActivity.this)
                        .setMessage(t.getMessage())
                        .setPositiveButton("OK",null)
                        .show();
            }
        });
    }

    private boolean checkInputs() {
        return !selectedBankName.equalsIgnoreCase("select") && !TextUtils.isEmpty(etAccountHolderName.getText())
                && !TextUtils.isEmpty(etAccountNumber.getText()) && !TextUtils.isEmpty(etIfscCode.getText());
    }

    private void initViews() {
        tvBankName=findViewById(R.id.tv_bank_name);
        etAccountHolderName=findViewById(R.id.et_account_holder_name);
        etAccountNumber=findViewById(R.id.et_account_number);
        etIfscCode=findViewById(R.id.et_ifsc_number);
        btnAddDetails=findViewById(R.id.btn_add_details);
        btnVerifyDetails=findViewById(R.id.btn_verify);
    }

    private void getBanks() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getBankDmt(AUTH_KEY, deviceId, deviceInfo, userId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;

                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = jsonObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            JSONArray dataArray=jsonObject.getJSONArray("data");

                            bankNameArrayList = new ArrayList<>();
                            ifscArrayList = new ArrayList<>();

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject transactionObject = dataArray.getJSONObject(i);

                                String responsebankName = transactionObject.getString("BankName");
                                String responseIfsc = transactionObject.getString("IFSC");

                                bankNameArrayList.add(responsebankName);
                                ifscArrayList.add(responseIfsc);

                            }

                            tvBankName.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SpinnerDialog operatorDialog = new SpinnerDialog(InstantPayAddBankDetailActivity.this, bankNameArrayList, "Select Bank", R.style.DialogAnimations_SmileWindow, "Close  ");// With 	Animation
                                    operatorDialog.setCancellable(true); // for cancellable
                                    operatorDialog.setShowKeyboard(false);// for open keyboard by default
                                    operatorDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                                        @Override
                                        public void onClick(String item, int position) {
                                            tvBankName.setText(item);
                                            selectedBankName = bankNameArrayList.get(position);
                                            etIfscCode.setText(ifscArrayList.get(position));
                                        }
                                    });

                                    operatorDialog.showSpinerDialog();
                                }
                            });


                            pDialog.dismiss();
                        } else if (responseCode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();
                            new AlertDialog.Builder(InstantPayAddBankDetailActivity.this)
                                    .setTitle("Alert!!!")
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(InstantPayAddBankDetailActivity.this)
                                .setTitle("Alert!!!")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(InstantPayAddBankDetailActivity.this)
                            .setTitle("Alert!!!")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(InstantPayAddBankDetailActivity.this)
                        .setTitle("Alert!!!")
                        .setMessage("Something went wrong.")
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }


    //  get location

    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            lat = mLastLocation.getLatitude() + "";
            longi = mLastLocation.getLongitude() + "";

           if (checkInputs())
           {
               verifyAddBankDetails();
           }

        }
    };

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    lat = location.getLatitude() + "";
                                    longi = location.getLongitude() + "";
                                    verifyAddBankDetails();
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(InstantPayAddBankDetailActivity.this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(InstantPayAddBankDetailActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(InstantPayAddBankDetailActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(InstantPayAddBankDetailActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                1
        );
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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(InstantPayAddBankDetailActivity.this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

}