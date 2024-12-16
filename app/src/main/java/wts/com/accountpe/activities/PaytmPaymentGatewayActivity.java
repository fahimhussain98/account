package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;


import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.retrofit.RetrofitClient;

public class PaytmPaymentGatewayActivity extends AppCompatActivity {

    ImageView backImg;
    EditText etAmount;
    Button btnSubmit;

    String userId, deviceId, deviceInfo, mobileNo;
    String orderID;
    SharedPreferences sharedPreferences;

    String strAmount, strPayVia;
    boolean isFromRecharge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paytm_payment_gateway);

        backImg = findViewById(R.id.backImg);
        etAmount = findViewById(R.id.etAmount);
        btnSubmit = findViewById(R.id.btn_submit);

        backImg.setOnClickListener(view ->
        {
            finish();
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(PaytmPaymentGatewayActivity.this);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        mobileNo = sharedPreferences.getString("mobileno", null);

        strAmount = getIntent().getStringExtra("rechargeAmount");
        strPayVia = getIntent().getStringExtra("payVia");
        isFromRecharge = getIntent().getBooleanExtra("isFromRecharge", false);
        etAmount.setText(strAmount);

        if (isFromRecharge) {
            etAmount.setEnabled(false);
        }

        btnSubmit.setOnClickListener(view ->
        {
            if (!TextUtils.isEmpty(etAmount.getText().toString())) {

                orderID = generateUniqueId();
                generateTxnToken();

            } else {
                etAmount.setError("Required");
            }

        });

    }

    private void generateTxnToken() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Initialising payment...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (etAmount.isEnabled()) {
            strAmount = etAmount.getText().toString().trim();
        }

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getCheckSum(AUTH_KEY, userId, deviceId, deviceInfo, orderID, strAmount);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        String statusCode = responseObject.getString("statuscode");
                        if (statusCode.equalsIgnoreCase("TXN")) {
                            String dataStr = responseObject.getString("data");

                            JSONObject dataObject = new JSONObject(dataStr);

                            JSONObject body = dataObject.getJSONObject("body");
                            try {
                                String txnToken = body.getString("txnToken");
                                doPayment(txnToken);
                            } catch (Exception e) {
                                new AlertDialog.Builder(PaytmPaymentGatewayActivity.this).setMessage("TXN Token not found").show();
                            }


                            progressDialog.dismiss();
                        } else {
                            String message = responseObject.getString("data");
                            progressDialog.dismiss();
                            new AlertDialog.Builder(PaytmPaymentGatewayActivity.this)
                                    .setMessage(message)
                                    .setTitle("Message")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", null)
                                    .show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        new AlertDialog.Builder(PaytmPaymentGatewayActivity.this)
                                .setMessage("Please try after some time.")
                                .setTitle("Message")
                                .setCancelable(false)
                                .setPositiveButton("OK", null)
                                .show();

                    }
                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(PaytmPaymentGatewayActivity.this)
                            .setMessage("Please try after some time.")
                            .setTitle("Message")
                            .setCancelable(false)
                            .setPositiveButton("OK", null)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(PaytmPaymentGatewayActivity.this)
                        .setMessage(t.getMessage())
                        .setTitle("Message")
                        .setCancelable(false)
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }

    public void doPayment(String token) {
        String mid = "enaqtd38124654560231";
        String amount = etAmount.getText().toString().trim();

        String callBackUrl = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=" + orderID;

        PaytmOrder paytmOrder = new PaytmOrder(orderID, mid, token, amount, callBackUrl);

        TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionResponse(@Nullable Bundle bundle) {

                Toast.makeText(getApplicationContext(), "Payment Transaction response " + bundle.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void networkNotAvailable() {
                Toast.makeText(getApplicationContext(), "No Internet ", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onErrorProceed(String s) {
                Toast.makeText(getApplicationContext(), "Error : " + s, Toast.LENGTH_LONG).show();
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                Toast.makeText(getApplicationContext(), "Client Authentication failed : " + s, Toast.LENGTH_LONG).show();
            }

            @Override
            public void someUIErrorOccurred(String s) {
                Toast.makeText(getApplicationContext(), "someUIErrorOccurred : " + s, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {

            }

            @Override
            public void onBackPressedCancelTransaction() {

            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {

            }
        });

        transactionManager.setShowPaymentUrl("https://securegw-stage.paytm.in/theia/api/v1/showPaymentPage");
        transactionManager.setAppInvokeEnabled(true);

        transactionManager.startTransaction(PaytmPaymentGatewayActivity.this, 1);
    }

    private String generateUniqueId() {

        long timeStamp = System.currentTimeMillis();
        String timeStampStr = String.valueOf(timeStamp);
        int length = timeStampStr.length();
        int subLength = length - 4;
        timeStampStr = timeStampStr.substring(subLength, length);

        String subMobileNo = mobileNo.substring(0, 6);


        return "UNID" + subMobileNo + "_" + timeStampStr;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //   Toast.makeText(this, data.getStringExtra("nativeSdkForMerchantMessage") + data.getStringExtra("response"), Toast.LENGTH_SHORT).show();

        if (requestCode == 1 && data != null) {
            String status = null;

            Log.d("TAG", "" + data.getStringExtra("response"));


            if (isFromRecharge) {
                String response = data.getStringExtra("response");
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.has("STATUS")) {
                        status = jsonObject.getString("STATUS");
                    }

                    if (status.equalsIgnoreCase("TXN_SUCCESS")) {

                        strAmount = jsonObject.getString("TXNAMOUNT");


                        Intent intent = new Intent(PaytmPaymentGatewayActivity.this, RechargeActivity.class);
                        intent.putExtra("rechargeAmount", strAmount);
                        intent.putExtra("payVia", strPayVia);
                        setResult(5, intent);
                    } else {
                        Toast.makeText(this, "Transaction failed", Toast.LENGTH_SHORT).show();
                    }
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Transaction failed", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                Intent intent = new Intent(PaytmPaymentGatewayActivity.this, SharePaytmTransactionActivity.class);
                intent.putExtra("response", data.getStringExtra("response"));
                startActivity(intent);
                finish();
            }


        }

    }

}