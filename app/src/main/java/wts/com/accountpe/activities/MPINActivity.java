package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.chaos.view.PinView;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.MainActivity;
import wts.com.accountpe.R;
import wts.com.accountpe.retrofit.RetrofitClient;


public class MPINActivity extends AppCompatActivity {
    ImageView imgFinger;
    TextView tvUserName;
    PinView mpinPinView;
    Button btnSubmit, btnEasyPin, btnForgetMpin, btnPassword;
    SharedPreferences sharedPreferences;
    String userid, username, usertype, usertypeId, mobileno, pancard, aadharCard, emailId, loginUserName, password, firmName, address, dob, city, imageUrl;
    String deviceId, deviceInfo;
    AlertDialog pDialog;
    //for lockscreen
    private final int LOCK_REQUEST_CODE = 1;
    private int count = 0;
///////////////////////////


    //////////////////////////////////////////////////////////////////////////FORGET PASSWORD
    EditText etForgetUsername, etForgetMobile;
    AppCompatButton btnCancel, btnOk;
    String forgetUsername, forgetMobile;
    androidx.appcompat.app.AlertDialog forgetPasswordDialog;
    //////////////////////////////////////////////////////////////////////////FORGET PASSWORD

    // for biometric
    androidx.biometric.BiometricPrompt biometricPrompt;
    androidx.biometric.BiometricPrompt.PromptInfo promptInfo;
////////////////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpin);
        initViews();

        //lockScreen();

