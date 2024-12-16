package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.cashfree.pg.CFPaymentService;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.payu.base.models.ErrorResponse;
import com.payu.base.models.PayUPaymentParams;
import com.payu.checkoutpro.PayUCheckoutPro;
import com.payu.checkoutpro.utils.PayUCheckoutProConstants;
import com.payu.ui.model.listeners.PayUCheckoutProListener;
import com.payu.ui.model.listeners.PayUHashGenerationListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.retrofit.RetrofitClient;

public class ElectricityActivity extends AppCompatActivity {

    private static final DecimalFormat df = new DecimalFormat("0.00");
    EditText etConsumerNumber, etMobileNo, etBillUnit;
    TextView tvOperator, tvDob;
    String selectedDob = "select";
    Button btnProceed, btnCancel;
    ImageView imgBack, imgBbps;
    TextView tvTitle;
    SharedPreferences sharedPreferences;
    Button btnPay;
    TextView tvCostumerName1, tvBillNumber1, tvBillAmount1, tvBilldate, tvBillPeriod, tvBillDueDate, tvWalletBalance;
    ConstraintLayout billPaymentLayout;
    String userid;
    String selectedOperatorId = "select";
    String operatorName, operatorId;
    ArrayList<String> operatorNameList, operatorIdList, idList;
    ArrayList<String> subdivisionNameList, subdivisionIdList;
    String billNumber, dueDate, billDate, customerName, amount,transactionId, requestId, billValidationId, billerInfo, inputInfo, additionalInfo;
    SpinnerDialog operatorDialog;
    String serviceType, serviceId;
    String deviceId, deviceInfo;
    String mobileNumber, strName, strEmail;
    AutoCompleteTextView autoSubdivision;
    String responseSubdivisionName = "Select Subdivision", responseSubdivisionID = "0";

    // redirect to payment gateway
    String status, number;
    TextView tvMainWallet, tvGatewayAmount, tvDebitFromGateway;
    LinearLayout gatewayLayout;
    CheckBox walletCheckbox, gatewayCheckbox;
    String walletBalance;
    String payVia = "santosh";
    String pgAmount = "";
    ///////////////////////////  // redirect to payment gateway

    SimpleDateFormat simpleDateFormat, webServiceDateFormat;
    String currentDate, currentDateApi;

    String  txnid ="txt12346",prodname ="Add_Money";
    String key="Sap6G7"; // given by anas

    String hashData,hashName;

    String saltKey = "KMa2w6oYQrtHgQXB5nByd6dHzwxFuK8C";  // given by anas

