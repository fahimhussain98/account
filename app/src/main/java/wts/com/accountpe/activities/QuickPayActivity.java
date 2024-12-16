package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.adapters.OperatorAdapter;
import wts.com.accountpe.models.OperatorModel;
import wts.com.accountpe.myInterface.OperatorInterface;
import wts.com.accountpe.retrofit.RetrofitClient;


public class QuickPayActivity extends AppCompatActivity {

    TextView tvTitle;
    EditText etRechargeNumber, etAmount;
    LinearLayout mobileOperatorContainer;
    String service;
    LinearLayout operatorLayout;
    String selectedOperatorId;
    String selectedOperatorName = "Select Operator",  selectedOperatorImage;
    TextView tvOperator;
    String userid, deviceInfo, deviceId;
    SharedPreferences sharedPreferences;
    Dialog operatorDialog;
    Button btnRecharge;
    String responseNumber, responseAmount, responseStatus, responseTransactionId, responseDateTime, responseOperator;


    ArrayList<OperatorModel> operatorsModelArrayList;
    Dialog tpinDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_pay);
        initViews();
        service = getIntent().getStringExtra("service");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(QuickPayActivity.this);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        userid = sharedPreferences.getString("userid", null);

        getService(service);

        operatorLayout.setOnClickListener(view -> {
            operatorDialog.show();
        });

        btnRecharge.setOnClickListener(v -> {


                if (checkInputs()) {
                    if (checkInternetState()) {

                        String number = etRechargeNumber.getText().toString().trim();
                        final String amount = etAmount.getText().toString().trim();
                        final View view1 = LayoutInflater.from(QuickPayActivity.this).inflate(R.layout.confirm_dialog, null, false);
                        final AlertDialog builder = new AlertDialog.Builder(QuickPayActivity.this).create();
                        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        builder.setView(view1);
                        builder.setCancelable(false);
                        builder.show();

                        TextView tvNumber = view1.findViewById(R.id.tv_number);
                        TextView tvAmount = view1.findViewById(R.id.tv_amount);
                        TextView tvOperator = view1.findViewById(R.id.tv_operator);
                        TextView tvConfirmRecharge = view1.findViewById(R.id.tv_confirm_recharge);
                        Button btnConfirmRecharge = view1.findViewById(R.id.btnConfirmRecharge);
                     //   ImageView imgOperator = view1.findViewById(R.id.img_operator);
                        ImageView imgClose = view1.findViewById(R.id.img_close);
                        LinearLayout checkBoxLayout = view1.findViewById(R.id.layout3);
                        LinearLayout commissionLayout = view1.findViewById(R.id.layout_commission);
                        LinearLayout totalAmountLayout = view1.findViewById(R.id.layout_totalAmount);
                        View view = view1.findViewById(R.id.view1);

                        checkBoxLayout.setVisibility(View.GONE);
                        commissionLayout.setVisibility(View.GONE);
                        totalAmountLayout.setVisibility(View.GONE);
                        view.setVisibility(View.GONE);


                        tvNumber.setText(number);
                        tvAmount.setText("\u20b9" + amount);
                        tvOperator.setText(selectedOperatorName);

                      //  Picasso.get().load(selectedOperatorImage).into(imgOperator);

                        imgClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                builder.dismiss();
                            }
                        });

                        btnConfirmRecharge.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                doRecharge();
                             //   showTpinDialog();

                                builder.dismiss();
                            }
                        });
                    } else {
                        showSnackBar("No Internet");
                    }
                }
        });


    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.recharge_layout), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private boolean checkInputs() {
        if (!TextUtils.isEmpty(etRechargeNumber.getText())) {
            if (!TextUtils.isEmpty(etAmount.getText())) {
                if (!selectedOperatorName.equalsIgnoreCase("Select Operator")) {
                    return true;
                } else {
                    new AlertDialog.Builder(QuickPayActivity.this).setMessage("Select Operator")
                            .setPositiveButton("Ok", null).show();
                    return false;
                }
            } else {
                etAmount.setError("Amount can't be empty.");
                return false;
            }
        } else {
            etRechargeNumber.setError("Enter Number.");
            return false;
        }
    }

    private void doRecharge() {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(QuickPayActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        String number = etRechargeNumber.getText().toString().trim();
        final String amount = etAmount.getText().toString().trim();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().postPaidQuickPay(AUTH_KEY,userid,deviceId,deviceInfo,selectedOperatorId,
                amount,number);

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;

                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));
                        String statusCode = jsonObject1.getString("statuscode");


                        if (statusCode.equalsIgnoreCase("TXN")) {
                            JSONArray jsonArray = jsonObject1.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                responseNumber = jsonObject.getString("ConsumerNo");
                                responseStatus = jsonObject.getString("Status");
                                responseAmount = jsonObject.getString("PayableAmount");
                                responseTransactionId = jsonObject.getString("UniqueID");
                                responseDateTime = jsonObject.getString("CreateDate");
                                responseOperator = jsonObject.getString("OperatorName");
                            }

                            @SuppressLint("SimpleDateFormat") DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
                            String[] splitDate = responseDateTime.split("T");
                            try {
                                Date date = inputDateFormat.parse(splitDate[0]);
                                Date time = simpleDateFormat.parse(splitDate[1]);

                                String outputDate = new SimpleDateFormat("dd MMM yyyy").format(date);
                                String outputTime = new SimpleDateFormat("hh:mm a").format(time);
                                Intent intent = new Intent(QuickPayActivity.this, RechargeStatusActivity.class);
                                intent.putExtra("responseStatus", responseStatus);
                                intent.putExtra("responseNumber", responseNumber);
                                intent.putExtra("responseAmount", responseAmount);
                                intent.putExtra("responseTransactionId", responseTransactionId);
                                intent.putExtra("liveId", "****");
                                intent.putExtra("responseOperator", responseOperator);
                                intent.putExtra("outputDate", outputDate);
                                intent.putExtra("outputTime", outputTime);
                                startActivity(intent);
                                finish();

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            pDialog.dismiss();
                        } else if (statusCode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();
                            String status = jsonObject1.getString("data");
                            final View view1 = LayoutInflater.from(QuickPayActivity.this).inflate(R.layout.recharge_status_layout, null, false);
                            final AlertDialog builder = new AlertDialog.Builder(QuickPayActivity.this).create();
                            builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            builder.setCancelable(false);
                            builder.setView(view1);
                            builder.show();

                            ImageView imgRechargeDialogIcon = view1.findViewById(R.id.img_recharge_dialog_icon);
                            TextView tvRechargeDialogNumber = view1.findViewById(R.id.tv_recharge_dialogue_number);
                            TextView tvRechargeDialogStatus = view1.findViewById(R.id.tv_recharge_dialogue_status);
                            TextView tvRechargeDialogAmount = view1.findViewById(R.id.tv_recharge_dialogue_amount);
                            Button btnRechargeDialog = view1.findViewById(R.id.btn_recharge_dialog);

                            tvRechargeDialogAmount.setVisibility(View.INVISIBLE);
                            tvRechargeDialogNumber.setVisibility(View.INVISIBLE);

                            tvRechargeDialogStatus.setTextColor(Color.RED);
                            tvRechargeDialogStatus.setText("Status : " + status);
                            imgRechargeDialogIcon.setImageResource(R.drawable.failureicon);
                            btnRechargeDialog.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    builder.dismiss();
                                }
                            });
                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(QuickPayActivity.this)
                                    .setTitle("Alert")
                                    .setMessage("Something went wrong")
                                    .setPositiveButton("Ok", null).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(QuickPayActivity.this)
                                .setTitle("Alert")
                                .setMessage("Something went wrong")
                                .setPositiveButton("Ok", null).show();
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

                pDialog.dismiss();
                new AlertDialog.Builder(QuickPayActivity.this)
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null).show();
            }
        });
    }

