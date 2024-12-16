package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.retrofit.RetrofitClient;


public class ChangeMpinActivity extends AppCompatActivity {

   /* Button btnChangeMpin;
    EditText  etNewMpin, etConfirmMpin,etenterEmailId;
    String newMpin,MobileNo,EmailId,AdharNO;

    SharedPreferences sharedPreferences;
    String userid,emailId,mobileno;
    String deviceId, deviceInfo;
    String pinType;*/
    //_________________________________________
   EditText etMpin, etConfirmMpin;
    Button btnSubmit;
    String userid, username, usertype,userTypeId, mobileno, pancard, aadharCard, emailId, loginUserName, password,firmName,address,dob,city,imageUrl, fromActivity;
    SharedPreferences sharedPreferences;
    String deviceId, deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_mpin);

        initViews();

       /* sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ChangeMpinActivity.this);
        userid = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
//        emailId = sharedPreferences.getString("email", null);//**
//        emailId = sharedPreferences.getString("emailId", null); //
//        mobileno = sharedPreferences.getString("mobileno", null);

        pinType=getIntent().getStringExtra("pinType");

        btnChangeMpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (

              //  checkInternetState()
                checkInputs()
                ) {

                    if (checkInternetState()) {
                        //changeMpin();
                        checkOtp();

                    } else {
                        showSnackBar();
                    }
                }

            }
        });

        //-----------------------------------------------------

    }

    private void checkOtp() {
        newMpin = etNewMpin.getText().toString().trim();
        AdharNO = etConfirmMpin.getText().toString().trim();
        EmailId = etenterEmailId.getText().toString().trim();
        MobileNo = etNewMpin.getText().toString().trim();

        if (newMpin.isEmpty() || AdharNO.isEmpty() || EmailId.isEmpty() || MobileNo.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
       // Log.d("InputValues", "NewMpin: " + newMpin + ", AdharNO: " + AdharNO + ", EmailId: " + EmailId + ", MobileNo: " + MobileNo);


        final android.app.AlertDialog progressDialog = new android.app.AlertDialog.Builder(ChangeMpinActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setView(convertView);
        progressDialog.setCancelable(false);
        progressDialog.show();


        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getOtp(
                AUTH_KEY, userid, deviceId, deviceInfo
        //       emailId,
        //        mobileno
                ,EmailId,
                MobileNo
        );
        Log.e("ChangeTpin2","\n aadharnumber ::"+AdharNO+"\nEmailId"+EmailId+"\n MobileNo"+MobileNo);

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

//                            Toast.makeText(ChangeMpinActivity.this, otp, Toast.LENGTH_SHORT).show();
//                            Toast.makeText(ChangeMpinActivity.this, otp, Toast.LENGTH_SHORT).show();
//                            Toast.makeText(ChangeMpinActivity.this, otp, Toast.LENGTH_SHORT).show();
//                            Toast.makeText(ChangeMpinActivity.this, otp, Toast.LENGTH_SHORT).show();
//                            Toast.makeText(ChangeMpinActivity.this, otp, Toast.LENGTH_SHORT).show();
//                            Toast.makeText(ChangeMpinActivity.this, otp, Toast.LENGTH_SHORT).show();

                            showOtpDialog(otp);
                        }

                        else if (responseCode.equalsIgnoreCase("NPA"))
                        {
                          //  changeMpin();
                            progressDialog.dismiss();
                            new AlertDialog.Builder(ChangeMpinActivity.this)
                                    .setMessage(responseObject.getString("data"))
                                    .show();
                        }

                        else {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(ChangeMpinActivity.this)
                                    .setMessage("Something went wrong dilogbox ERR")
                                    .show();
                        }

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(ChangeMpinActivity.this)
                                .setMessage("Something went wrong dilogbox ")
                                .show();
                    }
                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(ChangeMpinActivity.this)
                            .setMessage("Something went wrong unSuccessful")
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(ChangeMpinActivity.this)
                        .setMessage("Something went wrong dilogbox onFailure")
                        .show();
            }
        });
    }

    //--------------------------------------------------------------------------------------------


    //---------------------------------------------------------------------------------------------

    @SuppressLint("SetTextI18n")
    private void showOtpDialog(final String Otp) {
        View addSenderDialogView = getLayoutInflater().inflate(R.layout.add_sender_otp_dialog_layout, null, false);
        final AlertDialog addSenderDialog = new AlertDialog.Builder(ChangeMpinActivity.this).create();
        addSenderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addSenderDialog.setCancelable(false);
        addSenderDialog.setView(addSenderDialogView);
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
                        //changeMpin(); //old process
                        changeMpins();


                    } else {
                        new AlertDialog.Builder(ChangeMpinActivity.this)
                                .setMessage("Wrong Otp")
                                .show();
                    }
                } else {
                    etOtp.setError("Required");
                }
            }
        });
    }

    private void changeMpin() {

        final android.app.AlertDialog progressDialog = new android.app.AlertDialog.Builder(ChangeMpinActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setView(convertView);
        progressDialog.setCancelable(false);
        progressDialog.show();

        newMpin = etNewMpin.getText().toString().trim();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().generateMpin(AUTH_KEY, userid, deviceId, deviceInfo, pinType, newMpin);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = jsonObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            progressDialog.dismiss();


                            String dialogTitle = jsonObject.getString("status");
                            String dialogMessage = jsonObject.getString("data");
                            new AlertDialog.Builder(ChangeMpinActivity.this).setTitle(dialogTitle)
                                    .setMessage(dialogMessage)
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    finish();
                                }
                            }).show();


                        } else if (responseCode.equalsIgnoreCase("ERR")) {
                            progressDialog.dismiss();
                            String dialogTitle = jsonObject.getString("status");
                            String dialogMessage = jsonObject.getString("data");

                            new AlertDialog.Builder(ChangeMpinActivity.this).setTitle(dialogTitle)
                                    .setMessage(dialogMessage)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).show();

                        } else {
                            progressDialog.dismiss();

                            new AlertDialog.Builder(ChangeMpinActivity.this).setTitle("Alert")
                                    .setMessage("Something went wrong")
                                    .setPositiveButton("Ok", null).show();
                        }

                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(ChangeMpinActivity.this).setTitle("Alert")
                            .setMessage("Something went wrong")
                            .setPositiveButton("Ok", null).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(ChangeMpinActivity.this).setTitle("Alert")
                        .setMessage("Something went wrong")
                        .setPositiveButton("Ok", null).show();
            }
        });


    }

    //------------------------------------------------------------------------
    private void changeMpins() {

        newMpin = etNewMpin.getText().toString().trim();
        AdharNO = etConfirmMpin.getText().toString().trim();
        EmailId = etenterEmailId.getText().toString().trim();
        MobileNo = etNewMpin.getText().toString().trim();

        if (newMpin.isEmpty() || AdharNO.isEmpty() || EmailId.isEmpty() || MobileNo.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
//        Log.d("InputValues", "NewMpin: " + newMpin + ", AdharNO: " + AdharNO + ", EmailId: " + EmailId + ", MobileNo: " + MobileNo);


        final android.app.AlertDialog progressDialog = new android.app.AlertDialog.Builder(ChangeMpinActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setView(convertView);
        progressDialog.setCancelable(false);
        progressDialog.show();


        Call<JsonObject> call = RetrofitClient.getInstance().getApi()
                .ChangeMPINPassword(AUTH_KEY, userid, deviceId, deviceInfo, "1.1.2", AdharNO, EmailId, MobileNo);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                progressDialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    JsonObject jsonObject = response.body();
                    Log.d("jsonobject::","ChangeMPINPassword"+jsonObject);

                    String responseCode = jsonObject.get("statuscode").getAsString();
                    String dialogTitle = jsonObject.get("status").getAsString();
                    String dialogMessage = jsonObject.get("data").getAsString();


                    if (responseCode.equalsIgnoreCase("TXN")) {
                        new AlertDialog.Builder(ChangeMpinActivity.this)
                                .setTitle(dialogTitle)
                                .setMessage(dialogMessage)
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish(); // Close the activity after user confirmation
                                    }
                                })
                                .show();
                    }

                    else if (responseCode.equalsIgnoreCase("ERR")) {
                        new AlertDialog.Builder(ChangeMpinActivity.this)
                                .setTitle(dialogTitle)
                                .setMessage(dialogMessage)
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                    else {
                        showErrorDialog("Alert", "Something went wrong1");
                    }
                } else {
                    showErrorDialog("Alert", "Something went wrong2");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                showErrorDialog("Alert", "Something went wrong3");
            }
        });
    }
    // Helper method to show error dialogs
    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(ChangeMpinActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", null)
                .show();
    }
    //---------------------------------------------------------------------------
    private void initViews() {
        etNewMpin = findViewById(R.id.et_new_mpin); // mobile

        etConfirmMpin = findViewById(R.id.et_confirm_mpin); //aadhar
        btnChangeMpin = findViewById(R.id.btn_change_mpin);
        etenterEmailId = findViewById(R.id.et_enteryourEmailId);
    }

  */
        /*  private boolean checkInputs() {
        if (etNewMpin.getText().toString().trim().length()==6) {

            if (!TextUtils.isEmpty(etConfirmMpin.getText())) {
                if (etNewMpin.getText().toString().trim().equals(etConfirmMpin.getText().toString().trim())) {
                    return true;
                } else {
                    Toast.makeText(this, "PIN and Confirm PIN must be same", Toast.LENGTH_LONG).show();
                    return false;
                }
            } else {
                etConfirmMpin.setError("Required");
                return false;
            }

        } else {
            etNewMpin.setError("PIN must be of 6 digit");
            return false;
        }

    }  */
        /*
   private boolean checkInputs() {

       if (etNewMpin.getText().toString().trim().length() == 10) { //mobile ~ etNewMpin

           String email = etenterEmailId.getText().toString().trim();
           if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
               etenterEmailId.setError("Please enter a valid Email ID");
               return false;
           }

           String confirmMpin = etConfirmMpin.getText().toString().trim(); //etConfirmMpin == aadhar
           if (confirmMpin.length() != 12) {
               etConfirmMpin.setError("Required and must be 12 characters");//etConfirmMpin == aadhar
               return false;
           }


           if (!TextUtils.isEmpty(etNewMpin.getText().toString().trim())) {//mobile ~ etNewMpin
               return true;
           } else {
               Toast.makeText(this, "Please enter the correct Mobile Number", Toast.LENGTH_LONG).show();
               return false;
           }
       } else {
           etNewMpin.setError("Please enter the correct phone No.");////mobile ~ etNewMpin
           return false;
       }
   }


    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }

    private void showSnackBar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.change_password_layout), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
    }*/
        //__________________________________________________________

              initViews();

        username = getIntent().getStringExtra("userName");
        usertype = getIntent().getStringExtra("userType");
        userTypeId = getIntent().getStringExtra("userTypeId");
        pancard = getIntent().getStringExtra("panCard");
        aadharCard = getIntent().getStringExtra("aadharCard");
        loginUserName = getIntent().getStringExtra("loginUserName");
        password = getIntent().getStringExtra("password");
        firmName = getIntent().getStringExtra("firmName");
        address = getIntent().getStringExtra("address");
        dob = getIntent().getStringExtra("dob");
        city = getIntent().getStringExtra("city");
        imageUrl = getIntent().getStringExtra("imgUrl");
        fromActivity = getIntent().getStringExtra("mpinActivity");
        //___________________________________________

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ChangeMpinActivity.this);
        userid = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        emailId = sharedPreferences.getString("emailId", null);
        mobileno = sharedPreferences.getString("mobileno", null);



        btnSubmit.setOnClickListener(v ->
        {
            if (checkInternetState()) {
                if (checkInputs()) {
                    checkOtp();
                }
            } else {

                showSnackBar();
            }
        });

    }

    private void checkOtp() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
       // Log.e("loginCrediential","\n upserid :: "+ userid +"\n deviceId:: "+ deviceId   +"\n deviceInfo:: "+  deviceInfo +"\n emailId:: "+ emailId   +"\n mobileNo:: "+ mobileno  );


        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getOtp(AUTH_KEY, userid, deviceId, deviceInfo, emailId, mobileno);
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

                            Log.d("otp", "onResponse: "+otp);

                            showOtpDialog(otp);
                        }

                        else if (responseCode.equalsIgnoreCase("NPA"))
                        {
                           // genrateMpin();
                            progressDialog.dismiss();
                            new AlertDialog.Builder(ChangeMpinActivity.this)
                                    .setMessage(responseObject.getString("data"))
                                    .show();
                        }

                        else {
                            progressDialog.dismiss();
                            String message = responseObject.getString("data");

                            new AlertDialog.Builder(ChangeMpinActivity.this)
                                    .setMessage(message)
                                    .show();
                        }

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(ChangeMpinActivity.this)
                                .setMessage("Something went wrong Exception")
                                .show();
                    }
                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(ChangeMpinActivity.this)
                            .setMessage("Something went wrong unsuccessful")
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(ChangeMpinActivity.this)
                        .setMessage("Something went wrong onFailure")
                        .show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showOtpDialog(final String Otp) {
        View addSenderDialogView = getLayoutInflater().inflate(R.layout.add_sender_otp_dialog_layout, null, false);
        final AlertDialog addSenderDialog = new AlertDialog.Builder(ChangeMpinActivity.this).create();
        addSenderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addSenderDialog.setCancelable(false);
        addSenderDialog.setView(addSenderDialogView);
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
                        genrateMpin();


                    } else {
                        new AlertDialog.Builder(ChangeMpinActivity.this)
                                .setMessage("Wrong Otp")
                                .show();
                    }
                } else {
                    etOtp.setError("Required");
                }
            }
        });
    }

    private void genrateMpin() {
        ProgressDialog progressDialog = new ProgressDialog(ChangeMpinActivity.this);
        progressDialog.setMessage("Generating MPIN...");
        progressDialog.setTitle("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String mpin = etMpin.getText().toString().trim();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().generateMpin(AUTH_KEY, userid, deviceId, deviceInfo, "mpin", mpin);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String statusCode = responseObject.getString("statuscode");
                        if (statusCode.equalsIgnoreCase("TXN")) {
                            Toast.makeText(ChangeMpinActivity.this, "MPIN Generated Successful", Toast.LENGTH_LONG).show();

                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ChangeMpinActivity.this);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userid", userid);
                            editor.putString("username", username);
                            editor.putString("pancard", pancard);
                            editor.putString("mobileno", mobileno);
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
                            editor.putString("imgUrl", imageUrl);
                            editor.putBoolean("mpin", true);
                            editor.apply();

                            Intent intent = new Intent(ChangeMpinActivity.this, MPINActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String message = responseObject.getString("data");
                            progressDialog.dismiss();
                            new AlertDialog.Builder(ChangeMpinActivity.this)
                                    .setTitle("Message")
                                    .setMessage(message)
                                    .setPositiveButton("OK", null)
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        new AlertDialog.Builder(ChangeMpinActivity.this)
                                .setTitle("Message")
                                .setMessage("Something went wrong.\nPlease try after sometime.")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(ChangeMpinActivity.this)
                            .setTitle("Message")
                            .setMessage("Something went wrong.\nPlease try after sometime.")
                            .setPositiveButton("OK", null)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(ChangeMpinActivity.this)
                        .setTitle("Message")
                        .setMessage(t.getMessage())
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }

    private boolean checkInputs() {
        if (etMpin.getText().toString().trim().length() == 6) {
            if (etMpin.getText().toString().trim().equalsIgnoreCase(etConfirmMpin.getText().toString().trim())) {
                return true;
            } else {
                etConfirmMpin.setError("Mismatched");
            }
        } else {
            new AlertDialog.Builder(ChangeMpinActivity.this)
                    .setMessage("Six digit MPIN is must")
                    .show();
        }
        return false;
    }

    private void initViews() {
        etMpin = findViewById(R.id.et_mpin);
        etConfirmMpin = findViewById(R.id.et_confirm_mpin);
        btnSubmit = findViewById(R.id.btn_submit);
    }

    private void showSnackBar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.generate_mpin_layout), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }
}