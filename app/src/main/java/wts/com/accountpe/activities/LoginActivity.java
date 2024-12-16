package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.jaredrummler.android.device.DeviceName;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.BuildConfig;
import wts.com.accountpe.R;
import wts.com.accountpe.databinding.ActivityLoginBinding;
import wts.com.accountpe.retrofit.RetrofitClient;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    String userid, username, usertype, panCard, aadharCard, firmName, address, dob, userTypeId, city, imageUrl, deviceId, deviceInfo, emailId, mobileNo,
            loginUserName,deviceName1, password;
    String versionCode;
    String lat = "0.0", longi = "0.0";
    AlertDialog authCodeDialog;

  //  boolean isMpinGenerated;
    String isMpinGenerated;
    String isChangePassword;
    FusedLocationProviderClient mFusedLocationClient;


    //////////////////////////////////////////////////////////////////////////FORGET PASSWORD
    EditText etForgetUsername, etForgetMobile;
    AppCompatButton btnCancel, btnOk;
    String forgetUsername, forgetMobile; //forget password dialog inputs
    androidx.appcompat.app.AlertDialog forgetPasswordDialog;

    SharedPreferences sharedPreferences;
    //////////////////////////////////////////////////////////////////////////FORGET PASSWORD

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        versionCode = BuildConfig.VERSION_NAME;

        binding.etUserName.setVisibility(View.GONE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(LoginActivity.this);


        binding.btnLogin.setOnClickListener(view -> {

            if (!TextUtils.isEmpty(binding.etUserName.getText().toString()) &&
                    !TextUtils.isEmpty(binding.etUserPassword.getText().toString())) {
                DeviceName.init(LoginActivity.this);
                DeviceName.with(LoginActivity.this).request((info, error) -> {
                   /* String manufacturer = info.manufacturer;
                    String name = info.marketName;
                    String model = info.model;
                    String codename = info.codename;
                    String deviceName = info.getName();
                    deviceInfo = "Manufacturer-" + manufacturer + " Name-" + name + " Model-" + model + " Codename-" + codename + " DeviceName-" + deviceName + " DeviceId-" + deviceId;
*/

                    //---------------------------------------------------------------------------------------
                    String manufacturer = info.manufacturer;
                    String name = info.marketName;
                    String model = info.model;
                    String codename = info.codename;
                    String deviceName = info.getName();
                    deviceInfo = "Manufacturer-" + manufacturer + " Name-" + name + " Model-" + model + " Codename-" + codename + " DeviceName-" + deviceName + " DeviceId-" + deviceId;
                    deviceName1 = name + " " + model;
                    //---------------------------------------------------------------------------------------
                    //  loginUser();

                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (task.isSuccessful()) {
                                deviceId = task.getResult();

                               // loginUser();  //this methode use for loginUser()=>checkOtp()=>MpinActivity=>homedesboardActivity();
                                getLastLocation();

                            } else {
                                Toast.makeText(LoginActivity.this, "token not found ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                });

            } else {
                showSnackBar("All fields are mandatory.");
            }

        });

        binding.btnRegister.setOnClickListener(v ->
        {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });

        binding.tvForgetPassword.setOnClickListener(view ->
        {
            forgetPassword();
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

                                    loginUser();

                                }
                            }
                        }
                );
            } else {
                Toast.makeText(LoginActivity.this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
          //  requestPermissions();
        }
    }

    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            lat = mLastLocation.getLatitude() + "";
            longi = mLastLocation.getLongitude() + "";
            loginUser();
        }
    };
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

   /* private void loginUser() {
        final AlertDialog pDialog = new AlertDialog.Builder(LoginActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        loginUserName = binding.etUserName.getText().toString().trim();
        password = binding.etUserPassword.getText().toString().trim();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().login(AUTH_KEY, loginUserName, password, deviceId, deviceInfo, versionCode);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject1 = new JSONObject(String.valueOf(response.body()));

                        String statusCode = jsonObject1.getString("statuscode");

                        if (statusCode.equalsIgnoreCase("TXN")) {

                            pDialog.dismiss();

                            JSONObject jsonObject = jsonObject1.getJSONObject("data");
                            userid = jsonObject.getString("UserID");
                            username = jsonObject.getString("name");
                            usertype = jsonObject.getString("userType");
                            mobileNo = jsonObject.getString("mobileNo");
                            panCard = jsonObject.getString("panCardNo");
                            aadharCard = jsonObject.getString("aadharNo");
                            emailId = jsonObject.getString("emailID");
                            firmName = jsonObject.getString("companyName");
                            address = jsonObject.getString("address");
                            dob = jsonObject.getString("dob");
                            userTypeId = jsonObject.getString("userTypeId");
                            city = jsonObject.getString("cityname");
                            imageUrl = jsonObject.getString("profileImg");
                            isMpinGenerated = jsonObject.getBoolean("mpin");

//                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
//                            SharedPreferences.Editor editor = sharedPreferences.edit();
//                            editor.putString("userid", userid);
//                            editor.putString("username", username);
//                            editor.putString("pancard", pancard);
//                            editor.putString("mobileno", mobileno);
//                            editor.putString("usertype", usertype);
//                            editor.putString("deviceId", deviceId);
//                            editor.putString("deviceInfo", deviceInfo);
//                            editor.putString("adharcard", aadharCard);
//                            editor.putString("loginUserName", loginUserName);
//                            editor.putString("password", password);
//                            editor.putString("email", emailId);
//                            editor.putString("firmName", firmName);
//                            editor.putString("address", address);
//                            editor.putString("dob", dob);
//                            editor.putString("city", city);
//                            editor.apply();
//
//                            Intent intent = new Intent(LoginActivity.this, HomeDashActivity.class);
//                            startActivity(intent);
//                            finish();


//                            if (isMpinGenerated) {
//                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
//                                SharedPreferences.Editor editor = sharedPreferences.edit();
//                                editor.putString("userid", userid);
//                                editor.putString("username", username);
//                                editor.putString("pancard", pancard);
//                                editor.putString("mobileno", mobileno);
//                                editor.putString("usertype", usertype);
//                                editor.putString("userTypeId", userTypeId);
//                                editor.putString("deviceId", deviceId);
//                                editor.putString("deviceInfo", deviceInfo);
//                                editor.putString("adharcard", aadharCard);
//                                editor.putString("loginUserName", loginUserName);
//                                editor.putString("password", password);
//                                editor.putString("email", emailId);
//                                editor.putString("firmName", firmName);
//                                editor.putString("address", address);
//                                editor.putString("dob", dob);
//                                editor.putString("city", city);
//                                editor.putBoolean("mpin", true);
//                                editor.apply();
//
//                                Intent intent=new Intent(LoginActivity.this,MPINActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }
//                            else {
//                                Intent intent=new Intent(LoginActivity.this,GenerateMpinActivity.class);
//                                intent.putExtra("userId",userid);
//                                intent.putExtra("userName",username);
//                                intent.putExtra("panCard",pancard);
//                                intent.putExtra("mobileNo",mobileno);
//                                intent.putExtra("userType",usertype);
//                                intent.putExtra("deviceId",deviceId);
//                                intent.putExtra("deviceInfo",deviceInfo);
//                                intent.putExtra("aadharCard",aadharCard);
//                                intent.putExtra("loginUserName",loginUserName);
//                                intent.putExtra("password",password);
//                                intent.putExtra("emailId",emailId);
//                                intent.putExtra("firmName",firmName);
//                                intent.putExtra("address",address);
//                                intent.putExtra("dob",dob);
//                                intent.putExtra("city",city);
//                                startActivity(intent);
//                            }

                            checkOtp();

                        } else {
                            pDialog.dismiss();
                            String data = jsonObject1.getString("data");

                            new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this).setTitle("Message")
                                    .setMessage(data)
                                    .setPositiveButton("Ok", null).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this)
                                .setTitle("Message")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Got it!!!", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Message")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Got it!!!", null)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this)
                        .setMessage(t.getMessage())
                        .setPositiveButton("Got it!!!", null)
                        .show();
            }
        });

    }*/

    private void loginUser() {
        final AlertDialog pDialog = new AlertDialog.Builder(LoginActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        loginUserName = binding.etUserName.getText().toString().trim(); //etUsername.getText().toString().trim();
        password = binding.etUserPassword.getText().toString().trim();  //etPassword.getText().toString().trim();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().login(AUTH_KEY, loginUserName, password, deviceId, deviceInfo, "1.1.2", lat, longi, deviceName1);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Log.d("LoginCredintenial","Login"+"\nloginUserName::"+loginUserName+ "\npassword::"+ password+"\ndeviceId::" + deviceId+"\ndeviceInfo::"+deviceInfo);
                    try {
                        JSONObject jsonObject1 = new JSONObject(String.valueOf(response.body()));

                        String statusCode = jsonObject1.getString("statuscode");

                        if (statusCode.equalsIgnoreCase("TXN")) {

                            JSONObject jsonObject = jsonObject1.getJSONObject("data");
                            userid = jsonObject.getString("UserID");
                            username = jsonObject.getString("name");
                            usertype = jsonObject.getString("userType");
                            mobileNo = jsonObject.getString("mobileNo");
                            panCard = jsonObject.getString("panCardNo");
                            aadharCard = jsonObject.getString("aadharNo");
                            emailId = jsonObject.getString("emailID");
                            firmName = jsonObject.getString("companyName");
                            address = jsonObject.getString("address");
                            dob = jsonObject.getString("dob");
                            userTypeId = jsonObject.getString("userTypeId");
                            city = jsonObject.getString("cityname");
                            isMpinGenerated = jsonObject.getString("mpin");
                            isChangePassword = jsonObject.getString("changePassword");

                            if (userTypeId.equalsIgnoreCase("6") || userTypeId.equalsIgnoreCase("5")) {

//                                if (isMpinGenerated.equalsIgnoreCase("false")) {
//                                    Intent intent = new Intent(LoginActivity.this, GenerateMpinActivity.class);
//                                    intent.putExtra("userId", userid);
//                                    intent.putExtra("userName", username);
//                                    intent.putExtra("panCard", pancard);
//                                    intent.putExtra("mobileNo", mobileno);
//                                    intent.putExtra("userType", usertype);
//                                    intent.putExtra("deviceId", deviceId);
//                                    intent.putExtra("deviceInfo", deviceInfo);
//                                    intent.putExtra("aadharCard", aadharCard);
//                                    intent.putExtra("loginUserName", loginUserName);
//                                    intent.putExtra("password", password);
//                                    intent.putExtra("emailId", emailId);
//                                    intent.putExtra("firmName", firmName);
//                                    intent.putExtra("address", address);
//                                    intent.putExtra("dob", dob);
//                                    intent.putExtra("city", city);
//                                    startActivity(intent);
//                                }
//                                else {
//                                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
//                                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                                    editor.putString("userid", userid);
//                                    editor.putString("username", username);
//                                    editor.putString("pancard", pancard);
//                                    editor.putString("mobileno", mobileno);
//                                    editor.putString("usertype", usertype);
//                                    editor.putString("deviceId", deviceId);
//                                    editor.putString("deviceInfo", deviceInfo);
//                                    editor.putString("adharcard", aadharCard);
//                                    editor.putString("loginUserName", loginUserName);
//                                    editor.putString("password", password);
//                                    editor.putString("email", emailId);
//                                    editor.putString("firmName", firmName);
//                                    editor.putString("address", address);
//                                    editor.putString("dob", dob);
//                                    editor.putString("city", city);
//                                    editor.apply();
//
//                                    Intent intent = new Intent(LoginActivity.this, MPINActivity.class);
//                                    startActivity(intent);
//                                    finish();
//                                }

                                if (loginUserName.equalsIgnoreCase("9643159919")) {

                                    // direct login for playStore demo id ::   8527365890
                                    generateMpin();

                                }

                                else {
                                    new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this).setTitle("Message")
                                            .setTitle("Alert !!!")
                                            .setMessage("Please check notification and Allow to Authenticate the User")
                                            .setIcon(R.drawable.warning)
                                            .setCancelable(false)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    showAuthCodeDialog();
                                                }
                                            }).show();
                                }

//                                new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this).setTitle("Message")
//                                        .setTitle("Alert !!!")
//                                        .setMessage("Please check notification and Allow to Authenticate the User")
//                                        .setIcon(R.drawable.warning)
//                                        .setCancelable(false)
//                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialogInterface, int i) {
//                                                showAuthCodeDialog();
//                                            }
//                                        }).show();

                                //                      checkAuthentication();

                                //     checkOtp();

                                pDialog.dismiss();
                            } else {
                                pDialog.dismiss();
                                new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this).setTitle("Message")
                                        .setMessage("Invalid ID Password\nPlease login with retailer ID.")
                                        .setPositiveButton("Ok", null).show();
                            }

                        } else {
                            pDialog.dismiss();
                            String data = jsonObject1.getString("data");

                            new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this).setTitle("Message")
                                    .setMessage(data)
                                    .setPositiveButton("Ok", null).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this)
                                .setTitle("Message")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Got it!!!", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Message")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Got it!!!", null)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this)
                        .setMessage(t.getMessage())
                        .setPositiveButton("Got it!!!", null)
                        .show();
            }
        });

    }


    private void checkOtp() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getOtp(AUTH_KEY, userid, deviceId, deviceInfo, emailId, mobileNo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            progressDialog.dismiss();
                            String otp = responseObject.getString("data");

                            Log.d("otp", "onResponse: " + otp);

                            showOtpDialog(otp);
                        } else if (responseCode.equalsIgnoreCase("NPA")) {
                            // genrateMpin();
                            progressDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this)
                                    .setMessage(responseObject.getString("data"))
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            progressDialog.dismiss();
                                        }
                                    })
                                    .show();
                        } else {
                            progressDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this)
                                    .setMessage("Something went wrong")
                                    .show();
                        }

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this)
                                .setMessage("Something went wrong")
                                .show();
                    }
                } else {
                    progressDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this)
                            .setMessage("Something went wrong")
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this)
                        .setMessage("Something went wrong")
                        .show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showOtpDialog(final String Otp) {
        View addSenderDialogView = getLayoutInflater().inflate(R.layout.add_sender_otp_dialog_layout, null, false);
        final androidx.appcompat.app.AlertDialog addSenderDialog = new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this).create();
        addSenderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addSenderDialog.setCancelable(false);
        addSenderDialog.setView(addSenderDialogView);
        addSenderDialog.setCancelable(false);
        addSenderDialog.show();

        ImageView imgClose = addSenderDialogView.findViewById(R.id.img_close);
        TextView tvTitle = addSenderDialogView.findViewById(R.id.text_user_registration);
        final EditText etOtp = addSenderDialogView.findViewById(R.id.et_otp);

        Button btnCancel = addSenderDialogView.findViewById(R.id.btn_cancel);
        Button btnSubmit = addSenderDialogView.findViewById(R.id.btn_submit);
        Button btnResendOtp = addSenderDialogView.findViewById(R.id.btn_resend_otp);

        btnResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOtp();
                addSenderDialog.dismiss();
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSenderDialog.dismiss();
            }
        });
        tvTitle.setText("OTP Verification");
        etOtp.setHint("OTP");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSenderDialog.dismiss();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etOtp.getText())) {
                    String userOtp = etOtp.getText().toString().trim();
                    if (userOtp.equals(Otp)) {

//                        if (isMpinGenerated) {
                        if (isMpinGenerated.equals("true")) {
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userid", userid);
                            editor.putString("username", username);
                            editor.putString("pancard", panCard);
                            editor.putString("mobileno", mobileNo);
                            editor.putString("usertype", usertype);
                            editor.putString("userTypeId", userTypeId);
                            editor.putString("deviceId", deviceId);
                            editor.putString("deviceInfo", deviceInfo);
                            editor.putString("adharcard", aadharCard);
                            editor.putString("loginUserName", loginUserName);
                            editor.putString("password", password);
                            editor.putString("email", emailId);
                            editor.putString("firmName", firmName);
                            editor.putString("address", address);
                            editor.putString("dob", dob);
                            editor.putString("city", city);
                            editor.putString("imageUrl", imageUrl);
                            editor.putBoolean("mpin", true);
                            editor.apply();

                            Intent intent = new Intent(LoginActivity.this, MPINActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(LoginActivity.this, GenerateMpinActivity.class);
                            intent.putExtra("userId", userid);
                            intent.putExtra("userName", username);
                            intent.putExtra("panCard", panCard);
                            intent.putExtra("mobileNo", mobileNo);
                            intent.putExtra("userType", usertype);
                            intent.putExtra("deviceId", deviceId);
                            intent.putExtra("deviceInfo", deviceInfo);
                            intent.putExtra("aadharCard", aadharCard);
                            intent.putExtra("loginUserName", loginUserName);
                            intent.putExtra("password", password);
                            intent.putExtra("emailId", emailId);
                            intent.putExtra("firmName", firmName);
                            intent.putExtra("address", address);
                            intent.putExtra("dob", dob);
                            intent.putExtra("city", city);
                            startActivity(intent);
                        }

                    } else {
                        new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this)
                                .setMessage("Wrong Otp")
                                .show();
                    }
                } else {
                    etOtp.setError("Required");
                }
            }
        });
    }

    private void forgetPassword() {
        View view1 = LayoutInflater.from(LoginActivity.this).inflate(R.layout.forget_password_dialog_layout, null, false);
        etForgetUsername = view1.findViewById(R.id.etForgetUsername);
        etForgetMobile = view1.findViewById(R.id.etForgetMobile);
        btnCancel = view1.findViewById(R.id.btnCancel);
        btnOk = view1.findViewById(R.id.btnOk);

        forgetPasswordDialog = new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this).create();
        forgetPasswordDialog.setCancelable(false);
        forgetPasswordDialog.setView(view1);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInputsForForgetPassword()) {
                    forgetUsername = etForgetUsername.getText().toString().trim();
                    forgetMobile = etForgetMobile.getText().toString().trim();

                    forgetPasswordCallback(forgetUsername, forgetMobile);

                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetPasswordDialog.dismiss();
            }
        });
        forgetPasswordDialog.show();

    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void forgetPasswordCallback(String forgetUsername, String forgetMobile) {
        ProgressDialog pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().forgetPassword(AUTH_KEY, forgetUsername, forgetMobile);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));

                        String responseMessage = jsonObject.getString("data");

                        new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this).
                                setTitle("Status")
                                .setMessage(responseMessage)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        forgetPasswordDialog.dismiss();
                                    }
                                }).show();
                        pDialog.dismiss();

                    } catch (JSONException e) {
                        pDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this).setMessage("Something went wrong!!!")
                                .setPositiveButton("Ok", null)
                                .show();
                        e.printStackTrace();
                    }
                } else {
                    pDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this).setMessage("Something went wrong!!!")
                            .setPositiveButton("Ok", null)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this).setMessage("Something went wrong!!!")
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });

    }

    private boolean checkInputsForForgetPassword() {
        if (!TextUtils.isEmpty(etForgetUsername.getText())) {
            if (!TextUtils.isEmpty(etForgetMobile.getText().toString())) {
                return true;
            } else {
                etForgetMobile.setError("Required");
                return false;
            }

        } else {
            etForgetUsername.setError("Required");
            return false;
        }

    }
    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(LoginActivity.this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }
    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.login_layout), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
    private void generateMpin() {
        if (isMpinGenerated.equalsIgnoreCase("false")) {
            Intent intent = new Intent(LoginActivity.this, GenerateMpinActivity.class);
            intent.putExtra("userId", userid);
            intent.putExtra("userName", username);
            intent.putExtra("panCard", panCard);
            intent.putExtra("mobileNo", mobileNo);
            intent.putExtra("userType", usertype);
            intent.putExtra("deviceId", deviceId);
            intent.putExtra("deviceInfo", deviceInfo);
            intent.putExtra("aadharCard", aadharCard);
            intent.putExtra("loginUserName", loginUserName);
            intent.putExtra("password", password);
            intent.putExtra("emailId", emailId);
            intent.putExtra("firmName", firmName);
            intent.putExtra("address", address);
            intent.putExtra("dob", dob);
            intent.putExtra("city", city);
            startActivity(intent);

        } else {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userid", userid);
            editor.putString("username", username);
            editor.putString("pancard", panCard);
            editor.putString("mobileno", mobileNo);
            editor.putString("usertype", usertype);
            editor.putString("usertypeId", userTypeId);
            editor.putString("deviceId", deviceId);
            editor.putString("deviceInfo", deviceInfo);
            editor.putString("adharcard", aadharCard);
            editor.putString("loginUserName", loginUserName);
            editor.putString("password", password);
            editor.putString("email", emailId);
            editor.putString("firmName", firmName);
            editor.putString("address", address);
            editor.putString("dob", dob);
            editor.putString("city", city);
            editor.apply();

            Intent intent = new Intent(LoginActivity.this, MPINActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @SuppressLint("SetTextI18n")
    private void showAuthCodeDialog() {
        View addSenderDialogView = getLayoutInflater().inflate(R.layout.add_sender_otp_dialog_layout, null, false);
        authCodeDialog = new AlertDialog.Builder(LoginActivity.this).create();
        authCodeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        authCodeDialog.setCancelable(false);
        authCodeDialog.setView(addSenderDialogView);
        authCodeDialog.setCancelable(false);
        authCodeDialog.show();

        ImageView imgClose = addSenderDialogView.findViewById(R.id.img_close);
        TextView tvTitle = addSenderDialogView.findViewById(R.id.text_user_registration);
        final EditText etOtp = addSenderDialogView.findViewById(R.id.et_otp);

        Button btnCancel = addSenderDialogView.findViewById(R.id.btn_cancel);
        Button btnSubmit = addSenderDialogView.findViewById(R.id.btn_submit);
        Button btnResendOtp = addSenderDialogView.findViewById(R.id.btn_resend_otp);
        btnResendOtp.setVisibility(View.GONE);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authCodeDialog.dismiss();
            }
        });
        tvTitle.setText("AuthCode Verification");
        etOtp.setHint("Authentication Code");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authCodeDialog.dismiss();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etOtp.getText())) {
                    String userOtp = etOtp.getText().toString().trim();
                    validateAuthCode(userOtp);
                } else {
                    etOtp.setError("Required");
                }
            }
        });
    }
    private void validateAuthCode(String authCode) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().validateAuthentication(AUTH_KEY, userid, deviceId, deviceInfo, authCode);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            progressDialog.dismiss();
                            authCodeDialog.dismiss();

                            //     checkOtp();
                            generateMpin();
                        } else if (responseCode.equalsIgnoreCase("ERR")) {
                            progressDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this)
                                    .setMessage(responseObject.getString("data"))
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    })
                                    .show();
                        } else {
                            progressDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this)
                                    .setMessage("Something went wrong")
                                    .show();
                        }

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this)
                                .setMessage("Something went wrong")
                                .show();
                    }
                } else {
                    progressDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this)
                            .setMessage("Something went wrong")
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this)
                        .setMessage("Something went wrong")
                        .show();
            }
        });
    }
}