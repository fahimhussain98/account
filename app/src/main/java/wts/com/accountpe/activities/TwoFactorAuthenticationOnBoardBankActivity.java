package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.StringWriter;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.retrofit.RetrofitClient;
import wts.com.wts_aeps.scannerDevice.Opts;
import wts.com.wts_aeps.scannerDevice.PidOptions;

public class TwoFactorAuthenticationOnBoardBankActivity extends AppCompatActivity {

    LinearLayout transactionTypeLayout, userDetailLayout, deviceLayout;

    String userId, mobileNo, lat = "0.0", longi = "0.0", deviceId, deviceInfo,aadhaarCard;

    FusedLocationProviderClient mFusedLocationClient;

    TextView tvBalance,tvTitle;

    ////////////////TRANSACTION TYPE LAYOUT 1st LAYOUT//////////////
    LinearLayout cashWithDrawLayout;
    ImageView imgCashWithdraw;
    TextView tvCashWithdraw;
    String selectedTransactionType = "select";
    AppCompatButton btnProceedTransactionType;
    ////////////////TRANSACTION TYPE LAYOUT 1st LAYOUT//////////////

    ////////////////USER DETAIL LAYOUT 2nd LAYOUT//////////////

    String selectedBankName = "select", selectedBankIIN;


    EditText etMobile, etAadharCard;
    Button btnProceedUserDetail;
    CheckBox ckbTermsAndCondition;



    ArrayList<String> bankNameArrayList, bankIINArrayList;
    ////////////////USER DETAIL LAYOUT 2nd LAYOUT//////////////

    ////////////////DEVICE LAYOUT 3rd LAYOUT//////////////
    ImageView imgMorpho, imgStartek, imgMantra, imgEvolute,imgVriddhi;
    LinearLayout morphoLayout, startekLayout, mantraLayout, evoluteLayout,vriddhiLayout;
    TextView tvMorpho, tvMantra, tvStartek, tvEvolute,tvVriddhi;
    AppCompatButton btnProceedDeviceLayout;

    String selectedDevice = "select";
    ////////////////DEVICE LAYOUT 3rd LAYOUT//////////////

    ////////////////FINGER PRINT DATA/////////////////////
    Serializer serializer = null;
    String pidData = null;
    ////////////////FINGER PRINT DATA/////////////////////
    SharedPreferences sharedPreferences;
    String title,appId;

    @SuppressLint("StaticFieldLeak")
    public static Activity activity;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_factor_authentication_on_board_bank);

        initViews();

        //////CHANGE COLOR OF STATUS BAR///////////////
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(TwoFactorAuthenticationOnBoardBankActivity.this, R.color.black));
        //////CHANGE COLOR OF STATUS BAR///////////////

        //////////////////////////////////////////////////////////////////

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TwoFactorAuthenticationOnBoardBankActivity.this);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        mobileNo = sharedPreferences.getString("mobileno", null);
        aadhaarCard = sharedPreferences.getString("adharcard", null);
        title = getIntent().getStringExtra("title");
        appId = getIntent().getStringExtra("appId");
