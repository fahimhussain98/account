package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cashfree.pg.CFPaymentService;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.payu.base.models.ErrorResponse;
import com.payu.base.models.PayUPaymentParams;
import com.payu.checkoutpro.PayUCheckoutPro;
import com.payu.checkoutpro.utils.PayUCheckoutProConstants;
import com.payu.ui.model.listeners.PayUCheckoutProListener;
import com.payu.ui.model.listeners.PayUHashGenerationListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.retrofit.RetrofitClient;

public class CashfreePaymentGatewayActivity extends AppCompatActivity {

    ImageView backImg;
    TextView tvName, tvMobile, tvEmail;
    EditText etAmount;
    ImageView btnSubmit;

    SharedPreferences shp;
    String userId, deviceId, deviceInfo, strName, strEmail, strMobile;

    String strAmount, strPayVia;
    boolean isFromRecharge;

    String  txnid ="txt12346", prodname ="Add_Money";
    String key="noUomw"; // given by anas

    String orderID;

    PayUPaymentParams payUPaymentParams;

    String transactionId,amount,status;

    String hashData,hashName;

    String saltKey = "yRFK4QKuEra5fqkoSSH3vKvmiaQBUc3U";  // given by anas






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashfree_payment_gateway);

        initViews();

        shp = PreferenceManager.getDefaultSharedPreferences(this);
        userId = shp.getString("userid", null);
        deviceId = shp.getString("deviceId", null);
        deviceInfo = shp.getString("deviceInfo", null);
        strName = shp.getString("username", null);
        strEmail = shp.getString("email", null);
        strMobile = shp.getString("mobileno", null);
        tvName.setText(strName);
        tvEmail.setText(strEmail);
        tvMobile.setText(strMobile);

        strAmount = getIntent().getStringExtra("rechargeAmount");
        strPayVia = getIntent().getStringExtra("payVia");
        isFromRecharge = getIntent().getBooleanExtra("isFromRecharge", false);
        etAmount.setText(strAmount);

        if (isFromRecharge) {
            etAmount.setEnabled(false);
        }

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              orderID = generateUniqueId();
                String strAmount = etAmount.getText().toString();
                if (!strAmount.isEmpty()) {
                    if (checkInternetState()) {
                        getToken(orderID, strAmount);
                       // inserDataPayU(orderID,strAmount); // kalpesh


                    } else {
                        showSnackBar("No Internet");
                    }
                } else {
                    etAmount.setError("Enter Amount");
                }
            }
        });

    }

    private void getToken(final String orderID, final String amount) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().generateCashFreeToken(AUTH_KEY, orderID, amount, "INR");
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));

                       String statusCode = jsonObject.getString("statuscode");

                       if (statusCode.equalsIgnoreCase("TXN"))
                       {
                          JSONObject dataObject = jsonObject.getJSONObject("data");
                          String token = dataObject.getString("cftoken");

                          insertDataIntoApi(orderID, amount, token);
                       }
                       else
                       {
                           showSnackBar(jsonObject.getString("data"));
                       }

                        pDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        showSnackBar("something went wrong");
                    }
                } else {
                    pDialog.dismiss();
                    showSnackBar("something went wrong");
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                showSnackBar("something went wrong");
            }
        });
    }

    private void inserDataPayU(String transactionId, String amount) {
        ProgressDialog progressDialog = new ProgressDialog(CashfreePaymentGatewayActivity.this);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().generateHash(RetrofitClient.AUTH_KEY,userId,deviceId,deviceInfo,amount,transactionId,strName,strEmail,strMobile,"NA");

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {

                    try {
                        progressDialog.dismiss();
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = jsonObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN"))
                        {
                            paymentSetup(amount);

                        }
                        else
                        {
                            progressDialog.dismiss();
                            errorDialog("Something went wrong.");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        errorDialog(e.getMessage());
                    }
                } else {
                    progressDialog.dismiss();
                    errorDialog("Something went wrong.");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                errorDialog(t.getMessage());
            }
        });

    }

    private void paymentSetup(String strAmount) {
        HashMap<String, Object> additionalParams = new HashMap<>();
        additionalParams.put(PayUCheckoutProConstants.CP_UDF1, "udf1");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF2, "udf2");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF3, "udf3");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF4, "udf4");
        additionalParams.put(PayUCheckoutProConstants.CP_UDF5, "udf5");
        // to show saved sodexo card
        additionalParams.put(PayUCheckoutProConstants.SODEXO_SOURCE_ID, "srcid123");

        PayUPaymentParams.Builder builder = new PayUPaymentParams.Builder();
        builder.setAmount(strAmount)
                .setIsProduction(true)
                .setProductInfo(prodname)
                .setKey(key)
                .setPhone(strAmount)
                .setTransactionId(orderID)
                .setFirstName(strName)
                .setEmail(strEmail)
                .setSurl("http://api.pay4easype.com/Api/WebhookForPayUpg")
                .setFurl("http://api.pay4easype.com/Api/WebhookForPayUpg");

        //   payUPaymentParams = builder.build();

        try {
            payUPaymentParams = builder.build();
            // generateHashFromServer(paymentParam );
            //  getHashkey();

        } catch (Exception e) {
            Log.e("shuaib", " error s "+e.toString());
        }

        ////////////////////////////////////////////////////

        PayUCheckoutPro.open(
                this,
                payUPaymentParams,
                new PayUCheckoutProListener() {

                    @Override
                    public void onPaymentSuccess(Object response) {
                        //Cast response object to HashMap
                        HashMap<String,Object> result = (HashMap<String, Object>) response;
                        String payuResponse = (String)result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                        String merchantResponse = (String) result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);

                        try {
                            JSONObject jsonObject = new JSONObject(payuResponse);


                            if(jsonObject.has("result")){
                                JSONObject data = jsonObject.getJSONObject("result");


                                transactionId = data.getString("txnid");
                                amount = data.getString("amount");
                                status = data.getString("status");

                            }
                            else {

                                transactionId = jsonObject.getString("txnid");
                                amount = jsonObject.getString("amount");
                                status = jsonObject.getString("status");

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                        //    Toast.makeText(AddMoneyPayuMoneyActivity.this, "Success\npayuResponse : "+payuResponse+"\n merchantResponse : "+merchantResponse, Toast.LENGTH_SHORT).show();

//                        new androidx.appcompat.app.AlertDialog.Builder(AddMoneyPayuMoneyActivity.this)
//                                .setMessage("Status : "+status+"\n amount : "+amount+"\nTransactionId : "+transactionId)
//                                .setCancelable(false)
//                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                })
//                                .show();

                        final View view1 = LayoutInflater.from(CashfreePaymentGatewayActivity.this).inflate(R.layout.payu_response_dialog, null, false);
                        final androidx.appcompat.app.AlertDialog builder = new androidx.appcompat.app.AlertDialog.Builder(CashfreePaymentGatewayActivity.this).create();
                        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        builder.setCancelable(false);
                        builder.setView(view1);

                        TextView tvStatus = view1.findViewById(R.id.tv_status);
                        TextView tvTel = view1.findViewById(R.id.tv_txnid);
                        TextView tvBillAmount = view1.findViewById(R.id.tv_amount);
                        Button btnOk = view1.findViewById(R.id.btn_ok);

                        tvStatus.setText(status);
                        tvTel.setText(transactionId);
                        tvBillAmount.setText("₹ " + amount);

                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                builder.dismiss();

                                updateAddMoneyPayU(transactionId,status);
                            }
                        });

                        builder.show();

                    }
                    @Override
                    public void onPaymentFailure(Object response) {
                        //Cast response object to HashMap
                        HashMap<String,Object> result = (HashMap<String, Object>) response;
                        String payuResponse = (String)result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                        String merchantResponse = (String) result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);

                        try {
                            JSONObject jsonObject = new JSONObject(payuResponse);

                            transactionId = jsonObject.getString("txnid");
                            amount = jsonObject.getString("amount");
                            status = jsonObject.getString("status");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //  Toast.makeText(AddMoneyPayuMoneyActivity.this, "FAILURE", Toast.LENGTH_SHORT).show();
//                        new androidx.appcompat.app.AlertDialog.Builder(AddMoneyPayuMoneyActivity.this)
//                                .setMessage("Failure \npayuResponse : "+payuResponse+"\n merchantResponse : "+merchantResponse)
//                                .setCancelable(false)
//                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                }).show();
                        ///


                        final View view1 = LayoutInflater.from(CashfreePaymentGatewayActivity.this).inflate(R.layout.payu_response_dialog, null, false);
                        final androidx.appcompat.app.AlertDialog builder = new androidx.appcompat.app.AlertDialog.Builder(CashfreePaymentGatewayActivity.this).create();
                        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        builder.setCancelable(false);
                        builder.setView(view1);

                        TextView tvStatus = view1.findViewById(R.id.tv_status);
                        TextView tvTel = view1.findViewById(R.id.tv_txnid);
                        TextView tvBillAmount = view1.findViewById(R.id.tv_amount);
                        Button btnOk = view1.findViewById(R.id.btn_ok);

                        tvStatus.setText(status);
                        tvTel.setText(transactionId);
                        tvBillAmount.setText("₹ " + amount);

                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                builder.dismiss();

                                updateAddMoneyPayU(transactionId,status);
                            }

                        });

                        builder.show();


                        ///
                    }
                    @Override
                    public void onPaymentCancel(boolean isTxnInitiated) {

                    }
                    @Override
                    public void onError(ErrorResponse errorResponse) {
                        String errorMessage = errorResponse.getErrorMessage();
                        // Toast.makeText(AddMoneyPayuMoneyActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        new android.app.AlertDialog.Builder(CashfreePaymentGatewayActivity.this)
                                .setTitle("Alert")
                                .setMessage(errorMessage)
                                .setIcon(R.drawable.warning)
                                .setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                })
                                .setCancelable(false)
                                .show();

                    }
                    @Override
                    public void setWebViewProperties(@Nullable WebView webView, @Nullable Object o) {

                        //For setting webview properties, if any. Check Customized Integration section for more details on this
                    }
                    @Override
                    public void generateHash(HashMap<String, String> valueMap, PayUHashGenerationListener hashGenerationListener) {
                        hashName = valueMap.get(PayUCheckoutProConstants.CP_HASH_NAME);
                        hashData = valueMap.get(PayUCheckoutProConstants.CP_HASH_STRING);
                        if (!TextUtils.isEmpty(hashName) && !TextUtils.isEmpty(hashData)) {
                            //Do not generate hash from local, it needs to be calculated from server side only. Here, hashString contains hash created from your server side.
                            // Call rest api and send hashData to server
                            // generateHashFromServer(hashData);

                            //Calculate HmacSHA1 hash using the hashData and merchant secret key
                            String   hash = HashGenerationUtils.generateHashFromSDK(hashData,saltKey);

                            //  String hash = hashString;  // uncommented by shuaib

                            if (!TextUtils.isEmpty(hash)) {
                                HashMap<String, String> dataMap = new HashMap<>();
                                dataMap.put(hashName, hash);  //  commented by shuaib
                                hashGenerationListener.onHashGenerated(dataMap);
                            }
                            else {

                                Toast.makeText(CashfreePaymentGatewayActivity.this, "Hash Value is Empty", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            Toast.makeText(CashfreePaymentGatewayActivity.this, "HashData or HashName is Empty", Toast.LENGTH_SHORT).show();

                        }
                    }
                }
        );
        //////////////////////////
    }

    private void updateAddMoneyPayU(String transactionId, String status) {

        ProgressDialog progressDialog = new ProgressDialog(CashfreePaymentGatewayActivity.this);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().updateAddMoneyPayu(RetrofitClient.AUTH_KEY,userId,orderID,deviceId,deviceInfo,status,transactionId);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {

                    try {
                        progressDialog.dismiss();
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = jsonObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN"))
                        {
                            finish();
                        }
                        else
                        {
                            progressDialog.dismiss();
                            errorDialog("Something went wrong.");
                        }

                    } catch (JSONException e) {

                        e.printStackTrace();
                        progressDialog.dismiss();
                        errorDialog(e.getMessage());
                    }
                } else {
                    progressDialog.dismiss();
                    errorDialog("Something went wrong.");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                progressDialog.dismiss();
                errorDialog(t.getMessage());
            }
        });

    }



    private void insertDataIntoApi(final String orderID, final String amount, String token) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().insertData(AUTH_KEY, userId,deviceId, deviceInfo, orderID,amount);
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));

                       String statusCode = jsonObject.getString("statuscode");

                       if (statusCode.equalsIgnoreCase("TXN"))
                       {
                           insertInfo(amount, orderID, token);
                       }
                       else
                       {
                           showSnackBar(jsonObject.getString("data"));
                       }

                        pDialog.dismiss();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        showSnackBar("something went wrong");
                    }
                } else {
                    pDialog.dismiss();
                    showSnackBar("something went wrong");
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                showSnackBar("something went wrong");
            }
        });
    }

    public void insertInfo(String amount, String orderID, String token) {

        HashMap<String, String> hashMap = new HashMap<>();
        //  hashMap.put(CFPaymentService.PARAM_APP_ID, "109861e4f468265d79813bcaf4168901");  // for test
        hashMap.put(CFPaymentService.PARAM_APP_ID, "12109301b100c395be2a19e0db390121");     // for live
        hashMap.put(CFPaymentService.PARAM_ORDER_ID, orderID);
        hashMap.put(CFPaymentService.PARAM_ORDER_AMOUNT, amount);
        hashMap.put(CFPaymentService.PARAM_CUSTOMER_NAME, strName);
        hashMap.put(CFPaymentService.PARAM_CUSTOMER_EMAIL, strEmail);
        hashMap.put(CFPaymentService.PARAM_CUSTOMER_PHONE, strMobile);
        if (isFromRecharge)
        {
            hashMap.put(CFPaymentService.PARAM_NOTIFY_URL, "https://api.accountpe.in/api/cashfree");   // for not receive call back from cashfree
        }
        else
        {
            hashMap.put(CFPaymentService.PARAM_NOTIFY_URL, "https://api.accountpe.in/api/cashfreepgwebhook");   // for receive call back from cashfree
        }


     //   CFPaymentService.getCFPaymentServiceInstance().doPayment(this, hashMap, token, "PROD", "#D60006", "#ffffff", true);
        CFPaymentService.getCFPaymentServiceInstance().upiPayment(this, hashMap, token, "PROD");
    }

    private void UpdateCashFreeAddMoneyWebhook(String uniqueId, String status, String txnId) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().UpdateCashfreeAddMoneyWebhook(AUTH_KEY, userId,deviceId, deviceInfo, uniqueId, status, txnId, "Cashfree Response");
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));

                        String statusCode = jsonObject.getString("statuscode");

                        if (statusCode.equalsIgnoreCase("TXN"))
                        {

                        }
                        else
                        {
                            showSnackBar(jsonObject.getString("data"));
                        }

                        pDialog.dismiss();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        showSnackBar("something went wrong");
                    }
                } else {
                    pDialog.dismiss();
                    showSnackBar("something went wrong");
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                showSnackBar("something went wrong");
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CFPaymentService.REQ_CODE && data != null) {
            Bundle bundle = data.getExtras();

            if (bundle != null) {

                String status = bundle.getString("txStatus");
                String orderId = bundle.getString("orderId");

                UpdateCashFreeAddMoneyWebhook(orderId, status, orderId);

                Toast.makeText(this, status, Toast.LENGTH_SHORT).show();

                    if (isFromRecharge) {

                            if (status.equalsIgnoreCase("SUCCESS")) {

                                Toast.makeText(this, "Payment success", Toast.LENGTH_SHORT).show();

                                strAmount = bundle.getString("orderAmount");

                                Intent intent = new Intent(CashfreePaymentGatewayActivity.this, RechargeActivity.class);
                                intent.putExtra("rechargeAmount", strAmount);
                                intent.putExtra("payVia", strPayVia);
                                setResult(5, intent);
                            } else {
                                Toast.makeText(this, "Transaction failed", Toast.LENGTH_SHORT).show();
                            }
                            finish();

                    }
                    else
                    {
                      //  showResponse(transformBundleToString(bundle));    // for all response

                        Dialog alertDialog = new Dialog(CashfreePaymentGatewayActivity.this);
                        alertDialog.setContentView(R.layout.recharge_status_layout);
                        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.home_dash_back_white);
                        alertDialog.show();

                        ImageView imgStatus = alertDialog.findViewById(R.id.img_recharge_dialog_icon);
                        TextView tv_recharge_dialog_title = alertDialog.findViewById(R.id.tv_recharge_dialog_title);
                        TextView tvRechargeDialogNumber = alertDialog.findViewById(R.id.tv_recharge_dialogue_number);
                        TextView tvRechargeDialogStatus = alertDialog.findViewById(R.id.tv_recharge_dialogue_status);
                        TextView tvRechargeDialogAmount = alertDialog.findViewById(R.id.tv_recharge_dialogue_amount);
                        Button btnRechargeDialog = alertDialog.findViewById(R.id.btn_recharge_dialog);

                        tv_recharge_dialog_title.setText("Payment "+status);
                        tvRechargeDialogNumber.setText("Transaction ID : " + orderId);
                        tvRechargeDialogStatus.setText("Status : " + status);

                        if (status.equalsIgnoreCase("Success"))
                        {

                            imgStatus.setImageResource(R.drawable.successicon);
                            strAmount = bundle.getString("orderAmount");
                            tvRechargeDialogAmount.setText("Amount : " + strAmount);
                        }
                        else
                        {
                            tvRechargeDialogAmount.setVisibility(View.GONE);
                            imgStatus.setImageResource(R.drawable.failureicon);
                        }

                        btnRechargeDialog.setOnClickListener(view ->
                        {
                            alertDialog.dismiss();
                            finish();
                        });

                    }

                }
                else
                {
                    Toast.makeText(this, "Payment not found", Toast.LENGTH_LONG).show();
                }


            }
        }

    public void showResponse(String response) {
        new AlertDialog.Builder(this)
                .setTitle("Payment Response")
                .setMessage(response)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        finish();
                    }
                }).show();
    }

    public String transformBundleToString(Bundle bundle) {

        String response = "";
        for (String key : bundle.keySet()) {
            response = response.concat(String.format("%s : %s\n", key, bundle.getString(key)));

        }

        return response;
    }

    private String generateUniqueId() {

        long timeStamp = System.currentTimeMillis();
        String timeStampStr = String.valueOf(timeStamp);
        int length = timeStampStr.length();
        int subLength = length - 6;
        timeStampStr = timeStampStr.substring(subLength, length);

        String subMobileNo = strMobile.substring(0, 6);

        return "UNID" + subMobileNo + "_" + timeStampStr;

    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert manager != null;
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.main_layout), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void initViews() {
        backImg = findViewById(R.id.back_img);
        etAmount = findViewById(R.id.et_amount);
        btnSubmit = findViewById(R.id.btn_submit);
        tvName = findViewById(R.id.tv_name);
        tvMobile = findViewById(R.id.tv_mobile);
        tvEmail = findViewById(R.id.tv_email);
    }


    private void errorDialog(String message) {
        new android.app.AlertDialog.Builder(CashfreePaymentGatewayActivity.this)
                .setTitle("Alert")
                .setMessage(message)
                .setIcon(R.drawable.warning)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

}