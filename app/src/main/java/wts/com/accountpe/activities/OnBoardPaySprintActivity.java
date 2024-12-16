package wts.com.accountpe.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.paysprint.onboardinglib.activities.HostActivity;

import wts.com.accountpe.R;
public class OnBoardPaySprintActivity extends AppCompatActivity {

    TextView tvName, tvMobileNo, tvEmail;
    Button btnOnBoard;
    String name, mobileNo, email, firmName, userId, deviceId, deviceInfo;
    String partnerKey = "UFMwMDI2ODFlYzY0N2Y3OTk3NjdlN2UzNTVmY2JkNGM4ZmJiYzg4Mg==";
    String partnerId = "PS002681";

    //////////////////////////////////////////////LOCATION
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    String lat = "0.0", longi = "0.0";
    //////////////////////////////////////////////LOCATION

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board_pay_sprint);
        initViews();

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(OnBoardPaySprintActivity.this);
        name = sharedPreferences.getString("username",null);
        mobileNo = sharedPreferences.getString("mobileno",null);
        email = sharedPreferences.getString("email",null);
        firmName = sharedPreferences.getString("firmName",null);
        userId = sharedPreferences.getString("userid",null);
        deviceId = sharedPreferences.getString("deviceId",null);
        deviceInfo = sharedPreferences.getString("deviceInfo",null);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(OnBoardPaySprintActivity.this);

        tvName.setText(name);
        tvMobileNo.setText(mobileNo);
        tvEmail.setText(email);

        btnOnBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastLocation();
            }
        });
    }

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
                                    launchOnBoardSdk();
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(OnBoardPaySprintActivity.this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(OnBoardPaySprintActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA},
                PERMISSION_ID
        );
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(OnBoardPaySprintActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(OnBoardPaySprintActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(OnBoardPaySprintActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(OnBoardPaySprintActivity.this);
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
            launchOnBoardSdk();
        }
    };

    private void launchOnBoardSdk() {
        Intent intent = new Intent(OnBoardPaySprintActivity.this, HostActivity.class);
        intent.putExtra("pId", partnerId);//  "PS002681"  partner Id provided in credential
        intent.putExtra("pApiKey", partnerKey);//  "UFMwMDI2ODFlYzY0N2Y3OTk3NjdlN2UzNTVmY2JkNGM4ZmJiYzg4Mg=="  JWT API Key provided in credential
        intent.putExtra("mCode", mobileNo);//   8102428282   Merchant Code
        intent.putExtra("mobile", mobileNo);//   merchant mobile number
        intent.putExtra("lat", lat);
        intent.putExtra("lng", longi);
        intent.putExtra("firm", firmName);  //  NA
        intent.putExtra("email", email);   //   mdajmatm92@gmail.com
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 999);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 999) {
            if (resultCode == RESULT_OK) {
                boolean status = data.getBooleanExtra("status", false);
                int response = data.getIntExtra("response", 0);
                String message = data.getStringExtra("message");
                String detailedResponse = "Status " + status + "\nresponse " + response + "\nMessage " + message;

                new AlertDialog.Builder(OnBoardPaySprintActivity.this)
                        .setTitle("Info")
                        .setMessage(detailedResponse)
                        .setCancelable(false)
                        .setPositiveButton("ok", null)
                        .show();
            }
        }
    }

    private void initViews() {
        tvName = findViewById(R.id.tv_name);
        tvMobileNo = findViewById(R.id.tv_mobile_number);
        tvEmail = findViewById(R.id.tv_mail);
        btnOnBoard = findViewById(R.id.btn_on_board);
    }

}