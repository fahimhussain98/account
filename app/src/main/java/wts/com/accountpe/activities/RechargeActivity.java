    package wts.com.accountpe.activities;

    import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

    import android.annotation.SuppressLint;
    import android.app.Activity;
    import android.app.Dialog;
    import android.app.ProgressDialog;
    import android.content.Context;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.database.Cursor;
    import android.graphics.Color;
    import android.graphics.drawable.ColorDrawable;
    import android.net.ConnectivityManager;
    import android.net.NetworkInfo;
    import android.net.Uri;
    import android.os.Build;
    import android.os.Bundle;
    import android.preference.PreferenceManager;
    import android.provider.ContactsContract;
    import android.provider.Settings;
    import android.text.Editable;
    import android.text.InputFilter;
    import android.text.TextUtils;
    import android.text.TextWatcher;
    import android.util.Log;
    import android.view.Gravity;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.view.inputmethod.InputMethodManager;
    import android.webkit.WebView;
    import android.widget.Button;
    import android.widget.CheckBox;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.LinearLayout;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.activity.result.ActivityResultLauncher;
    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.annotation.RequiresApi;
    import androidx.appcompat.app.AlertDialog;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.appcompat.widget.AppCompatButton;
    import androidx.constraintlayout.widget.ConstraintLayout;
    import androidx.loader.content.CursorLoader;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import com.airbnb.lottie.BuildConfig;
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

    import java.text.DateFormat;
    import java.text.DecimalFormat;
    import java.text.ParseException;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Date;
    import java.util.HashMap;

    import retrofit2.Call;
    import retrofit2.Callback;
    import retrofit2.Response;
    import wts.com.accountpe.R;
    import wts.com.accountpe.adapters.CircleAdapter;
    import wts.com.accountpe.adapters.OperatorAdapter;
    import wts.com.accountpe.models.OperatorModel;
    import wts.com.accountpe.myInterface.CircleInterface;
    import wts.com.accountpe.myInterface.OperatorInterface;
    import wts.com.accountpe.retrofit.RetrofitClient;

    public class RechargeActivity extends AppCompatActivity {

        private static final int PICK_CONTACT = 120;
        TextView tvTitle;
        EditText etRechargeNumber, etAmount;
        LinearLayout mobileOperatorContainer;
        ConstraintLayout dthOperatorContainer, mobileNoContainer;
        TextView tvDthOperator;
        String service, gateWayService;
        LinearLayout operatorLayout, circleLayout;
        String selectedOperatorId;
        String selectedOperatorName = "Select Operator", selectedStateName = "select", selectedOperatorImage;
        TextView tvOperator, tvCircle;
        Button btnRecharge;
        String userid, deviceInfo, deviceId,mobileNumber,strName, strEmail;
        SharedPreferences sharedPreferences;
        String versionCodeStr;
        String loginUserName, password;
        String responseNumber, responseAmount, responseStatus, responseTransactionId,responseLiveId, responseDateTime, responseOperator;
        Dialog operatorDialog, circleDialog;
        ImageView imgDirector;
        boolean isBrowsePlans = false;
        String rechargeNumber;
        TextView tvViewPlans, tvDescription;
        AppCompatButton btnMyInfo, btnBrowsePlans;
        String monthlyRecharge, customerName, status, nextRecharge, lastRechargeAmount, planName, balance;
        TextView tvMonthlyRecharge, tvBalance, tvCustomerName, tvStatus, tvNextRechargeDate, tvLastRechargeAmount, tvPlanName;
        Button btnOk;
        String[] stateNameArray = {"Assam", "Bihar Jharkhand", "Chennai", "Delhi NCR", "Gujarat", "Haryana",
                "Himachal Pradesh", "Jammu Kashmir", "Karnataka", "Kerala", "Kolkata", "Madhya Pradesh Chhattisgarh", "Maharashtra Goa",
                "Mumbai", "North East", "Orissa", "Punjab", "Rajasthan", "Tamil Nadu", "UP East", "UP West", "West Bengal"};

        // for mnp

        String whoButtonClick;
        String walletBalance;
        String rechargeAmount = "amount";

        /////////////////////////////////////////////
        String  txnid ="txt12346", prodname ="Add_Money";

        String transactionId,amount;
        String key="Sap6G7"; // given by anas

        String hashData,hashName;
        String saltKey = "KMa2w6oYQrtHgQXB5nByd6dHzwxFuK8C";  // given by anas



        PayUPaymentParams payUPaymentParams;


        ////////////////////////////////////////


        Call<JsonObject> call;

        ActivityResultLauncher<Intent> activityResultLauncher;

        private static final DecimalFormat df = new DecimalFormat("0.00");
        String payVia = "santosh";
        String pgAmount = "";
        String versionName;

        @SuppressLint({"SetTextI18n", "IntentReset"})
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_recharge);

            initViews();
            service = getIntent().getStringExtra("service");
            gateWayService = getIntent().getStringExtra("gatewayService");
            walletBalance = getIntent().getStringExtra("walletBalance");

            setViews();

            versionName = BuildConfig.VERSION_NAME;

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RechargeActivity.this);
            deviceInfo = sharedPreferences.getString("deviceInfo", null);
            deviceId = sharedPreferences.getString("deviceId", null);
            userid = sharedPreferences.getString("userid", null);
            mobileNumber = sharedPreferences.getString("mobileno", null);
            strName = sharedPreferences.getString("username", null);
            strEmail = sharedPreferences.getString("email", null);

            rechargeAmount = getIntent().getStringExtra("gatewayAmount");

            loginUserName = sharedPreferences.getString("loginUserName", null);
            password = sharedPreferences.getString("password", null);

            getService(service);
            //getCircleCode();
            setCircle();

            int versionCode = BuildConfig.VERSION_CODE;
            versionCodeStr = String.valueOf(versionCode);

            operatorLayout.setOnClickListener(view -> operatorDialog.show());

            dthOperatorContainer.setOnClickListener(view -> operatorDialog.show());

            circleLayout.setOnClickListener(view -> circleDialog.show());

            btnRecharge.setOnClickListener(v -> {
                whoButtonClick = "rechargebutton";
    //            if (service.equalsIgnoreCase("DTH"))
    //            {
    //               getMnp();
    //            }
    //            else
    //            {

                if (service.equalsIgnoreCase("Google Play")){
                    if (!etAmount.getText().toString().isEmpty())
                    {
                     //   doRecharge();
                       if (checkInternetState())
                       {
                           calculateCommission();
                       }
                       else
                       {
                           showSnackBar("No Internet");
                       }
                    }
                    else
                    {
                        etAmount.setError("Enter Amount");
                    }
                }
                else{
                    if (checkInputs()) {
                        if (checkInternetState()) {

    //                        String number = etRechargeNumber.getText().toString().trim();
    //                        final String amount = etAmount.getText().toString().trim();
    //
    //                        //doRecharge();
    //                        new AlertDialog.Builder(RechargeActivity.this)
    //                                .setMessage("Amount = " + amount + "\n" + "Number = " + number + "\n" + "Operator = " + selectedOperatorName)
    //                                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
    //                                    @Override
    //                                    public void onClick(DialogInterface dialogInterface, int i) {
    //                                        //showMpinDialog();
    //                                        doRecharge();
    //                                    }
    //                                }).show();
                            String number = etRechargeNumber.getText().toString().trim();
                            rechargeAmount = etAmount.getText().toString().trim();
    //                    final View confirmDialog = LayoutInflater.from(RechargeActivity.this).inflate(R.layout.confirm_dialog, null, false);
    //                    final AlertDialog builder = new AlertDialog.Builder(RechargeActivity.this).create();
    //                    builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    //                    builder.setView(confirmDialog);
    //                    builder.setCancelable(false);
    //                    builder.show();

                            calculateCommission();

                            Dialog confirmDialog = new Dialog(RechargeActivity.this);
                            confirmDialog.setContentView(R.layout.confirm_dialog);
                            confirmDialog.getWindow().setGravity(Gravity.TOP);
                            confirmDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                            confirmDialog.getWindow().setBackgroundDrawableResource(R.drawable.card_back_white);
                            //  confirmDialog.show();

                            TextView tvNumber = confirmDialog.findViewById(R.id.tv_number);
                            TextView tvAmount = confirmDialog.findViewById(R.id.tv_amount);
                            TextView tvOperator = confirmDialog.findViewById(R.id.tv_operator);
                            TextView tvConfirmRecharge = confirmDialog.findViewById(R.id.tv_confirm_recharge);
                            //  ImageView imgOperator = confirmDialog.findViewById(R.id.img_operator);
                            ImageView imgClose = confirmDialog.findViewById(R.id.img_close);

                            tvNumber.setText(number);
                            tvAmount.setText("\u20b9" + rechargeAmount);
                            tvOperator.setText(selectedOperatorName);

                            //    Picasso.get().load(selectedOperatorImage).into(imgOperator);

                            double rechargeAmountDouble = Double.parseDouble(rechargeAmount);
                            double balanceAmountDouble = Double.parseDouble(walletBalance);

    //                    if (rechargeAmountDouble > balanceAmountDouble) {
    //                        tvWalletGateway.setVisibility(View.VISIBLE);
    //                        tvGateway.setVisibility(View.VISIBLE);
    //                        tvConfirmRecharge.setVisibility(View.GONE);
    //                    } else {
    //                        tvWalletGateway.setVisibility(View.GONE);
    //                        tvGateway.setVisibility(View.GONE);
    //                        tvConfirmRecharge.setVisibility(View.VISIBLE);
    //                    }

                            imgClose.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    confirmDialog.dismiss();
                                }
                            });

                            tvConfirmRecharge.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    doRecharge();
                                    //  showTpinDialog();
                                    confirmDialog.dismiss();
                                }
                            });

                        } else {
                            showSnackBar("No Internet");
                        }
                    }
                }

                // }
            });

            imgDirector.setOnClickListener(view -> {
                @SuppressLint("IntentReset") Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(i, PICK_CONTACT);
            });

            etRechargeNumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 10) {
                        if (service.equalsIgnoreCase("Mobile") || service.equalsIgnoreCase("Postpaid")) {
                            if (checkInternetState()) {
                                isBrowsePlans = false;
                                rechargeNumber = etRechargeNumber.getText().toString();
                                hideKeyBoard();
                                getMnp();
                            } else {
                                hideKeyBoard();
                                showSnackBar("No Internet");
                            }
                        }
                    }

                }
            });

            tvViewPlans.setOnClickListener(view -> {

                if (service.equalsIgnoreCase("DTH")) {
                    whoButtonClick = "browseplandth";
                    //    getMnp();
                    if (checkInternetState()) {
                        if (checkDthNumberForPlans()) {
                            Intent intent = new Intent(RechargeActivity.this, DthPlansActivity.class);
                            intent.putExtra("operator", selectedOperatorName);
                            intent.putExtra("userId", userid);
                            intent.putExtra("deviceId", deviceId);
                            intent.putExtra("deviceInfo", deviceInfo);
                            startActivityForResult(intent, 2);

                        }
                    } else {
                        showSnackBar("No Internet");
                    }
                } else {
                    if (checkInternetState()) {
                        if (checkInputsForMyPlans()) {

                            Intent intent = new Intent(RechargeActivity.this, PlansActivity.class);
                            intent.putExtra("operator", selectedOperatorName);
                            intent.putExtra("commcircle", selectedStateName);
                            intent.putExtra("userId", userid);
                            intent.putExtra("deviceId", deviceId);
                            intent.putExtra("deviceInfo", deviceInfo);
                            startActivityForResult(intent, 1);
                        }
                    } else {
                        showSnackBar("No Internet");
                    }
                }

            });

            btnMyInfo.setOnClickListener(v -> {

                whoButtonClick = "myinfo";
                //   getMnp();

                if (checkInternetState()) {
                    if (checkDthNumberForPlans()) {
                        getDthUserInfo();
                    }
                } else {
                    showSnackBar("No Internet");
                }

            });

            btnBrowsePlans.setOnClickListener(v -> {
                if (checkInternetState()) {
                    if (checkInputNumber()) {

                        String mobile = etRechargeNumber.getText().toString().trim();

                        Intent intent = new Intent(RechargeActivity.this, MplanActivity.class);
                        intent.putExtra("mobile", mobile);
                        intent.putExtra("operator", selectedOperatorName);
                        intent.putExtra("userId", userid);
                        intent.putExtra("deviceId", deviceId);
                        intent.putExtra("deviceInfo", deviceInfo);
                        startActivityForResult(intent, 1);
                    }
                } else {
                    showSnackBar("No Internet");
                }
            });

        }

        private boolean checkInputNumber() {
            if (!TextUtils.isEmpty(etRechargeNumber.getText())) {

                if (!selectedOperatorName.equalsIgnoreCase("Select Operator")) {
                    return true;

                } else {
                    new AlertDialog.Builder(RechargeActivity.this).setMessage("Select Operator")
                            .setPositiveButton("Ok", null).show();
                    return false;
                }
            } else {
                etRechargeNumber.setError("Required");
                return false;
            }
        }

        private void getDthUserInfo() {

            ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading....");
            pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
            pDialog.setCancelable(false);
            pDialog.show();

            String number = etRechargeNumber.getText().toString().trim();
            Call<JsonObject> call = RetrofitClient.getInstance().getApi().getDthUserInfo(userid, deviceId, deviceInfo, selectedOperatorName, number);
            call.enqueue(new Callback<JsonObject>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                            String data = responseObject.getString("data");
                            JSONObject dataObject = new JSONObject(data);

                            JSONArray recordsArray = dataObject.getJSONArray("records");
                            for (int i = 0; i < recordsArray.length(); i++) {
                                JSONObject recordsObject = recordsArray.getJSONObject(i);

                                try {
                                    monthlyRecharge = recordsObject.getString("MonthlyRecharge");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    balance = recordsObject.getString("Balance");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    customerName = recordsObject.getString("customerName");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    status = recordsObject.getString("status");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    nextRecharge = recordsObject.getString("NextRechargeDate");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    lastRechargeAmount = recordsObject.getString("lastrechargeamount");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    planName = recordsObject.getString("planname");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                            final View view1 = LayoutInflater.from(RechargeActivity.this).inflate(R.layout.dth_user_info_dialog, null, false);
                            final AlertDialog builder = new AlertDialog.Builder(RechargeActivity.this).create();
                            builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            builder.setCancelable(false);
                            builder.setView(view1);

                            tvMonthlyRecharge = view1.findViewById(R.id.tv_monthly_recharge);
                            tvBalance = view1.findViewById(R.id.tv_balance);
                            tvCustomerName = view1.findViewById(R.id.tv_customer_name);
                            tvStatus = view1.findViewById(R.id.tv_status);
                            tvNextRechargeDate = view1.findViewById(R.id.tv_next_recharge_date);
                            tvLastRechargeAmount = view1.findViewById(R.id.tv_last_recharge_amount);
                            tvPlanName = view1.findViewById(R.id.tv_plan_name);
                            btnOk = view1.findViewById(R.id.btn_ok);

                            tvMonthlyRecharge.setText("₹ " + monthlyRecharge);
                            tvBalance.setText("₹ " + balance);
                            tvCustomerName.setText(customerName);
                            tvStatus.setText(status);
                            tvNextRechargeDate.setText(nextRecharge);
                            tvLastRechargeAmount.setText("₹ " + lastRechargeAmount);
                            tvPlanName.setText(planName);

                            btnOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    builder.dismiss();
                                    pDialog.dismiss();
                                }
                            });

                            builder.show();
                            pDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                            new AlertDialog.Builder(RechargeActivity.this).setMessage("No Info")
                                    .setTitle("Alert!!!")
                                    .setPositiveButton("OK", null)
                                    .show();
                        }
                    } else {

                        new AlertDialog.Builder(RechargeActivity.this).setMessage("No Info")
                                .setTitle("Alert!!!")
                                .setPositiveButton("OK", null)
                                .show();
                        pDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    pDialog.dismiss();
                    new AlertDialog.Builder(RechargeActivity.this).setMessage("No Info")
                            .setTitle("Alert!!!")
                            .setPositiveButton("OK", null)
                            .show();
                }
            });
        }

        private void setCircle() {
            circleDialog = new Dialog(RechargeActivity.this, R.style.DialogTheme);
            circleDialog.setContentView(R.layout.circle_dialog);

            int width = (int) (getResources().getDisplayMetrics().widthPixels * 1.0);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.6);

            circleDialog.getWindow().setLayout(width, height);

            circleDialog.getWindow().setGravity(Gravity.BOTTOM);
            circleDialog.getWindow().setBackgroundDrawableResource(R.drawable.card_back_white);
            circleDialog.getWindow().setWindowAnimations(R.style.SlidingDialog);

            RecyclerView rv = circleDialog.findViewById(R.id.recyclerView);
            ImageView cancelImg = circleDialog.findViewById(R.id.cancelImg);
            cancelImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    circleDialog.dismiss();
                }
            });

            CircleAdapter circleAdapter = new CircleAdapter(stateNameArray);
            rv.setLayoutManager(new LinearLayoutManager(RechargeActivity.this, RecyclerView.VERTICAL, false));
            rv.setAdapter(circleAdapter);

            circleAdapter.setMyInterface(new CircleInterface() {
                @Override
                public void circleData(String circleName) {
                    circleDialog.dismiss();
                    selectedStateName = circleName;
                    tvCircle.setText(circleName);
                }
            });

        }

        private boolean checkDthNumberForPlans() {
            if (!selectedOperatorName.equalsIgnoreCase("Select Operator")) {
                return true;
            } else {
                new AlertDialog.Builder(RechargeActivity.this).setMessage("Select Operator")
                        .setPositiveButton("OK", null)
                        .show();
                return false;
            }

        }

        private boolean checkInputsForMyPlans() {
            if (etRechargeNumber.length() == 10) {

                if (!selectedOperatorName.equalsIgnoreCase("Select Operator")) {
                    return true;
                } else {
                    new AlertDialog.Builder(RechargeActivity.this).setMessage("Select Operator")
                            .setPositiveButton("Ok", null).show();
                    return false;
                }
            } else {
                etRechargeNumber.setError("Enter Number.");
                return false;
            }
        }

        public void getMnp() {
            ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading....");
            pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
            pDialog.setCancelable(false);
            pDialog.show();

            String number = etRechargeNumber.getText().toString().trim();

            if (service.equalsIgnoreCase("DTH")) {
                call = RetrofitClient.getInstance().getApi().getMNpForDTH(AUTH_KEY, userid, deviceId, deviceInfo, number);
            } else {
                call = RetrofitClient.getInstance().getApi().getMNp(AUTH_KEY, userid, deviceId, deviceInfo, number);
            }

            //    Call<JsonObject>  call = RetrofitClient.getInstance().getApi().getMNp(AUTH_KEY,userId, deviceId,deviceInfo, number);

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));

                            String statusCode = jsonObject.getString("statuscode");

                            if (statusCode.equalsIgnoreCase("TXN")) {
                                pDialog.dismiss();
                                String data = jsonObject.getString("data");
                                JSONObject jsonObject1 = new JSONObject(data);

                                selectedOperatorId = jsonObject1.getString("OperatorId");
                                selectedOperatorName = jsonObject1.getString("OperatorName");
                                selectedStateName = jsonObject1.getString("ComCirle");
                                selectedOperatorImage = jsonObject1.getString("Image");

                                //  etOperator.setVisibility(View.GONE);

                                //  circleLayout.setVisibility(View.GONE);
                                tvCircle.setText(selectedStateName);
                                tvCircle.setVisibility(View.VISIBLE);
                                //  Picasso.get().load(selectedImageUrl).into(imgServiceType);
                                tvOperator.setText(selectedOperatorName);

                                checkMplanStatusForDth();

                            } else {
                                pDialog.dismiss();
                                checkMplanStatusForDth();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                            checkMplanStatusForDth();

                        }

                    } else {
                        pDialog.dismiss();
                        checkMplanStatusForDth();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    pDialog.dismiss();
                    checkMplanStatusForDth();

                }
            });

        }

        public void checkMplanStatusForDth() {
            if (service.equalsIgnoreCase("DTH")) {
                circleLayout.setVisibility(View.GONE);
                if (whoButtonClick.equalsIgnoreCase("myinfo")) {
                    if (checkInternetState()) {
                        if (checkDthNumberForPlans()) {
                            getDthUserInfo();
                        }
                    } else {
                        showSnackBar("No Internet");
                    }

                } else if (whoButtonClick.equalsIgnoreCase("rechargebutton")) {
                    if (checkInputs()) {
                        if (checkInternetState()) {

                            String number = etRechargeNumber.getText().toString().trim();
                            final String amount = etAmount.getText().toString().trim();
                            //doRecharge();
                            new AlertDialog.Builder(RechargeActivity.this)
                                    .setMessage("Amount = " + amount + "\n" + "Number = " + number + "\n" + "Operator = " + selectedOperatorName)
                                    .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //showMpinDialog();
                                            doRecharge();
                                        }
                                    }).show();
                        } else {
                            showSnackBar("No Internet");
                        }
                    }
                } else if (whoButtonClick.equalsIgnoreCase("browseplandth")) {
    //                                    Intent intent = new Intent(RechargeActivity.this, DthPlansActivity.class);
    //                                    intent.putExtra("operator", selectedOperatorName);
    //                                    intent.putExtra("number", etRechargeNumber.getText().toString().trim());
    //                                    startActivityForResult(intent, 3);

                    if (checkInternetState()) {
                        if (checkDthNumberForPlans()) {
                            Intent intent = new Intent(RechargeActivity.this, DthPlansActivity.class);
                            intent.putExtra("operator", selectedOperatorName);
                            intent.putExtra("userId", userid);
                            intent.putExtra("deviceId", deviceId);
                            intent.putExtra("deviceInfo", deviceInfo);
                            startActivityForResult(intent, 2);

                        }
                    } else {
                        showSnackBar("No Internet");
                    }

                }
            }
        }

        public void hideKeyBoard() {
            View view1 = this.getCurrentFocus();
            if (view1 != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
            }
        }

        /*@SuppressLint("SetTextI18n")
        private void showMpinDialog() {
            View mpinView = getLayoutInflater().inflate(R.layout.mpin_dialog, null, false);
            final AlertDialog mpinDialog = new AlertDialog.Builder(RechargeActivity.this).create();
            mpinDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mpinDialog.setView(mpinView);
            mpinDialog.setCancelable(false);
            mpinDialog.show();

            Button btnSubmit = mpinView.findViewById(R.id.btn_submit);
            Button btnCancel = mpinView.findViewById(R.id.btn_cancel);
            PinView mpinPinView = mpinView.findViewById(R.id.otp_pin_view);
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mpin = mpinPinView.getText().toString().trim();
                    if (mpin.length() == 4) {
                        checkMpin(mpin);
                        mpinDialog.dismiss();
                    } else {
                        mpinPinView.setError("Required");
                    }
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mpinDialog.dismiss();
                }
            });

        }

        private void checkMpin(String mpin) {
            final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(RechargeActivity.this).create();
            LayoutInflater inflater = getLayoutInflater();
            View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            pDialog.setView(convertView);
            pDialog.setCancelable(false);
            pDialog.show();

            Call<JsonObject> call = RetrofitClient.getInstance().getApi().checkMpin(AUTH_KEY, mpin, userid);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                            String responseCode = responseObject.getString("response_code");
                            if (responseCode.equalsIgnoreCase("TXN")) {
                                doRecharge();
                            } else {
                                pDialog.dismiss();
                                new androidx.appcompat.app.AlertDialog.Builder(RechargeActivity.this)
                                        .setMessage("You have entered wrong mpin.")
                                        .setPositiveButton("ok", null)
                                        .show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(RechargeActivity.this)
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("ok", null)
                                    .show();
                        }
                    } else {
                        pDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(RechargeActivity.this)
                                .setMessage("Something went wrong.")
                                .setPositiveButton("ok", null)
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    pDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(RechargeActivity.this)
                            .setMessage("Something went wrong.")
                            .setPositiveButton("ok", null)
                            .show();
                }
            });
        }*/

        private void doRecharge() {
            final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(RechargeActivity.this).create();
            LayoutInflater inflater = getLayoutInflater();
            View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            pDialog.setView(convertView);
            pDialog.setCancelable(false);
            pDialog.show();
            String number;
          if (mobileNoContainer.getVisibility() == View.VISIBLE)
          {
              number = etRechargeNumber.getText().toString().trim();
          }
          else
          {
              number = mobileNumber;
          }
            final String amount = etAmount.getText().toString().trim();

            Call<JsonObject> call = null;
            if (service.equalsIgnoreCase("DTH")) {
                call = RetrofitClient.getInstance().getApi().doDthRecharge(AUTH_KEY, userid, deviceId, deviceInfo, selectedOperatorId, amount, number, service
                );
            }

            else if (service.equalsIgnoreCase("Google Play"))
            {
                call = RetrofitClient.getInstance().getApi().doRecharge(AUTH_KEY, userid, deviceId, deviceInfo, selectedOperatorId, amount,number , "MOBILE");
            }

            else {
                call = RetrofitClient.getInstance().getApi().doRecharge(AUTH_KEY, userid, deviceId, deviceInfo, selectedOperatorId, amount, number, service);
            }

            call.enqueue(new Callback<JsonObject>() {
                @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        JSONObject jsonObject1 = null;

                        try {
                            jsonObject1 = new JSONObject(String.valueOf(response.body()));
                            String statuscode = jsonObject1.getString("statuscode");


                            if (statuscode.equalsIgnoreCase("TXN")) {
                                JSONArray jsonArray = jsonObject1.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    responseNumber = jsonObject.getString("NUMBER");
                                    responseStatus = jsonObject.getString("Status");
                                    responseAmount = jsonObject.getString("Amount");
                                    responseTransactionId = jsonObject.getString("TxnID");
                                    responseDateTime = jsonObject.getString("TxnDate");
                                    responseOperator = jsonObject.getString("OperatorName");
                                    responseLiveId = jsonObject.getString("LiveID");
                                }

                                @SuppressLint("SimpleDateFormat") DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
                                String[] splitDate = responseDateTime.split("T");
                                try {
                                    Date date = inputDateFormat.parse(splitDate[0]);
                                    Date time = simpleDateFormat.parse(splitDate[1]);

                                    String outputDate = new SimpleDateFormat("dd MMM yyyy").format(date);
                                    String outputTime = new SimpleDateFormat("hh:mm a").format(time);
                                    Intent intent = new Intent(RechargeActivity.this, RechargeStatusActivity.class);
                                    intent.putExtra("responseStatus", responseStatus);
                                    intent.putExtra("responseNumber", responseNumber);
                                    intent.putExtra("responseAmount", responseAmount);
                                    intent.putExtra("responseTransactionId", responseTransactionId);
                                    intent.putExtra("liveId", responseLiveId);
                                    intent.putExtra("responseOperator", responseOperator);
                                    intent.putExtra("outputDate", outputDate);
                                    intent.putExtra("outputTime", outputTime);
                                    startActivity(intent);
                                    finish();

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                pDialog.dismiss();
                            } else if (statuscode.equalsIgnoreCase("ERR")) {
                                pDialog.dismiss();
                                String status = jsonObject1.getString("data");
                                final View view1 = LayoutInflater.from(RechargeActivity.this).inflate(R.layout.recharge_status_layout, null, false);
                                final AlertDialog builder = new AlertDialog.Builder(RechargeActivity.this).create();
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
                                        finish();
                                    }
                                });
                            } else {
                                pDialog.dismiss();
                                new AlertDialog.Builder(RechargeActivity.this)
                                        .setTitle("Alert")
                                        .setMessage("Something went wrong")
                                        .setPositiveButton("Ok", null).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                            new AlertDialog.Builder(RechargeActivity.this)
                                    .setTitle("Alert")
                                    .setMessage("Something went wrong")
                                    .setPositiveButton("Ok", null).show();
                        }

                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                    pDialog.dismiss();
                    new AlertDialog.Builder(RechargeActivity.this)
                            .setMessage(t.getMessage())
                            .setPositiveButton("Ok", null).show();
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
                        new AlertDialog.Builder(RechargeActivity.this).setMessage("Select Operator")
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

        private void getService(final String serviceName) {
            final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(RechargeActivity.this).create();
            LayoutInflater inflater = getLayoutInflater();
            View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            pDialog.setView(convertView);
            pDialog.setCancelable(false);
            pDialog.show();

            Call<JsonObject> call = RetrofitClient.getInstance().getApi().getServices(AUTH_KEY, deviceId, deviceInfo, userid);

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                    if (response.isSuccessful()) {
                        JSONObject jsonObject1 = null;

                        try {
                            jsonObject1 = new JSONObject(String.valueOf(response.body()));

                            String statusCode = jsonObject1.getString("statuscode");

                            if (statusCode.equalsIgnoreCase("TXN")) {

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
                            } else if (statusCode.equalsIgnoreCase("ERR")) {
                                pDialog.dismiss();

                                String errorMessage = jsonObject1.getString("response_msg");

                                new AlertDialog.Builder(RechargeActivity.this).setCancelable(false)
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

                                new AlertDialog.Builder(RechargeActivity.this).setCancelable(false)
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

                            new AlertDialog.Builder(RechargeActivity.this).setCancelable(false)
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
                        new AlertDialog.Builder(RechargeActivity.this).setCancelable(false)
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
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    pDialog.dismiss();
                    new AlertDialog.Builder(RechargeActivity.this).setCancelable(false)
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

        private void getOperators(String serviceId) {

            final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(RechargeActivity.this).create();
            LayoutInflater inflater = getLayoutInflater();
            View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            pDialog.setView(convertView);
            pDialog.setCancelable(false);
            pDialog.show();

            Call<JsonObject> call = RetrofitClient.getInstance().getApi().getOperators(AUTH_KEY, deviceId, deviceInfo, userid, serviceId);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        JSONObject jsonObject1 = null;
                        try {
                            jsonObject1 = new JSONObject(String.valueOf(response.body()));

                            JSONArray jsonArray = jsonObject1.getJSONArray("data");

                            ArrayList<OperatorModel> operatorModelArrayList = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                OperatorModel operatorModel = new OperatorModel();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String operatorName = jsonObject.getString("OperatorName");

                                String operatorId = jsonObject.getString("ID");
                                String operatorImage = jsonObject.getString("OPImage");

                                operatorModel.setOperatorName(operatorName);
                                operatorModel.setOperatorId(operatorId);
                                operatorModel.setOperatorImg("http://login.accountpe.in" + operatorImage);

                                operatorModelArrayList.add(operatorModel);

                            }

                            operatorDialog = new Dialog(RechargeActivity.this, R.style.DialogTheme);
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
                            OperatorAdapter recyclerViewItemAdapter = new OperatorAdapter(operatorModelArrayList, RechargeActivity.this);
                            rv.setLayoutManager(new LinearLayoutManager(RechargeActivity.this, RecyclerView.VERTICAL, false));
                            rv.setAdapter(recyclerViewItemAdapter);

                            recyclerViewItemAdapter.setMyInterface(new OperatorInterface() {
                                @Override
                                public void operatorData(String operatorName, String operatorId, String operatorImage) {
                                    operatorDialog.dismiss();
                                    selectedOperatorId = operatorId;
                                    selectedOperatorName = operatorName;
                                    selectedOperatorImage = operatorImage;
                                    if (service.equalsIgnoreCase("DTH")) {
                                        tvDthOperator.setText(selectedOperatorName);
                                    } else {
                                        tvOperator.setText(operatorName);

                                    }
                                }
                            });

                            pDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                            new AlertDialog.Builder(RechargeActivity.this).setTitle("Alert")
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
                        new AlertDialog.Builder(RechargeActivity.this).setTitle("Alert")
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
                    new AlertDialog.Builder(RechargeActivity.this).setTitle("Failed")
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

        @SuppressLint("SetTextI18n")
        private void setViews() {
            if (service.equalsIgnoreCase("Mobile")) {
                dthOperatorContainer.setVisibility(View.GONE);
                btnMyInfo.setVisibility(View.GONE);
                tvTitle.setText("Mobile Recharge\nPrepaid");
                etRechargeNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

            } else if (service.equalsIgnoreCase("Dth")) {
                mobileOperatorContainer.setVisibility(View.GONE);
                btnBrowsePlans.setVisibility(View.GONE);
                tvTitle.setText("DTH Recharge");
                etRechargeNumber.setHint("Enter Subscriber I'd");

            }

            else if (service.equalsIgnoreCase("Google Play")) {
                mobileNoContainer.setVisibility(View.GONE);
                btnBrowsePlans.setVisibility(View.GONE);
                btnMyInfo.setVisibility(View.GONE);
                tvViewPlans.setVisibility(View.GONE);
                mobileOperatorContainer.setVisibility(View.GONE);
                dthOperatorContainer.setVisibility(View.GONE);
                tvTitle.setText("Google Play Recharge");
                selectedOperatorId = "1345";

            }

            else {
                btnMyInfo.setVisibility(View.GONE);
                etRechargeNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                dthOperatorContainer.setVisibility(View.GONE);
                tvTitle.setText("Mobile Recharge\nPostpaid");
                btnBrowsePlans.setVisibility(View.GONE);
            }
        }

        private void initViews() {
            tvTitle = findViewById(R.id.tv_title);
            etRechargeNumber = findViewById(R.id.et_recharge_number);
            mobileOperatorContainer = findViewById(R.id.mobile_operator_container);
            dthOperatorContainer = findViewById(R.id.dth_operator_container);
            mobileNoContainer = findViewById(R.id.mobileNoContainer);
            operatorLayout = findViewById(R.id.operator_layout);
            circleLayout = findViewById(R.id.circle_layout);
            tvOperator = findViewById(R.id.tv_operator);
            tvDthOperator = findViewById(R.id.tv_dth_operator);
            btnRecharge = findViewById(R.id.btn_recharge);
            etAmount = findViewById(R.id.et_amount);
            tvCircle = findViewById(R.id.tv_circle);
            imgDirector = findViewById(R.id.img_directory);

            tvViewPlans = findViewById(R.id.tv_view_plan);
            btnMyInfo = findViewById(R.id.btn_my_info);
            btnBrowsePlans = findViewById(R.id.btn_browse_plans);
            tvDescription = findViewById(R.id.tv_desc);

        }

        private void calculateCommission() {
            ProgressDialog pDialog = new ProgressDialog(RechargeActivity.this);
            pDialog.setMessage("Fetching....");
            pDialog.setCancelable(false);
            pDialog.show();

            String amount = etAmount.getText().toString().trim();

            Call<JsonObject> call = null;
            if (service.equalsIgnoreCase("DTH")) {
                call = RetrofitClient.getInstance().getApi().getCommissionCalculation(AUTH_KEY, userid, deviceId, deviceInfo,
                        selectedOperatorId, amount, "2");
            }

            else if (service.equalsIgnoreCase("Google Play"))
            {
                call = RetrofitClient.getInstance().getApi().getCommissionCalculation(AUTH_KEY, userid, deviceId, deviceInfo, selectedOperatorId, amount, "1");
            }

            else {
                call = RetrofitClient.getInstance().getApi().getCommissionCalculation(AUTH_KEY, userid, deviceId, deviceInfo,
                        selectedOperatorId, amount, "1");
            }

            call.enqueue(new Callback<JsonObject>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        JSONObject jsonObject1 = null;

                        try {
                            jsonObject1 = new JSONObject(String.valueOf(response.body()));
                            String statusCode = jsonObject1.getString("statuscode");

                            if (statusCode.equalsIgnoreCase("TXN")) {

                                JSONObject dataObject = jsonObject1.getJSONObject("data");

                                String commission = dataObject.getString("Commission");
                                String tds = dataObject.getString("Tds");
                                String surcharge = dataObject.getString("Surcharge");
                                String cost = dataObject.getString("PayAmount");
                               selectedOperatorName = dataObject.getString("OpName");

                                String number = etRechargeNumber.getText().toString().trim();
                                String amount = etAmount.getText().toString().trim();

                                Dialog confirmDialog = new Dialog(RechargeActivity.this);
                                confirmDialog.setContentView(R.layout.confirm_dialog);
                                confirmDialog.getWindow().setGravity(Gravity.TOP);
                                confirmDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                confirmDialog.getWindow().setBackgroundDrawableResource(R.drawable.card_back_white);
                                confirmDialog.show();

                                TextView tvNumber = confirmDialog.findViewById(R.id.tv_number);
                                TextView tvAmount = confirmDialog.findViewById(R.id.tv_amount);
                                TextView tvOperator = confirmDialog.findViewById(R.id.tv_operator);
                                TextView tvCommission = confirmDialog.findViewById(R.id.tv_commission);
                                TextView tvMainWallet = confirmDialog.findViewById(R.id.textWallet);
                                TextView tvGatewayAmount = confirmDialog.findViewById(R.id.textGateway);
                                TextView tvDebitFromGateway = confirmDialog.findViewById(R.id.tvDebitFromGateway);
                                LinearLayout gatewayLayout = confirmDialog.findViewById(R.id.layout_gateway);
                                CheckBox walletCheckbox = confirmDialog.findViewById(R.id.checkbox_wallet);
                                CheckBox gatewayCheckbox = confirmDialog.findViewById(R.id.checkbox_gateway);

                               // TextView tvTds = view1.findViewById(R.id.tv_tds);
                              //  TextView tvSurcharge = view1.findViewById(R.id.tv_surcharge);
                                TextView tvCost = confirmDialog.findViewById(R.id.tv_totalAmount);
                                Button btnConfirmRecharge = confirmDialog.findViewById(R.id.btnConfirmRecharge);
                             //   ImageView imgOperator = view1.findViewById(R.id.img_operator);
                                ImageView imgClose = confirmDialog.findViewById(R.id.img_close);

                                tvNumber.setText(number);
                                tvAmount.setText("\u20b9" + amount);
                                tvOperator.setText(selectedOperatorName);
                                tvCommission.setText("₹ " + commission);
                             //   tvTds.setText("TDS  :  ₹ " + tds);
                             //   tvSurcharge.setText("Surcharge  :  ₹ " + surcharge);
                                tvCost.setText(" ₹ " + cost);
                                tvMainWallet.setText("\u20b9 " + walletBalance);
                                tvGatewayAmount.setText("\u20b9 " + amount);

                                if (Double.parseDouble(amount) <= Double.parseDouble(walletBalance))
                                {
                                    tvDebitFromGateway.setVisibility(View.GONE);
                                    gatewayLayout.setVisibility(View.GONE);
                                    btnConfirmRecharge.setText("Recharge");

                                }
                                else
                                {
                                    tvDebitFromGateway.setVisibility(View.VISIBLE);
                                    gatewayLayout.setVisibility(View.VISIBLE);
                                    gatewayCheckbox.setChecked(true);

                                }

                                if (walletCheckbox.isChecked())
                                {
                                   // tvGatewayAmount.setText(String.valueOf(Double.parseDouble(amount) - Double.parseDouble(walletBalance)));
                                    tvGatewayAmount.setText(df.format(Double.parseDouble(amount) - Double.parseDouble(walletBalance)));

                                    if (Double.parseDouble(amount) <= Double.parseDouble(walletBalance))
                                    {
                                        btnConfirmRecharge.setText("Recharge");
                                    }
                                    else
                                    {
                                        btnConfirmRecharge.setText("PAY \u20b9" + df.format(Double.parseDouble(amount) - Double.parseDouble(walletBalance)) );
                                       gatewayCheckbox.setClickable(false);

                                    }

                                }
                                else
                                {

                                    if (Double.parseDouble(amount) <= Double.parseDouble(walletBalance))
                                    {
                                        btnConfirmRecharge.setText("Recharge");
                                    }
                                    else
                                    {
                                        btnConfirmRecharge.setText("PAY \u20b9" + amount );

                                    }

                                    tvGatewayAmount.setText(amount);
                                }

                                walletCheckbox.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (walletCheckbox.isChecked())
                                        {
    //                                        tvGatewayAmount.setText(String.valueOf(Double.parseDouble(amount) - Double.parseDouble(walletBalance)));
                                            tvGatewayAmount.setText(df.format(Double.parseDouble(amount) - Double.parseDouble(walletBalance)));

                                            if (Double.parseDouble(amount) <= Double.parseDouble(walletBalance))
                                            {
                                                btnConfirmRecharge.setText("Recharge");
                                            }
                                            else
                                            {
                                                btnConfirmRecharge.setText("PAY \u20b9" + df.format(Double.parseDouble(amount) - Double.parseDouble(walletBalance)) );
                                            }

                                        }
                                        else
                                        {
                                            tvGatewayAmount.setText(amount);

                                            if (Double.parseDouble(amount) <= Double.parseDouble(walletBalance))
                                            {
                                                btnConfirmRecharge.setText("Recharge");
                                            }
                                            else
                                            {
                                                btnConfirmRecharge.setText("PAY\t \u20b9" + amount );
                                            }

                                        }
                                    }
                                });

                               // Picasso.get().load(operatorImage).into(imgOperator);

                                imgClose.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        confirmDialog.dismiss();
                                    }
                                });

                               /* btnConfirmRecharge.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        showTpinDialog();

                                        if (walletCheckbox.isChecked() && gatewayCheckbox.isChecked())
                                        {

                                            double gatewayAmount = Double.parseDouble(amount) - Double.parseDouble(walletBalance);
                                            String actualAmount = df.format(gatewayAmount);
                                            pgAmount = actualAmount;

                                         //   String actualAmount = String.valueOf(gatewayAmount);
    //                                        Intent in = new Intent(RechargeActivity.this, CashfreePaymentGatewayActivity.class);   //  this intent is use for do payment for recharge from cashFreePaymentGatewayActivity
    //
    //                                        in.putExtra("rechargeAmount", actualAmount);
    //                                        in.putExtra("payVia", "gateway+wallet");
    //                                        in.putExtra("isFromRecharge", true);
    //                                        startActivityForResult(in, 5);
                                            payVia = "gateway+wallet";
//                                           startPaymentForPG(pgAmount);
                                           //-----we have to add the tpin in it----------------
                                            showTpinDialog();

                                            confirmDialog.dismiss();
                                        }
                                        else if (gatewayCheckbox.isChecked())
                                        {
      //                                     Intent in = new Intent(RechargeActivity.this, CashfreePaymentGatewayActivity.class);   //  this intent is use for do payment for recharge from cashFreePaymentGatewayActivity
    //                                        in.putExtra("rechargeAmount", amount);
    //                                        in.putExtra("payVia", "gateway");
    //                                        in.putExtra("isFromRecharge", true);
    //                                        startActivityForResult(in, 5);
                                            pgAmount = amount;
                                            payVia = "gateway";
                                             startPaymentForPG(pgAmount);

                                            confirmDialog.dismiss();
                                        }
                                        else if (walletCheckbox.isChecked())
                                        {
                                          // doRecharge();
                                            showTpinDialogs_onlywallet();
                                            confirmDialog.dismiss();
                                        }
                                        else
                                        {
                                            new AlertDialog.Builder(RechargeActivity.this)
                                                    .setTitle("Alert")
                                                    .setMessage("Select Payment Through")
                                                    .setIcon(R.drawable.warning)
                                                    .setPositiveButton("OK", null)
                                                    .show();
                                        }

                                    }
                                });*/
                                //set tpin before all process---------
                                btnConfirmRecharge.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (walletCheckbox.isChecked() && gatewayCheckbox.isChecked()) {
                                            double gatewayAmount = Double.parseDouble(amount) - Double.parseDouble(walletBalance);
                                            pgAmount = df.format(gatewayAmount);
                                            payVia = "gateway+wallet";

                                            showTpinDialog(() -> startPaymentForPG(pgAmount));
                                            confirmDialog.dismiss();
                                        } else if (gatewayCheckbox.isChecked()) {
                                            // Gateway-only Payment
                                            pgAmount = amount;
                                            payVia = "gateway";

                                            showTpinDialog(() -> startPaymentForPG(pgAmount));
                                            confirmDialog.dismiss();
                                        } else if (walletCheckbox.isChecked()) {
                                            payVia = "wallet";

                                            showTpinDialog(() -> doRecharge());
                                            confirmDialog.dismiss();
                                        } else {
                                            new AlertDialog.Builder(RechargeActivity.this)
                                                    .setTitle("Alert")
                                                    .setMessage("Please select a payment method.")
                                                    .setIcon(R.drawable.warning)
                                                    .setPositiveButton("OK", null)
                                                    .show();
                                        }
                                    }
                                });

                                pDialog.dismiss();


                            } else {
                                pDialog.dismiss();
                                String message = jsonObject1.getString("data");
                                new AlertDialog.Builder(RechargeActivity.this)
                                        .setTitle("Message")
                                        .setMessage(message)
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
                            new AlertDialog.Builder(RechargeActivity.this)
                                    .setTitle("Message")
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
                        new AlertDialog.Builder(RechargeActivity.this)
                                .setTitle("Message")
                                .setMessage("Something went wrong.")
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
                    new AlertDialog.Builder(RechargeActivity.this)
                            .setTitle("Message")
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

        public void doRechargeViaGateway(String pgAmount, String pgResponseAmount, String uniqueId) {
            final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(RechargeActivity.this).create();
            LayoutInflater inflater = getLayoutInflater();
            View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            pDialog.setView(convertView);
            pDialog.setCancelable(false);
            pDialog.show();
            String number;
           if (mobileNoContainer.getVisibility() == View.VISIBLE)
           {
               number = etRechargeNumber.getText().toString().trim();
           }
           else
           {
               number = mobileNumber;
           }
            String amount = etAmount.getText().toString().trim();

            Call<JsonObject> call = RetrofitClient.getInstance().getApi().doRechargeViaGateway(AUTH_KEY, userid, deviceId, deviceInfo, selectedOperatorId, amount, number, gateWayService, payVia, walletBalance, pgAmount, pgResponseAmount, uniqueId);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                    if (response.isSuccessful()) {

                        try {
                            JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));

                            String statusCode = jsonObject.getString("statuscode");
                            if (statusCode.equalsIgnoreCase("TXN")) {

                                pDialog.dismiss();

                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject responseObject = jsonArray.getJSONObject(i);

                                    responseNumber = responseObject.getString("NUMBER");
                                    responseStatus = responseObject.getString("Status");
                                    responseAmount = responseObject.getString("Amount");
                                    responseTransactionId = responseObject.getString("TxnID");
                                    responseDateTime = responseObject.getString("TxnDate");
                                    responseOperator = responseObject.getString("OperatorName");

                                     responseLiveId = responseObject.getString("LiveID");
                                    String UniqueID = responseObject.getString("UniqueID");
                                    String sType = responseObject.getString("SType");
                                    String apiName = responseObject.getString("ApiName");
                                    String commission = responseObject.getString("Commission");
                                    String tds = responseObject.getString("TDS");
                                    String PayableAmount = responseObject.getString("PayableAmount");

                                }

                                @SuppressLint("SimpleDateFormat") DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
                                String[] splitDate = responseDateTime.split("T");
                                try {
                                    Date date = inputDateFormat.parse(splitDate[0]);
                                    Date time = simpleDateFormat.parse(splitDate[1]);

                                    assert date != null;
                                    @SuppressLint("SimpleDateFormat") String outputDate = new SimpleDateFormat("dd MMM yyyy").format(date);
                                    assert time != null;
                                    @SuppressLint("SimpleDateFormat") String outputTime = new SimpleDateFormat("hh:mm a").format(time);

                                    Intent intent = new Intent(RechargeActivity.this, RechargeStatusActivity.class);
                                    intent.putExtra("responseStatus", responseStatus);
                                    intent.putExtra("responseNumber", responseNumber);
                                    intent.putExtra("responseAmount", responseAmount);
                                    intent.putExtra("responseTransactionId", responseTransactionId);
                                    intent.putExtra("liveId", responseLiveId);
                                    intent.putExtra("responseOperator", responseOperator);
                                    intent.putExtra("outputDate", outputDate);
                                    intent.putExtra("outputTime", outputTime);
                                    startActivity(intent);
                                    finish();

                                } catch (ParseException e) {
                                    e.printStackTrace();

                                }

                            } else if (statusCode.equalsIgnoreCase("ERR")) {
                                pDialog.dismiss();
                                new AlertDialog.Builder(RechargeActivity.this)
                                        .setTitle("Alert")
                                        .setMessage(jsonObject.getString("data"))
                                        .setPositiveButton("OK", null)
                                        .show();
                            } else {
                                pDialog.dismiss();
                                new AlertDialog.Builder(RechargeActivity.this)
                                        .setTitle("Alert")
                                        .setMessage("Something went wrong")
                                        .setPositiveButton("OK", null)
                                        .show();
                            }

                        } catch (JSONException e) {
                            pDialog.dismiss();
                            new AlertDialog.Builder(RechargeActivity.this)
                                    .setTitle("Alert")
                                    .setMessage("Something went wrong")
                                    .setPositiveButton("OK", null)
                                    .show();
                            e.printStackTrace();
                        }

                    } else {
                        pDialog.dismiss();
                        new AlertDialog.Builder(RechargeActivity.this)
                                .setTitle("Alert")
                                .setMessage("Something went wrong")
                                .setPositiveButton("OK", null)
                                .show();
                    }

                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    pDialog.dismiss();
                    new AlertDialog.Builder(RechargeActivity.this)
                            .setTitle("Alert")
                            .setMessage("Something went wrong")
                            .setPositiveButton("OK", null)
                            .show();
                }
            });

        }

        // cashfree payment Gateway

        public void startPaymentForPG(String pgAmount) {
            String orderID = generateUniqueId();
            txnid = generateUniqueId();

            if (checkInternetState()) {
               //  getToken(orderID, pgAmount);
               // updateAddMoneyPayU(transactionId,status);
                inserDataPayU(txnid,pgAmount);
            } else {
                showSnackBar("No Internet");
            }

        }
        //----------------------------------tpinDialog-------------------------------------
      /*  @SuppressLint("SetTextI18n")
        private void showTpinDialog() {
            View addSenderDialogView = getLayoutInflater().inflate(R.layout.add_sender_otp_dialog_layout, null, false);
            final AlertDialog addSenderDialog = new AlertDialog.Builder(RechargeActivity.this).create();
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
                        new AlertDialog.Builder(RechargeActivity.this)
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
                        try {
                            JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                            String responseCode = responseObject.getString("statuscode");
                            if (responseCode.equalsIgnoreCase("TXN")) {
                                pDialog.dismiss();
                                startPaymentForPG(pgAmount);
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

        @SuppressLint("SetTextI18n")
        private void showTpinDialogs_onlywallet() {
            View addSenderDialogView = getLayoutInflater().inflate(R.layout.add_sender_otp_dialog_layout, null, false);
            final AlertDialog addSenderDialog = new AlertDialog.Builder(RechargeActivity.this).create();
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
                        checkTpin_onlywallet(tpin);
                        addSenderDialog.dismiss();
                    } else {
                        new AlertDialog.Builder(RechargeActivity.this)
                                .setMessage("Something went wrong.")
                                .show();
                    }
                }
            });

        }

        private void checkTpin_onlywallet(String tpin) {
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
                        try {
                            JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                            String responseCode = responseObject.getString("statuscode");
                            if (responseCode.equalsIgnoreCase("TXN")) {
                                pDialog.dismiss();
                                doRecharge();
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
        }*/


        //after  all all process before tpin----then tpiin process
        @SuppressLint("SetTextI18n")
        private void showTpinDialog(Runnable onTpinVerified) {
            View dialogView = getLayoutInflater().inflate(R.layout.add_sender_otp_dialog_layout, null, false);
            final AlertDialog dialog = new AlertDialog.Builder(RechargeActivity.this).create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
            dialog.setView(dialogView);
            dialog.show();

            ImageView imgClose = dialogView.findViewById(R.id.img_close);
            final EditText etTpin = dialogView.findViewById(R.id.et_otp);
            Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
            Button btnSubmit = dialogView.findViewById(R.id.btn_submit);
            Button btnResendOtp = dialogView.findViewById(R.id.btn_resend_otp);

            btnResendOtp.setVisibility(View.GONE);

            imgClose.setOnClickListener(v -> dialog.dismiss());
            btnCancel.setOnClickListener(v -> dialog.dismiss());
            btnSubmit.setOnClickListener(v -> {
                String tpin = etTpin.getText().toString().trim();
                if (!TextUtils.isEmpty(tpin)) {
                    verifyTpin(tpin, onTpinVerified, dialog);
                } else {
                    new AlertDialog.Builder(RechargeActivity.this)
                            .setMessage("TPIN cannot be empty.")
                            .show();
                }
            });
        }
        private void verifyTpin(String tpin, Runnable onSuccess, AlertDialog dialog) {
            final ProgressDialog pDialog = new ProgressDialog(this, R.style.MyTheme);
            pDialog.setMessage("Verifying TPIN...");
            pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
            pDialog.setCancelable(false);
            pDialog.show();

            Call<JsonObject> call = RetrofitClient.getInstance().getApi().checkMpinOrTPIN(RetrofitClient.AUTH_KEY, userid, deviceId, deviceInfo, "tpin", tpin);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    pDialog.dismiss();
                    if (response.isSuccessful()) {
                        try {
                            JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                            Log.d("changeTpin"," response: "+ response.body() );
                            String responseCode = responseObject.getString("statuscode");
                            String responsestatus = responseObject.getString("status");
                            if (responseCode.equalsIgnoreCase("TXN")) {
                                dialog.dismiss();
                                onSuccess.run();
                            } else {
                                String errorMessage = responseObject.getString("status");
                                showSnackBar(errorMessage);
                            }
                        } catch (Exception e) {
                            showSnackBar("Something went wrong");
                        }
                    } else {
                        showSnackBar("Something went wrong");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    pDialog.dismiss();
                    showSnackBar("Failed to verify TPIN. Please try again.");
                }
            });
        }



        //----------------------------------tpinDialogEnd-------------------------------------

        //_______________________________check credential_start_____________________________________
        @Override
        protected void onResume() {
            super.onResume();
            if (checkInternetState()) {
                if (userid != null) {
                  //  checkOfflineUserOnboard();
                    checkCredentials();
//                    getAllNotification();
//                    getBalance();
//                    getSalesReport();
//                    getAePSBalance();
//                    In_app_update();
                }
            } else {
                new AlertDialog.Builder(RechargeActivity.this)
                        .setMessage("You have not internet connection")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.P)
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_DATA_USAGE_SETTINGS);
                                startActivity(intent);
                            }
                        })
                        .show()
                ;
            }

        }
        private void checkCredentials() {
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading....");
            pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
            pDialog.setCancelable(false);
            pDialog.show();

            Call<JsonObject> call = RetrofitClient.getInstance().getApi().checkCredential(AUTH_KEY, loginUserName, password, deviceInfo, "1.1.2");
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    if (response.isSuccessful()) {

                        try {
                            JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                            String responseCode = responseObject.getString("statuscode");

                            if (responseCode.equalsIgnoreCase("TXN")) {
                                pDialog.dismiss();

                                JSONObject dataObject = responseObject.getJSONObject("data");
                                String userId = dataObject.getString("userID");
                                //    kycStatus = dataObject.getString("kycStatus");
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RechargeActivity.this);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("userid", userId);
                                editor.apply();

                            } else {
                                pDialog.dismiss();
                                new androidx.appcompat.app.AlertDialog.Builder(RechargeActivity.this)
                                        .setCancelable(false)
                                        .setMessage("You have to login again.")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(RechargeActivity.this).edit();
                                                editor.clear();
                                                editor.apply();
                                                //  startActivity(new Intent(HomeDashActivity.this, LoginActivity.class));
                                                startActivity(new Intent(RechargeActivity.this, LoginNewActivity.class));
                                                finish();
                                            }
                                        }).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(RechargeActivity.this)
                                    .setCancelable(false)
                                    .setMessage("You have to login again.")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(RechargeActivity.this).edit();
                                            editor.clear();
                                            editor.apply();
                                            //    startActivity(new Intent(HomeDashActivity.this, LoginActivity.class));
                                            startActivity(new Intent(RechargeActivity.this, LoginNewActivity.class));
                                            finish();
                                        }
                                    }).show();
                        }

                    } else {
                        pDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(RechargeActivity.this)
                                .setCancelable(false)
                                .setMessage("You have to login again.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(RechargeActivity.this).edit();
                                        editor.clear();
                                        editor.apply();
                                        //  startActivity(new Intent(HomeDashActivity.this, LoginActivity.class));
                                        startActivity(new Intent(RechargeActivity.this, LoginNewActivity.class));
                                        finish();
                                    }
                                }).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    pDialog.dismiss();
                    new AlertDialog.Builder(RechargeActivity.this)
                            .setCancelable(false)
                            .setMessage("You have to login again.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(RechargeActivity.this).edit();
                                    editor.clear();
                                    editor.apply();
                                    //   startActivity(new Intent(HomeDashActivity.this, LoginActivity.class));
                                    startActivity(new Intent(RechargeActivity.this, LoginNewActivity.class));
                                    finish();
                                }
                            }).show();
                }
            });
        }


        //_______________________________check credential_End_______________________________________

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


        private void updateAddMoneyPayU(String transactionId, String status) {

            ProgressDialog progressDialog = new ProgressDialog(RechargeActivity.this);
            progressDialog.setMessage("Loading");
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
            progressDialog.setCancelable(false);
            progressDialog.show();

            Call<JsonObject> call = RetrofitClient.getInstance().getApi().updateAddMoneyPayu(RetrofitClient.AUTH_KEY,userid,txnid,deviceId,deviceInfo,status,transactionId);

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

        private void inserDataPayU(String transactionId, String amount) {
            ProgressDialog progressDialog = new ProgressDialog(RechargeActivity.this);
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

                            final View view1 = LayoutInflater.from(RechargeActivity.this).inflate(R.layout.payu_response_dialog, null, false);
                            final androidx.appcompat.app.AlertDialog builder = new androidx.appcompat.app.AlertDialog.Builder(RechargeActivity.this).create();
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
                                    doRechargeViaGateway(pgAmount,pgAmount,txnid);
                                }
                            });

                            builder.show();

                        }
                       /* @Override
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


                            final View view1 = LayoutInflater.from(RechargeActivity.this).inflate(R.layout.payu_response_dialog, null, false);
                            final androidx.appcompat.app.AlertDialog builder = new androidx.appcompat.app.AlertDialog.Builder(RechargeActivity.this).create();
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
                            final View view1 = LayoutInflater.from(RechargeActivity.this).inflate(R.layout.payu_response_dialog, null, false);
                            final androidx.appcompat.app.AlertDialog builder = new androidx.appcompat.app.AlertDialog.Builder(RechargeActivity.this).create();
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



/*

                        @Override
                        public void onPaymentFailure(Object response) {
                            try {
                                // Cast response to HashMap
                                HashMap<String, Object> result = (HashMap<String, Object>) response;

                                // Now extract the PayU response and merchant response from the HashMap
                                HashMap<String, Object> payuResponseMap = (HashMap<String, Object>) result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                                HashMap<String, Object> merchantResponseMap = (HashMap<String, Object>) result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);

                                // Extract the relevant data from payuResponseMap and merchantResponseMap
                                String txnId = payuResponseMap.containsKey("txnid") ? (String) payuResponseMap.get("txnid") : "N/A";
                                String amount = payuResponseMap.containsKey("amount") ? (String) payuResponseMap.get("amount") : "0.00";
                                String status = payuResponseMap.containsKey("status") ? (String) payuResponseMap.get("status") : "Failed";

                                // Show the dialog with parsed data
                                final View view1 = LayoutInflater.from(RechargeActivity.this).inflate(R.layout.payu_response_dialog, null, false);
                                final androidx.appcompat.app.AlertDialog builder = new androidx.appcompat.app.AlertDialog.Builder(RechargeActivity.this).create();
                                builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                builder.setCancelable(false);
                                builder.setView(view1);

                                TextView tvStatus = view1.findViewById(R.id.tv_status);
                                TextView tvTel = view1.findViewById(R.id.tv_txnid);
                                TextView tvBillAmount = view1.findViewById(R.id.tv_amount);
                                Button btnOk = view1.findViewById(R.id.btn_ok);

                                // Set data in the dialog
                                tvStatus.setText(status);
                                tvTel.setText(txnId);
                                tvBillAmount.setText("₹ " + amount);

                                btnOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        builder.dismiss();
                                    }
                                });

                                builder.show();

                            } catch (ClassCastException e) {
                                e.printStackTrace();
                                // Handle class cast exceptions
                            } catch (Exception e) {
                                e.printStackTrace();
                                // Handle other exceptions
                            }
                        }
*/

                        /*@Override
                        public void onPaymentFailure(Object response) {
                            try {
                                // Cast response to HashMap
                                HashMap<String, Object> result = (HashMap<String, Object>) response;

                                // Extract the PayU response and merchant response (assuming both are still in HashMap format)
                                HashMap<String, Object> payuResponseMap = (HashMap<String, Object>) result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);

                                // Convert PayU response to JSONObject for further parsing
                                JSONObject jsonObject = new JSONObject(payuResponseMap);

                                // Check if "result" exists and extract data, otherwise set default values
                                if (jsonObject.has("result")) {
                                    JSONObject data = jsonObject.getJSONObject("result");

                                    transactionId = data.has("txnid") ? data.getString("txnid") : "N/A";
                                    amount = data.has("amount") ? data.getString("amount") : "0.00";
                                    status = data.has("status") ? data.getString("status") : "Failed if";

                                } else {
                                    // If the response doesn't have "result", set default values
                                    transactionId = jsonObject.has("txnid") ? jsonObject.getString("txnid") : "N/A";
                                    amount = jsonObject.has("amount") ? jsonObject.getString("amount") : "0.00";
                                    status = jsonObject.has("status") ? jsonObject.getString("status") : "Failed else";
                                }

                                // Show the dialog with parsed data
                                showFailureDialog(transactionId, amount, status);

                            } catch (ClassCastException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                // Set default values in case of parsing error
                                showFailureDialog("N/A", "0.00", "Parsing Failed");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }*/



                        /*@Override
                        public void onPaymentFailure(Object response) {
                            try {
                                // Log the entire response for inspection
                                Log.d("PayUResponse", "Response received: " + response.toString());

                                // Cast response to HashMap and extract PayU response and merchant response
                                HashMap<String, Object> result = (HashMap<String, Object>) response;

                                // Log individual entries from the response
                                Log.d("PayUResponse", "PayU Response: " + result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE));
                                Log.d("PayUResponse", "Merchant Response: " + result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE));

                                // Extract and process responses if not null
                                if (result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE) != null) {
                                    HashMap<String, Object> payuResponseMap = (HashMap<String, Object>) result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);

                                    // Convert PayU response to JSONObject for further parsing
                                    JSONObject jsonObject = new JSONObject(payuResponseMap);
                                    Log.d("PayUResponse", "PayU Response as JSONObject: " + jsonObject.toString());

                                    // Check if "result" exists and extract data
                                    if (jsonObject.has("result")) {
                                        JSONObject data = jsonObject.getJSONObject("result");
                                        transactionId = data.optString("txnid", "N/A");
                                        amount = data.optString("amount", "0.00");
                                        status = data.optString("status", "Failed");
                                    }

                                    // Show the dialog
                                    showFailureDialog(transactionId, amount, status);
                                } else {
                                    Toast.makeText(RechargeActivity.this, "PayU response is null", Toast.LENGTH_SHORT).show();
                                }

                            } catch (ClassCastException e) {
                                e.printStackTrace();
                                Toast.makeText(RechargeActivity.this, "Error casting payment data", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(RechargeActivity.this, "Error parsing payment response", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(RechargeActivity.this, "Unexpected error occurred", Toast.LENGTH_SHORT).show();
                            }
                        }*/

                     /*   @Override
                        public void onPaymentFailure(Object response) {
                            try {
                                // Check if the response is a HashMap
                                if (response instanceof HashMap) {
                                    HashMap<String, Object> result = (HashMap<String, Object>) response;

                                    // Extract PayU and merchant responses
                                    Object payuResponse = result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                                    Object merchantResponse = result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);

                                    // Log extracted responses
                                    Log.d("PayUResponse", "PayU Response: " + payuResponse);
                                    Log.d("PayUResponse", "Merchant Response: " + merchantResponse);
                                    Log.d("PayUResponse", "Raw response: " + response);

                                    // Check if responses are null
                                    if (payuResponse != null) {
                                        JSONObject jsonObject = new JSONObject((String) payuResponse);

                                        // Check if "result" exists and extract data
                                        if (jsonObject.has("result")) {
                                            JSONObject data = jsonObject.getJSONObject("result");
                                            transactionId = data.optString("txnid", "N/A");
                                            amount = data.optString("amount", "0.00");
                                            status = data.optString("status", "Failed");
                                        } else {
                                            // If "result" doesn't exist, use default values
                                            transactionId = jsonObject.optString("txnid", "N/A");
                                            amount = jsonObject.optString("amount", "0.00");
                                            status = jsonObject.optString("status", "Failed");
                                        }
                                    } else {
                                        // Handle null PayU response
                                        transactionId = "N/A";
                                        amount = "0.00";
                                        status = "Failed (PayU response is null)";
                                    }

                                    // Show the dialog with the extracted or default data
                                    showFailureDialog(transactionId, amount, status);

                                } else {
                                    // Handle case where response is not a HashMap
                                    Log.e("PayUResponse", "Unexpected response type: " + response.getClass().getName());
                                    Toast.makeText(RechargeActivity.this, "Unexpected response format", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(RechargeActivity.this, "Error parsing payment response", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(RechargeActivity.this, "Unexpected error occurred", Toast.LENGTH_SHORT).show();
                            }
                        }*/



                        //this is show the pop message on failure of payuSDK
                        private void showFailureDialog(String txnId, String amount, String status) {
                            // Inflate the custom dialog view
                            final View view = LayoutInflater.from(RechargeActivity.this).inflate(R.layout.payu_response_dialog, null, false);

                            final androidx.appcompat.app.AlertDialog builder = new androidx.appcompat.app.AlertDialog.Builder(RechargeActivity.this).create();
                            builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            builder.setCancelable(false);
                            builder.setView(view);

                            // Bind the views
                            TextView tvStatus = view.findViewById(R.id.tv_status);
                            TextView tvTxnId = view.findViewById(R.id.tv_txnid);
                            TextView tvAmount = view.findViewById(R.id.tv_amount);
                            Button btnOk = view.findViewById(R.id.btn_ok);

                            // Set the extracted data into the dialog fields
                            tvStatus.setText(status);
                            tvTxnId.setText(txnId);
                            tvAmount.setText("₹ " + amount);

                            // Set up the button click to dismiss the dialog
                            btnOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    builder.dismiss();
                                }
                            });

                            // Show the dialog
                            builder.show();
                        }
                        //her is the end of the failure of payusdk please don't tuch it






                        @Override
                        public void onPaymentCancel(boolean isTxnInitiated) {

                        }
                        @Override
                        public void onError(ErrorResponse errorResponse) {
                            String errorMessage = errorResponse.getErrorMessage();
                            // Toast.makeText(AddMoneyPayuMoneyActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            new android.app.AlertDialog.Builder(RechargeActivity.this)
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

                                    Toast.makeText(RechargeActivity.this, "Hash Value is Empty", Toast.LENGTH_SHORT).show();
                                }
                            } else {

                                Toast.makeText(RechargeActivity.this, "HashData or HashName is Empty", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
            );
            //////////////////////////
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
            hashMap.put(CFPaymentService.PARAM_NOTIFY_URL, "https://api.accountpe.in/api/cashfree");         // use wrong url for not receive call back from cashfree

            //   CFPaymentService.getCFPaymentServiceInstance().doPayment(this, hashMap, token, "PROD", "#D60006", "#ffffff", true);
            CFPaymentService.getCFPaymentServiceInstance().upiPayment(this, hashMap, token, "PROD");
        }

        private void updateCashFreeAddMoneyWebhook(String uniqueId, String status, String txnId) {
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading....");
            pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
            pDialog.setCancelable(false);
            pDialog.show();
            Call<JsonObject> call = RetrofitClient.getInstance().getApi().UpdateCashfreeAddMoneyWebhook(AUTH_KEY, userid,deviceId, deviceInfo, uniqueId, status, txnId, "Cashfree Response");
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

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PICK_CONTACT) {
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactsData = data.getData();
                    CursorLoader loader = new CursorLoader(this, contactsData, null, null, null, null);
                    Cursor c = loader.loadInBackground();
                    if (c != null && c.moveToFirst()) {
                        @SuppressLint("Range") String number = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if (number.contains("+91")) {
                            number = number.replace("+91", "");
                        }
                        if (number.contains(" ")) {
                            number = number.replace(" ", "");
                        }
                        number = number.trim();
                        etRechargeNumber.setText(number);
                    }
                }
            }

            if (requestCode == 1) {
                if (data != null) {
                    String amountPlan = data.getStringExtra("amount");
                    String desc = data.getStringExtra("desc");
                    etAmount.setText(amountPlan);
                    tvDescription.setText(desc);
                }
            }
            if (requestCode == 2) {
                if (data != null) {
                    String amountPlan = data.getStringExtra("amount");
                    String desc = data.getStringExtra("desc");
                    etAmount.setText(amountPlan);
                    tvDescription.setText(desc);
                }
            }

    // this response is getting from cashfree payment gateway activity

    //        if (requestCode == 5) {
    //            if (data != null) {
    //                String rechargeAmount = data.getStringExtra("rechargeAmount");
    //                String payVia = data.getStringExtra("payVia");
    //                doRechargeViaGateway(rechargeAmount, payVia);
    //
    //            }
    //        }

            // from cashfree PG response

            if (requestCode == CFPaymentService.REQ_CODE && data != null) {
                Bundle bundle = data.getExtras();

                if (bundle != null) {

                    String status = bundle.getString("txStatus");
                    String orderId = bundle.getString("orderId");

                    Toast.makeText(this, status, Toast.LENGTH_SHORT).show();

                    if (status.equalsIgnoreCase("SUCCESS")) {

                        Toast.makeText(this, "Payment success", Toast.LENGTH_SHORT).show();

                        String pgResponseAmount = bundle.getString("orderAmount");
                        doRechargeViaGateway(pgAmount,pgResponseAmount, orderId);

                    } else {
                       updateCashFreeAddMoneyWebhook(orderId, status, orderId);
                    }

                }

            }
            else {
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
        private void errorDialog(String message) {
            new android.app.AlertDialog.Builder(RechargeActivity.this)
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