        imgFinger.setOnClickListener(view ->
        {
         //   lockScreen();
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MPINActivity.this);
        userid = sharedPreferences.getString("userid", null);
        username = sharedPreferences.getString("username", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        loginUserName = sharedPreferences.getString("loginUserName", null);
        password = sharedPreferences.getString("password", null);
        usertype = sharedPreferences.getString("usertype", null);
        usertypeId = sharedPreferences.getString("usertypeId", null);
        mobileno = sharedPreferences.getString("mobileno", null);
        pancard = sharedPreferences.getString("pancard", null);
        aadharCard = sharedPreferences.getString("adharcard", null);
        emailId = sharedPreferences.getString("email", null);
        firmName = sharedPreferences.getString("firmName", null);
        address = sharedPreferences.getString("address", null);
        dob = sharedPreferences.getString("dob", null);
        city = sharedPreferences.getString("city", null);
        imageUrl = sharedPreferences.getString("imgUrl", null);

        tvUserName.setText(username);

        mpinPinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 6) {

                    String mpin = mpinPinView.getText().toString().trim();
                    checkMpin(mpin);
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mpin = mpinPinView.getText().toString().trim();
                if (mpin.length() == 6) {
                    checkMpin(mpin);
                } else {
                    mpinPinView.setError("Required");
                }
            }
        });

        btnForgetMpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MPINActivity.this, GenerateMpinActivity.class);
                intent.putExtra("userId", userid);
                intent.putExtra("userName", username);
                intent.putExtra("panCard", pancard);
                intent.putExtra("mobileNo", mobileno);
                intent.putExtra("userType", usertype);
                intent.putExtra("userTypeId", usertypeId);
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
                intent.putExtra("mpinActivity", "mpinActivity");
                startActivity(intent);
            }
        });

        btnPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MPINActivity.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                //   startActivity(new Intent(MPINActivity.this, LoginActivity.class));
                startActivity(new Intent(MPINActivity.this, LoginNewActivity.class));
                finish();
            }
        });

        if (checkInternetState()) {
            checkCredentials();
        } else {
            new AlertDialog.Builder(MPINActivity.this).setMessage("Internet Connection Required").show();
        }

    }

    private void checkCredentials() {
         pDialog = new AlertDialog.Builder(MPINActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().checkCredential(AUTH_KEY, loginUserName, password, deviceInfo, "1.1.2");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {

                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            pDialog.dismiss();
                            JSONObject dataObject = responseObject.getJSONObject("data");
                            String userId = dataObject.getString("userID");
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MPINActivity.this);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userid", userId);
                            editor.apply();

                        } else {
                            pDialog.dismiss();
                            String message = responseObject.getString("data");
                            new androidx.appcompat.app.AlertDialog.Builder(MPINActivity.this)
                                    .setCancelable(false)
                                    .setMessage(message + "\nYou have to login again")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MPINActivity.this).edit();
                                            editor.clear();
                                            editor.apply();
                                            //  startActivity(new Intent(MPINActivity.this, LoginActivity.class));
                                            startActivity(new Intent(MPINActivity.this, LoginNewActivity.class));
                                            finish();
                                        }
                                    }).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(MPINActivity.this)
                                .setCancelable(false)
                                .setMessage("You have to login again.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MPINActivity.this).edit();
                                        editor.clear();
                                        editor.apply();
                                        //  startActivity(new Intent(MPINActivity.this, LoginActivity.class));
                                        startActivity(new Intent(MPINActivity.this, LoginNewActivity.class));
                                        finish();
                                    }
                                }).show();
                    }

                } else {
                    pDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(MPINActivity.this)
                            .setCancelable(false)
                            .setMessage("You have to login again.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MPINActivity.this).edit();
                                    editor.clear();
                                    editor.apply();
                                    //   startActivity(new Intent(MPINActivity.this, LoginActivity.class));
                                    startActivity(new Intent(MPINActivity.this, LoginNewActivity.class));
                                    finish();
                                }
                            }).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(MPINActivity.this)
                        .setCancelable(false)
                        .setMessage("You have to login again.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MPINActivity.this).edit();
                                editor.clear();
                                editor.apply();
                                //   startActivity(new Intent(MPINActivity.this, LoginActivity.class));
                                startActivity(new Intent(MPINActivity.this, LoginNewActivity.class));
                                finish();
                            }
                        }).show();
            }
        });
    }

    private void checkMpin(String mpin) {
        final AlertDialog pDialog = new AlertDialog.Builder(MPINActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().checkMpinOrTPIN(AUTH_KEY, userid, deviceId, deviceInfo, "mpin", mpin);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        Log.d("changeMPIN"," response: "+ response.body() );
                        String responseCode = responseObject.getString("statuscode");
                        if (responseCode.equalsIgnoreCase("TXN")) {


                            pDialog.dismiss();

                            startActivity(new Intent(MPINActivity.this, HomeDashActivity.class));
                            finish();

                        } else if (responseCode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();
                            String transaction = responseObject.getString("status");
                            new androidx.appcompat.app.AlertDialog.Builder(MPINActivity.this)
                                    .setMessage(transaction)
                                    .setPositiveButton("ok", null)
                                    .show();
                        } else {
                            pDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(MPINActivity.this)
                                    .setMessage("You have entered wrong mpin.")
                                    .setPositiveButton("ok", null)
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(MPINActivity.this)
                                .setMessage("Something went wrong.")
                                .setPositiveButton("ok", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(MPINActivity.this)
                            .setMessage("Something went wrong.")
                            .setPositiveButton("ok", null)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(MPINActivity.this)
                        .setMessage(t.getMessage())
                        .setPositiveButton("ok", null)
                        .show();
            }
        });

    }


    private void lockScreen() {
//        try {
//            KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
//
//            Intent i = km.createConfirmDeviceCredentialIntent("Please verify", "Use your phone lock to use services");
//            startActivityForResult(i, LOCK_REQUEST_CODE);
//
//        }
//        catch (Exception e)
//        {
//            Toast.makeText(MPINActivity.this, "Please Add Phone lock for secure transaction", Toast.LENGTH_SHORT).show();
//        }


        BiometricManager biometricManager = BiometricManager.from(this);

        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "Device does not have fingerPrint", Toast.LENGTH_SHORT).show();
                break;

            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "Not working", Toast.LENGTH_SHORT).show();
                break;

            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "No finger print assigned", Toast.LENGTH_SHORT).show();
        }

        Executor executor = ContextCompat.getMainExecutor(this);


        biometricPrompt = new androidx.biometric.BiometricPrompt(MPINActivity.this, executor, new androidx.biometric.BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                //  Toast.makeText(MPINActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull androidx.biometric.BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                startActivity(new Intent(MPINActivity.this, HomeDashActivity.class));
                finish();

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });


        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Finger Print")
                .setDescription("Use fingerprint to login")
                .setNegativeButtonText("Use MPIN")
                .setConfirmationRequired(false)
                .build();


        biometricPrompt.authenticate(promptInfo);

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode==LOCK_REQUEST_CODE)
//        {
//            if (resultCode == RESULT_OK) {
//                //Toast.makeText(this, "Chaa Gya Ladke", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(MPINActivity.this, HomeDashActivity.class));
//                finish();
//            } else {
//                count++;
//                if (count < 3) {
//                    Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
//                    KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
//                    Intent i = km.createConfirmDeviceCredentialIntent("Please verify", "Use your phone lock to use services");
//                    startActivityForResult(i, LOCK_REQUEST_CODE);
//                } else {
//                    //Toast.makeText(this, "Tere Baski na h", Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//            }
//        }
//    }

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

    private void initViews() {
        imgFinger = findViewById(R.id.imgAeps);
        tvUserName = findViewById(R.id.tv_user_name);
        mpinPinView = findViewById(R.id.otp_pin_view);
        btnSubmit = findViewById(R.id.btn_submit);
        btnEasyPin = findViewById(R.id.btn_easy_pin);
        btnForgetMpin = findViewById(R.id.btn_forget_pin);
        btnPassword = findViewById(R.id.btn_password);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }

    }
   /* @Override
    protected void onResume() {
        super.onResume();
        checkCredentials();
    }*/

}