    PayUPaymentParams payUPaymentParams;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electricity);

        initViews();
        serviceType = getIntent().getStringExtra("service");
        serviceId = getIntent().getStringExtra("serviceId");
        walletBalance = getIntent().getStringExtra("walletBalance");

        tvTitle.setText(serviceType);

        if (serviceType.equalsIgnoreCase("Electricity")) {
            etConsumerNumber.setHint("Consumer Number");
            //  imgBbps.setImageResource(R.drawable.electrcity);
        } else if (serviceType.equalsIgnoreCase("Gas")) {
          //  etConsumerNumber.setHint("Subscriber Id");
            etConsumerNumber.setHint("Enter Registered MobileNo");
            // imgBbps.setImageResource(R.drawable.gas);
        } else if (serviceType.equalsIgnoreCase("Broadband")) {
            etConsumerNumber.setHint("Customer no.");
        } else if (serviceType.equalsIgnoreCase("INSURANCE")) {
            etConsumerNumber.setHint("Consumer Number");
            //   imgBbps.setImageResource(R.drawable.insurance);
            tvDob.setVisibility(View.VISIBLE);
        } else if (serviceType.equalsIgnoreCase("Loan Repayment")) {
            etConsumerNumber.setHint("Consumer Number");
            //   imgBbps.setImageResource(R.drawable.loan_new);
        } else if (serviceType.equalsIgnoreCase("Water")) {
            etConsumerNumber.setHint("Connection no.");
        } else if (serviceType.equalsIgnoreCase("LANDLINE")) {
            etConsumerNumber.setHint("Account no.");
        } else if (serviceType.equalsIgnoreCase("FASTAG")) {
            etConsumerNumber.setHint("Vehicle no.");
        } else {
            etConsumerNumber.setHint("number");
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ElectricityActivity.this);
        userid = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        mobileNumber = sharedPreferences.getString("mobileno", null);
        strName = sharedPreferences.getString("username", null);
        strEmail = sharedPreferences.getString("email", null);

        //  current date

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        webServiceDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        Calendar newDate1 = Calendar.getInstance();
        newDate1.set(year, month, day);

        currentDateApi = webServiceDateFormat.format(newDate1.getTime());
        currentDate = simpleDateFormat.format(newDate1.getTime());

        ///////////////////////////////////////

        imgBack.setOnClickListener(v -> finish());

        getOperators();

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        webServiceDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        tvDob.setOnClickListener(new View.OnClickListener() {

            final Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);

            @Override
            public void onClick(View v) {
                DatePickerDialog fromDatePicker = new DatePickerDialog(ElectricityActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newDate1 = Calendar.getInstance();
                        newDate1.set(year, month, dayOfMonth);

                        tvDob.setText(simpleDateFormat.format(newDate1.getTime()));

                        selectedDob = webServiceDateFormat.format(newDate1.getTime());

                    }
                }, year, month, day);
                fromDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                fromDatePicker.show();
            }
        });

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetState()) {
                    if (checkInputs()) {
                        //showTpinDialog();
                        //_________________________________________________

                        if (tvDob.getVisibility() != View.VISIBLE) {
                            selectedDob = "NA";
                            if (autoSubdivision.getVisibility() == View.GONE) {
                                fetchBill(selectedDob);
                            } else {
                                if (!responseSubdivisionID.equalsIgnoreCase("0")) {
                                    fetchBill(selectedDob);
                                } else {
                                    autoSubdivision.setError("Required");
                                }
                            }
                        }
                        else {
                            if (!selectedDob.equalsIgnoreCase("select")) {
                                fetchBill(selectedDob);
                            } else {
                                new AlertDialog.Builder(ElectricityActivity.this).setMessage("DOB Required").show();
                            }
                        }
                        //____________________________________________________

                        //  fetchBill();
                        //showTpinDialog();
                    } else {
                        showSnackBar("Above fields are mandatory.");
                    }
                } else {
                    showSnackBar("No Internet");
                }
            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (checkInternetState()) {
//                    payBill();
//                } else {
//                    showSnackbar("No Internet");
//                }
                showTpinDialog();
               /* if (walletCheckbox.isChecked() && gatewayCheckbox.isChecked()) {

                    payVia = "gateway+wallet";

                    double gatewayAmount = Double.parseDouble(amount) - Double.parseDouble(walletBalance);

                    //   String actualAmount = String.valueOf(gatewayAmount);
                    String actualAmount = df.format(gatewayAmount);
                    pgAmount = actualAmount;
                    startPaymentForPG(pgAmount);

                } else if (gatewayCheckbox.isChecked()) {

                    payVia = "gateway";
                    pgAmount = amount;
                    startPaymentForPG(pgAmount);


                } else if (walletCheckbox.isChecked()) {
                    if (checkInternetState()) {
                        payBill();
                    } else {
                        showSnackBar("No Internet");
                    }

                } else {
                    new AlertDialog.Builder(ElectricityActivity.this).setTitle("Alert").setMessage("Select Payment Through").setIcon(R.drawable.warning).setPositiveButton("OK", null).show();
                }*/

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void fetchBill(String dob) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setCancelable(false);
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.show();

        String consumerNumber = etConsumerNumber.getText().toString().trim();
        String billUnit = etBillUnit.getText().toString().trim();

        if (selectedOperatorId.equalsIgnoreCase("1363") && etMobileNo.getVisibility() == View.VISIBLE) {
            responseSubdivisionID = etMobileNo.getText().toString();
        }

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().fetchBill(AUTH_KEY, userid, deviceId, deviceInfo, serviceType, serviceId, selectedOperatorId, consumerNumber, mobileNumber, responseSubdivisionID, dob);
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {

                            JSONObject dataObject = responseObject.getJSONObject("data");

                            amount = dataObject.getString("billAmount");
                            dueDate = dataObject.getString("dueDate");
                            billDate = dataObject.getString("billDate");
                            customerName = dataObject.getString("consumerName");
                            billNumber = dataObject.getString("billNo");
                            requestId = dataObject.getString("requestid");
                            billValidationId = dataObject.getString("requestid");

                            if (billNumber.equalsIgnoreCase("NA") || billNumber.equalsIgnoreCase("N/A") || billNumber.equalsIgnoreCase("")) {
                                billNumber = consumerNumber;
                            }

                            try {
                                billerInfo = dataObject.getString("billerinfo");
                            } catch (Exception e) {
                                billerInfo = "NA";
                            }
                            try {
                                inputInfo = dataObject.getString("inputinfo");
                            } catch (Exception e) {
                                inputInfo = "NA";
                            }
                            try {
                                additionalInfo = dataObject.getString("additionalInfo");
                            } catch (Exception e) {
                                additionalInfo = "NA";
                            }

                            pDialog.dismiss();

                            showPaymentDialog(true, billNumber);

                        } else {
                            pDialog.dismiss();
                            billPaymentLayout.setVisibility(View.GONE);
                            String message = responseObject.getString("data");
                            new AlertDialog.Builder(ElectricityActivity.this).setCancelable(false).setTitle("Message").setMessage(message).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (message.contains("Bill fetch service not available")) {
                                        showPaymentDialog(false, consumerNumber);
                                    } else {
                                        dialog.dismiss();
                                        //  finish();
                                    }
                                }
                            }).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        billPaymentLayout.setVisibility(View.GONE);
                        new AlertDialog.Builder(ElectricityActivity.this).setCancelable(false).setTitle("Message").setMessage("Something went wrong\nPlease Try After Sometime").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //   finish();
                            }
                        }).show();
                    }
                } else {
                    pDialog.dismiss();
                    billPaymentLayout.setVisibility(View.GONE);
                    new AlertDialog.Builder(ElectricityActivity.this).setCancelable(false).setTitle("Message").setMessage("Something went wrong\nPlease Try After Sometime").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            // finish();
                        }
                    }).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                billPaymentLayout.setVisibility(View.GONE);
                new AlertDialog.Builder(ElectricityActivity.this).setCancelable(false).setTitle("Message").setMessage("Something went wrong\nPlease Try After Sometime").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //   finish();
                    }
                }).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void showPaymentDialog(boolean isBillFetch, String consumerNo) {
        final View view1 = LayoutInflater.from(ElectricityActivity.this).inflate(R.layout.electricity_bill_dialog_layout, null, false);
        final AlertDialog builder = new AlertDialog.Builder(ElectricityActivity.this).create();
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.setCancelable(false);
        builder.setView(view1);

        LinearLayout amountLayout = view1.findViewById(R.id.layout3);
        LinearLayout etBillAmountLayout = view1.findViewById(R.id.etBillAmount_layout);
        TextView tvCostumerName = view1.findViewById(R.id.tv_customer_name);
        final TextView tvBillDate = view1.findViewById(R.id.tv_bill_date);
        TextView tvBillAmount = view1.findViewById(R.id.tv_bill_amount);
        EditText etAmount = view1.findViewById(R.id.et_bill_amount);
        final TextView tvDueDate = view1.findViewById(R.id.tv_due_date);
        TextView tvCaNumber = view1.findViewById(R.id.tv_tel);
        Button btnProceedToPay = view1.findViewById(R.id.btn_proceed_to_pay);
        Button btnCancel = view1.findViewById(R.id.btn_cancel);

        if (isBillFetch) {
            if (serviceType.equalsIgnoreCase("FASTAG") || selectedOperatorId.equalsIgnoreCase("1362") || selectedOperatorId.equalsIgnoreCase("1360") || selectedOperatorId.equalsIgnoreCase("140") || selectedOperatorId.equalsIgnoreCase("1359")) {
                amountLayout.setVisibility(View.GONE);
                etBillAmountLayout.setVisibility(View.VISIBLE);
                tvCostumerName.setText(customerName);
                tvBillDate.setText(dueDate);
                etAmount.setText(amount);
                //  tvBillAmount.setText("₹ " + amount);
                amount = etAmount.getText().toString();
            } else {
                tvCostumerName.setText(customerName);
                tvBillDate.setText(dueDate);
                tvBillAmount.setText("₹ " + amount);
            }
            tvDueDate.setText(dueDate);
        } else {
            etBillAmountLayout.setVisibility(View.VISIBLE);
            amount = etAmount.getText().toString();

            amountLayout.setVisibility(View.GONE);
            tvBillDate.setText(currentDate);
            tvDueDate.setText(currentDate);

            dueDate = currentDate;
            requestId = "NA";
            customerName = strName;

        }
        tvCaNumber.setText(consumerNo);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                billPaymentLayout.setVisibility(View.GONE);

                builder.dismiss();
            }
        });

        btnProceedToPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etBillAmountLayout.getVisibility() == View.VISIBLE) {
                    amount = etAmount.getText().toString().trim();
                }
                billPaymentLayout.setVisibility(View.VISIBLE);
                tvBillAmount1.setText("₹ " + amount);
                tvBillNumber1.setText("Bill Number. " + consumerNo);
                tvCostumerName1.setText("Name:- " + customerName);
                //tvBillPeriod.setText("Bill Period:- " + billPeriod);
                tvBillDueDate.setText("Due Date:- " + dueDate);
                btnPay.setText("PAY ₹" + amount);

                tvMainWallet.setText("\u20b9" + walletBalance);

                if (Double.parseDouble(amount) <= Double.parseDouble(walletBalance)) {
                    tvDebitFromGateway.setVisibility(View.GONE);
                    gatewayLayout.setVisibility(View.GONE);
                    btnPay.setText("PAY \u20b9" + amount);

                } else {
                    tvDebitFromGateway.setVisibility(View.VISIBLE);
                    gatewayLayout.setVisibility(View.VISIBLE);
                    gatewayCheckbox.setChecked(true);
                }

                if (walletCheckbox.isChecked()) {
                    // tvGatewayAmount.setText(String.valueOf(Double.parseDouble(amount) - Double.parseDouble(walletBalance)));
                    tvGatewayAmount.setText(df.format(Double.parseDouble(amount) - Double.parseDouble(walletBalance)));

                    if (Double.parseDouble(amount) <= Double.parseDouble(walletBalance)) {
                        btnPay.setText("PAY \u20b9" + amount);
                    } else {
                        btnPay.setText("PAY \u20b9" + df.format(Double.parseDouble(amount) - Double.parseDouble(walletBalance)));
                        gatewayCheckbox.setClickable(false);
                    }

                } else {

                    if (Double.parseDouble(amount) <= Double.parseDouble(walletBalance)) {
                        btnPay.setText("PAY");
                    } else {
                        btnPay.setText("PAY \u20b9" + amount);
                    }

                    tvGatewayAmount.setText(amount);
                }

                walletCheckbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (walletCheckbox.isChecked()) {
//                                        tvGatewayAmount.setText(String.valueOf(Double.parseDouble(amount) - Double.parseDouble(walletBalance)));
                            tvGatewayAmount.setText(df.format(Double.parseDouble(amount) - Double.parseDouble(walletBalance)));

                            if (Double.parseDouble(amount) < Double.parseDouble(walletBalance)) {
                                btnPay.setText("PAY");
                            } else {
                                btnPay.setText("PAY \u20b9" + df.format(Double.parseDouble(amount) - Double.parseDouble(walletBalance)));
                            }

                        } else {
                            tvGatewayAmount.setText(amount);

                            if (Double.parseDouble(amount) <= Double.parseDouble(walletBalance)) {
                                btnPay.setText("PAY");
                            } else {
                                btnPay.setText("PAY\t \u20b9" + amount);
                            }
                        }
                    }
                });

                builder.dismiss();

            }
        });
        builder.show();
    }

    private void payBill() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();

        String billUnit = "";

        String consumerNumber = etConsumerNumber.getText().toString().trim();
        if (etBillUnit.getVisibility() == View.VISIBLE) {
            billUnit = etBillUnit.getText().toString().trim();
        } else {
            billUnit = "NA";
        }

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().payBill(AUTH_KEY, userid, deviceId, deviceInfo, serviceType, selectedOperatorId, consumerNumber, mobileNumber, customerName, amount, dueDate, serviceId, "NA", "NA", "NA", requestId, billUnit, billValidationId, billerInfo, inputInfo, additionalInfo, selectedDob);
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = responseObject.getString("statuscode");
                        if (responseCode.equalsIgnoreCase("TXN")) {

                            JSONArray transactionArray = responseObject.getJSONArray("data");

                            for (int i = 0; i < transactionArray.length(); i++) {
                                JSONObject transactionObject = transactionArray.getJSONObject(i);

                                String custId = transactionObject.getString("UserID");
                                String sType = transactionObject.getString("ServiceType");
                                number = transactionObject.getString("ConsumerNo");
                                amount = transactionObject.getString("Amount");
                                String comm = transactionObject.getString("Commission");
                                String surcharge = transactionObject.getString("Surcharge");
                                String cost = transactionObject.getString("PayableAmount");
                                String balance = transactionObject.getString("ClosingBalance");
                                String tDateTime = transactionObject.getString("CreateDate");
                                status = transactionObject.getString("Status");    //  for online pay
                                // String status = transactionObject.getString("offlinestatus");  // for offline pay
                                String transactionId = transactionObject.getString("TransactionID");
                                String brId = transactionObject.getString("UniqueID");
                                String opName = transactionObject.getString("OperatorName");

                            }
                            final View view1 = LayoutInflater.from(ElectricityActivity.this).inflate(R.layout.electricity_bill_response_dialog, null, false);
                            final AlertDialog builder = new AlertDialog.Builder(ElectricityActivity.this).create();
                            builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            builder.setCancelable(false);
                            builder.setView(view1);

                            TextView tvStatus = view1.findViewById(R.id.tv_status);
                            TextView tvTel = view1.findViewById(R.id.tv_tel);
                            TextView tvBillAmount = view1.findViewById(R.id.tv_bill_amount);
                            Button btnOk = view1.findViewById(R.id.btn_ok);

                            tvStatus.setText(status);
                            tvTel.setText(consumerNumber);
                            tvBillAmount.setText("₹ " + amount);

                            btnOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    billPaymentLayout.setVisibility(View.GONE);
                                    builder.dismiss();
                                    //  finish();
                                }
                            });

                            builder.show();
                            pDialog.dismiss();
                        } else if (responseCode.equalsIgnoreCase("ERR")) {
                            String status = responseObject.getString("data");

                            final View view1 = LayoutInflater.from(ElectricityActivity.this).inflate(R.layout.electricity_bill_response_dialog, null, false);
                            final AlertDialog builder = new AlertDialog.Builder(ElectricityActivity.this).create();
                            builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            builder.setCancelable(false);
                            builder.setView(view1);

                            TextView tvStatus = view1.findViewById(R.id.tv_status);
                            TextView tvTel = view1.findViewById(R.id.tv_tel);
                            TextView tvBillAmount = view1.findViewById(R.id.tv_bill_amount);
                            Button btnOk = view1.findViewById(R.id.btn_ok);

                            tvStatus.setText(status);
                            tvTel.setText(billNumber);
                            tvBillAmount.setText("₹ " + amount);

                            btnOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    billPaymentLayout.setVisibility(View.GONE);
                                    builder.dismiss();
                                    //  finish();
                                }
                            });

                            builder.show();
                            pDialog.dismiss();
                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(ElectricityActivity.this).setTitle("Message!!!").setCancelable(false).setMessage("Something went wrong.").setPositiveButton("Ok", null).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(ElectricityActivity.this).setTitle("Message!!!").setCancelable(false).setMessage("Something went wrong.").setPositiveButton("Ok", null).show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(ElectricityActivity.this).setTitle("Message!!!").setCancelable(false).setMessage("Something went wrong.").setPositiveButton("Ok", null).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(ElectricityActivity.this).setTitle("Message!!!").setCancelable(false).setMessage("Something went wrong.").setPositiveButton("Ok", null).show();
            }
        });
    }

    private void payBillViaGateway(String gatewayAmount, String pgResponseAmount, String orderId) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();

        String consumerNumber = etConsumerNumber.getText().toString().trim();
        String billUnit = etBillUnit.getText().toString().trim();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().payBillViaGateway(AUTH_KEY, userid, deviceId, deviceInfo, serviceType, selectedOperatorId, consumerNumber, mobileNumber, customerName, amount, dueDate, requestId, responseSubdivisionID, serviceId, payVia, walletBalance, gatewayAmount, pgResponseAmount, orderId);
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = responseObject.getString("statuscode");
                        if (responseCode.equalsIgnoreCase("TXN")) {

                            JSONArray transactionArray = responseObject.getJSONArray("data");

                            for (int i = 0; i < transactionArray.length(); i++) {
                                JSONObject transactionObject = transactionArray.getJSONObject(i);

                                String custId = transactionObject.getString("UserID");
                                String sType = transactionObject.getString("ServiceType");
                                number = transactionObject.getString("ConsumerNo");
                                amount = transactionObject.getString("Amount");
                                String comm = transactionObject.getString("Commission");
                                String surcharge = transactionObject.getString("Surcharge");
                                String cost = transactionObject.getString("PayableAmount");
                                String balance = transactionObject.getString("ClosingBalance");
                                String tDateTime = transactionObject.getString("CreateDate");
                                status = transactionObject.getString("Status");    //  for online pay
                                // String status = transactionObject.getString("offlinestatus");  // for offline pay
                                String transactionId = transactionObject.getString("TransactionID");
                                String brId = transactionObject.getString("UniqueID");
                                String opName = transactionObject.getString("OperatorName");

                            }

                            final View view1 = LayoutInflater.from(ElectricityActivity.this).inflate(R.layout.electricity_bill_response_dialog, null, false);
                            final AlertDialog builder = new AlertDialog.Builder(ElectricityActivity.this).create();
                            builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            builder.setCancelable(false);
                            builder.setView(view1);

                            TextView tvStatus = view1.findViewById(R.id.tv_status);
                            TextView tvTel = view1.findViewById(R.id.tv_tel);
                            TextView tvBillAmount = view1.findViewById(R.id.tv_bill_amount);
                            Button btnOk = view1.findViewById(R.id.btn_ok);

                            tvStatus.setText(status);
                            tvTel.setText(billNumber);
                            tvBillAmount.setText("₹ " + amount);

                            btnOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    billPaymentLayout.setVisibility(View.GONE);
                                    builder.dismiss();
                                    //   finish();
                                }
                            });

                            builder.show();
                            pDialog.dismiss();
                        } else if (responseCode.equalsIgnoreCase("ERR")) {
                            String status = responseObject.getString("data");

                            final View view1 = LayoutInflater.from(ElectricityActivity.this).inflate(R.layout.electricity_bill_response_dialog, null, false);
                            final AlertDialog builder = new AlertDialog.Builder(ElectricityActivity.this).create();
                            builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            builder.setCancelable(false);
                            builder.setView(view1);

                            TextView tvStatus = view1.findViewById(R.id.tv_status);
                            TextView tvTel = view1.findViewById(R.id.tv_tel);
                            TextView tvBillAmount = view1.findViewById(R.id.tv_bill_amount);
                            Button btnOk = view1.findViewById(R.id.btn_ok);

                            tvStatus.setText(status);
                            tvTel.setText(billNumber);
                            tvBillAmount.setText("₹ " + amount);

                            btnOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    billPaymentLayout.setVisibility(View.GONE);
                                    builder.dismiss();
                                    //  finish();
                                }
                            });

                            builder.show();
                            pDialog.dismiss();
                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(ElectricityActivity.this).setTitle("Message!!!").setCancelable(false).setMessage("Something went wrong.").setPositiveButton("Ok", null).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(ElectricityActivity.this).setTitle("Message!!!").setCancelable(false).setMessage("Something went wrong.").setPositiveButton("Ok", null).show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(ElectricityActivity.this).setTitle("Message!!!").setCancelable(false).setMessage("Something went wrong.").setPositiveButton("Ok", null).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(ElectricityActivity.this).setTitle("Message!!!").setCancelable(false).setMessage("Something went wrong.").setPositiveButton("Ok", null).show();
            }
        });
    }

    private void getOperators() {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getOperators(AUTH_KEY, deviceId, deviceInfo, userid, serviceId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseJsonObject = new JSONObject(String.valueOf(response.body()));

                        operatorIdList = new ArrayList<>();
                        operatorNameList = new ArrayList<>();
                        idList = new ArrayList<>();

                        JSONArray dataArray = responseJsonObject.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject dataObject = dataArray.getJSONObject(i);

                            operatorName = dataObject.getString("OperatorName");
                            operatorNameList.add(operatorName);
                            operatorId = dataObject.getString("ID");
                            operatorIdList.add(operatorId);

                        }

                        tvOperator.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                operatorDialog = new SpinnerDialog(ElectricityActivity.this, operatorNameList, "Select Operator", R.style.DialogAnimations_SmileWindow, "Close  ");// With 	Animation
                                operatorDialog.setCancellable(true); // for cancellable
                                operatorDialog.setShowKeyboard(false);// for open keyboard by default
                                operatorDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                                    @Override
                                    public void onClick(String item, int position) {
                                        tvOperator.setText(item);
                                        selectedOperatorId = operatorIdList.get(position);
                                        //   Toast.makeText(ElectricityActivity.this, selectedOperatorId, Toast.LENGTH_SHORT).show();
                                        if (selectedOperatorId.equalsIgnoreCase("78")) {
                                            etBillUnit.setVisibility(View.VISIBLE);
                                        } else {
                                            etBillUnit.setVisibility(View.GONE);
                                        }

                                        if (selectedOperatorId.equalsIgnoreCase("60")) {
                                            autoSubdivision.setVisibility(View.VISIBLE);
                                            getSubdivisionCode();
                                        } else {
                                            autoSubdivision.setVisibility(View.GONE);
                                        }
                                        if (selectedOperatorId.equalsIgnoreCase("1363")) {
                                            etMobileNo.setVisibility(View.VISIBLE);
                                        }

                                    }
                                });

                                operatorDialog.showSpinerDialog();
                            }
                        });

                        pDialog.dismiss();

                    } catch (JSONException e) {
                        pDialog.dismiss();

                        new AlertDialog.Builder(ElectricityActivity.this).setCancelable(false).setTitle("Alert").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
                        e.printStackTrace();
                    }
                } else {
                    pDialog.dismiss();

                    new AlertDialog.Builder(ElectricityActivity.this).setCancelable(false).setTitle("Alert").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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

                new AlertDialog.Builder(ElectricityActivity.this).setCancelable(false).setTitle("Alert").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
            }
        });
    }

    private void getSubdivisionCode() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getSubDivisionCode(AUTH_KEY, userid, deviceId, deviceInfo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;
                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));

                        String responseCode = jsonObject1.getString("statuscode");
                        if (responseCode.equalsIgnoreCase("TXN")) {

                            JSONArray jsonArray = jsonObject1.getJSONArray("data");
                            subdivisionNameList = new ArrayList<>();
                            subdivisionIdList = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String subDivisionName = jsonObject.getString("SubDivisionName");
                                String subDivisionID = jsonObject.getString("SubDivisionCode");

                                subdivisionNameList.add(subDivisionName);
                                subdivisionIdList.add(subDivisionID);

                            }

                            ArrayAdapter<String> subdivisionAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, subdivisionNameList);

                            autoSubdivision.setAdapter(subdivisionAdapter);
                            autoSubdivision.setThreshold(1);
                            autoSubdivision.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    autoSubdivision.showDropDown();
                                }
                            });
                            autoSubdivision.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    int index = subdivisionNameList.indexOf(autoSubdivision.getText().toString());
                                    responseSubdivisionName = subdivisionNameList.get(index);
                                    responseSubdivisionID = subdivisionIdList.get(index);
                                    // Toast.makeText(ViewBillActivity.this, responseOperatorId, Toast.LENGTH_SHORT).show();
                                }
                            });

                            pDialog.dismiss();
                        } else if (responseCode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();
                            new AlertDialog.Builder(ElectricityActivity.this).setTitle(jsonObject1.getString("status")).setMessage(jsonObject1.getString("data")).setCancelable(false).setIcon(R.drawable.failureicon).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).show();
                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(ElectricityActivity.this).setTitle("Alert").setMessage("Something went wrong.").setCancelable(false).setIcon(R.drawable.warning).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(ElectricityActivity.this).setTitle("Alert").setMessage("Something went wrong.").setCancelable(false).setIcon(R.drawable.warning).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(ElectricityActivity.this).setTitle("Alert").setMessage("Something went wrong.").setCancelable(false).setIcon(R.drawable.warning).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
                new AlertDialog.Builder(ElectricityActivity.this).setTitle("Alert").setCancelable(false).setMessage(t.getMessage()).setIcon(R.drawable.warning).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
            }
        });

    }

    private void initViews() {
        billPaymentLayout = findViewById(R.id.bill_payment_layout);
        tvCostumerName1 = findViewById(R.id.tv_customer_name1);
        tvBillNumber1 = findViewById(R.id.tv_bill_number1);
        tvBillAmount1 = findViewById(R.id.tv_bill_amount1);
        btnPay = findViewById(R.id.btn_pay);
        etConsumerNumber = findViewById(R.id.et_consumer_number);
        etMobileNo = findViewById(R.id.et_mobileNo);
        etBillUnit = findViewById(R.id.et_bill_unit);
        tvOperator = findViewById(R.id.tv_operator_name);
        tvDob = findViewById(R.id.tv_dob);
        autoSubdivision = findViewById(R.id.viewBill_auto_subdivisionCode);
        btnProceed = findViewById(R.id.btn_proceed);
        btnCancel = findViewById(R.id.btn_cancel);
        tvTitle = findViewById(R.id.activity_title);
        tvBilldate = findViewById(R.id.tv_bill_date);
        tvBillPeriod = findViewById(R.id.tv_bill_period);
        tvBillDueDate = findViewById(R.id.tv_bill_due_date);
        tvWalletBalance = findViewById(R.id.tv_balance);
        imgBack = findViewById(R.id.back_button);
        imgBbps = findViewById(R.id.img_bbps);

        tvMainWallet = findViewById(R.id.textWallet);
        tvGatewayAmount = findViewById(R.id.textGateway);
        tvDebitFromGateway = findViewById(R.id.tvDebitFromGateway);
        gatewayLayout = findViewById(R.id.layout_gateway);
        walletCheckbox = findViewById(R.id.checkbox_wallet);
        gatewayCheckbox = findViewById(R.id.checkbox_gateway);
    }

   /* private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.electricity_layout), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }*/
   private void showSnackBar(String message) {
       Snackbar snackbar = Snackbar.make(findViewById(R.id.electricity_layout), message, Snackbar.LENGTH_LONG);
       snackbar.setBackgroundTint(getResources().getColor(R.color.red));
       snackbar.setTextColor(getResources().getColor(R.color.white));
       snackbar.show();
   }


    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (manager != null) {
            networkInfo = manager.getActiveNetworkInfo();
        }

        if (networkInfo != null) {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    private boolean checkInputs() {
        return !TextUtils.isEmpty(etConsumerNumber.getText()) && !selectedOperatorId.equalsIgnoreCase("select");
    }
    //_________________________________tPIN_Dialog_Box______________________________________________
       @SuppressLint("SetTextI18n")
        private void showTpinDialog() {
            View addSenderDialogView = getLayoutInflater().inflate(R.layout.add_sender_otp_dialog_layout, null, false);
            final AlertDialog addSenderDialog = new AlertDialog.Builder(ElectricityActivity.this).create();
            addSenderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            addSenderDialog.setCancelable(false);
            addSenderDialog.setView(addSenderDialogView);
            addSenderDialog.show();

            ImageView imgClose = addSenderDialogView.findViewById(R.id.img_close);
            final EditText etTpin = addSenderDialogView.findViewById(R.id.et_otp);
            Button btnCancel = addSenderDialogView.findViewById(R.id.btn_cancel);
            Button btnSubmit = addSenderDialogView.findViewById(R.id.btn_submit);
            Button btnResendOtp = addSenderDialogView.findViewById(R.id.btn_resend_otp);

            btnResendOtp.setVisibility(View.GONE);

            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addSenderDialog.dismiss();
                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addSenderDialog.dismiss();
                }
            });
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(etTpin.getText())) {
                        String tpin = etTpin.getText().toString().trim();
                        checkTpin(tpin);
                        addSenderDialog.dismiss();
                    } else {
                        new AlertDialog.Builder(ElectricityActivity.this)
                                .setMessage("Something went wrong.")
                                .show();
                    }
                }
            });

        }
        private void checkTpin(String tpin) {
            final ProgressDialog pDialog = new ProgressDialog(this, R.style.MyTheme);
            pDialog.setMessage("Loading....");
            pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
            pDialog.setCancelable(false);
            pDialog.show();

            Call<JsonObject> call = RetrofitClient.getInstance().getApi().checkMpinOrTPIN(RetrofitClient.AUTH_KEY, userid, deviceId, deviceInfo, "tpin", tpin);

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        pDialog.dismiss();
                        try {
                            JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                            String responseCode = responseObject.getString("statuscode");

                            if (responseCode.equalsIgnoreCase("TXN")) {
//____________________________________________________________________________________________
                                if (walletCheckbox.isChecked() && gatewayCheckbox.isChecked()) {

                                    payVia = "gateway+wallet";

                                    double gatewayAmount = Double.parseDouble(amount) - Double.parseDouble(walletBalance);

                                    //   String actualAmount = String.valueOf(gatewayAmount);
                                    String actualAmount = df.format(gatewayAmount);
                                    pgAmount = actualAmount;
                                    startPaymentForPG(pgAmount);

                                } else if (gatewayCheckbox.isChecked()) {

                                    payVia = "gateway";
                                    pgAmount = amount;
                                    startPaymentForPG(pgAmount);
                                } else if (walletCheckbox.isChecked()) {
                                    if (checkInternetState()) {
                                        payBill();
                                    } else {
                                        showSnackBar("No Internet");
                                    }
                                } else {
                                    new AlertDialog.Builder(ElectricityActivity.this).setTitle("Alert").setMessage("Select Payment Through").setIcon(R.drawable.warning).setPositiveButton("OK", null).show();
                                }

//____________________________________________________________________________________________

                            } else {
                                pDialog.dismiss();
                                String transaction = responseObject.getString("status");
                                showSnackBar(transaction);

                            }

                        } catch (Exception e) {
                            pDialog.dismiss();
                            showSnackBar("Something went wrong");
                        }
                    } else {
                        pDialog.dismiss();
                        showSnackBar("Something went wrong");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    pDialog.dismiss();
                    showSnackBar("Something went wrong");
                }
            });
        }
    //_________________________________tPIN_Dialog_Box_End__________________________________________

    // ________________________________cashfree payment Gateway_____________________________________
    public void startPaymentForPG(String pgAmount) {
        String orderID = generateUniqueId();
        txnid = generateUniqueId();


        if (checkInternetState()) {
           // getToken(orderID, pgAmount);
            inserDataPayU(txnid,pgAmount); // kalpesh
        } else {
            showSnackBar("No Internet");
        }

    }

    private void inserDataPayU(String transactionId, String amount) {
        ProgressDialog progressDialog = new ProgressDialog(ElectricityActivity.this);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().generateHash(RetrofitClient.AUTH_KEY,userid,deviceId,deviceInfo,amount,transactionId,strName,strEmail,mobileNumber,"NA");

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
                .setPhone(mobileNumber)
                .setTransactionId(txnid)
                .setFirstName(strName)
                .setEmail(strEmail)
                .setSurl("http://api.accountpe.in/Api/WebhookForPayUpg")
                .setFurl("http://api.accountpe.in/Api/WebhookForPayUpg");

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

                        final View view1 = LayoutInflater.from(ElectricityActivity.this).inflate(R.layout.payu_response_dialog, null, false);
                        final androidx.appcompat.app.AlertDialog builder = new androidx.appcompat.app.AlertDialog.Builder(ElectricityActivity.this).create();
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

                                // updateAddMoneyPayU(transactionId,status);
                               // doRechargeViaGateway(pgAmount,pgAmount,txnid);
                                payBillViaGateway(pgAmount, payuResponse, txnid);

                            }
                        });

                        builder.show();

                    }
                /*    @Override
                    public void onPaymentFailure(Object response) {
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


                        final View view1 = LayoutInflater.from(ElectricityActivity.this).inflate(R.layout.payu_response_dialog, null, false);
                        final androidx.appcompat.app.AlertDialog builder = new androidx.appcompat.app.AlertDialog.Builder(ElectricityActivity.this).create();
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

                                // updateAddMoneyPayU(transactionId,status);

                              //  doRechargeViaGateway(pgAmount,pgAmount,txnid);

                             //   payBillViaGateway(pgAmount, payuResponse, txnid);



                            }

                        });

                        builder.show();


                        ///
                    }*/

                    public void onPaymentFailure(Object response) {
                        HashMap<String, Object> result = (HashMap<String, Object>) response;
                        String payuResponse = (String) result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                        String merchantResponse = (String) result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);

                        try {
                            JSONObject jsonObject = new JSONObject(payuResponse);

                            // Check if "result" exists and extract data, otherwise set default values
                            if (jsonObject.has("result")) {
                                JSONObject data = jsonObject.getJSONObject("result");

                                transactionId = data.has("txnid") ? data.getString("txnid") : "N/A";
                                amount = data.has("amount") ? data.getString("amount") : "0.00";
                                status = data.has("status") ? data.getString("status") : "ye filed hai";

                            } else {
                                // If the response doesn't have "result", set default values
                                transactionId = jsonObject.has("txnid") ? jsonObject.getString("txnid") : "N/A";
                                amount = jsonObject.has("amount") ? jsonObject.getString("amount") : "0.00";
                                status = jsonObject.has("status") ? jsonObject.getString("status") : "ye else se filed hai";
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // In case of JSON parsing error, set default values
                            transactionId = "N/A";
                            amount = "0.00";
                            status = "Failed";
                        }

                        // Show the dialog with retrieved or default values
                        final View view1 = LayoutInflater.from(ElectricityActivity.this).inflate(R.layout.payu_response_dialog, null, false);
                        final androidx.appcompat.app.AlertDialog builder = new androidx.appcompat.app.AlertDialog.Builder(ElectricityActivity.this).create();
                        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        builder.setCancelable(false);
                        builder.setView(view1);

                        TextView tvStatus = view1.findViewById(R.id.tv_status);
                        TextView tvTel = view1.findViewById(R.id.tv_txnid);
                        TextView tvBillAmount = view1.findViewById(R.id.tv_amount);
                        Button btnOk = view1.findViewById(R.id.btn_ok);

                        // Set values in the dialog
                        tvStatus.setText(status != null ? status : "Unknown");
                        tvTel.setText(transactionId != null ? transactionId : "N/A");
                        tvBillAmount.setText("₹ " + (amount != null ? amount : "0.00"));

                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                builder.dismiss();
                                // You can add further actions here
                            }
                        });

                        builder.show();
                    }
                    @Override
                    public void onPaymentCancel(boolean isTxnInitiated) {

                    }
                    @Override
                    public void onError(ErrorResponse errorResponse) {
                        String errorMessage = errorResponse.getErrorMessage();
                        // Toast.makeText(AddMoneyPayuMoneyActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        new android.app.AlertDialog.Builder(ElectricityActivity.this)
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

                                Toast.makeText(ElectricityActivity.this, "Hash Value is Empty", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            Toast.makeText(ElectricityActivity.this, "HashData or HashName is Empty", Toast.LENGTH_SHORT).show();

                        }
                    }
                }
        );
        //////////////////////////
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

                        if (statusCode.equalsIgnoreCase("TXN")) {
                            JSONObject dataObject = jsonObject.getJSONObject("data");
                            String token = dataObject.getString("cftoken");

                            insertDataIntoApi(orderID, amount, token);
                        } else {
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
    private void insertDataIntoApi(final String orderID, final String amount, String token) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().insertData(AUTH_KEY, userid, deviceId, deviceInfo, orderID, amount);
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));

                        String statusCode = jsonObject.getString("statuscode");

                        if (statusCode.equalsIgnoreCase("TXN")) {
                            insertInfo(amount, orderID, token);
                        } else {
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
        hashMap.put(CFPaymentService.PARAM_CUSTOMER_PHONE, mobileNumber);
//        hashMap.put(CFPaymentService.PARAM_NOTIFY_URL, "https://api.accountpe.in/api/cashfreepgwebhook");
        hashMap.put(CFPaymentService.PARAM_NOTIFY_URL, "https://api.accountpe.in/api/cashfree");         // for not receive call back from cashfree

        //   CFPaymentService.getCFPaymentServiceInstance().doPayment(this, hashMap, token, "PROD", "#D60006", "#ffffff", true);
        CFPaymentService.getCFPaymentServiceInstance().upiPayment(this, hashMap, token, "PROD");
    }

    private void UpdateCashfreeAddMoneyWebhook(String uniqueId, String status, String txnId) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().UpdateCashfreeAddMoneyWebhook(AUTH_KEY, userid, deviceId, deviceInfo, uniqueId, status, txnId, "Cashfree Response");
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));

                        String statusCode = jsonObject.getString("statuscode");

                        if (statusCode.equalsIgnoreCase("TXN")) {

                        } else {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CFPaymentService.REQ_CODE && data != null) {
            Bundle bundle = data.getExtras();

            if (bundle != null) {

                String status = bundle.getString("txStatus");
                String orderId = bundle.getString("orderId");

                Toast.makeText(this, status, Toast.LENGTH_SHORT).show();


                if (status.equalsIgnoreCase("SUCCESS")) {

                    Toast.makeText(this, "Payment success", Toast.LENGTH_SHORT).show();

                    String pgResponseAmount = bundle.getString("orderAmount");
                    payBillViaGateway(pgAmount, pgResponseAmount, orderId);

                } else {
                    UpdateCashfreeAddMoneyWebhook(orderId, status, orderId);

                }

            }


        } else {
            Toast.makeText(this, "Payment not found", Toast.LENGTH_LONG).show();
        }


    }

    private String generateUniqueId() {

        long timeStamp = System.currentTimeMillis();
        String timeStampStr = String.valueOf(timeStamp);
        int length = timeStampStr.length();
        int subLength = length - 6;
        timeStampStr = timeStampStr.substring(subLength, length);

        String subMobileNo = mobileNumber.substring(0, 6);

        return "UNID" + subMobileNo + "_" + timeStampStr;

    }
//////////////////////////////////////////////////////////////////////////
    private void errorDialog(String message) {
        new android.app.AlertDialog.Builder(ElectricityActivity.this)
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