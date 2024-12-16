package wts.com.accountpe.activities;

import static org.json.JSONObject.wrap;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.retrofit.RetrofitClient;

public class AddMoneyActivity extends AppCompatActivity {

    LinearLayout historyLayout;

    EditText amountET;
    Context context;
    public static final int PAYMENT_REQUEST = 4400;
    static final String AMAZON_PAY = "in.amazon.mShop.android.shopping";
    static final String BHIM_UPI = "in.org.npci.upiapp";
    static final String GOOGLE_PAY = "com.google.android.apps.nbu.paisa.user";
    static final String PHONE_PE = "com.phonepe.app";
    static final String PAYTM = "net.one97.paytm";
    TextView walletBalanceTT, nameTT;
    String walletBalance;
    String appNameStr, emailIdStr;
    String orderId, enterAmountStr;
    String name, mobileNo, userId;
    SharedPreferences sharedPreferences;
    CardView card_GooglePay, card_PhonePe, card_Paytm, card_bhimUPI, card_amzonPay;
    ImageView imgBack;

    String deviceId, deviceInfo;
    String upiId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);

        context = this;
        initViews();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AddMoneyActivity.this);
        name = sharedPreferences.getString("username", null);
        mobileNo = sharedPreferences.getString("mobileno", null);
        userId = sharedPreferences.getString("userid", null);
        emailIdStr = sharedPreferences.getString("email", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        getBalance();
        getUpiId();

        nameTT.setText(name);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        historyLayout.setOnClickListener(view ->
        {
            startActivity(new Intent(AddMoneyActivity.this, AddMoneyReportActivity.class));
        });

        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String transactionUserId = userId.substring(0, 4);
        //orderId = "SD" + transactionUserId + timeStamp;

        card_GooglePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetState()) {
                    enterAmountStr = amountET.getText().toString();
                    if (!enterAmountStr.equalsIgnoreCase("")) {
                        appNameStr = "Google Pay";
                        String packageName = GOOGLE_PAY;
                        insertUPI_Info_to_Server(packageName, "3");
                    } else {
                        showSnackBar("Please enter amount");
                    }
                } else {
                    showSnackBar("No Internet Access");
                }
            }
        });

        card_PhonePe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetState()) {
                    enterAmountStr = amountET.getText().toString();
                    if (!enterAmountStr.equalsIgnoreCase("")) {
                        appNameStr = "Phone Pe";
                        String packageName = PHONE_PE;
                        insertUPI_Info_to_Server(packageName, "2");
                    } else {
                        showSnackBar("Please enter amount");
                    }
                } else {
                    showSnackBar("No Internet Access");
                }
            }
        });

        card_Paytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetState()) {
                    enterAmountStr = amountET.getText().toString();
                    if (!enterAmountStr.equalsIgnoreCase("")) {
                        appNameStr = "Paytm";
                        String packageName = PAYTM;
                        insertUPI_Info_to_Server(packageName, "1");
                    } else {
                        showSnackBar("Please enter amount");
                    }
                } else {
                    showSnackBar("No Internet Access");
                }
            }
        });
        card_bhimUPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetState()) {
                    enterAmountStr = amountET.getText().toString();
                    if (!enterAmountStr.equalsIgnoreCase("")) {
                        appNameStr = "Bhim";
                        String packageName = BHIM_UPI;
                        insertUPI_Info_to_Server(packageName, "5");
                    } else {
                        showSnackBar("Please enter amount");
                    }
                } else {
                    showSnackBar("No Internet Access");
                }
            }
        });

        card_amzonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetState()) {
                    enterAmountStr = amountET.getText().toString();
                    if (!enterAmountStr.equalsIgnoreCase("")) {
                        appNameStr = "Amzon";
                        String packageName = AMAZON_PAY;
                        insertUPI_Info_to_Server(packageName, "4");
                    } else {
                        showSnackBar("Please enter amount");
                    }
                } else {
                    showSnackBar("No Internet Access");
                }
            }
        });
    }

    private void getUpiId() {
        ProgressDialog pDialog = new ProgressDialog(AddMoneyActivity.this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getUpiId(AUTH_KEY);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            JSONArray dataArray = responseObject.getJSONArray("data");
                            JSONObject dataObject = dataArray.getJSONObject(0);
                            upiId = dataObject.getString("upiId");
                            pDialog.dismiss();
                        } else {
                            String message = responseObject.getString("data");
                            pDialog.dismiss();
                            new AlertDialog.Builder(AddMoneyActivity.this)
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    })
                                    .setMessage(message)
                                    .setCancelable(false)
                                    .show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(AddMoneyActivity.this)
                                .setMessage("Please try after sometime.")
                                .setCancelable(false)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(AddMoneyActivity.this)
                            .setMessage("Please try after sometime.")
                            .setCancelable(false)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(AddMoneyActivity.this)
                        .setMessage(t.getMessage())
                        .setCancelable(false)
                        .show();
            }
        });

    }

    private void insertUPI_Info_to_Server(final String packageName, final String upiPermissionId) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().insertUPI_info(AUTH_KEY, userId, name, deviceId, deviceInfo, enterAmountStr, upiPermissionId);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        String res_code = jsonObject.getString("statuscode");
                        if (res_code.equalsIgnoreCase("TXN")) {
                            pDialog.dismiss();
                            orderId = jsonObject.getString("data");
                            payNow(packageName);

                        } else {
                            String response_msg = jsonObject.getString("status");
                            showSnackBar(response_msg);
                            pDialog.dismiss();
                        }
                    } catch (JSONException e) {
                        pDialog.dismiss();
                        showSnackBar("Something went wrong try after sometime");
                        e.printStackTrace();
                    }
                } else {
                    pDialog.dismiss();
                    showSnackBar("" + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                showSnackBar("" + t.toString());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void appNotInstall() {
        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(AddMoneyActivity.this).create();
        final LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.device_not_connected, null);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LinearLayout ic_close = convertView.findViewById(R.id.ic_close);
        TextView tag_line = convertView.findViewById(R.id.tag_line);
        TextView device_name = convertView.findViewById(R.id.device_name);
        Button done_btn = convertView.findViewById(R.id.done_btn);
        TextView skip_btn = convertView.findViewById(R.id.skip_btn);
        ImageView image_set = convertView.findViewById(R.id.image_set);
        image_set.setImageResource(R.drawable.app_not_install);
        tag_line.setText("Application not installed!");
        device_name.setText(appNameStr + " UPI application has not been installed in your phone.Please install this api application for successful transaction.");
        ic_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        done_btn.setText("Download App");
        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appNameStr.equalsIgnoreCase("Google Pay")) {
                    String url = "https://play.google.com/store/apps/details?id=com.google.android.apps.nbu.paisa.user";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                } else if (appNameStr.equalsIgnoreCase("Phone Pe")) {
                    String url = "https://play.google.com/store/apps/details?id=com.phonepe.app";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                } else if (appNameStr.equalsIgnoreCase("Paytm")) {
                    String url = "https://play.google.com/store/apps/details?id=net.one97.paytm";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                } else if (appNameStr.equalsIgnoreCase("Bhim")) {
                    String url = "https://play.google.com/store/apps/details?id=in.org.npci.upiapp";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                } else if (appNameStr.equalsIgnoreCase("Amzon")) {
                    String url = "https://play.google.com/store/apps/details?id=in.amazon.mShop.android.shopping";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
                alertDialog.dismiss();
            }
        });
        skip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(convertView);
        alertDialog.show();
        alertDialog.setCancelable(false);

    }

    private void payNow(String packageName) {
        try {
            PackageManager packageManager = getPackageManager();
            Uri uri = new Uri.Builder()
                    .scheme("upi")
                    .authority("pay")
                    .appendQueryParameter("pa", upiId)
                    .appendQueryParameter("pn", "Account Pe")
                    .appendQueryParameter("mc", "")
                    .appendQueryParameter("tr", orderId)
                    .appendQueryParameter("tn", "Top Up wallet")
                    .appendQueryParameter("am", enterAmountStr)
                    .appendQueryParameter("cu", "INR")
                    .build();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.setPackage(packageName);
            startActivityForResult(intent, PAYMENT_REQUEST);
        } catch (Exception e) {
            appNotInstall();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYMENT_REQUEST) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                String response = data.getStringExtra("response");
                String responseData = getJson(bundle);
                try {
                    JSONObject jsonObject = new JSONObject(responseData);
                    String status = jsonObject.getString("Status");
                    if (status.equalsIgnoreCase("Success")) {
                        String txnId = jsonObject.getString("txnId");
                        updateBalanceUPI("Success", txnId, response);
                    } else if (status.equalsIgnoreCase("Failed")) {
                        updateBalanceUPI("Failed", "NA", response);
                    } else {
                        updateBalanceUPI("Failed", "NA", response);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateBalanceUPI(String status, String upiTxnId, String response) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().updateUPI_info(AUTH_KEY, userId, orderId, deviceId, deviceInfo, upiTxnId, status, response);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        String res_code = jsonObject.getString("statuscode");
                        if (res_code.equalsIgnoreCase("TXN")) {
                            pDialog.dismiss();
                            successDialog();
                        } else {
                            pDialog.dismiss();
                            showSnackBar("Transaction failed, Due to technical error");
                        }
                    } catch (Exception e) {
                        pDialog.dismiss();
                        e.printStackTrace();
                        showSnackBar("Something went wrong");
                    }
                } else {
                    pDialog.dismiss();
                    showSnackBar("" + response.errorBody());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                showSnackBar("Something went wrong");
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void successDialog() {
        final View view1 = LayoutInflater.from(AddMoneyActivity.this).inflate(R.layout.recharge_status_layout, null, false);
        final AlertDialog alertDialog = new AlertDialog.Builder(AddMoneyActivity.this).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.setView(view1);
        alertDialog.show();
        TextView tv_recharge_dialog_title = alertDialog.findViewById(R.id.tv_recharge_dialog_title);
        TextView tvRechargeDialogNumber = alertDialog.findViewById(R.id.tv_recharge_dialogue_number);
        TextView tvRechargeDialogStatus = alertDialog.findViewById(R.id.tv_recharge_dialogue_status);
        TextView tvRechargeDialogAmount = alertDialog.findViewById(R.id.tv_recharge_dialogue_amount);
        Button btnRechargeDialog = alertDialog.findViewById(R.id.btn_recharge_dialog);
        tv_recharge_dialog_title.setText("Payment Successful!");
        tvRechargeDialogNumber.setText("Transaction ID : " + orderId);
        tvRechargeDialogAmount.setText("Amount : " + enterAmountStr);
        tvRechargeDialogStatus.setText("Status : " + "Success");
        btnRechargeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                getBalance();
            }
        });
    }

    private String getJson(final Bundle bundle) {
        if (bundle == null) return null;
        JSONObject jsonObject = new JSONObject();

        for (String key : bundle.keySet()) {
            Object obj = bundle.get(key);
            try {
                jsonObject.put(key, wrap(bundle.get(key)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject.toString();
    }

    private void getBalance() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getBalance(AUTH_KEY, userId, deviceId, deviceInfo, "Login", "0");
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = jsonObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {

                            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                            walletBalance = jsonObject1.getString("userBalance");
                            walletBalanceTT.setText("₹ " + walletBalance);

                            pDialog.dismiss();
                        } else if (responseCode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();
                            walletBalanceTT.setText("₹ 00.00");
                        } else {
                            pDialog.dismiss();
                            walletBalanceTT.setText("₹ 00.00");
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        walletBalanceTT.setText("₹ 00.00");
                    }
                } else {
                    pDialog.dismiss();
                    walletBalanceTT.setText("₹ 00.00");
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                walletBalanceTT.setText("₹ 00.00");
            }
        });
    }

    private void initViews() {
        walletBalanceTT = findViewById(R.id.walletBalanceTT);
        nameTT = findViewById(R.id.nameTT);
        amountET = findViewById(R.id.amountET);
        historyLayout = findViewById(R.id.layoutHistory);
        card_GooglePay = findViewById(R.id.card_GooglePay);
        card_PhonePe = findViewById(R.id.card_PhonePe);
        card_Paytm = findViewById(R.id.card_Paytm);
        card_bhimUPI = findViewById(R.id.card_bhimUPI);
        card_amzonPay = findViewById(R.id.card_amzonPay);
        imgBack = findViewById(R.id.backPress);
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
        Snackbar snackbar = Snackbar.make(findViewById(R.id.add_money_layout), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}