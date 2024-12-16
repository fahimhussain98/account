package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.retrofit.RetrofitClient;

public class ChangePasswordActivity extends AppCompatActivity {

    String title;
    TextView activityTitle;
    ImageView backButton;

    Button btnChangePassword;
    EditText etCurrentPassword, etNewPssword, etConfirmPassword;
    String currentPassword, newPassword;

    SharedPreferences sharedPreferences;
    String userid;
    ProgressDialog progressDialog;
    String deviceId, deviceInfo, emailId, mobileno;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initViews();
        //////////////////////////////////////////////////////////////////Toolbar
        activityTitle.setText("Change Password");
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //////////////////////////////////////////////////////////////////Toolbar


        //webserviceInterface = WebServiceInterface.retrofit.create(WebServiceInterface.class);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ChangePasswordActivity.this);
        userid = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        //emailId = sharedPreferences.getString("email", null);
        emailId = sharedPreferences.getString("emailId", null);
        mobileno = sharedPreferences.getString("mobileno", null);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInputs()) {

                    if (checkInternetState()) {
                      //  changePassword();
                        checkOtp();
                    } else {
                        showSnackBar();
                    }
                }

            }
        });
    }

    private void checkOtp() {

        final android.app.AlertDialog progressDialog = new android.app.AlertDialog.Builder(ChangePasswordActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setView(convertView);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getOtp(AUTH_KEY, userid, deviceId, deviceInfo, emailId, mobileno);
       // Log.d("changePassoword" ,"getopt credential::  " +"\nuserid : "+ userid + "\n deviceId : "+ deviceId +"\n deviceInfo: "+ deviceInfo +"\nemailId : "+ emailId + "\n mobileNo: "+ mobileno);
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
                            new AlertDialog.Builder(ChangePasswordActivity.this)
                                    .setMessage(responseObject.getString("data"))
                                    .show();
                        }

                        else {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(ChangePasswordActivity.this)
                                    .setMessage("Something went wrong  ERR")
                                    .show();
                        }

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(ChangePasswordActivity.this)
                                .setMessage("Something went wrong Exception e")
                                .show();
                    }
                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(ChangePasswordActivity.this)
                            .setMessage("Something went wrong ")
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(ChangePasswordActivity.this)
                        .setMessage("Something went wrong unSuccessful")
                        .show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showOtpDialog(final String Otp) {
        View addSenderDialogView = getLayoutInflater().inflate(R.layout.add_sender_otp_dialog_layout, null, false);
        final AlertDialog addSenderDialog = new AlertDialog.Builder(ChangePasswordActivity.this).create();
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
                        changePassword();


                    } else {
                        new AlertDialog.Builder(ChangePasswordActivity.this)
                                .setMessage("Wrong Otp")
                                .show();
                    }
                } else {
                    etOtp.setError("Required");
                }
            }
        });
    }


/*
    private void changePassword() {

        progressDialog = new ProgressDialog(ChangePasswordActivity.this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Please wait while logging in...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        currentPassword = etCurrentPassword.getText().toString().trim();
        newPassword = etNewPssword.getText().toString().trim();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().changePassword(AUTH_KEY, deviceId, deviceInfo, userid, currentPassword, newPassword);
        Log.d("changePassoword2" ,"changePassword::  " +"\nuserid : "+ userid + "\n deviceId : "+ deviceId +"\n deviceInfo: "+ deviceInfo +"\ncurrentPassword : "+ currentPassword+"\nnewPassword : "+ newPassword );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        Log.d("changePassoword","changePassword dialog box :: " + response.body());

                        String responseCode = jsonObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            progressDialog.dismiss();


                            String dialogTitle = jsonObject.getString("status");
                            String dialogMessage = jsonObject.getString("data");
                            new AlertDialog.Builder(ChangePasswordActivity.this).setTitle(dialogTitle)
                                    .setMessage(dialogMessage)
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
                            String dialogTitle = jsonObject.getString("response_msg");
                            String dialogMessage = jsonObject.getString("transactions");

                            new AlertDialog.Builder(ChangePasswordActivity.this).setTitle(dialogTitle)
                                    .setMessage(dialogMessage)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).show();

                        } else {
                            progressDialog.dismiss();

                            new AlertDialog.Builder(ChangePasswordActivity.this).setTitle("Alert")
                                    .setMessage("Something went wrong")
                                    .setPositiveButton("Ok", null).show();
                        }

                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(ChangePasswordActivity.this).setTitle("Alert")
                            .setMessage("Something went wrong")
                            .setPositiveButton("Ok", null).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(ChangePasswordActivity.this).setTitle("Alert")
                        .setMessage("Something went wrong")
                        .setPositiveButton("Ok", null).show();
            }
        });
    }
*/

    private void changePassword() {

        progressDialog = new ProgressDialog(ChangePasswordActivity.this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Please wait while logging in...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        currentPassword = etCurrentPassword.getText().toString().trim();
        newPassword = etNewPssword.getText().toString().trim();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().changePassword(AUTH_KEY, deviceId, deviceInfo, userid, currentPassword, newPassword);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                     //   Log.d("changePassword", "Response: " + response.body());

                        String responseCode = jsonObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            String dialogTitle = jsonObject.getString("status");
                            String dialogMessage = jsonObject.getString("data");

                            new AlertDialog.Builder(ChangePasswordActivity.this)
                                    .setTitle(dialogTitle)
                                    .setMessage(dialogMessage)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            finish();
                                        }
                                    })
                                    .show();
                        } else if (responseCode.equalsIgnoreCase("ERR")) {
                            String dialogTitle = jsonObject.getString("status"); // E.g., "Transaction Failed"
                            String dialogMessage = jsonObject.getString("data");  // E.g., "Invalid Details..!!"

                            new AlertDialog.Builder(ChangePasswordActivity.this)
                                    .setTitle(dialogTitle)
                                    .setMessage(dialogMessage)
                                    .setPositiveButton("Ok", null)
                                    .show();
                        } else {
                            showErrorDialog("Alert", "Something went wrong unknown");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        showErrorDialog("Alert", "Something went wrong JSONException e");
                    }
                } else {
                    showErrorDialog("Alert", "Something went wrong unsuccessfull");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                showErrorDialog("Alert", "Something went wrong onFailure");
            }
        });
    }

    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(ChangePasswordActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", null)
                .show();
    }


    private void initViews() {
        backButton = findViewById(R.id.back_button);
        activityTitle = findViewById(R.id.activity_title);

        etCurrentPassword = findViewById(R.id.et_current_password);
        etNewPssword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnChangePassword = findViewById(R.id.btn_change_password);
    }

    private boolean checkInputs() {
        if (!TextUtils.isEmpty(etCurrentPassword.getText())) {
            if (!TextUtils.isEmpty(etNewPssword.getText())) {

                if (!TextUtils.isEmpty(etConfirmPassword.getText())) {
                    if (etNewPssword.getText().toString().trim().equals(etConfirmPassword.getText().toString().trim())) {
                        return true;
                    } else {
                        Toast.makeText(this, "Password and Confirm Password must be same", Toast.LENGTH_LONG).show();
                        return false;
                    }
                } else {
                    etConfirmPassword.setError("Confirm Password can't be empty.");
                    return false;
                }

            } else {
                etNewPssword.setError("New Password can't be empty.");
                return false;
            }
        } else {
            etCurrentPassword.setError("Current Password can't be empty.");
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
    }
}