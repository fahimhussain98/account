package wts.com.accountpe.fragments;

import static wts.com.accountpe.activities.HomeDashActivity.kycStatus;
import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonObject;
import com.wts.wts_aeps_release.WTS_Aeps_Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.activities.AddMoneyOnlineActivity;
import wts.com.accountpe.activities.AdminQrCodeActivity;
import wts.com.accountpe.activities.CashfreePaymentGatewayActivity;
import wts.com.accountpe.activities.CredoAepsKycActivity;
import wts.com.accountpe.activities.CredoOnboardActivity;
import wts.com.accountpe.activities.ElectricityActivity;
import wts.com.accountpe.activities.ExpressDmtGetSenderActivity;
import wts.com.accountpe.activities.InstantPayAepsTransactionActivity;
import wts.com.accountpe.activities.InstantPayAepsUserOnBoardActivity;
import wts.com.accountpe.activities.InstantPaySettlementActivity;
import wts.com.accountpe.activities.LoginNewActivity;
import wts.com.accountpe.activities.OnBoardPaySprintActivity;
import wts.com.accountpe.activities.PaySprintActivity;
import wts.com.accountpe.activities.PaySprintActivityBank2;
import wts.com.accountpe.activities.PayspirntSettlementActivity;
import wts.com.accountpe.activities.RechargeActivity;
import wts.com.accountpe.activities.ReferEarnActivity;
import wts.com.accountpe.activities.SenderValidationActivity;
import wts.com.accountpe.activities.SettlementActivity;
import wts.com.accountpe.activities.SignUpActivity;
import wts.com.accountpe.activities.TwoFactorAuthenticationActivity;
import wts.com.accountpe.activities.TwoFactorAuthenticationOnBoardBankActivity;
import wts.com.accountpe.activities.UserKycNewActivity;
import wts.com.accountpe.activities.WtsInstantUserOnboardActivity;
import wts.com.accountpe.retrofit.RetrofitClient;
import wts.com.wts_aeps.ui.WtsAepsHome;
import wts.com.wts_aeps.utils.WtsAepsConstants;

public class HomeFragment extends Fragment {

    ImageView imgReferEarn;
    ImageSlider imageSlider;
    ArrayList<SlideModel> mySliderList;
    View view;
    String userId, deviceId, deviceInfo, loginUserName, password, userType, mobileNo, pan;
    SharedPreferences sharedPreferences;
    String balance = "0.0", aepsBalance = "0.00";
    TextView tvBalance, tvAepsBalance, tvNews;
    LinearLayout prepaidLayout, postpaidLayout, dthLayout, googlePlayLayout, landlineLayout, electricityLayout, insuranceLayout,loanRepayment, gasBookingLayout, fastagLayout,
            addMoneyLayout, adminQrCode, aeps2Layout, aeps1Layout, payoutLayout, paySprintPayoutLayout, addMoneyOnlineLayout, paytmPaymentGatewayLayout, upiDmtLayout, dmtLayout, expressDmtLayout;
    String appName;

    //////////////////////////////////////////////LOCATION
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    String lat = "0.0", longi = "0.0";
    String whichButtonClick = "";
    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            lat = mLastLocation.getLatitude() + "";
            longi = mLastLocation.getLongitude() + "";

