package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.retrofit.RetrofitClient;

public class AddMoneyOnlineActivity extends AppCompatActivity {

    TextView tvName, tvMobile, tvEmail;
    EditText etAmount;
    AppCompatButton btnSubmit;
    SharedPreferences sharedPreferences;
    String name, mobile, email, userId, deviceId, deviceInfo;
    String  aadharNumber,emailAddress,mobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money_online);
        initViews();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AddMoneyOnlineActivity.this);
        name = sharedPreferences.getString("username", null);
        mobile = sharedPreferences.getString("mobileno", null);
       email = sharedPreferences.getString("emailId", null);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        aadharNumber=sharedPreferences.getString("aadharNumber",null);//***mber
        emailAddress=sharedPreferences.getString("email",null);//***mail
        mobileNumber=sharedPreferences.getString("mobileNumber",null);//***ber

        tvName.setText(name);
//        tvMobile.setText(mobile);
        if (mobile != null && mobile.length() >= 3) {
            String maskedMobile = "*******" + mobile.substring(mobile.length() - 3);
            tvMobile.setText(maskedMobile);
        } else {
            tvMobile.setText(mobile);
        }

//        tvEmail.setText(email);
        if (email != null && email.length() > 11) {
            String maskedEmail = "********" + email.substring(email.length() - 11);
            tvEmail.setText(maskedEmail);
        } else {
            tvEmail.setText(email);
        }

        btnSubmit.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(etAmount.getText())) {
                getQrCode();
            } else {
                etAmount.setError("Required");
            }
        });

    }

    private void getQrCode() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String amount = etAmount.getText().toString().trim();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().upiGateway(AUTH_KEY, userId, deviceId, deviceInfo, amount,
                name, email, mobile);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");
                        String data = responseObject.getString("data");

                        if (responseCode.equalsIgnoreCase("TXN")) {

//                            Intent intent = new Intent(AddMoneyOnlineActivity.this, MyWebViewActivity.class);
//                            intent.putExtra("url", data);
//                            startActivity(intent);

                            String strStatus = responseObject.getString("status");
                            JSONObject objStatus = new JSONObject(strStatus);
                            String link  = objStatus.getString("bhim_link");

                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                            startActivity(intent);

                         //   finish();
                        } else {
                            new AlertDialog.Builder(AddMoneyOnlineActivity.this)
                                    .setMessage(data)
                                    .setPositiveButton("OK", null)
                                    .show();
                        }
                        progressDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        new AlertDialog.Builder(AddMoneyOnlineActivity.this)
                                .setMessage("Please try after sometime.")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(AddMoneyOnlineActivity.this)
                            .setMessage(response.message())
                            .setPositiveButton("OK", null)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(AddMoneyOnlineActivity.this)
                        .setMessage(t.getMessage())
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }

    private void initViews() {
        tvName = findViewById(R.id.tv_name);
        tvMobile = findViewById(R.id.tv_mobile);
        tvEmail = findViewById(R.id.tv_email);
        etAmount = findViewById(R.id.et_amount);
        btnSubmit = findViewById(R.id.btn_submit);
    }
}