//        String balance = getIntent().getStringExtra("balance");
//        tvBalance.setText("₹ " + balance);
        tvTitle.setText(title);

        activity = TwoFactorAuthenticationOnBoardBankActivity.this;

        etMobile.setText(mobileNo);
        etAadharCard.setText(aadhaarCard);  // added by shuaib after client require auto fill
        //////////////////////////////////////////////////////////////////

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(TwoFactorAuthenticationOnBoardBankActivity.this);

        transactionTypeLayoutListeners();
        userDetailLayoutListeners();
        deviceLayoutListeners();

        //////////////////////////////////////////////
        serializer = new Persister();
        //////////////////////////////////////////////
    }

    /**
     * FIRST STEP
     * selects transaction type(Cash withdraw,Balance enquiry etc. and then proceed to Second Step.
     * after proceed hide TRANSACTION TYPE LAYOUT and show USER DETAILS LAYOUT
     */
    private void transactionTypeLayoutListeners() {
        cashWithDrawLayout.setOnClickListener(view -> {
            imgCashWithdraw.setImageResource(R.drawable.cash_withdraw_selected);


            tvCashWithdraw.setTextColor(getResources().getColor(R.color.teal_200));

            selectedTransactionType = "authentication";

        });

        btnProceedTransactionType.setOnClickListener(view -> {
            if (!selectedTransactionType.equalsIgnoreCase("select")) {
                transactionTypeLayout.setVisibility(View.GONE);
                userDetailLayout.setVisibility(View.VISIBLE);


            } else {
                showMessageDialog("Hey!!!You forgot to select two factor Auth. .");
            }
        });
    }

    /**
     * SECOND STEP
     * get Aadhar details from user input and then proceed to third step.
     * after proceed hide USER DETAIL LAYOUT and show DEVICE LAYOUT
     */
    private void userDetailLayoutListeners() {




        btnProceedUserDetail.setOnClickListener(v ->
        {
            if (checkUserDetails()) {
                userDetailLayout.setVisibility(View.GONE);
                deviceLayout.setVisibility(View.VISIBLE);
            }
        });

    }

    /**
     * THIRD STEP
     * get device name from user and get pid data from RD Service
     * after that do final transaction
     */
    private void deviceLayoutListeners() {

        mantraLayout.setOnClickListener(view -> {
            imgMantra.setImageResource(R.drawable.mantra_selected);
            imgMorpho.setImageResource(R.drawable.morpho_unselected);
            imgStartek.setImageResource(R.drawable.startek_unselected);
            imgEvolute.setImageResource(R.drawable.evolute_unselected);
            imgVriddhi.setImageResource(R.drawable.mantra_unselected);


            tvMantra.setTextColor(getResources().getColor(R.color.teal_200));
            tvMorpho.setTextColor(getResources().getColor(R.color.white));
            tvStartek.setTextColor(getResources().getColor(R.color.white));
            tvEvolute.setTextColor(getResources().getColor(R.color.white));
            tvVriddhi.setTextColor(getResources().getColor(R.color.white));

            selectedDevice = "Mantra";
        });

        startekLayout.setOnClickListener(view -> {
            imgMantra.setImageResource(R.drawable.mantra_unselected);
            imgMorpho.setImageResource(R.drawable.morpho_unselected);
            imgStartek.setImageResource(R.drawable.startek_selected);
            imgVriddhi.setImageResource(R.drawable.mantra_unselected);


            tvMantra.setTextColor(getResources().getColor(R.color.white));
            tvMorpho.setTextColor(getResources().getColor(R.color.white));
            tvEvolute.setTextColor(getResources().getColor(R.color.white));
            tvStartek.setTextColor(getResources().getColor(R.color.teal_200));
            tvVriddhi.setTextColor(getResources().getColor(R.color.white));

            selectedDevice = "Startek";
        });

        morphoLayout.setOnClickListener(view -> {
            imgMantra.setImageResource(R.drawable.mantra_unselected);
            imgMorpho.setImageResource(R.drawable.morpho_selected);
            imgStartek.setImageResource(R.drawable.startek_unselected);
            imgEvolute.setImageResource(R.drawable.evolute_unselected);
            imgVriddhi.setImageResource(R.drawable.mantra_unselected);


            tvMantra.setTextColor(getResources().getColor(R.color.white));
            tvMorpho.setTextColor(getResources().getColor(R.color.teal_200));
            tvStartek.setTextColor(getResources().getColor(R.color.white));
            tvEvolute.setTextColor(getResources().getColor(R.color.white));
            tvVriddhi.setTextColor(getResources().getColor(R.color.white));


            selectedDevice = "Morpho";
        });

        evoluteLayout.setOnClickListener(view ->
        {
            imgMantra.setImageResource(R.drawable.mantra_unselected);
            imgMorpho.setImageResource(R.drawable.morpho_unselected);
            imgStartek.setImageResource(R.drawable.startek_unselected);
            imgEvolute.setImageResource(R.drawable.evolute_selected);
            imgVriddhi.setImageResource(R.drawable.mantra_unselected);

            tvMantra.setTextColor(getResources().getColor(R.color.white));
            tvMorpho.setTextColor(getResources().getColor(R.color.white));
            tvStartek.setTextColor(getResources().getColor(R.color.white));
            tvEvolute.setTextColor(getResources().getColor(R.color.teal_200));
            tvVriddhi.setTextColor(getResources().getColor(R.color.white));


            selectedDevice = "Evolute";
        });

        vriddhiLayout.setOnClickListener(view ->
        {
            imgMantra.setImageResource(R.drawable.mantra_unselected);
            imgMorpho.setImageResource(R.drawable.morpho_unselected);
            imgStartek.setImageResource(R.drawable.startek_unselected);
            imgEvolute.setImageResource(R.drawable.evolute_unselected);
            imgVriddhi.setImageResource(R.drawable.mantra_selected);

            tvMantra.setTextColor(getResources().getColor(R.color.white));
            tvMorpho.setTextColor(getResources().getColor(R.color.white));
            tvStartek.setTextColor(getResources().getColor(R.color.white));
            tvEvolute.setTextColor(getResources().getColor(R.color.white));
            tvVriddhi.setTextColor(getResources().getColor(R.color.teal_200));

            // selectedDevice = "Vriddhi";
            selectedDevice = "MantraL1";
        });


        btnProceedDeviceLayout.setOnClickListener(view -> {
            if (!selectedDevice.equalsIgnoreCase("select")) {
                //startActivity(new Intent(PaySprintActivity.this,AepsTransactionActivity.class));
                String packageName = null;
                if (selectedDevice.equalsIgnoreCase("Morpho"))
                    packageName = "com.scl.rdservice";
                else if (selectedDevice.equalsIgnoreCase("Startek"))
                    packageName = "com.acpl.registersdk";
                else if (selectedDevice.equalsIgnoreCase("Mantra"))
                    packageName = "com.mantra.rdservice";
                else if (selectedDevice.equalsIgnoreCase("Evolute"))
                    packageName = "com.evolute.rdservice";

                else if (selectedDevice.equalsIgnoreCase("MantraL1"))
                    // packageName = "com.nextbiometrics.onetouchrdservice";
                    packageName =  "com.mantra.mfs110.rdservice";

                try {

                    String pidOption = getPIDOptions();
                    Intent intent2 = new Intent();
                    intent2.setPackage(packageName);
                    intent2.setAction("in.gov.uidai.rdservice.fp.CAPTURE");
                    intent2.putExtra("PID_OPTIONS", pidOption);
                    startActivityForResult(intent2, 1);
                } catch (Exception e) {
                    showMessageDialog("Please install " + selectedDevice + " Rd Service first.");
                }
            } else {
                showMessageDialog("Please Select Your Device");
            }
        });
    }

    private String getPIDOptions() {

        try {
            Opts opts = new Opts();
            opts.fCount = "1";
            opts.fType = "2";
            opts.format = "0";
            opts.timeout = "15000";
            opts.wadh = "";
            opts.iCount = "0";
            opts.iType = "0";
            opts.pCount = "0";
            opts.pType = "0";
            opts.pidVer = "2.0";
            opts.env = "P";
            PidOptions pidOptions = new PidOptions();
            pidOptions.ver = "1.0";
            pidOptions.Opts = opts;
            Serializer serializer = new Persister();
            StringWriter writer = new StringWriter();
            serializer.write(pidOptions, writer);
            return writer.toString();

        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
        return null;
    }



    private boolean checkUserDetails() {
        if (etMobile.getText().toString().length() == 10) {
            if (etAadharCard.getText().toString().trim().length() == 12) {
                if (ckbTermsAndCondition.isChecked()) {
                    hideKeyBoard();
                    return true;
                } else {
                    showMessageDialog("Please accept terms and condition to continue.");
                    return false;
                }


            } else {
                showMessageDialog("Please enter valid aadhar number.");
                return false;
            }

        } else {
            showMessageDialog("Please enter valid mobile number.");
            return false;
        }
    }

    public void hideKeyBoard() {
        View view1 = this.getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }
    }


    private void showMessageDialog(String message) {
        final AlertDialog messageDialog = new AlertDialog.Builder(TwoFactorAuthenticationOnBoardBankActivity.this).create();
        final LayoutInflater inflater = LayoutInflater.from(TwoFactorAuthenticationOnBoardBankActivity.this);
        View convertView = inflater.inflate(R.layout.message_dialog, null);
        messageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        messageDialog.setCancelable(false);
        messageDialog.setView(convertView);

        ImageView imgClose = convertView.findViewById(R.id.img_close);
        TextView tvMessage = convertView.findViewById(R.id.tv_message);
        Button btnTryAgain = convertView.findViewById(R.id.btn_try_again);

        imgClose.setOnClickListener(view -> {
            messageDialog.dismiss();
        });
        btnTryAgain.setOnClickListener(view -> {
            messageDialog.dismiss();
        });
        tvMessage.setText(message);

        messageDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String result;
        //List<Param> params = new ArrayList<>();
        if (data != null) {
            if (requestCode == 1) {
                if (resultCode == Activity.RESULT_OK) {
                    result = data.getStringExtra("PID_DATA");
                    if (result.contains("Device not ready")) {
                        showMessageDialog("Device Not Connected");
                    } else {
                        //if (result.contains("srno") && result.contains("rdsId") && result.contains("rdsVer")) {
                        if (result.contains("rdsId") && result.contains("rdsVer")) {
                            try {
                                pidData = result;

                                getLastLocation();
                               /* pidDataStr = pidData._Data.value;
                                Log.d("xml_data_show", pidDataStr);
                                sessionKey = pidData._Skey.value;
                                hmac = pidData._Hmac;
                                dpId = pidData._DeviceInfo.dpId;
                                rdsId = pidData._DeviceInfo.rdsId;
                                rdsVer = pidData._DeviceInfo.rdsVer;
                                dc = pidData._DeviceInfo.dc;
                                mc = pidData._DeviceInfo.mc;
                                mi = pidData._DeviceInfo.mi;
                                errCode = pidData._Resp.errCode;
                                errInfo = pidData._Resp.errInfo;
                                errCode = pidData._Resp.errCode;
                                fcount = pidData._Resp.fCount;
                                qScore = pidData._Resp.qScore;
                                nmPoints = pidData._Resp.nmPoints;
                                ci = pidData._Skey.ci;
                                params = pidData._DeviceInfo.add_info.params;
                                for (int i = 0; i < params.size(); i++) {
                                    String name = params.get(i).name;
                                    if (name.equalsIgnoreCase("srno")) {
                                        serialNo = params.get(i).value;
                                        Log.e("serialNu", serialNo);
                                    } else if (name.equalsIgnoreCase("sysid")) {
                                        String systemId = params.get(i).value;
                                        Log.e("systemId", systemId);
                                    }
                                }
                                //getTransactionNow();
                                tvData.setText(result);
                                Toast.makeText(MainActivity.this, "Data Collected Successfully", Toast.LENGTH_SHORT).show();*/
                            } catch (Exception e) {
                                e.printStackTrace();
                                showMessageDialog("There are some issues please contact to administration.");
                            }
                        } else {
                            showMessageDialog("Device Not Connected");
                        }
                    }

                }

            }
        }
    }

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
                                    //launchWTSAEPS();
                                    doAepsAuthentication();
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(TwoFactorAuthenticationOnBoardBankActivity.this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(TwoFactorAuthenticationOnBoardBankActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(TwoFactorAuthenticationOnBoardBankActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(TwoFactorAuthenticationOnBoardBankActivity.this,
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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(TwoFactorAuthenticationOnBoardBankActivity.this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            lat = mLastLocation.getLatitude() + "";
            longi = mLastLocation.getLongitude() + "";
            //launchWTSAEPS();
            doAepsAuthentication();

        }
    };

    private void doAepsAuthentication() {
        ProgressDialog progressDialog = new ProgressDialog(TwoFactorAuthenticationOnBoardBankActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...Don't press back button");
        progressDialog.show();

        String aadharNo = etAadharCard.getText().toString().trim();
        String custMobile = etMobile.getText().toString().trim();  //  added by shuaib client want cust Mobile
        Call<JsonObject> call= null;

        if(title.equalsIgnoreCase("TwoFactorAuthDaily")){


            call = RetrofitClient.getInstance().getApi().twoFactorDailyCredo(AUTH_KEY,userId,deviceId,deviceInfo,aadharNo,custMobile,pidData,
                    lat,longi,"auth","NA","0");

        }

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {

                            progressDialog.show();
                            String message = responseObject.getString("status");
                            String data = responseObject.getString("data");
                            new AlertDialog.Builder(TwoFactorAuthenticationOnBoardBankActivity.this)
                                    .setTitle(message)
                                    .setMessage(message)
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();

                        }

                        else if (responseCode.equalsIgnoreCase("AUTH")) {
                            progressDialog.show();

                            String message = responseObject.getString("status");
                            String data = responseObject.getString("data");
                            new AlertDialog.Builder(TwoFactorAuthenticationOnBoardBankActivity.this)
                                    .setTitle("Message")
                                    .setMessage(message)
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }

                        else if (responseCode.equalsIgnoreCase("ERR")) {
                            progressDialog.show();

                            String message = responseObject.getString("status");
                            String data = responseObject.getString("data");
                            new AlertDialog.Builder(TwoFactorAuthenticationOnBoardBankActivity.this)
                                    .setTitle(message)
                                    .setMessage(data)
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }
                        else {
                            progressDialog.dismiss();
                            String message = responseObject.getString("data");
                            showMessageDialog(message);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        showMessageDialog("Something Went Wrong.");
                    }
                } else {
                    progressDialog.dismiss();
                    showMessageDialog(response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                showMessageDialog(t.getMessage());

            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (userDetailLayout.getVisibility() == View.VISIBLE) {
            /*
            //////////////////RESET FIRST LAYOUT VIEWS//////////////////////
            imgCashWithdraw.setImageResource(R.drawable.cash_withdraw_unselected);
            imgBalanceEnquiry.setImageResource(R.drawable.balance_enquiry_unselected);
            imgMiniStatement.setImageResource(R.drawable.mini_statement_unselected);
            imgAadharPay.setImageResource(R.drawable.aadhar_pay_unselected);

            tvCashWithdraw.setTextColor(getResources().getColor(R.color.white));
            tvBalanceEnquiry.setTextColor(getResources().getColor(R.color.white));
            tvMiniStatement.setTextColor(getResources().getColor(R.color.white));
            tvAadharPay.setTextColor(getResources().getColor(R.color.white));

            selectedTransactionType = "select";
            //////////////////RESET FIRST LAYOUT VIEWS//////////////////////

            //////////////////RESET SECOND LAYOUT VIEWS///////////////////////////////
            etMobile.setText("");
            etAadharCard.setText("");
            etAmount.setText("");
            rgFirst.clearCheck();
            rgSecond.clearCheck();
            tvBankName.setText("");
            //////////////////RESET SECOND LAYOUT VIEWS///////////////////////////////

             */


            userDetailLayout.setVisibility(View.GONE);
            transactionTypeLayout.setVisibility(View.VISIBLE);
        } else if (deviceLayout.getVisibility() == View.VISIBLE) {
            deviceLayout.setVisibility(View.GONE);
            userDetailLayout.setVisibility(View.VISIBLE);
            /*

            //////////////////RESET SECOND LAYOUT VIEWS///////////////////////////////
            etMobile.setText("");
            etAadharCard.setText("");
            etAmount.setText("");
            rgFirst.clearCheck();
            rgSecond.clearCheck();
            tvBankName.setText("");
            //////////////////RESET SECOND LAYOUT VIEWS///////////////////////////////

            //////////////////RESET THIRD LAYOUT VIEWS///////////////////////////////
            imgMorpho.setImageResource(R.drawable.morpho_unselected);
            imgStartek.setImageResource(R.drawable.startek_unselected);
            imgMantra.setImageResource(R.drawable.mantra_unselected);

            tvMorpho.setTextColor(getResources().getColor(R.color.white));
            tvStartek.setTextColor(getResources().getColor(R.color.white));
            tvMantra.setTextColor(getResources().getColor(R.color.white));

            selectedDevice = "select";
            //////////////////RESET THIRD LAYOUT VIEWS///////////////////////////////

             */


        } else {
            super.onBackPressed();
        }
    }

    private void initViews() {
        ////////////////TRANSACTION TYPE LAYOUT 1st LAYOUT//////////////
        cashWithDrawLayout = findViewById(R.id.cash_withdraw_layout);


        imgCashWithdraw = findViewById(R.id.img_cash_withdraw);


        tvCashWithdraw = findViewById(R.id.tv_cash_withdraw);


        transactionTypeLayout = findViewById(R.id.transaction_type_layout);

        btnProceedTransactionType = findViewById(R.id.btn_proceed_transaction_type);
        ////////////////TRANSACTION TYPE LAYOUT 1st LAYOUT//////////////

        ////////////////USER DETAIL LAYOUT 2nd LAYOUT//////////////

        userDetailLayout = findViewById(R.id.user_detail_layout);


        etMobile = findViewById(R.id.et_mobile_number);
        etAadharCard = findViewById(R.id.et_aadhar_number);



        ckbTermsAndCondition = findViewById(R.id.ckb_terms_condition);



        btnProceedUserDetail = findViewById(R.id.btn_proceed_user_details);

        ////////////////USER DETAIL LAYOUT 2nd LAYOUT//////////////


        ////////////////DEVICE LAYOUT 3rd LAYOUT//////////////
        deviceLayout = findViewById(R.id.device_layout);

        imgMorpho = findViewById(R.id.img_morpho);
        imgMantra = findViewById(R.id.img_mantra);
        imgStartek = findViewById(R.id.img_startek);
        imgEvolute = findViewById(R.id.img_evolute);
        imgVriddhi = findViewById(R.id.img_vriddhi);

        morphoLayout = findViewById(R.id.morpho_layout);
        mantraLayout = findViewById(R.id.mantra_layout);
        startekLayout = findViewById(R.id.startek_layout);
        evoluteLayout = findViewById(R.id.evolute_layout);
        vriddhiLayout = findViewById(R.id.vriddhi_layout);

        tvStartek = findViewById(R.id.tv_startek);
        tvMorpho = findViewById(R.id.tv_morpho);
        tvMantra = findViewById(R.id.tv_mantra);
        tvEvolute = findViewById(R.id.tv_evolute);
        tvVriddhi = findViewById(R.id.tv_vriddhi);

        btnProceedDeviceLayout = findViewById(R.id.btn_proceed_device);
        ////////////////DEVICE LAYOUT 3rd LAYOUT//////////////

        tvBalance = findViewById(R.id.tv_aeps_balance);
        tvTitle = findViewById(R.id.tv_title);



    }

    private void getAepsBalance() {


        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getAepsBalance(AUTH_KEY, userId, deviceId, deviceInfo, "Login", "");

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;
                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));

                        String statuscode = jsonObject1.getString("statuscode");

                        if (statuscode.equalsIgnoreCase("TXN")) {

                            JSONObject jsonObject = jsonObject1.getJSONObject("data");
                            String aepsBalance = jsonObject.getString("userBalance");
                            tvBalance.setText("₹ " + aepsBalance);

                        }
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }

                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }


    private void getBalance() {

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getBalance(AUTH_KEY, userId, deviceId, deviceInfo, "Login", "0");

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;
                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));

                        String statuscode = jsonObject1.getString("statuscode");

                        if (statuscode.equalsIgnoreCase("TXN")) {


                            JSONObject jsonObject = jsonObject1.getJSONObject("data");
                            String balance = jsonObject.getString("userBalance");
                            tvBalance.setText("\u20b9"+balance);


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAepsBalance();
        // getBalance();
    }



}