package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import android.view.LayoutInflater;
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

public class ExpressDmtAddSenderActivity extends AppCompatActivity {

    ImageView imgClose;
    TextView tvMobile;
    EditText etFirstName, etAddress, etPinCode, etOtp;
    Button btnRegister;
    String mobileNumber;

    SharedPreferences sharedPreferences;
    String deviceId,deviceInfo;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_express_dmt_add_sender);

        initViews();

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(ExpressDmtAddSenderActivity.this);
        deviceId=sharedPreferences.getString("deviceId",null);
        deviceInfo=sharedPreferences.getString("deviceInfo",null);
        userId=sharedPreferences.getString("userid",null);

        mobileNumber = getIntent().getStringExtra("mobileNo");
        tvMobile.setText(mobileNumber);

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
                    if (!TextUtils.isEmpty(etFirstName.getText())  && !TextUtils.isEmpty(etAddress.getText())) {
                        if (etPinCode.getText().length() == 6) {


                                if (!etOtp.getText().toString().isEmpty())
                                {
                                    addSenderExpressDmt();
                                }
                                else
                                {
                                    new AlertDialog.Builder(ExpressDmtAddSenderActivity.this).setMessage("Please enter otp").show();
                                }
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

    private void addSenderExpressDmt() {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(ExpressDmtAddSenderActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        String firstName = etFirstName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String pinCode = etPinCode.getText().toString().trim();
        String otp = etOtp.getText().toString().trim();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().addSenderExpress(AUTH_KEY,deviceId,deviceInfo,userId, mobileNumber, firstName,
                pinCode, address, otp);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {

                    JSONObject jsonObject = null;

                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = jsonObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN") || responseCode.equalsIgnoreCase("ERR")) {

                            String message=jsonObject.getString("data");

                            new AlertDialog.Builder(ExpressDmtAddSenderActivity.this).
                                    setTitle("Message")
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
                            new AlertDialog.Builder(ExpressDmtAddSenderActivity.this).
                                    setTitle("Alert!!!")
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();

                        pDialog.dismiss();
                        new AlertDialog.Builder(ExpressDmtAddSenderActivity.this).
                                setTitle("Alert!!!")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Ok", null)
                                .show();

                    }


                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(ExpressDmtAddSenderActivity.this).
                            setTitle("Alert!!!")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null)
                            .show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(ExpressDmtAddSenderActivity.this).
                        setTitle("Alert!!!")
                        .setMessage("Something went wrong.")
                        .setPositiveButton("Ok", null)
                        .show();

            }
        });

    }
    private void initViews() {
        imgClose = findViewById(R.id.img_close);
        tvMobile = findViewById(R.id.tv_mobile_number);
        etFirstName = findViewById(R.id.et_first_name);
        etAddress = findViewById(R.id.et_address);
        etPinCode = findViewById(R.id.et_pin_code);
        etOtp = findViewById(R.id.et_otp);
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
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

}