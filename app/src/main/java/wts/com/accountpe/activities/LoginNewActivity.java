package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;
import androidx.multidex.BuildConfig;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.jaredrummler.android.device.DeviceName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
//import wts.com.accountpe.BuildConfig;
import wts.com.accountpe.R;
import wts.com.accountpe.SmsBroadcastReceiver;
import wts.com.accountpe.databinding.ActivityLoginNewBinding;
import wts.com.accountpe.retrofit.RetrofitClient;

public class LoginNewActivity extends AppCompatActivity {
    ActivityLoginNewBinding binding;
    String userid, username, usertype, panCard, aadharCard, firmName, address, dob, userTypeId, city, imageUrl, deviceId, deviceInfo, emailId, mobileNo,
            loginUserName, password, otp;
    String aadharNumber, email,mobileNumber;
    String versionCode;
    String isMpinGenerated;
    String isChangePassword;

   // boolean isMpinGenerated;
    AlertDialog authCodeDialog;

    String deviceName1;

    //////////////////////////////////////////////////////////////////////////FORGET PASSWORD
    EditText etForgetUsername, etForgetMobile;
    AppCompatButton btnCancel, btnOk;
    String forgetUsername, forgetMobile; //forget password dialog inputs
    androidx.appcompat.app.AlertDialog forgetPasswordDialog;

    //////////////////////////////////////////////////////////////////////////FORGET PASSWORD

    ////  for auto read sms

    private static final int REQ_USER_CONSENT = 200;
    SmsBroadcastReceiver smsBroadcastReceiver;
    String lat = "0.0", longi = "0.0";