            if (whichButtonClick.equalsIgnoreCase("aeps1Clicked")) {
                launchWTSAEPS();
            } else {
                launchNewWtsAePS();
            }

        }
    };
    //////////////////////////////////////////////LOCATION

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        loginUserName = sharedPreferences.getString("loginUserName", null);
        password = sharedPreferences.getString("password", null);
        userType = sharedPreferences.getString("usertype", null);
        mobileNo = sharedPreferences.getString("mobileno", null);
        pan = sharedPreferences.getString("pancard", null);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        appName = getAppLabel(requireContext());
        tvNews.setSelected(true);
        //getNews();
        if (userId != null)
            getBanner();
        else
            setImageSlider();

        handleClickEvents();
        return view;
    }

    public String getAppLabel(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(context.getApplicationInfo().packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
        }
        return (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "Unknown");
    }

    private void handleClickEvents() {

        imgReferEarn.setOnClickListener(view -> {

            if (userId != null) {
                startActivity(new Intent(getActivity(), ReferEarnActivity.class));
            } else {
                showLoginDialog();
            }

        });

        prepaidLayout.setOnClickListener(v -> {

            if (userId != null) {
                checkServicePermission("22");
            } else {
                showLoginDialog();
            }

        });

        postpaidLayout.setOnClickListener(view1 -> {

            if (userId != null) {
                checkServicePermission("25");
            } else {
                showLoginDialog();
            }

        });

        dthLayout.setOnClickListener(v -> {

            if (userId != null) {
                checkServicePermission("32");
            } else {
                showLoginDialog();
            }

        });

        googlePlayLayout.setOnClickListener(v -> {

            if (userId != null) {
                checkServicePermission("3014");
            } else {
                showLoginDialog();
            }

        });

        landlineLayout.setOnClickListener(view1 -> {

            showComingSoonDialog();

        });

        electricityLayout.setOnClickListener(v -> {

            if (userId != null) {
                checkServicePermission("24");
            } else {
                showLoginDialog();
            }

        });

        insuranceLayout.setOnClickListener(view1 -> {

            if (userId != null) {
                checkServicePermission("72");
            } else {
                showLoginDialog();
            }

        });

        loanRepayment.setOnClickListener(view1 -> {

            if (userId != null) {
                checkServicePermission("75");
            } else {
                showLoginDialog();
            }

        });
        gasBookingLayout.setOnClickListener(view1 -> {

            if (userId != null) {
                checkServicePermission("71");
            } else {
                showLoginDialog();
            }


        });

        fastagLayout.setOnClickListener(view1 -> {

            if (userId != null) {
                checkServicePermission("70");
            } else {
                showLoginDialog();
            }

        });

        addMoneyLayout.setOnClickListener(view1 -> {

            if (userId != null) {
                startActivity(new Intent(getContext(), AddMoneyOnlineActivity.class));
                //   startActivity(new Intent(getContext(), AddMoneyActivity.class));
                //   checkServicePermission("3085");
            } else {
                showLoginDialog();
            }

        });

        adminQrCode.setOnClickListener(view1 -> {
            startActivity(new Intent(getActivity(), AdminQrCodeActivity.class));
        });

        aeps1Layout.setOnClickListener(view1 -> {

            if (userId != null) {
                if (kycStatus.equalsIgnoreCase("APPROVED")) {
                    whichButtonClick = "aeps1clicked";

                    checkServicePermission("3115");

                } else if (kycStatus.equalsIgnoreCase("DOKYC")) {
//                      Intent intent = new Intent(getActivity(), UserKycActivity.class);
                    Intent intent = new Intent(getActivity(), UserKycNewActivity.class);
                    intent.putExtra("kycStatus", kycStatus);
                    startActivity(intent);
                } else if (kycStatus.equalsIgnoreCase("RJC")) {
                   new AlertDialog.Builder(requireActivity())
                           .setTitle("Message!!!")
                           .setMessage("Your KYC has rejected\nPlease Upload Document Again")
                           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialogInterface, int i) {
                                   Intent intent = new Intent(getActivity(), UserKycNewActivity.class);
                                   intent.putExtra("kycStatus", kycStatus);
                                   startActivity(intent);
                               }
                           }).show();

                } else {
                    new AlertDialog.Builder(requireContext()).setMessage("Your KYC activation is under review. Please wait connect our team will shortly").show();
                }

            } else {
                showLoginDialog();
            }
        });

        aeps2Layout.setOnClickListener(view1 ->     // paySprint aeps
        {
            if (userId != null) {


//                if (kycStatus.equalsIgnoreCase("APPROVED")) {
//                    whichButtonClick = "aeps2clicked";
//                    //  getLastLocation();
//                    checkServicePermission("3031");
//                } else {
//                    Intent intent = new Intent(getActivity(), UserKycActivity.class);
//                    intent.putExtra("kycStatus", kycStatus);
//                    startActivity(intent);
//                }


                if (kycStatus.equalsIgnoreCase("APPROVED")) {
                    whichButtonClick = "aeps2clicked";

                //    checkServicePermission("3031");

                    checkServicePermission("4026");  //  credo aeps working

                } else if (kycStatus.equalsIgnoreCase("DOKYC")) {
                    Intent intent = new Intent(getActivity(), UserKycNewActivity.class);
                    intent.putExtra("kycStatus", kycStatus);
                    startActivity(intent);
                } else if (kycStatus.equalsIgnoreCase("RJC")) {
                    new AlertDialog.Builder(requireActivity())
                            .setTitle("Message!!!")
                            .setMessage("Your KYC has rejected\nPlease Upload Document Again")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(getActivity(), UserKycNewActivity.class);
                                    intent.putExtra("kycStatus", kycStatus);
                                    startActivity(intent);
                                }
                            }).show();
                } else {
                    new AlertDialog.Builder(requireContext()).setMessage("Your KYC activation is under review. Please wait connect our team will shortly").show();
                }

            } else {
                showLoginDialog();
            }

        });

        payoutLayout.setOnClickListener(view1 -> {

            if (userId != null) {

//                if (kycStatus.equalsIgnoreCase("APPROVED")) {
//                    //  checkServicePermission("59");
//                    checkServicePermission("3118");
//                } else {
//                    Intent intent = new Intent(getActivity(), UserKycActivity.class);
//                    intent.putExtra("kycStatus", kycStatus);
//                    startActivity(intent);
//                }

                if (kycStatus.equalsIgnoreCase("APPROVED")) {

                    checkServicePermission("3118");

                } else if (kycStatus.equalsIgnoreCase("DOKYC")) {
                    Intent intent = new Intent(getActivity(), UserKycNewActivity.class);
                    intent.putExtra("kycStatus", kycStatus);
                    startActivity(intent);
                } else if (kycStatus.equalsIgnoreCase("RJC")) {
                    new AlertDialog.Builder(requireActivity())
                            .setTitle("Message!!!")
                            .setMessage("Your KYC has rejected\nPlease Upload Document Again")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(getActivity(), UserKycNewActivity.class);
                                    intent.putExtra("kycStatus", kycStatus);
                                    startActivity(intent);
                                }
                            }).show();
                } else {
                    new AlertDialog.Builder(requireContext()).setMessage("Your KYC activation is under review. Please wait connect our team will shortly").show();
                }


            } else {
                showLoginDialog();
            }
        });

        addMoneyOnlineLayout.setOnClickListener(v -> {
            if (userId != null) {
                startActivity(new Intent(getContext(), AddMoneyOnlineActivity.class));
            } else {
                showLoginDialog();
            }
        });

        paySprintPayoutLayout.setOnClickListener(view1 -> {
            if (userId != null) {
//                if (kycStatus.equalsIgnoreCase("APPROVED")) {
//                    checkServicePermission("3043");
//                } else {
//                    Intent intent = new Intent(getActivity(), UserKycActivity.class);
//                    intent.putExtra("kycStatus", kycStatus);
//                    startActivity(intent);
//                }

                if (kycStatus.equalsIgnoreCase("APPROVED")) {

                    checkServicePermission("3043");

                } else if (kycStatus.equalsIgnoreCase("DOKYC")) {
                    Intent intent = new Intent(getActivity(), UserKycNewActivity.class);
                    intent.putExtra("kycStatus", kycStatus);
                    startActivity(intent);
                } else if (kycStatus.equalsIgnoreCase("RJC")) {
                    new AlertDialog.Builder(requireActivity())
                            .setTitle("Message!!!")
                            .setMessage("Your KYC has rejected\nPlease Upload Document Again")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(getActivity(), UserKycNewActivity.class);
                                    intent.putExtra("kycStatus", kycStatus);
                                    startActivity(intent);
                                }
                            }).show();
                } else {
                    new AlertDialog.Builder(requireContext()).setMessage("Your KYC activation is under review. Please wait connect our team will shortly").show();
                }

            } else {
                showLoginDialog();
            }
        });

        paytmPaymentGatewayLayout.setOnClickListener(view1 -> {
            if (userId != null) {
                // startActivity(new Intent(getActivity(), PaytmPaymentGatewayActivity.class));
                startActivity(new Intent(getActivity(), CashfreePaymentGatewayActivity.class));
            } else {
                showLoginDialog();
            }
        });

        upiDmtLayout.setOnClickListener(view1 -> {
//       if (userId != null)
//       {
//
//           if (kycStatus.equalsIgnoreCase("APPROVED"))
//           {
//               Intent dmtIntent = new Intent(getActivity(), SenderValidationActivity.class);
//               dmtIntent.putExtra("title", "Money Transfer");
//               dmtIntent.putExtra("dmtType", "upiDmt");
//               startActivity(dmtIntent);
//           }
//           else
//           {
//               Intent intent = new Intent(getActivity(), UserKycActivity.class);
//               intent.putExtra("kycStatus", kycStatus);
//               startActivity(intent);
//           }
//       }
//       else
//       {
//           showLoginDialog();
//       }
            showComingSoonDialog();
        });

        dmtLayout.setOnClickListener(view1 -> {
            if (userId != null) {
//                if (kycStatus.equalsIgnoreCase("APPROVED")) {
//                    checkServicePermission("76");
//
//                } else {
//                    Intent intent = new Intent(getActivity(), UserKycActivity.class);
//                    intent.putExtra("kycStatus", kycStatus);
//                    startActivity(intent);
//                }

                if (kycStatus.equalsIgnoreCase("APPROVED")) {

                    checkServicePermission("76");

                } else if (kycStatus.equalsIgnoreCase("DOKYC")) {
                    Intent intent = new Intent(getActivity(), UserKycNewActivity.class);
                    intent.putExtra("kycStatus", kycStatus);
                    startActivity(intent);
                } else if (kycStatus.equalsIgnoreCase("RJC")) {
                    new AlertDialog.Builder(requireActivity())
                            .setTitle("Message!!!")
                            .setMessage("Your KYC has rejected\nPlease Upload Document Again")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(getActivity(), UserKycNewActivity.class);
                                    intent.putExtra("kycStatus", kycStatus);
                                    startActivity(intent);
                                }
                            }).show();
                } else {
                    new AlertDialog.Builder(requireContext()).setMessage("Your KYC activation is under review. Please wait connect our team will shortly").show();
                }


            } else {
                showLoginDialog();
            }
        });

        expressDmtLayout.setOnClickListener(view1 -> {
            if (userId != null) {
//                if (kycStatus.equalsIgnoreCase("APPROVED")) {
//                    checkServicePermission("3025");
//                    //  showComingSoonDialog();
//                } else {
//                    Intent intent = new Intent(getActivity(), UserKycActivity.class);
//                    intent.putExtra("kycStatus", kycStatus);
//                    startActivity(intent);
//                }

                if (kycStatus.equalsIgnoreCase("APPROVED")) {

                    checkServicePermission("3025");

                } else if (kycStatus.equalsIgnoreCase("DOKYC")) {
                    Intent intent = new Intent(getActivity(), UserKycNewActivity.class);
                    intent.putExtra("kycStatus", kycStatus);
                    startActivity(intent);
                } else if (kycStatus.equalsIgnoreCase("RJC")) {
                    new AlertDialog.Builder(requireActivity())
                            .setTitle("Message!!!")
                            .setMessage("Your KYC has rejected\nPlease Upload Document Again")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(getActivity(), UserKycNewActivity.class);
                                    intent.putExtra("kycStatus", kycStatus);
                                    startActivity(intent);
                                }
                            }).show();
                } else {
                    new AlertDialog.Builder(requireContext()).setMessage("Your KYC activation is under review. Please wait connect our team will shortly").show();
                }

            } else {
                showLoginDialog();
            }
        });
    }

    private void checkServicePermission(String permissionId) {
        final ProgressDialog progressDialog = new ProgressDialog(requireActivity());
        progressDialog.setMessage("Please wait while checking permission...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().checkServicePermission(AUTH_KEY, userId, deviceId, deviceInfo, permissionId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = responseObject.getString("statuscode");
                        if (responseCode.equalsIgnoreCase("TXN")) {
                            switch (permissionId) {
                                case "22": {
                                    Intent intent = new Intent(getContext(), RechargeActivity.class);
                                    intent.putExtra("service", "Mobile");
                                    intent.putExtra("gatewayService", "PrePaid");
                                    intent.putExtra("walletBalance", balance);
                                    startActivity(intent);
                                    break;
                                }

                                case "25": {
                                    Intent intent = new Intent(getContext(), ElectricityActivity.class);
                                    intent.putExtra("service", "PostPaid");
                                    intent.putExtra("serviceId", "6");
                                    intent.putExtra("walletBalance", balance);
                                    startActivity(intent);
                                    break;
                                }

                                case "3014": {
                                    Intent intent = new Intent(getContext(), RechargeActivity.class);
                                    intent.putExtra("service", "Google Play");
                                    intent.putExtra("gatewayService", "PrePaid");
                                    intent.putExtra("walletBalance", balance);
                                    startActivity(intent);
                                    break;
                                }

                                case "32": {
                                    Intent intent = new Intent(getContext(), RechargeActivity.class);
                                    intent.putExtra("service", "Dth");
                                    intent.putExtra("gatewayService", "DTH");
                                    intent.putExtra("walletBalance", balance);
                                    startActivity(intent);
                                    break;
                                }

                                case "24": {
                                    Intent intent = new Intent(getContext(), ElectricityActivity.class);
                                    intent.putExtra("service", "Electricity");
                                    intent.putExtra("serviceId", "5");
                                    intent.putExtra("walletBalance", balance);
                                    startActivity(intent);
                                    break;
                                }

                                case "72": {
                                    Intent in = new Intent(getContext(), ElectricityActivity.class);
                                    in.putExtra("service", "INSURANCE");
                                    in.putExtra("serviceId", "13");
                                    in.putExtra("walletBalance", balance);
                                    startActivity(in);
                                    break;
                                }

                                case "75": {
                                    Intent in = new Intent(getContext(), ElectricityActivity.class);
                                    in.putExtra("service", "Loan Repayment");
                                    in.putExtra("serviceId", "9");
                                    in.putExtra("walletBalance", balance);
                                    startActivity(in);
                                    break;
                                }

                                case "71": {
                                    Intent gasIn = new Intent(getContext(), ElectricityActivity.class);
                                    gasIn.putExtra("service", "Gas");
                                    gasIn.putExtra("serviceId", "12");
                                    gasIn.putExtra("walletBalance", balance);
                                    startActivity(gasIn);
                                    break;
                                }

                                case "70": {
                                    Intent fastagIn = new Intent(getContext(), ElectricityActivity.class);
                                    fastagIn.putExtra("service", "FASTAG");
                                    fastagIn.putExtra("serviceId", "17");
                                    fastagIn.putExtra("walletBalance", balance);
                                    startActivity(fastagIn);
                                    break;
                                }

                                case "3085":
                                    startActivity(new Intent(getActivity(), CashfreePaymentGatewayActivity.class));
                                    break;

                                case "3115":
                                    //  checkKycInstatPayStatus();
                                    checkInstantPayAepsKycStatus();
                                    break;

                                case "3031":
                                    checkKycStatus();
                                    break;

                                case "4026":  //  credo aeps


                                    checkKycCredoStatus();

                                    break;

                                case "59":
                                    startActivity(new Intent(getContext(), SettlementActivity.class));
                                    break;

                                case "3118": {
                                    startActivity(new Intent(requireActivity(), InstantPaySettlementActivity.class));
                                    break;
                                }

                                case "3043":
                                    startActivity(new Intent(getContext(), PayspirntSettlementActivity.class));
                                    break;

                                case "76": {
                                    Intent dmtIntent = new Intent(getActivity(), SenderValidationActivity.class);
                                    dmtIntent.putExtra("title", "Money Transfer");
                                    dmtIntent.putExtra("dmtType", "dmt");
                                    startActivity(dmtIntent);
                                    break;
                                }

                                case "3025": {
                                    Intent dmtIntent = new Intent(requireActivity(), ExpressDmtGetSenderActivity.class);
                                    startActivity(dmtIntent);
                                    break;
                                }

                            }
                            progressDialog.dismiss();
                        } else {
                            String message = responseObject.getString("data");
                            progressDialog.dismiss();
                            new AlertDialog.Builder(requireActivity())
                                    .setMessage(message)
                                    .setPositiveButton("ok", null)
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        new AlertDialog.Builder(requireActivity())
                                .setMessage("Please try after sometime.")
                                .setPositiveButton("ok", null)
                                .show();
                    }
                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(requireActivity())
                            .setMessage(response.message())
                            .setPositiveButton("ok", null)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(requireActivity())
                        .setMessage(t.getMessage())
                        .setPositiveButton("ok", null)
                        .show();
            }
        });
    }

    private void checkInstantPayAepsKycStatus() {
        ProgressDialog pDialog = new ProgressDialog(requireActivity());
        pDialog.setCancelable(false);
        pDialog.setMessage("Please wait...");
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().checkInstantPayAepsKycStatus(AUTH_KEY, userId, deviceId, deviceInfo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            pDialog.dismiss();
                            String kycStatus = responseObject.getString("data");
                            Intent instantIntent = null;
                            if (kycStatus.equalsIgnoreCase("true")) {
                                instantIntent = new Intent(requireActivity(), InstantPayAepsTransactionActivity.class);
                                instantIntent.putExtra("balance", balance);
                                startActivity(instantIntent);

                            } else if (kycStatus.equalsIgnoreCase("false")) {
                                instantIntent = new Intent(requireActivity(), InstantPayAepsUserOnBoardActivity.class);
                                startActivity(instantIntent);
                            } else if (kycStatus.equalsIgnoreCase("TFA")) {
                                String message = responseObject.getString("status");

                                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                        .setCancelable(false)
                                        .setMessage("Please do 2FA first")
                                        .setTitle(kycStatus + " Please Proceed ..")
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        })
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                Intent intent = new Intent(requireActivity(), TwoFactorAuthenticationActivity.class);
                                                intent.putExtra("balance", balance);
                                                intent.putExtra("title", "TwoFactorAuth");
                                                intent.putExtra("appId", "11");
                                                startActivity(intent);
                                            }
                                        }).show();

                            }

                        } else {
                            String message = responseObject.getString("data");
                            pDialog.dismiss();
                            new AlertDialog.Builder(requireActivity())
                                    .setTitle("Message")
                                    .setMessage(message)
                                    .setPositiveButton("OK", null)
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(requireActivity())
                                .setTitle("Message")
                                .setMessage("Please try after sometime")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(requireActivity())
                            .setTitle("Message")
                            .setMessage("Please try after sometime")
                            .setPositiveButton("OK", null)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(requireActivity())
                        .setTitle("Message")
                        .setMessage(t.getMessage())
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }

    private void checkTwoFactorAuthentication() {
        ProgressDialog pDialog = new ProgressDialog(requireContext());
        pDialog.setMessage("Loading ....");
        pDialog.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().checkTwoFactorAuthStatus(AUTH_KEY, userId, deviceId, deviceInfo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");
                        String responseData = responseObject.getString("data");

                        if (responseCode.equalsIgnoreCase("TXN")) {

                            if (responseData.equalsIgnoreCase("true")) {

                                pDialog.dismiss();

                                Intent intent = new Intent(requireActivity(), PaySprintActivity.class);
                                intent.putExtra("balance", balance);
                                startActivity(intent);

                            } else if (responseData.equalsIgnoreCase("false")) {

                                pDialog.dismiss();
                                Intent intent = new Intent(requireActivity(), TwoFactorAuthenticationActivity.class);
                                intent.putExtra("balance", balance);
                                intent.putExtra("title", "Two Factor Onboard");
                                intent.putExtra("appId", "");
                                startActivity(intent);

                            } else if (responseData.equalsIgnoreCase("TFA")) {
                                pDialog.dismiss();
                                String message = responseObject.getString("status");
                                String data = responseObject.getString("data");

                                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                        .setCancelable(false)
                                        .setMessage(message)
                                        .setTitle(data + " Please Proceed ..")
                                        .setNegativeButton("No", null)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                Intent intent = new Intent(requireActivity(), TwoFactorAuthenticationActivity.class);
                                                intent.putExtra("balance", balance);
                                                intent.putExtra("title", "TwoFactorAuth");
                                                intent.putExtra("appId", "");
                                                startActivity(intent);

                                            }
                                        }).show();

                            }

                        } else if (responseCode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();

                            String message = responseObject.getString("status");
                            String data = responseObject.getString("data");

                            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                    .setCancelable(false)
                                    .setTitle(data)
                                    .setMessage(message)
                                    .setPositiveButton("Okay", null)
                                    .show();

                        }
                        pDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                    }
                } else {
                    pDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            .setTitle("Message")
                            .setMessage(response.message())
                            .setPositiveButton("OK", null)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
            }
        });

    }

    private void showLoginDialog() {
        final View view1 = LayoutInflater.from(getContext()).inflate(R.layout.login_dialog, null, false);
        final AlertDialog builder = new AlertDialog.Builder(requireContext()).create();
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.getWindow().setWindowAnimations(R.style.SlidingNavDialog);
        builder.setView(view1);
        builder.show();

        AppCompatButton btnLogin = view1.findViewById(R.id.btn_login);
        ImageView imgClose = view1.findViewById(R.id.img_close);
        TextView tvSignUp = view1.findViewById(R.id.tv_sign_up);

        imgClose.setOnClickListener(v -> {
            builder.dismiss();
        });

        btnLogin.setOnClickListener(v -> {
            builder.dismiss();
            //   startActivity(new Intent(getContext(), LoginActivity.class));
            startActivity(new Intent(getContext(), LoginNewActivity.class));
            requireActivity().finish();
        });

        tvSignUp.setOnClickListener(v -> {
            builder.dismiss();
            startActivity(new Intent(getContext(), SignUpActivity.class));
            requireActivity().finish();
        });

    }

    // for aeps

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        task -> {
                            Location location = task.getResult();
                            if (location == null) {
                                requestNewLocationData();
                            } else {
                                lat = location.getLatitude() + "";
                                longi = location.getLongitude() + "";

                                if (whichButtonClick.equalsIgnoreCase("aeps1Clicked")) {
                                    launchWTSAEPS();
                                } else {
                                    launchNewWtsAePS();
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(getContext(), "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
    }

    private void launchWTSAEPS() {

        if (aepsBalance.equalsIgnoreCase("0.00"))
            aepsBalance = "0.0";

        Intent intent = new Intent(getContext(), WTS_Aeps_Activity.class);
        intent.putExtra("app_Id", "201");//app Id provide by WTS Net India Pvt Ltd.
        intent.putExtra("authorise_Key", "rN5QbOg1bdzqEjI3RX/4HJJGQ0Uhd/TCTchfY+daGTEAmdieX1YyOA==");//authorise_Key provide by WTS Net India Pvt Ltd.
        intent.putExtra("main_wallet_bal", aepsBalance);//This is your wallet balance, show in AEPS dashboard
        intent.putExtra("panno", pan);//This is your wallet balance, show in AEPS dashboard
        intent.putExtra("lat", lat);//latitude is mandatory
        intent.putExtra("long", longi); //longitude is mandatory
        startActivity(intent);
    }

    public void launchNewWtsAePS() {
        Intent intent = new Intent(getActivity(), WtsAepsHome.class);
        intent.putExtra(WtsAepsConstants.AEPS_BALANCE, aepsBalance);
        intent.putExtra(WtsAepsConstants.API_KEY, "rN5QbOg1bdzqEjI3RX/4HJJGQ0Uhd/TCTchfY+daGTEAmdieX1YyOA==");
        intent.putExtra(WtsAepsConstants.SECURITY_KEY, "avvTv+CAhQ/SGs5g+7GZxmM/lBHAEId5unnu/tov3qo=");
        intent.putExtra(WtsAepsConstants.PAN_NO, pan);
        intent.putExtra(WtsAepsConstants.MERCHANT_CODE, mobileNo);
        intent.putExtra(WtsAepsConstants.APP_ID, "201");
        intent.putExtra(WtsAepsConstants.LATITUDE, lat);
        intent.putExtra(WtsAepsConstants.LONGITUDE, longi);
        startActivity(intent);
    }

    //////////////////////////////  aeps

    private void showComingSoonDialog() {
        View convertView = getLayoutInflater().inflate(R.layout.comming_soon_dialog, null, false);
        final AlertDialog comingSoonDialog = new AlertDialog.Builder(requireActivity()).create();
        comingSoonDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        comingSoonDialog.setView(convertView);
        comingSoonDialog.show();

        ImageView imgClose = comingSoonDialog.findViewById(R.id.img_close);
        assert imgClose != null;
        imgClose.setOnClickListener(v ->
        {
            comingSoonDialog.dismiss();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (userId != null) {
            getBalance();
            getAePSBalance();
        }
    }

    private void setImageSlider() {
        mySliderList = new ArrayList<>();

        mySliderList.add(new SlideModel(R.drawable.slider1, ScaleTypes.FIT));
        mySliderList.add(new SlideModel(R.drawable.slider2, ScaleTypes.FIT));
        mySliderList.add(new SlideModel(R.drawable.slider3, ScaleTypes.FIT));
        mySliderList.add(new SlideModel(R.drawable.slider4, ScaleTypes.FIT));
        mySliderList.add(new SlideModel(R.drawable.slider1, ScaleTypes.FIT));
        mySliderList.add(new SlideModel(R.drawable.slider2, ScaleTypes.FIT));

        imageSlider.setImageList(mySliderList, ScaleTypes.FIT);
    }

    private void initViews() {
        imgReferEarn = view.findViewById(R.id.img_referandearn);
        imageSlider = view.findViewById(R.id.image_slider);
        tvBalance = view.findViewById(R.id.tv_balance);
        tvAepsBalance = view.findViewById(R.id.tvAepsBalance);
        prepaidLayout = view.findViewById(R.id.prepaid_layout);
        postpaidLayout = view.findViewById(R.id.postpaidLayout);
        dthLayout = view.findViewById(R.id.dth_layout);
        googlePlayLayout = view.findViewById(R.id.googlePlay_layout);
        landlineLayout = view.findViewById(R.id.landlineLayout);
        electricityLayout = view.findViewById(R.id.electricity_layout);
        insuranceLayout = view.findViewById(R.id.insuranceLayout);
        loanRepayment = view.findViewById(R.id.loanEmiLayout);
        gasBookingLayout = view.findViewById(R.id.gasBookingLayout);
        fastagLayout = view.findViewById(R.id.fastagLayout);
        addMoneyLayout = view.findViewById(R.id.addMoneyLayout);
        adminQrCode = view.findViewById(R.id.adminQrCodeLayout);
        aeps1Layout = view.findViewById(R.id.aeps1Layout);
        aeps2Layout = view.findViewById(R.id.aepsLayout);
        payoutLayout = view.findViewById(R.id.payoutLayout);
        paySprintPayoutLayout = view.findViewById(R.id.paysprint_payoutLayout);
        addMoneyOnlineLayout = view.findViewById(R.id.addMoneyOnlineLayout);
        paytmPaymentGatewayLayout = view.findViewById(R.id.paytmPaymentGatewayLayout);
        upiDmtLayout = view.findViewById(R.id.upiDmtLayout);
        dmtLayout = view.findViewById(R.id.DmtLayout);
        expressDmtLayout = view.findViewById(R.id.expressDmtLayout);
        tvNews = view.findViewById(R.id.tv_news);
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

                        String statusCode = jsonObject1.getString("statuscode");

                        if (statusCode.equalsIgnoreCase("TXN")) {

                            JSONObject jsonObject = jsonObject1.getJSONObject("data");
                            balance = jsonObject.getString("userBalance");
                            tvBalance.setText("₹ " + balance);

                        } else {
                            tvBalance.setText("₹ 00.00");
                        }

                    } catch (JSONException e) {
                        tvBalance.setText("₹ 00.00");
                        e.printStackTrace();
                    }

                } else {
                    tvBalance.setText("₹ 00.00");
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

                tvBalance.setText("₹ " + "00.00");

            }
        });
    }

    private void getAePSBalance() {
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getAepsBalance(AUTH_KEY, userId, deviceId, deviceInfo, "Login", "");

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
                            JSONObject jsonObject = jsonObject1.getJSONObject("data");
                            aepsBalance = jsonObject.getString("userBalance");
                            tvAepsBalance.setText("AEPS ₹ " + aepsBalance);

                        } else {
                            tvAepsBalance.setText("AEPS ₹ 00.00");
                        }

                    } catch (JSONException e) {
                        tvAepsBalance.setText("AEPS ₹ 00.00");
                        e.printStackTrace();
                    }

                } else {
                    tvAepsBalance.setText("AEPS ₹ 00.00");
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                tvAepsBalance.setText("AEPS ₹ 00.00");
            }
        });
    }

    private void getBanner() {
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getBanner(AUTH_KEY, userId, deviceId, deviceInfo);

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
                            JSONArray dataArray = jsonObject1.getJSONArray("data");

                            JSONObject jsonObject = dataArray.getJSONObject(0);

                            String banner1 = jsonObject.getString("Banner1");
                            banner1 = banner1.replaceAll("~", "");
                            String banner2 = jsonObject.getString("Banner2");
                            banner2 = banner2.replace("~", "");
                            String banner3 = jsonObject.getString("Banner3");
                            banner3 = banner3.replace("~", "");
                            String banner4 = jsonObject.getString("Banner4");
                            banner4 = banner4.replace("~", "");
                            String banner5 = jsonObject.getString("Banner5");
                            banner5 = banner5.replace("~", "");
                            String banner6 = jsonObject.getString("Banner6");
                            banner6 = banner6.replace("~", "");

                            mySliderList = new ArrayList<>();
                            if (banner1.equalsIgnoreCase("N/A") || banner1.equalsIgnoreCase("")) {
                                mySliderList.add(new SlideModel(R.drawable.slider1, ScaleTypes.FIT));
                            } else {
                                mySliderList.add(new SlideModel("http://login.accountpe.in" + banner1, ScaleTypes.FIT));
                            }
                            if (banner2.equalsIgnoreCase("N/A") || banner2.equalsIgnoreCase("")) {
                                mySliderList.add(new SlideModel(R.drawable.slider2, ScaleTypes.FIT));
                            } else {
                                mySliderList.add(new SlideModel("http://login.accountpe.in" + banner2, ScaleTypes.FIT));
                            }
                            if (banner3.equalsIgnoreCase("N/A") || banner3.equalsIgnoreCase("")) {
                                mySliderList.add(new SlideModel(R.drawable.slider3, ScaleTypes.FIT));
                            } else {
                                mySliderList.add(new SlideModel("http://login.accountpe.in" + banner3, ScaleTypes.FIT));
                            }
                            if (banner4.equalsIgnoreCase("N/A") || banner4.equalsIgnoreCase("")) {
                                mySliderList.add(new SlideModel(R.drawable.slider4, ScaleTypes.FIT));
                            } else {
                                mySliderList.add(new SlideModel("http://login.accountpe.in" + banner4, ScaleTypes.FIT));
                            }
                            if (banner5.equalsIgnoreCase("N/A") || banner5.equalsIgnoreCase("")) {
                                mySliderList.add(new SlideModel(R.drawable.slider3, ScaleTypes.FIT));
                            } else {
                                mySliderList.add(new SlideModel("http://login.accountpe.in" + banner5, ScaleTypes.FIT));
                            }
                            if (banner6.equalsIgnoreCase("N/A") || banner6.equalsIgnoreCase("")) {
                                mySliderList.add(new SlideModel(R.drawable.slider4, ScaleTypes.FIT));
                            } else {
                                mySliderList.add(new SlideModel("http://login.accountpe.in" + banner6, ScaleTypes.FIT));
                            }
                            imageSlider.setImageList(mySliderList, ScaleTypes.FIT);

                        } else {
                            setImageSlider();
                        }

                    } catch (JSONException e) {
                        setImageSlider();
                        e.printStackTrace();
                    }

                } else {
                    setImageSlider();
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                setImageSlider();
            }
        });
    }

    private void checkKycInstatPayStatus() {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().checkUserKycInstantPayStatus(AUTH_KEY, userId, deviceId, deviceInfo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        JSONArray dataArray = responseObject.getJSONArray("data");
                        JSONObject dataObject = dataArray.getJSONObject(0);
                        String status = dataObject.getString("AePS2KycStatus");

                        if (status.equalsIgnoreCase("1")) {
                            getLastLocation();
                        } else {
                            Intent intent = new Intent(getContext(), WtsInstantUserOnboardActivity.class);
                            startActivity(intent);
                        }

                        pDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                    }
                } else {
                    pDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
            }
        });
    }

    private void checkKycCredoStatus() {
        ProgressDialog pDialog = new ProgressDialog(requireContext());
        pDialog.setMessage("Loading ....");
        pDialog.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().checkCredoserKycStatus(AUTH_KEY, userId, deviceId, deviceInfo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("data");

                        Intent intent;
                        if (responseCode.equalsIgnoreCase("true")) {

                            pDialog.dismiss();

                            checkTwoFactorAuthenticationCredo();


                        } else if (responseCode.equalsIgnoreCase("false")) {

                            String message = responseObject.getString("status");
                                /*

                                new AlertDialog.Builder(HomeDashboardActivity.this)
                                        .setTitle("Message")
                                        .setMessage(message)
                                        .setPositiveButton("OK", null)
                                        .show();
                                */

                            Intent intentCredo = new Intent(requireContext(), CredoOnboardActivity.class);
                            intentCredo.putExtra("service", "CredoOnBoard");
                            startActivity(intentCredo);

                            pDialog.dismiss();
                        }

                        else if (responseCode.equalsIgnoreCase("DO_KYC")) {

                            String message = responseObject.getString("status");
                                /*

                                new AlertDialog.Builder(HomeDashboardActivity.this)
                                        .setTitle("Message")
                                        .setMessage(message)
                                        .setPositiveButton("OK", null)
                                        .show();
                                */

                            Intent intentCredo = new Intent(requireContext(), CredoAepsKycActivity.class);
                            intentCredo.putExtra("service", "CredoAepsKyc");
                            startActivity(intentCredo);

                            pDialog.dismiss();
                        }
                        // startActivity(intent);

                        pDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Message")
                            .setMessage(response.message())
                            .setPositiveButton("OK", null)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
            }
        });

    }

    private void checkTwoFactorAuthenticationCredo() {
        ProgressDialog pDialog = new ProgressDialog(requireContext());
        pDialog.setMessage("Loading ....");
        pDialog.setProgress(android.R.style.Widget_ProgressBar_Horizontal);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().checkTwoFactorCredoAuthStatus(AUTH_KEY, userId, deviceId, deviceInfo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");
                        String responseData = responseObject.getString("data");

                        if(responseCode.equalsIgnoreCase("TXN")){

                            pDialog.dismiss();

                            if (responseData.equalsIgnoreCase("true")) {

                                Intent intent = new Intent(requireActivity(), PaySprintActivityBank2.class);
                                //  intent.putExtra("balance", userBal);
                                intent.putExtra("balance", aepsBalance);
                                startActivity(intent);

                            } else if(responseData.equalsIgnoreCase("false")){

                                pDialog.dismiss();

                                Intent intent = new Intent(requireActivity(), TwoFactorAuthenticationOnBoardBankActivity.class);
                                intent.putExtra("balance", aepsBalance);
                                intent.putExtra("title","TwoFactorAuthDaily");
                                intent.putExtra("appId","");
                                startActivity(intent);

                            }

                        } else if(responseCode.equalsIgnoreCase("ERR")){

                            pDialog.dismiss();

                            String message = responseObject.getString("status");
                            String data = responseObject.getString("data");

                            new AlertDialog.Builder(requireContext())
                                    .setCancelable(false)
                                    .setTitle(data)
                                    .setMessage(message)
                                    .setPositiveButton("Okay",null)
                                    .show();

                        }
                        pDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Message")
                            .setMessage(response.message())
                            .setPositiveButton("OK", null)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(requireContext())
                        .setTitle("Message")
                        .setMessage(t.getMessage())
                        .setPositiveButton("OK", null)
                        .show();
            }
        });

    }

    private void checkKycStatus() {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().checkUserKycStatus(AUTH_KEY, userId, deviceId, deviceInfo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");
                        if (responseCode.equalsIgnoreCase("TXN")) {
                            String data = responseObject.getString("data");
                            Intent intent;
                            if (data.equalsIgnoreCase("true")) {

                                //  getLastLocation();

//                               intent = new Intent(getContext(), PaySprintActivity.class);
//                               intent.putExtra("balance", balance);
                                checkTwoFactorAuthentication();

                            } else {
                                intent = new Intent(getContext(), OnBoardPaySprintActivity.class);
                                startActivity(intent);
                            }

                        } else {
                            String message = responseObject.getString("data");
                            new AlertDialog.Builder(requireActivity()).setMessage(message).show();
                        }

                        pDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                    }
                } else {
                    pDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
            }
        });
    }
}