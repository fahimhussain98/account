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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.retrofit.RetrofitClient;

public class AddSenderActivity extends AppCompatActivity {

    ImageView imgClose;
    TextView tvMobile, tvTextMode;
    EditText etFirstName, etLastName, etAddress, etPinCode;
    Button btnRegister;
    String mobileNumber, selectedTxtMode;
    String remitterId;
    AlertDialog addSenderOTPDialog;

    SharedPreferences sharedPreferences;
    String deviceId,deviceInfo;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sender);

        initViews();

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(AddSenderActivity.this);
        deviceId=sharedPreferences.getString("deviceId",null);
        deviceInfo=sharedPreferences.getString("deviceInfo",null);
        userId=sharedPreferences.getString("userid",null);

        mobileNumber = getIntent().getStringExtra("mobileNo");
        selectedTxtMode = getIntent().getStringExtra("mode");
        tvMobile.setText(mobileNumber);
        tvTextMode.setText(selectedTxtMode);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetState()) {
                    if (!TextUtils.isEmpty(etFirstName.getText()) && !TextUtils.isEmpty(etLastName.getText()) && !TextUtils.isEmpty(etAddress.getText())) {
                        if (etPinCode.getText().length() == 6) {


                            addSender();
                        } else {
                            etPinCode.setError("Invalid pincode");
                        }
                    } else {
                        showSnackBar("All Fields Are Mandatory!!!");
                    }
                } else {
                    showSnackBar("No Internet");
                }
            }
        });

    }

    private void addSender() {
        ProgressDialog pDialog = new ProgressDialog(AddSenderActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();

        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String pincode = etPinCode.getText().toString().trim();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().addSender(AUTH_KEY,deviceId,deviceInfo,userId,
                mobileNumber, firstName, lastName, pincode, address, "NA");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {

                    JSONObject jsonObject = null;

                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = jsonObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("OTP SENT")) {

                            remitterId=jsonObject.getString("status");
                            showAddSenderOtpDialog();

                            pDialog.dismiss();
                        } else {
                            pDialog.dismiss();
                            String message=jsonObject.getString("data");
                            new AlertDialog.Builder(AddSenderActivity.this).
                                    setTitle("Message")
                                    .setMessage(message)
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();

                        pDialog.dismiss();
                        new AlertDialog.Builder(AddSenderActivity.this).
                                setTitle("Alert!!!")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Ok", null)
                                .show();

                    }


                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(AddSenderActivity.this).
                            setTitle("Alert!!!")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null)
                            .show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(AddSenderActivity.this).
                        setTitle("Alert!!!")
                        .setMessage("Something went wrong.")
                        .setPositiveButton("Ok", null)
                        .show();

            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void showAddSenderOtpDialog() {
        final View addSenderOTPDialogView = getLayoutInflater().inflate(R.layout.add_sender_otp_dialog_layout, null, false);
        addSenderOTPDialog = new AlertDialog.Builder(AddSenderActivity.this).create();
        addSenderOTPDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addSenderOTPDialog.setCancelable(false);
        addSenderOTPDialog.setView(addSenderOTPDialogView);
        addSenderOTPDialog.show();

        ImageView imgClose = addSenderOTPDialogView.findViewById(R.id.img_close);
        TextView tvTitle = addSenderOTPDialogView.findViewById(R.id.text_user_registration);
        Button btnCancel = addSenderOTPDialogView.findViewById(R.id.btn_cancel);
        Button btnSubmit = addSenderOTPDialogView.findViewById(R.id.btn_submit);
        Button btnResendOtp = addSenderOTPDialogView.findViewById(R.id.btn_resend_otp);
        final EditText etOTP = addSenderOTPDialogView.findViewById(R.id.et_otp);

        etOTP.setHint("OTP");
        tvTitle.setText("OTP Verification");

        btnResendOtp.setVisibility(View.GONE);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSenderOTPDialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSenderOTPDialog.dismiss();
            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = etOTP.getText().toString().trim();
                verifySenderOtp(otp);
            }
        });
    }

    private void verifySenderOtp(String otp) {
        ProgressDialog pDialog = new ProgressDialog(AddSenderActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();


        Call<JsonObject> call=RetrofitClient.getInstance().getApi().verifySender(AUTH_KEY,deviceId,deviceInfo,remitterId,userId,mobileNumber,otp);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {

                    JSONObject jsonObject = null;

                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = jsonObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {


                           /* int status=transactionObject.getInt("statuscode");
                            if (status==1)
                            {

                            }
                            else
                            {

                            }*/
                            String message=jsonObject.getString("data");
                            new AlertDialog.Builder(AddSenderActivity.this).
                                    setTitle("Status")
                                    .setMessage(message)
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    })
                                    .show();


                            pDialog.dismiss();
                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(AddSenderActivity.this).
                                    setTitle("Alert!!!")
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();

                        pDialog.dismiss();
                        new AlertDialog.Builder(AddSenderActivity.this).
                                setTitle("Alert!!!")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Ok", null)
                                .show();

                    }


                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(AddSenderActivity.this).
                            setTitle("Alert!!!")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null)
                            .show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(AddSenderActivity.this).
                        setTitle("Alert!!!")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null)
                        .show();

            }
        });
    }

    private void initViews() {
        imgClose = findViewById(R.id.img_close);
        tvMobile = findViewById(R.id.tv_mobile_number);
        tvTextMode = findViewById(R.id.tv_txt_mode);
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etAddress = findViewById(R.id.et_address);
        etPinCode = findViewById(R.id.et_pin_code);
        btnRegister = findViewById(R.id.btn_register);
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.add_sender_layout), message, Snackbar.LENGTH_LONG);
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