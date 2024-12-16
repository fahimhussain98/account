package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.retrofit.RetrofitClient;

public class WalletToWalletActivity extends AppCompatActivity {

    EditText etMobileNumber,etAmount,etRemarks;
    TextView tvUsername,tvCompany,tvEmailId;
    AppCompatButton btnSubmit,btnProceedToPay;
    String mobileNumber;

    String userId,deviceId,deviceInfo;
    SharedPreferences sharedPreferences;

    String transferTo,userName,companyName,targetMobileNumber,emailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_to_wallet);

        initViews();

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(WalletToWalletActivity.this);
        userId=sharedPreferences.getString("userid",null);
        deviceId=sharedPreferences.getString("deviceId",null);
        deviceInfo=sharedPreferences.getString("deviceInfo",null);

        etAmount.setVisibility(View.GONE);
        etRemarks.setVisibility(View.GONE);
        tvUsername.setVisibility(View.GONE);
        tvCompany.setVisibility(View.GONE);
        tvEmailId.setVisibility(View.GONE);
        btnProceedToPay.setVisibility(View.GONE);

        btnSubmit.setOnClickListener(view->
        {
            mobileNumber=etMobileNumber.getText().toString().trim();
            if (mobileNumber.length() ==10)
            {
                getUserDetails();
            }
            else
            {
                new AlertDialog.Builder(WalletToWalletActivity.this)
                        .setMessage("Please enter valid mobile number.")
                        .setPositiveButton("OK",null)
                        .show();
            }
        });

        btnProceedToPay.setOnClickListener(view->
        {
            if (!TextUtils.isEmpty(etAmount.getText().toString()))
            doWalletToWalletTransaction();
            else
                new AlertDialog.Builder(WalletToWalletActivity.this)
                        .setMessage("Please enter amount.")
                        .setPositiveButton("OK",null)
                        .show();
        });

    }

    private void doWalletToWalletTransaction() {
        ProgressDialog progressDialog=new ProgressDialog(WalletToWalletActivity.this);
        progressDialog.setMessage("Please wait\nGetting Details...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String amount=etAmount.getText().toString().trim();

        Call<JsonObject> call= RetrofitClient.getInstance().getApi().doWalletToWalletTransaction(AUTH_KEY,userId,deviceId,deviceInfo,
                amount,transferTo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful())
                {
                    try {
                        JSONObject responseObject=new JSONObject(String.valueOf(response.body()));

                        //String responseCode=responseObject.getString("statuscode");

                            progressDialog.dismiss();
                            String message=responseObject.getString("data");
                            new AlertDialog.Builder(WalletToWalletActivity.this)
                                    .setMessage(message)
                                    .setCancelable(false)
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    }).show();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        new AlertDialog.Builder(WalletToWalletActivity.this)
                                .setMessage("Please try after sometime.")
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                }).show();
                    }
                }
                else
                {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(WalletToWalletActivity.this)
                            .setMessage("Please try after sometime.")
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(WalletToWalletActivity.this)
                        .setMessage(t.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).show();
            }
        });
    }

    private void getUserDetails() {
        ProgressDialog progressDialog=new ProgressDialog(WalletToWalletActivity.this);
        progressDialog.setMessage("Please wait\nGetting Details...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<JsonObject> call= RetrofitClient.getInstance().getApi().getUserDetails(AUTH_KEY,userId,deviceId,deviceInfo,mobileNumber);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful())
                {
                    try {
                        JSONObject responseObject=new JSONObject(String.valueOf(response.body()));

                        String responseCode=responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN"))
                        {
                            JSONArray dataArray=responseObject.getJSONArray("data");
                            JSONObject dataObject=dataArray.getJSONObject(0);

                            transferTo=dataObject.getString("ID");
                            userName=dataObject.getString("UserName");
                            companyName=dataObject.getString("CompanyName");
                            targetMobileNumber=dataObject.getString("MobileNo");
                            emailId=dataObject.getString("EmailID");

                            tvUsername.setText(userName);
                            tvCompany.setText(companyName);
                            tvEmailId.setText(emailId);

                            tvUsername.setVisibility(View.VISIBLE);
                            etAmount.setVisibility(View.VISIBLE);
                            //etRemarks.setVisibility(View.VISIBLE);
                            tvCompany.setVisibility(View.VISIBLE);
                            tvEmailId.setVisibility(View.VISIBLE);
                            btnProceedToPay.setVisibility(View.VISIBLE);
                            btnSubmit.setVisibility(View.GONE);
                            etMobileNumber.setVisibility(View.GONE);

                            progressDialog.dismiss();
                        }
                        else
                        {
                            progressDialog.dismiss();
                            String message=responseObject.getString("data");
                            new AlertDialog.Builder(WalletToWalletActivity.this)
                                    .setMessage(message)
                                    .setCancelable(false)
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    }).show();

                            tvUsername.setText(userName);
                            tvCompany.setText(companyName);
                            tvEmailId.setText(emailId);

                            tvUsername.setVisibility(View.GONE);
                            etAmount.setVisibility(View.GONE);
                            //etRemarks.setVisibility(View.GONE);
                            tvCompany.setVisibility(View.GONE);
                            tvEmailId.setVisibility(View.GONE);
                            btnProceedToPay.setVisibility(View.GONE);
                            btnSubmit.setVisibility(View.VISIBLE);
                            etMobileNumber.setVisibility(View.VISIBLE);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        new AlertDialog.Builder(WalletToWalletActivity.this)
                                .setMessage("Please try after sometime.")
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                }).show();

                        tvUsername.setText(userName);
                        tvCompany.setText(companyName);
                        tvEmailId.setText(emailId);

                        tvUsername.setVisibility(View.GONE);
                        etAmount.setVisibility(View.GONE);
                        //etRemarks.setVisibility(View.GONE);
                        tvCompany.setVisibility(View.GONE);
                        tvEmailId.setVisibility(View.GONE);
                        btnProceedToPay.setVisibility(View.GONE);
                        btnSubmit.setVisibility(View.VISIBLE);
                        etMobileNumber.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(WalletToWalletActivity.this)
                            .setMessage("Please try after sometime.")
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).show();

                    tvUsername.setText(userName);
                    tvCompany.setText(companyName);
                    tvEmailId.setText(emailId);

                    tvUsername.setVisibility(View.GONE);
                    etAmount.setVisibility(View.GONE);
                    //etRemarks.setVisibility(View.GONE);
                    tvCompany.setVisibility(View.GONE);
                    tvEmailId.setVisibility(View.GONE);
                    btnProceedToPay.setVisibility(View.GONE);
                    btnSubmit.setVisibility(View.VISIBLE);
                    etMobileNumber.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(WalletToWalletActivity.this)
                        .setMessage(t.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).show();

                tvUsername.setText(userName);
                tvCompany.setText(companyName);
                tvEmailId.setText(emailId);

                tvUsername.setVisibility(View.GONE);
                etAmount.setVisibility(View.GONE);
                //etRemarks.setVisibility(View.GONE);
                tvCompany.setVisibility(View.GONE);
                tvEmailId.setVisibility(View.GONE);
                btnProceedToPay.setVisibility(View.GONE);
                btnSubmit.setVisibility(View.VISIBLE);
                etMobileNumber.setVisibility(View.VISIBLE);
            }
        });
        
    }

    private void initViews() {
        etMobileNumber=findViewById(R.id.et_mobile_number);
        etAmount=findViewById(R.id.et_amount);
        etRemarks=findViewById(R.id.et_remarks);
        tvUsername=findViewById(R.id.tv_username);
        tvCompany=findViewById(R.id.tv_company);
        tvEmailId=findViewById(R.id.tv_email_id);
        btnSubmit=findViewById(R.id.btn_submit);
        btnProceedToPay=findViewById(R.id.btn_proceed);
    }
}