    ///////////////////////////////////////
    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_new);

        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        startSmsUserConsent();  //  auto read sms

        versionCode = BuildConfig.VERSION_NAME;

        binding.etMobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (binding.etMobileNo.getText().toString().length() == 10) {
                    hideKeyBoard();
                    checkOtpForLogin();

                    //-----------------------------------------------------------------------

                    /*DeviceName.init(LoginNewActivity.this);
                    DeviceName.with(LoginNewActivity.this).request((info, error) -> {
                        String manufacturer = info.manufacturer;
                        String name = info.marketName;
                        String model = info.model;
                        String codename = info.codename;
                        String deviceName = info.getName();
                        deviceInfo = "Manufacturer-" + manufacturer + " Name-" + name + " Model-" + model + " Codename-" + codename + " DeviceName-" + deviceName + " DeviceId-" + deviceId;
                          loginUser();

                        String strOtp = binding.etOtp.getText().toString();
                        */
                    /*if (strOtp.equalsIgnoreCase(otp)) {
                            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    if (task.isSuccessful()) {
                                        deviceId = task.getResult();

                                        loginUser();

                                    } else {
                                        Toast.makeText(LoginNewActivity.this, "token not found ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else {
                            new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this).setMessage("Invalid OTP").show();
                        }*/
                    /*

                    });*/
                    //------------------------------------------------------------------------
                   // loginUser();

                }
            }
        });

        binding.tvForgetPassword.setOnClickListener(view ->
        {

            forgetPassword();
        });

        binding.btnRegister.setOnClickListener(v ->
        {
            startActivity(new Intent(LoginNewActivity.this, SignUpActivity.class));
        });

        binding.etOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (binding.etOtp.getText().toString().length() == 6) {
                    DeviceName.init(LoginNewActivity.this);
                    DeviceName.with(LoginNewActivity.this).request((info, error) -> {
                        String manufacturer = info.manufacturer;
                        String name = info.marketName;
                        String model = info.model;
                        String codename = info.codename;
                        String deviceName = info.getName();
                        deviceInfo = "Manufacturer-" + manufacturer + " Name-" + name + " Model-" + model + " Codename-" + codename + " DeviceName-" + deviceName + " DeviceId-" + deviceId;
                          //loginUser();

                        String strOtp = binding.etOtp.getText().toString();
                        if (strOtp.equalsIgnoreCase(otp)) {
                            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    if (task.isSuccessful()) {
                                        deviceId = task.getResult();

                                        loginUser();

                                    } else {
                                        Toast.makeText(LoginNewActivity.this, "token not found ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this).setMessage("Invalid OTP").show();
                        }

                    });
                }
            }
        });

        binding.btnLogin.setOnClickListener(view ->
                {
                    if (!TextUtils.isEmpty(binding.etMobileNo.getText().toString()) &&
                            !TextUtils.isEmpty(binding.etOtp.getText().toString())) {
                        DeviceName.init(LoginNewActivity.this);
                        DeviceName.with(LoginNewActivity.this).request((info, error) -> {
                            String manufacturer = info.manufacturer;
                            String name = info.marketName;
                            String model = info.model;
                            String codename = info.codename;
                            String deviceName = info.getName();
                            deviceInfo = "Manufacturer-" + manufacturer + " Name-" + name + " Model-" + model + " Codename-" + codename + " DeviceName-" + deviceName + " DeviceId-" + deviceId;
                            deviceName1 = name + " " + model;
                            //  loginUser();

                            String strOtp = binding.etOtp.getText().toString();
                            if (strOtp.equalsIgnoreCase(otp)) {
                                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                                    @Override
                                    public void onComplete(@NonNull Task<String> task) {
                                        if (task.isSuccessful()) {
                                            deviceId = task.getResult();

                                            loginUser();

                                        } else {
                                            Toast.makeText(LoginNewActivity.this, "token not found ", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this).setMessage("Invalid OTP").show();
                            }

                        });

                    } else {
                        showSnackBar("All fields are mandatory.");
                    }
                }
        );

    }

/*
    private void loginUser() {
        final AlertDialog pDialog = new AlertDialog.Builder(LoginNewActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        loginUserName = binding.etMobileNo.getText().toString().trim();
        password = binding.etMobileNo.getText().toString().trim();                // username and password same for this process

//        Call<JsonObject> call = RetrofitClient.getInstance().getApi().login(AUTH_KEY, loginUserName, password, deviceId, deviceInfo, "1.1.2");
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().login(AUTH_KEY, loginUserName, password, deviceId, deviceInfo, "1.2.2", lat, longi, deviceName1);

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
                            imageUrl = imageUrl.replace("\\", "");
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

                            if (isMpinGenerated) {
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginNewActivity.this);
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
                                editor.putString("imgUrl", imageUrl);
                                editor.putBoolean("mpin", true);
                                editor.apply();

                                Intent intent = new Intent(LoginNewActivity.this, MPINActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(LoginNewActivity.this, GenerateMpinActivity.class);
                                intent.putExtra("userId", userid);
                                intent.putExtra("userName", username);
                                intent.putExtra("panCard", panCard);
                                intent.putExtra("mobileNo", mobileNo);
                                intent.putExtra("userType", usertype);
                                intent.putExtra("userTypeId", userTypeId);
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
                                intent.putExtra("imgUrl", imageUrl);
                                startActivity(intent);
                            }

                            //      checkOtp();

                        } else {
                            pDialog.dismiss();
                            String data = jsonObject1.getString("data");

                            new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this).setTitle("Message")
                                    .setMessage(data)
                                    .setPositiveButton("Ok", null).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this)
                                .setTitle("Message")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Got it!!!", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this)
                            .setTitle("Message")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Got it!!!", null)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this)
                        .setMessage(t.getMessage())
                        .setPositiveButton("Got it!!!", null)
                        .show();
            }
        });

    }
*/
private void loginUser() {
    final AlertDialog pDialog = new AlertDialog.Builder(LoginNewActivity.this).create();
    LayoutInflater inflater = getLayoutInflater();
    View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
    pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    pDialog.setView(convertView);
    pDialog.setCancelable(false);
    pDialog.show();



    loginUserName = binding.etMobileNo.getText().toString().trim(); //etUsername.getText().toString().trim();
    password = binding.etMobileNo.getText().toString().trim();  //etPassword.getText().toString().trim();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().login(AUTH_KEY, loginUserName, password, deviceId, deviceInfo, "1.1.2", lat, longi, deviceName1);
    call.enqueue(new Callback<JsonObject>() {
        @Override
        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
            if (response.isSuccessful()) {
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

                        aadharNumber = jsonObject.getString("aadharNumber"); //***mber
                        email = jsonObject.getString("email"); //***mail
                        mobileNumber = jsonObject.getString("mobileNumber");//***ber

                        userTypeId = jsonObject.getString("userTypeId");


                        Log.d("loginData","log::"+aadharNumber +"\n email: "+email +"\n mobioleNumber:"+mobileNumber+"\n panCard : "+panCard  + "\n userTypeId : "+ userTypeId +"\nuserid::"+userid);

                        emailId = jsonObject.getString("emailID");
                        firmName = jsonObject.getString("companyName");
                        address = jsonObject.getString("address");
                        dob = jsonObject.getString("dob");
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

                            if (loginUserName.equalsIgnoreCase("9555869918")) {

                                /// direct login for playStore demo id   ::  8527365890
                                generateMpin();
                            }

                            else {
                                new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this).setTitle("Message")
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
                            pDialog.dismiss();
                        } else {
                            pDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this).setTitle("Message")
                                    .setMessage("Invalid ID Password\nPlease login with retailer ID.")
                                    .setPositiveButton("Ok", null).show();
                        }

                    } else {
                        pDialog.dismiss();
                        /*String data = jsonObject1.getString("data");

                        new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this).setTitle("Message")
                                .setMessage(data)
                                .setPositiveButton("Ok", null).show();*/

                        String data = jsonObject1.getString("data");
                        Log.d("LoginActivity","data:::\n "+data);
                        if ( statusCode.equalsIgnoreCase("ERR") || data.equalsIgnoreCase("Please download authenticator app..!") ) {

                            new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this)
                                    .setTitle("Message")
                                    .setMessage("Please download authenticator app..!")
                                    .setPositiveButton("Ok", (dialog, which) -> {

                                        String url = "https://play.google.com/store/apps/details?id=com.wts.AccountPEAuthenticator&pcampaignid=web_share";
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse(url));
                                        startActivity(intent);
                                    })
                                    .show();
                        } else {
                            // Handle other cases
                            new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this)
                                    .setTitle("Message")
                                    .setMessage(data)
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    pDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this)
                            .setTitle("Message")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Got it!!!", null)
                            .show();
                }
            } else {
                pDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this)
                        .setTitle("Message")
                        .setMessage("Something went wrong.")
                        .setPositiveButton("Got it!!!", null)
                        .show();
            }
        }
        @SuppressLint("SetTextI18n")
        private void showAuthCodeDialog() {
            View addSenderDialogView = getLayoutInflater().inflate(R.layout.add_sender_otp_dialog_layout, null, false);
            authCodeDialog = new AlertDialog.Builder(LoginNewActivity.this).create();
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
/*
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
*/

            //-----------------------


            etOtp.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (s.length() == 6) {
                        String userOtp = s.toString().trim();
                        validateAuthCode(userOtp);
                    }
                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            });



            //-----------------------


        }

        @Override
        public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
            pDialog.dismiss();
            new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this)
                    .setMessage(t.getMessage())
                    .setPositiveButton("Got it!!!", null)
                    .show();
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
                            new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this)
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
                            new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this)
                                    .setMessage("Something went wrong")
                                    .show();
                        }

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this)
                                .setMessage("Something went wrong")
                                .show();
                    }
                } else {
                    progressDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this)
                            .setMessage("Something went wrong")
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this)
                        .setMessage("Something went wrong")
                        .show();
            }
        });
    }
    private void generateMpin() {
        if (isMpinGenerated.equalsIgnoreCase("false")) {
            Intent intent = new Intent(LoginNewActivity.this, GenerateMpinActivity.class);
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

            //----------------------------------------------------
            intent.putExtra("aadharNumber",aadharNumber);//***mber
            intent.putExtra("email",email);//***mail
            intent.putExtra("mobileNumber",mobileNumber);//***ber
            //------------------------------------------------------

            intent.putExtra("city", city);
            startActivity(intent);

        } else {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginNewActivity.this);
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
            editor.putString("emailId", emailId);
            editor.putString("firmName", firmName);
            editor.putString("address", address);
            editor.putString("dob", dob);
            //----------------------------------------------------
            editor.putString("aadharNumber",aadharNumber);//***mber
            editor.putString("email",email);//***mail
            editor.putString("mobileNumber",mobileNumber);//***ber
            //-----------------------------------------------------
            editor.putString("city", city);
            editor.apply();

            Intent intent = new Intent(LoginNewActivity.this, MPINActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void checkOtpForLogin() {

        final android.app.AlertDialog progressDialog = new android.app.AlertDialog.Builder(LoginNewActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setView(convertView);
        progressDialog.setCancelable(false);
        progressDialog.show();

        String strMobileNo = binding.etMobileNo.getText().toString();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().loginWithOtp(AUTH_KEY, strMobileNo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            progressDialog.dismiss();
                            otp = responseObject.getString("status");
                            Log.d("otp", "onResponse: " + otp);

                            binding.otpContainer.setVisibility(View.VISIBLE);

                        }
                        else if (responseCode.equalsIgnoreCase("MNF")) {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(LoginNewActivity.this)
                                    .setTitle("Alert")
                                    .setMessage("This Number not Registered\nPlease SignUP")
                                    .setIcon(R.drawable.failureicon)
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent in = new Intent(LoginNewActivity.this, SignUpActivity.class);
                                            in.putExtra("mobileNo", strMobileNo);
                                            in.putExtra("title", "SignUp");
                                            startActivity(in);
                                            finish();
                                        }
                                    })
                                    .show();

                        }
                        else if (responseCode.equalsIgnoreCase("ERR")) {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(LoginNewActivity.this)
                                    .setMessage(responseObject.getString("data"))
                                    .show();
                        }
                        else {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(LoginNewActivity.this)
                                    .setMessage("Something went wrong")
                                    .show();
                           // Toast.makeText(LoginNewActivity.this, "this is main reson", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(LoginNewActivity.this)
                                .setMessage("Something went wrong")
                                .show();
                    //    Toast.makeText(LoginNewActivity.this, "this is second resion ", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(LoginNewActivity.this)
                            .setMessage("Something went wrong")
                            .show();
                   // Toast.makeText(LoginNewActivity.this, "this is third reason", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(LoginNewActivity.this)
                        .setMessage(t.getMessage())
                        .show();
                Toast.makeText(LoginNewActivity.this, "this is failer message !!!", Toast.LENGTH_SHORT).show();
            }
            /*@Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Log.e("RetrofitError", "onFailure: ", t); // Logs the specific error
                Toast.makeText(LoginNewActivity.this, "Request failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }*/
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
                            new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this)
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
                            new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this)
                                    .setMessage("Something went wrong")
                                    .show();
                        }

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this)
                                .setMessage("Something went wrong")
                                .show();
                    }
                } else {
                    progressDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this)
                            .setMessage("Something went wrong")
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this)
                        .setMessage("Something went wrong")
                        .show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showOtpDialog(final String Otp) {
        View addSenderDialogView = getLayoutInflater().inflate(R.layout.add_sender_otp_dialog_layout, null, false);
        final androidx.appcompat.app.AlertDialog addSenderDialog = new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this).create();
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

                        if (isMpinGenerated.equals("true")) {
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginNewActivity.this);
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
                            //----------------------------------------------------
                            editor.putString("aadharNumber",aadharNumber);//***mber
                            editor.putString("email",email);//***mail
                            editor.putString("mobileNumber",mobileNumber);//***ber
                            //------------------------------------------------------


                            editor.putString("imageUrl", imageUrl);
                            editor.putBoolean("mpin", true);
                            editor.apply();

                            Intent intent = new Intent(LoginNewActivity.this, MPINActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(LoginNewActivity.this, GenerateMpinActivity.class);
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
                            //------------------------------------------------
                            intent.putExtra("aadharNumber",aadharNumber);//***mber
                            intent.putExtra("email",email);//***mail
                            intent.putExtra("mobileNumber",mobileNumber);//***ber

                            //-------------------------------------------------
                            intent.putExtra("city", city);
                            startActivity(intent);
                        }

                    } else {
                        new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this)
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
        View view1 = LayoutInflater.from(LoginNewActivity.this).inflate(R.layout.forget_password_dialog_layout, null, false);
        etForgetUsername = view1.findViewById(R.id.etForgetUsername);
        etForgetMobile = view1.findViewById(R.id.etForgetMobile);
        btnCancel = view1.findViewById(R.id.btnCancel);
        btnOk = view1.findViewById(R.id.btnOk);

        forgetPasswordDialog = new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this).create();
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

    private void forgetPasswordCallback(String forgetUsername, String forgetMobile) {
        ProgressDialog pDialog = new ProgressDialog(LoginNewActivity.this);
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

                        new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this).
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
                        new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this).setMessage("Something went wrong!!!")
                                .setPositiveButton("Ok", null)
                                .show();
                        e.printStackTrace();
                    }
                } else {
                    pDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this).setMessage("Something went wrong!!!")
                            .setPositiveButton("Ok", null)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(LoginNewActivity.this).setMessage("Something went wrong!!!")
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

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.login_layout), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void hideKeyBoard() {
        View view1 = this.getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }
    }

    ///////////////  auto read sms

    private void startSmsUserConsent() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        //We can add sender phone number or leave it blank
        // I'm adding null here
        client.startSmsUserConsent(null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //  Toast.makeText(getApplicationContext(), "On Success", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //   Toast.makeText(getApplicationContext(), "On OnFailure", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_USER_CONSENT) {
            if ((resultCode == RESULT_OK) && (data != null)) {
                //That gives all message to us.
                // We need to get the code from inside with regex
                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                getOtpFromMessage(message);
            }
        }
    }

    private void getOtpFromMessage(String message) {
        // This will match any 6 digit number in the message
        Pattern pattern = Pattern.compile("(|^)\\d{6}");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            binding.etOtp.setText(matcher.group(0));
        }
    }

    private void registerBroadcastReceiver() {
        smsBroadcastReceiver = new SmsBroadcastReceiver();
        smsBroadcastReceiver.smsBroadcastReceiverListener = new SmsBroadcastReceiver.SmsBroadcastReceiverListener() {
            @Override
            public void onSuccess(Intent intent) {
                startActivityForResult(intent, REQ_USER_CONSENT);
            }

            @Override
            public void onFailure() {
            }
        };
        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(smsBroadcastReceiver, intentFilter,RECEIVER_EXPORTED);
        }
        else {
            registerReceiver(smsBroadcastReceiver, intentFilter);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBroadcastReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(smsBroadcastReceiver);
    }
    /////////////////////////////////////////
}