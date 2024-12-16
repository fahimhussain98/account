package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.Arrays;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.retrofit.RetrofitClient;

public class SenderValidationActivity extends AppCompatActivity {
    EditText etNumber;
    Button btnValidate;
    Spinner spinner;
    String[] txtModeList = {"IMPS", "NEFT"};
    String selectedTxtMode = "IMPS";
    String mobileNumber;
    public static String senderMobileNumber, sendername, senderId, availablelimit, totalLimit, remitterId, dmtType;

    ImageView imgBack;
    TextView tvDmrWalletBalance;
    String userid;
    String deviceId, deviceInfo;

    //////////////////////////////////////////////LOCATION
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    public static String lat = "0.0", longi = "0.0";
    //////////////////////////////////////////////LOCATION

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender_validation);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initViews();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SenderValidationActivity.this);
        userid = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        dmtType = getIntent().getStringExtra("dmtType");

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        HintSpinner<String> hintSpinner = new HintSpinner<>(
                spinner,
                new HintAdapter<String>(SenderValidationActivity.this, selectedTxtMode, Arrays.asList(txtModeList)),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        selectedTxtMode = txtModeList[position];
                    }
                });
        hintSpinner.init();

        etNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 10) {
                    hideKeyBoard();
                }
            }
        });

        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetState()) {
                    checkInputs();
                } else {
                    showSnackBar();
                }
            }
        });

    }

    private void isSenderValidate() {
        ProgressDialog pDialog = new ProgressDialog(SenderValidationActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();

        mobileNumber = etNumber.getText().toString();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().isUserValidate(AUTH_KEY, deviceId, deviceInfo, userid, mobileNumber);

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;

                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = jsonObject.getString("statuscode");
                        if (responseCode.equalsIgnoreCase("TXN")) {

                            //recipientModelArrayList = new ArrayList<>();
                            senderMobileNumber = jsonObject.getString("remitterMobile");
                            sendername = jsonObject.getString("remitterName");
                            senderId = jsonObject.getString("remitterId");
                            availablelimit = jsonObject.getString("availableLimit");
                            totalLimit = jsonObject.getString("totalLimit");
                            remitterId = jsonObject.getString("remitterId");

                            Float totalFloatLimit = Float.valueOf(totalLimit);
                            Float availableFloatLimit = Float.valueOf(availablelimit);
                            Float consumedLimit = totalFloatLimit - availableFloatLimit;

                            Intent intent = new Intent(SenderValidationActivity.this, NewMoneyTransferActivity.class);
                            intent.putExtra("senderMobileNumber", senderMobileNumber);
                            intent.putExtra("senderName", sendername);
                            intent.putExtra("mode", selectedTxtMode);
                            intent.putExtra("availableLimit", availablelimit);
                            intent.putExtra("totalLimit", totalLimit);
                            intent.putExtra("consumedLimit", consumedLimit + "");
                            intent.putExtra("remitterId", remitterId);

                            pDialog.dismiss();

                            startActivity(intent);

                        } else if (responseCode.equalsIgnoreCase("ERR")) {
                            Intent intent = new Intent(SenderValidationActivity.this, AddSenderActivity.class);
                            intent.putExtra("mobileNo", mobileNumber);
                            intent.putExtra("mode", selectedTxtMode);
                            startActivity(intent);

                            pDialog.dismiss();
                        } else {
                            pDialog.dismiss();
                            String message = jsonObject.getString("statusMsg");
                            new AlertDialog.Builder(SenderValidationActivity.this).setTitle("Message")
                                    .setMessage(message)
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(SenderValidationActivity.this)
                                .setTitle("Alert!!!")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(SenderValidationActivity.this)
                            .setTitle("Alert!!!")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                new AlertDialog.Builder(SenderValidationActivity.this)
                        .setTitle("Alert!!!")
                        .setMessage("Something went wrong.")
                        .setPositiveButton("Ok", null)
                        .show();
                pDialog.dismiss();
            }
        });

    }

    private void showSnackBar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.sender_validation_layout), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void checkInputs() {
        if (!selectedTxtMode.equalsIgnoreCase("Select Mode")) {
            if (etNumber.getText().length() == 10) {
                // isSenderValidate();
                //  getLastLocation();

                if (dmtType.equalsIgnoreCase("dmt")) {
                    isSenderValidate();
                } else {
                    getLastLocation();
                }

            } else {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.sender_validation_layout), "Enter valid number.", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        } else {
            new AlertDialog.Builder(SenderValidationActivity.this)
                    .setTitle("Alert!!!")
                    .setMessage("Please select transfer mode.")
                    .setPositiveButton("Ok", null)
                    .show();
        }
    }

    private boolean checkInternetState() {
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (manager != null) {
            networkInfo = manager.getActiveNetworkInfo();
        }

        if (networkInfo != null) {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    public void hideKeyBoard() {
        View view1 = this.getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
            }
        }
    }

    private void initViews() {
        imgBack = findViewById(R.id.img_back);
        spinner = findViewById(R.id.spinner);
        etNumber = findViewById(R.id.et_mobile_number);
        btnValidate = findViewById(R.id.btn_validate);
        tvDmrWalletBalance = findViewById(R.id.tv_dmr_balance);
    }

    //  get location  //////////////////////////////////////

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            lat = location.getLatitude() + "";
                            longi = location.getLongitude() + "";

                            isSenderValidate();
                        }
                    }
                });

            } else {
                Toast.makeText(SenderValidationActivity.this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(SenderValidationActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(SenderValidationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(SenderValidationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(SenderValidationActivity.this);
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

            isSenderValidate();

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

    /////////////////////////////////////////////////////////

}