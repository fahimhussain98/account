package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.BuildConfig;
import wts.com.accountpe.R;
import wts.com.accountpe.retrofit.RetrofitClient;

public class InstantPaySettlementActivity extends AppCompatActivity {

    Spinner spinnerPaymentType, spinnerAccountType, spinnerTransactionType,spinnerBankName;
    ArrayList<String> paymentTypeList, accountTypeList, transactionTypeList,bankNameList,accountHolderNameList,ifscCodeList, accountNumberList;
    String selectedPaymentType = "select", selectedTransactionType = "select", selectedAccountType = "select", userId, userName,selectedBankName="select";
    ConstraintLayout bankDetailsContainer;
    boolean isWallet = true;
    ImageView imgBack;
    EditText etAmount, etAccountHolderName,  etAccountNumber, etIfscNumber;
    Button btnSubmit,btnAddMoreBanks;
    SharedPreferences sharedPreferences;
    String amount, selectedAccountHolderName, bankName, selectedAccountNumber, selectedIfscNumber,  panCard, mobileNo;
    SimpleDateFormat webServiceDateFormat;
    String currentDate;
    String deviceId, deviceInfo;
    String versionName;

    //////////////////////////////////////////////LOCATION
    String pan;
    int LOCATION_PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    String lat = "0.0", longi = "0.0";
    //////////////////////////////////////////////LOCATION

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instant_pay_settlement);

        initViews();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(InstantPaySettlementActivity.this);

        versionName= BuildConfig.VERSION_NAME;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(InstantPaySettlementActivity.this);
        userId = sharedPreferences.getString("userid", null);
        panCard = sharedPreferences.getString("pancard", null);
        mobileNo = sharedPreferences.getString("mobileno", null);
        userName = sharedPreferences.getString("username", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        webServiceDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        setViews();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Calendar newDate1 = Calendar.getInstance();
        newDate1.set(year, month, day);
        currentDate = webServiceDateFormat.format(newDate1.getTime());

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetState()) {
                    if (checkInputs()) {
                        //doSettlement();
                        getLastLocation();
                    } else {
                        showSnackBar("Select Above Details");
                    }
                } else {
                    showSnackBar("No Internet");
                }
            }
        });

        btnAddMoreBanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //   startActivity(new Intent(SettlementActivity.this,AddBankDetailActivity.class));
                startActivity(new Intent(InstantPaySettlementActivity.this, InstantPayAddBankDetailActivity.class));
            }
        });


    }

    @SuppressLint("SetTextI18n")
    private void showTpinDialog() {
        View addSenderDialogView = getLayoutInflater().inflate(R.layout.add_sender_otp_dialog_layout, null, false);
        final androidx.appcompat.app.AlertDialog addSenderDialog = new androidx.appcompat.app.AlertDialog.Builder(InstantPaySettlementActivity.this).create();
        addSenderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addSenderDialog.setCancelable(false);
        addSenderDialog.setView(addSenderDialogView);
        addSenderDialog.show();

        ImageView imgClose = addSenderDialogView.findViewById(R.id.img_close);
        final EditText etTpin = addSenderDialogView.findViewById(R.id.et_otp);
        Button btnCancel = addSenderDialogView.findViewById(R.id.btn_cancel);
        Button btnSubmit = addSenderDialogView.findViewById(R.id.btn_submit);
        Button btnResendOtp=addSenderDialogView.findViewById(R.id.btn_resend_otp);

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

                    etTpin.setError("Required");
                }
            }
        });

    }

    private void checkTpin(String tpin) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();


        Call<JsonObject> call= RetrofitClient.getInstance().getApi().checkMpinOrTPIN(AUTH_KEY,userId,deviceId,deviceInfo
                ,"tpin",tpin);
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
                            pDialog.dismiss();
                            doSettlement();
                        }
                        else
                        {
                            pDialog.dismiss();
                            String transaction=responseObject.getString("status");
                            showSnackBar(transaction);
                        }

                    }
                    catch (Exception e)
                    {
                        pDialog.dismiss();
                        showSnackBar("Something went wrong");
                    }
                }
                else
                {
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

    private void getAccountDetails() {
        final AlertDialog pDialog = new AlertDialog.Builder(InstantPaySettlementActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call=RetrofitClient.getInstance().getApi().getAccountDetails(AUTH_KEY,userId,deviceId,deviceInfo);
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
                            bankNameList=new ArrayList<>();
                            accountHolderNameList=new ArrayList<>();
                            accountNumberList =new ArrayList<>();
                            ifscCodeList=new ArrayList<>();

                            JSONArray dataArray=responseObject.getJSONArray("data");
                            for (int i=0;i<dataArray.length();i++)
                            {
                                JSONObject dataObject=dataArray.getJSONObject(i);
                                String bankName=dataObject.getString("BankName");
                                String accountHolderName=dataObject.getString("AccountHolderName");
                                String accountNo=dataObject.getString("AccountNo");
                                String ifscCode=dataObject.getString("IfscCode");

                                bankNameList.add(bankName);
                                accountHolderNameList.add(accountHolderName);
                                accountNumberList.add(accountNo);
                                ifscCodeList.add(ifscCode);

                            }

                            if (bankNameList.size()<15)
                            {
                                btnAddMoreBanks.setVisibility(View.VISIBLE);
                            }

                            HintSpinner<String> hintSpinner1 = new HintSpinner<>(spinnerBankName, new HintAdapter<String>(InstantPaySettlementActivity.this, "Bank Name", bankNameList),
                                    new HintSpinner.Callback<String>() {
                                        @Override
                                        public void onItemSelected(int position, String itemAtPosition) {
                                            selectedBankName = bankNameList.get(position);
                                            selectedAccountHolderName = accountHolderNameList.get(position);
                                            selectedAccountNumber = accountNumberList.get(position);
                                            selectedIfscNumber = ifscCodeList.get(position);

                                            etAccountHolderName.setText(selectedAccountHolderName);
                                            etAccountNumber.setText(selectedAccountNumber);
                                            etIfscNumber.setText(selectedIfscNumber);

                                            etAccountHolderName.setEnabled(false);
                                            etAccountNumber.setEnabled(false);
                                            etIfscNumber.setEnabled(false);
                                        }
                                    });
                            hintSpinner1.init();
                            pDialog.dismiss();
                        }
                        else
                        {
                            pDialog.dismiss();
                            new AlertDialog.Builder(InstantPaySettlementActivity.this).setTitle("Alert")
                                    .setMessage("Please add Bank Details first.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            startActivity(new Intent(InstantPaySettlementActivity.this,AddBankDetailActivity.class));
                                            finish();
                                        }
                                    })
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(InstantPaySettlementActivity.this).setTitle("Alert")
                                .setMessage("Something went wrong.")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                })
                                .show();
                    }
                }
                else
                {
                    pDialog.dismiss();
                    new AlertDialog.Builder(InstantPaySettlementActivity.this).setTitle("Alert")
                            .setMessage("Something went wrong.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(InstantPaySettlementActivity.this).setTitle("Alert")
                        .setMessage("Something went wrong.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .show();
            }
        });
    }

    private void doSettlement() {
        final AlertDialog pDialog = new AlertDialog.Builder(InstantPaySettlementActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        if (selectedTransactionType.equalsIgnoreCase("IMPS"))
            selectedTransactionType = "IMPS";
        else if (selectedTransactionType.equalsIgnoreCase("RTGS"))
            selectedTransactionType = "RGS";
        else if (selectedTransactionType.equalsIgnoreCase("NEFT"))
            selectedTransactionType = "NEFT";

//        if (selectedPaymentType.equalsIgnoreCase("WALLET"))
//            selectedPaymentType = "2";
//        else
//            selectedPaymentType = "1";

        selectedPaymentType = "1";  // use this for transfer payout in bank


        Call<JsonObject> call = RetrofitClient.getInstance().getApiPlans().instantPayMoveToBank(AUTH_KEY, userId, deviceId, deviceInfo, selectedPaymentType, amount, selectedAccountNumber,
                selectedIfscNumber, selectedAccountType, selectedTransactionType, selectedAccountHolderName,"6498", lat, longi, selectedBankName);    // for instantPay Payout
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");
                        if (responseCode.equalsIgnoreCase("TXN")) {
                            pDialog.dismiss();

                            String status = responseObject.getString("data");
                            new AlertDialog.Builder(InstantPaySettlementActivity.this)
                                    .setTitle("Transaction Status")
                                    .setMessage(status)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();

//                            JSONArray dataArray = responseObject.getJSONArray("data");
//                            JSONObject dataObject = dataArray.getJSONObject(0);
//                            String txnID = dataObject.getString("TransactionId");
//                            String amount = dataObject.getString("Amount");
//                            String surcharge = dataObject.getString("Surcharge");
//                            String closingBal = dataObject.getString("ClosingBal");
//                            String createdOn = dataObject.getString("CreatedOn");
//                            String status = dataObject.getString("Status");
//                            String walletType = dataObject.getString("WalletType");
//                            String openingBal = dataObject.getString("OpeningBal");
//                            String accountHolderName = dataObject.getString("AccountHolderName");
//                            String accountNumber = dataObject.getString("AccountNumber");
//                            String bankName = dataObject.getString("BankName");
//                            String gst = dataObject.getString("Gst");
//                            String bankRrNo = dataObject.getString("BankRrNo");
//
//                            Intent intent=new Intent(InstantPaySettlementActivity.this, SharePayoutReportActivity.class);
//                            intent.putExtra("transactionId",txnID);
//                            intent.putExtra("amount",amount);
//                            intent.putExtra("comm",gst);
//                            intent.putExtra("balance",closingBal);
//                            intent.putExtra("dateTime",createdOn);
//                            intent.putExtra("status",status);
//                            intent.putExtra("transactionType",walletType);
//                            intent.putExtra("oldBalance",openingBal);
//                            intent.putExtra("accountName",accountHolderName);
//                            intent.putExtra("accountNo",accountNumber);
//                            intent.putExtra("bankName",bankName);
//                            intent.putExtra("surcharge",surcharge);
//                            intent.putExtra("banRRN",bankRrNo);
//                            intent.putExtra("isPayoutReport",true);
//                            intent.putExtra("serviceType","payout");
//                            startActivity(intent);
//                            finish();

                        }

                        else if ( responseCode.equalsIgnoreCase("ERR"))
                        {
                            pDialog.dismiss();
                            String status = responseObject.getString("data");
                            new AlertDialog.Builder(InstantPaySettlementActivity.this)
                                    .setTitle("Transaction Status")
                                    .setMessage(status)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }

                        else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(InstantPaySettlementActivity.this).setTitle("Alert")
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    })
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(InstantPaySettlementActivity.this).setTitle("Alert")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                })
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(InstantPaySettlementActivity.this).setTitle("Alert")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(InstantPaySettlementActivity.this).setTitle("Alert")
                        .setMessage("Something went wrong."+t.getMessage())
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .show();
            }
        });
    }

    private boolean checkInputs() {

        if (!selectedPaymentType.equalsIgnoreCase("select")) {
            if (isWallet) {
                selectedAccountHolderName = "NA";
                selectedAccountType = "NA";
                bankName = "NA";
                selectedAccountNumber = "NA";
                selectedIfscNumber = "NA";

                amount = etAmount.getText().toString().trim();

                return !TextUtils.isEmpty(etAmount.getText());
            } else {
                if (!TextUtils.isEmpty(etAmount.getText())
                        //  && !selectedTransactionType.equalsIgnoreCase("select")      // comment this for payout in bank
                        && !TextUtils.isEmpty(etAccountHolderName.getText()) && !selectedBankName.equalsIgnoreCase("select")
                        && !TextUtils.isEmpty(etAccountNumber.getText()) && !TextUtils.isEmpty(etIfscNumber.getText())
                        && !selectedAccountType.equalsIgnoreCase("select")) {
                    amount = etAmount.getText().toString().trim();
                    selectedAccountHolderName = etAccountHolderName.getText().toString().trim();
                    selectedAccountNumber = etAccountNumber.getText().toString().trim();
                    selectedIfscNumber = etIfscNumber.getText().toString().trim();
                    return true;
                } else {
                    return false;
                }
            }

        } else {
            return false;
        }
    }

    private void setViews() {
        paymentTypeList = new ArrayList<>();
        accountTypeList = new ArrayList<>();
        transactionTypeList = new ArrayList<>();

        paymentTypeList.add("WALLET");
        paymentTypeList.add("BANK");

        accountTypeList.add("Saving");
        accountTypeList.add("Current");

        transactionTypeList.add("IMPS");
        transactionTypeList.add("NEFT");
     //   transactionTypeList.add("RTGS");


        // for payout direct in bank
        selectedPaymentType = "BANK";
        getAccountDetails();
        isWallet = false;
        bankDetailsContainer.setVisibility(View.VISIBLE);
//////////////////////////////////////////////////////

        HintSpinner<String> hintSpinner = new HintSpinner<>(spinnerPaymentType, new HintAdapter<String>(InstantPaySettlementActivity.this, "Payment Mode", paymentTypeList),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        selectedPaymentType = paymentTypeList.get(position);
                        if (selectedPaymentType.equalsIgnoreCase("WALLET")) {
                            isWallet = true;
                            bankDetailsContainer.setVisibility(View.GONE);
                        } else {
                            getAccountDetails();
                            isWallet = false;
                            bankDetailsContainer.setVisibility(View.VISIBLE);
                        }
                    }
                });
        hintSpinner.init();


        HintSpinner<String> hintSpinner1 = new HintSpinner<>(spinnerAccountType, new HintAdapter<String>(InstantPaySettlementActivity.this, "Account type", accountTypeList),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        selectedAccountType = itemAtPosition;
                    }
                });
        hintSpinner1.init();


        HintSpinner<String> hintSpinner3 = new HintSpinner<>(spinnerTransactionType, new HintAdapter<String>(InstantPaySettlementActivity.this, "Transaction Mode", transactionTypeList),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        selectedTransactionType = itemAtPosition;

                    }
                });
        hintSpinner3.init();

    }

    private void initViews() {

        etAmount = findViewById(R.id.et_amount);
        etAccountHolderName = findViewById(R.id.et_account_holder_name);
        etAccountNumber = findViewById(R.id.et_account_number);
        etIfscNumber = findViewById(R.id.et_ifsc_number);
        imgBack = findViewById(R.id.img_back);
        spinnerPaymentType = findViewById(R.id.payment_type_spinner);
        spinnerAccountType = findViewById(R.id.account_type_spinner);
        spinnerTransactionType = findViewById(R.id.transaction_type_spinner);
        spinnerBankName = findViewById(R.id.bank_name_spinner);
        bankDetailsContainer = findViewById(R.id.bank_details_container);
        btnSubmit = findViewById(R.id.btn_submit);
        btnAddMoreBanks = findViewById(R.id.btn_add_banks);
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.settlement_layout), message, Snackbar.LENGTH_LONG);
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


    //  get location

    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            lat = mLastLocation.getLatitude() + "";
            longi = mLastLocation.getLongitude() + "";

           doSettlement();

        }
    };

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    lat = location.getLatitude() + "";
                                    longi = location.getLongitude() + "";
                                    showTpinDialog();
                                   // doSettlement();

                                }
                            }
                        }
                );
            } else {
                Toast.makeText(InstantPaySettlementActivity.this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(InstantPaySettlementActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(InstantPaySettlementActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(InstantPaySettlementActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                1
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(InstantPaySettlementActivity.this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }



}