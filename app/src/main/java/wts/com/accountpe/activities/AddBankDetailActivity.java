package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.retrofit.RetrofitClient;

public class AddBankDetailActivity extends AppCompatActivity {

    TextView tvBankName;
    EditText etAccountHolderName,etAccountNumber,etIfscCode;
    Button btnAddDetails;

    String userId,deviceId,deviceInfo, emailId, mobileno;
    SharedPreferences sharedPreferences;

    ArrayList<String> bankNameArrayList, ifscArrayList;
    String selectedBankName="select";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_detail);

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(AddBankDetailActivity.this);
        userId= sharedPreferences.getString("userid",null);
        deviceId=sharedPreferences.getString("deviceId",null);
        deviceInfo= sharedPreferences.getString("deviceInfo",null);

        emailId = sharedPreferences.getString("email", null);
        mobileno = sharedPreferences.getString("mobileno", null);

        initViews();
        getBanks();

        btnAddDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInputs())
                {
                  //  addBankDetails();
                    checkOtp();
                }
                else
                {
                    Toast.makeText(AddBankDetailActivity.this, "All Fields Are Mandatory!!!", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void addBankDetails() {
        ProgressDialog progressDialog =new ProgressDialog(AddBankDetailActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        String accountHolderName=etAccountHolderName.getText().toString().trim();
        String accountHolderNumber=etAccountNumber.getText().toString().trim();
        String ifsc=etIfscCode.getText().toString().trim();
        Call<JsonObject> call= RetrofitClient.getInstance().getApi().addbankDetails(AUTH_KEY,userId,deviceId,deviceInfo,selectedBankName,
                accountHolderNumber,accountHolderName,ifsc);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful())
                {
                    try {
                        JSONObject responseObject=new JSONObject(String.valueOf(response.body()));
                        String message=responseObject.getString("data");
                        progressDialog.dismiss();
                        new AlertDialog.Builder(AddBankDetailActivity.this)
                                .setCancelable(false)
                                .setMessage(message)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                })
                                .show();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        new AlertDialog.Builder(AddBankDetailActivity.this)
                                .setMessage("Something went wrong.")
                                .setPositiveButton("OK",null)
                                .show();
                    }
                }
                else
                {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(AddBankDetailActivity.this)
                            .setMessage("Something went wrong.")
                            .setPositiveButton("OK",null)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(AddBankDetailActivity.this)
                        .setMessage(t.getMessage())
                        .setPositiveButton("OK",null)
                        .show();
            }
        });
    }

    private boolean checkInputs() {
        return !selectedBankName.equalsIgnoreCase("select") && !TextUtils.isEmpty(etAccountHolderName.getText())
                && !TextUtils.isEmpty(etAccountNumber.getText()) && !TextUtils.isEmpty(etIfscCode.getText());
    }

    private void initViews() {
        tvBankName=findViewById(R.id.tv_bank_name);
        etAccountHolderName=findViewById(R.id.et_account_holder_name);
        etAccountNumber=findViewById(R.id.et_account_number);
        etIfscCode=findViewById(R.id.et_ifsc_number);
        btnAddDetails=findViewById(R.id.btn_add_details);
    }

    private void getBanks() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getBankDmt(AUTH_KEY, deviceId, deviceInfo, userId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;

                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = jsonObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            JSONArray dataArray=jsonObject.getJSONArray("data");

                            bankNameArrayList = new ArrayList<>();
                            ifscArrayList = new ArrayList<>();

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject transactionObject = dataArray.getJSONObject(i);

                                String responsebankName = transactionObject.getString("BankName");
                                String responseIfsc = transactionObject.getString("IFSC");

                                bankNameArrayList.add(responsebankName);
                                ifscArrayList.add(responseIfsc);

                            }

                            tvBankName.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SpinnerDialog operatorDialog = new SpinnerDialog(AddBankDetailActivity.this, bankNameArrayList, "Select Bank", R.style.DialogAnimations_SmileWindow, "Close  ");// With 	Animation
                                    operatorDialog.setCancellable(true); // for cancellable
                                    operatorDialog.setShowKeyboard(false);// for open keyboard by default
                                    operatorDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                                        @Override
                                        public void onClick(String item, int position) {
                                            tvBankName.setText(item);
                                            selectedBankName = bankNameArrayList.get(position);
                                            etIfscCode.setText(ifscArrayList.get(position));
                                        }
                                    });

                                    operatorDialog.showSpinerDialog();
                                }
                            });

                            pDialog.dismiss();
                        } else if (responseCode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();
                            new AlertDialog.Builder(AddBankDetailActivity.this)
                                    .setTitle("Alert!!!")
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(AddBankDetailActivity.this)
                                .setTitle("Alert!!!")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(AddBankDetailActivity.this)
                            .setTitle("Alert!!!")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(AddBankDetailActivity.this)
                        .setTitle("Alert!!!")
                        .setMessage("Something went wrong.")
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }

    private void checkOtp() {

        final android.app.AlertDialog progressDialog = new android.app.AlertDialog.Builder(AddBankDetailActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setView(convertView);
        progressDialog.setCancelable(false);
        progressDialog.show();


        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getOtp(AUTH_KEY, userId, deviceId, deviceInfo, emailId, mobileno);
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
                            new AlertDialog.Builder(AddBankDetailActivity.this)
                                    .setMessage(responseObject.getString("data"))
                                    .show();
                        }

                        else {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(AddBankDetailActivity.this)
                                    .setMessage("Something went wrong")
                                    .show();
                        }

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        new AlertDialog.Builder(AddBankDetailActivity.this)
                                .setMessage("Something went wrong")
                                .show();
                    }
                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(AddBankDetailActivity.this)
                            .setMessage("Something went wrong")
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(AddBankDetailActivity.this)
                        .setMessage("Something went wrong")
                        .show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showOtpDialog(final String Otp) {
        View addSenderDialogView = getLayoutInflater().inflate(R.layout.add_sender_otp_dialog_layout, null, false);
        final AlertDialog addSenderDialog = new AlertDialog.Builder(AddBankDetailActivity.this).create();
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
                        addBankDetails();


                    } else {
                        new AlertDialog.Builder(AddBankDetailActivity.this)
                                .setMessage("Wrong Otp")
                                .show();
                    }
                } else {
                    etOtp.setError("Required");
                }
            }
        });
    }
}