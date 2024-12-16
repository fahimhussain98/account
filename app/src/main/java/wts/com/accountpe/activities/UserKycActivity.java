package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.retrofit.RetrofitClient;


public class UserKycActivity extends AppCompatActivity {

    EditText etAadharNumber, etOtp, etPanNumber;
    AppCompatButton btnProceedAadhar, btnVerifyAadhar, btnProceedPan;
    String userId, deviceId, deviceInfo;
    SharedPreferences sharedPreferences;
    String aadharCard,pancard;
    String transactionId;
    LinearLayout aadharLayout, panLayout;
    String kycStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_kyc);
        initViews();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(UserKycActivity.this);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        aadharCard = sharedPreferences.getString("adharcard", null);
        pancard = sharedPreferences.getString("pancard", null);

        etAadharNumber.setText(aadharCard);
        etPanNumber.setText(pancard);
        //etAadharNumber.setEnabled(false);
        //etPanNumber.setEnabled(false);

        kycStatus = getIntent().getStringExtra("kycStatus");

        if (kycStatus.equalsIgnoreCase("PENDING")) {
            aadharLayout.setVisibility(View.VISIBLE);
            panLayout.setVisibility(View.GONE);
        } else {
            aadharLayout.setVisibility(View.GONE);
            panLayout.setVisibility(View.VISIBLE);
        }

        btnProceedAadhar.setOnClickListener(v ->
        {
            if (etAadharNumber.getText().toString().length() == 12) {
                checkAadhar();
            } else {
                etAadharNumber.setError("Invalid");
            }
        });

        btnVerifyAadhar.setOnClickListener(v ->
        {
            verifyAadhar();
        });

        btnProceedPan.setOnClickListener(v ->
        {
            verifyPan();
        });

    }

    private void verifyPan() {
        ProgressDialog progressDialog = new ProgressDialog(UserKycActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String pan = etPanNumber.getText().toString().trim();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().verifyPan(AUTH_KEY, userId, deviceId, deviceInfo, pan);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        String message = responseObject.getString("data");
                        progressDialog.dismiss();
                        new AlertDialog.Builder(UserKycActivity.this)
                                .setMessage(message)
                                .setCancelable(false)
                                .setPositiveButton("OK", (dialogInterface, i) -> finish()).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        new AlertDialog.Builder(UserKycActivity.this)
                                .setMessage("Please try after sometime")
                                .setCancelable(false)
                                .setPositiveButton("OK", (dialogInterface, i) -> finish()).show();
                    }
                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(UserKycActivity.this)
                            .setMessage("Please try after sometime")
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialogInterface, i) -> finish()).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(UserKycActivity.this)
                        .setMessage(t.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialogInterface, i) -> finish()).show();
            }
        });
    }

    private void verifyAadhar() {
        ProgressDialog progressDialog = new ProgressDialog(UserKycActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String aadharNo = etAadharNumber.getText().toString().trim();
        String otp = etOtp.getText().toString().trim();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().verifyAadharOTP(AUTH_KEY, userId, deviceId, deviceInfo, transactionId, aadharNo, otp);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            progressDialog.dismiss();

                            String message = responseObject.getString("data");
                            progressDialog.dismiss();
                            new AlertDialog.Builder(UserKycActivity.this)
                                    .setMessage(message)
                                    .setCancelable(false)
                                    .setPositiveButton("OK", (dialogInterface, i) -> finish()).show();

//                            aadharLayout.setVisibility(View.GONE);
//                            panLayout.setVisibility(View.VISIBLE);

                        } else {
                            String message = responseObject.getString("data");
                            progressDialog.dismiss();
                            new AlertDialog.Builder(UserKycActivity.this)
                                    .setMessage(message)
                                    .setCancelable(false)
                                    .setPositiveButton("OK", (dialogInterface, i) -> finish()).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        new AlertDialog.Builder(UserKycActivity.this)
                                .setMessage("Please try after sometime")
                                .setCancelable(false)
                                .setPositiveButton("OK", (dialogInterface, i) -> finish()).show();
                    }
                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(UserKycActivity.this)
                            .setMessage("Please try after sometime")
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialogInterface, i) -> finish()).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(UserKycActivity.this)
                        .setMessage(t.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialogInterface, i) -> finish()).show();
            }
        });
    }

    private void checkAadhar() {
        ProgressDialog progressDialog = new ProgressDialog(UserKycActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String aadharNo = etAadharNumber.getText().toString().trim();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().verifyAadhar(AUTH_KEY, userId, deviceId, deviceInfo, aadharNo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            progressDialog.dismiss();

                            transactionId = responseObject.getString("status");
                            etAadharNumber.setVisibility(View.GONE);
                            btnProceedAadhar.setVisibility(View.GONE);
                            etOtp.setVisibility(View.VISIBLE);
                            btnVerifyAadhar.setVisibility(View.VISIBLE);

                        } else {
                            String message = responseObject.getString("data");
                            progressDialog.dismiss();
                            new AlertDialog.Builder(UserKycActivity.this)
                                    .setMessage(message)
                                    .setCancelable(false)
                                    .setPositiveButton("OK", (dialogInterface, i) -> finish()).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        new AlertDialog.Builder(UserKycActivity.this)
                                .setMessage("Please try after sometime")
                                .setCancelable(false)
                                .setPositiveButton("OK", (dialogInterface, i) -> finish()).show();
                    }
                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(UserKycActivity.this)
                            .setMessage("Please try after sometime")
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialogInterface, i) -> finish()).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(UserKycActivity.this)
                        .setMessage(t.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialogInterface, i) -> finish()).show();
            }
        });
    }

    private void initViews() {
        etAadharNumber = findViewById(R.id.et_aadhar_number);
        etOtp = findViewById(R.id.et_otp);
        etPanNumber = findViewById(R.id.et_pan_number);
        btnProceedAadhar = findViewById(R.id.btn_proceed_aadhar);
        btnVerifyAadhar = findViewById(R.id.btn_verify_aadhar);
        btnProceedPan = findViewById(R.id.btn_proceed_pan);
        aadharLayout = findViewById(R.id.aadhar_layout);
        panLayout = findViewById(R.id.pan_layout);
    }
}