//    public void showTpinDialog() {
//        tpinDialog = new Dialog(this);
//        tpinDialog.setCancelable(false);
//        tpinDialog.setContentView(R.layout.check_tpin_dialog);
//        tpinDialog.getWindow().setBackgroundDrawableResource(R.drawable.bordered_back);
//        tpinDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        tpinDialog.show();
//
//        EditText et_tpin = tpinDialog.findViewById(R.id.et_tpin);
//        Button btn_submit = tpinDialog.findViewById(R.id.btn_submit);
//
//        btn_submit.setOnClickListener(view ->
//        {
//            String strTpin = et_tpin.getText().toString();
//            if (!strTpin.isEmpty())
//            {
//                checkTpin(strTpin);
//            }
//
//        });
//
//    }
//
//    private void checkTpin(String tpin) {
//        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(QuickPayActivity.this).create();
//        LayoutInflater inflater = getLayoutInflater();
//        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
//        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        pDialog.setView(convertView);
//        pDialog.setCancelable(false);
//        pDialog.show();
//
//        Call<JsonObject> call= RetrofitClient.getInstance().getApi().checkMpinOrTPIN(AUTH_KEY,userid,deviceId,deviceInfo,"tpin",tpin);
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                if (response.isSuccessful())
//                {
//                    try {
//                        JSONObject responseObject=new JSONObject(String.valueOf(response.body()));
//                        String responseCode=responseObject.getString("statuscode");
//                        if (responseCode.equalsIgnoreCase("TXN"))
//                        {
//                            pDialog.dismiss();
//                            tpinDialog.dismiss();
//
//                            doRecharge();
//                        }
//                        else if (responseCode.equalsIgnoreCase("ERR"))
//                        {
//                            pDialog.dismiss();
////                            String transaction=responseObject.getString("data");
//                            showSnackbar("wrong Tpin");
//
//                        }
//                        else
//                        {
//                            pDialog.dismiss();
//                            showSnackbar("Something went wrong");
//                        }
//
//
//                    }
//                    catch (Exception e)
//                    {
//                        pDialog.dismiss();
//                        showSnackbar("Something went wrong");
//                    }
//                }
//                else
//                {
//                    pDialog.dismiss();
//                    showSnackbar("Something went wrong");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                pDialog.dismiss();
//                showSnackbar("Something went wrong");
//            }
//        });
//    }

    private void getService(final String serviceName) {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(QuickPayActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getServices(AUTH_KEY, deviceId, deviceInfo, userid);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;

                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));


                        String statuscode = jsonObject1.getString("statuscode");

                        if (statuscode.equalsIgnoreCase("TXN")) {

                            JSONArray jsonArray = jsonObject1.getJSONArray("data");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String serviceid = jsonObject.getString("ID");
                                String responseServiceName = jsonObject.getString("ServiceName");

                                if (responseServiceName.equalsIgnoreCase(serviceName)) {

                                    pDialog.dismiss();
                                    getOperators(serviceid);
                                    break;
                                }
                            }
                            pDialog.dismiss();
                        } else if (statuscode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();

                            String errorMessage = jsonObject1.getString("data");

                            new AlertDialog.Builder(QuickPayActivity.this).setCancelable(false)
                                    .setTitle("Alert")
                                    .setMessage(errorMessage)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        } else {
                            pDialog.dismiss();

                            new AlertDialog.Builder(QuickPayActivity.this).setCancelable(false)
                                    .setTitle("Alert")
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                        pDialog.dismiss();

                        new AlertDialog.Builder(QuickPayActivity.this).setCancelable(false)
                                .setTitle("Alert")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(QuickPayActivity.this).setCancelable(false)
                            .setTitle("Alert")
                            .setMessage("Something went wrong")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(QuickPayActivity.this).setCancelable(false)
                        .setTitle("Alert")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();

            }
        });

    }

    private void getOperators(String serviceid) {

        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(QuickPayActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getOperators(AUTH_KEY, deviceId, deviceInfo, userid, serviceid);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;
                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));

                        JSONArray jsonArray = jsonObject1.getJSONArray("data");

                         operatorsModelArrayList = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {

                            OperatorModel operatorModel = new OperatorModel();
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String operatorName = jsonObject.getString("OperatorName");

                            String operatorId = jsonObject.getString("ID");
                            String operatorImage = jsonObject.getString("OPImage");

                            operatorModel.setOperatorName(operatorName);
                            operatorModel.setOperatorId(operatorId);
                            operatorModel.setOperatorImg("http://login.accountpe.in" + operatorImage);

                            operatorsModelArrayList.add(operatorModel);

                        }

                        operatorDialog = new Dialog(QuickPayActivity.this, R.style.DialogTheme);
                        operatorDialog.setContentView(R.layout.operator_dialog);

                        int width = (int) (getResources().getDisplayMetrics().widthPixels * 1.0);
                        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.6);

                        operatorDialog.getWindow().setLayout(width, height);

                        operatorDialog.getWindow().setGravity(Gravity.BOTTOM);
                        operatorDialog.getWindow().setBackgroundDrawableResource(R.drawable.card_back_white);
                        operatorDialog.getWindow().setWindowAnimations(R.style.SlidingDialog);

                        RecyclerView rv = operatorDialog.findViewById(R.id.recyclerView);
                        ImageView cancelImg = operatorDialog.findViewById(R.id.cancelImg);
                        cancelImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                operatorDialog.dismiss();
                            }
                        });
                        OperatorAdapter recyclerViewItemAdapter = new OperatorAdapter(operatorsModelArrayList, QuickPayActivity.this);
                        rv.setLayoutManager(new LinearLayoutManager(QuickPayActivity.this, RecyclerView.VERTICAL, false));
                        rv.setAdapter(recyclerViewItemAdapter);

                        recyclerViewItemAdapter.setMyInterface(new OperatorInterface() {
                            @Override
                            public void operatorData(String operatorName, String operatorId, String operatorImage) {
                                operatorDialog.dismiss();
                                selectedOperatorId = operatorId;
                                selectedOperatorName = operatorName;
                                selectedOperatorImage = operatorImage;

                                    tvOperator.setText(operatorName);

                            }
                        });

                        pDialog.dismiss();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(QuickPayActivity.this).setTitle("Alert")
                                .setMessage("Something went wrong.")
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
                        e.printStackTrace();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(QuickPayActivity.this).setTitle("Alert")
                            .setMessage("Something went wrong.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(QuickPayActivity.this).setTitle("Failed")
                        .setCancelable(false)
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
        });

    }

    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        etRechargeNumber = findViewById(R.id.et_recharge_number);
        mobileOperatorContainer = findViewById(R.id.mobile_operator_container);
        tvOperator = findViewById(R.id.tv_operator);
        btnRecharge = findViewById(R.id.btn_recharge);
        etAmount = findViewById(R.id.et_amount);
        operatorLayout = findViewById(R.id.operator_layout);

    }

}