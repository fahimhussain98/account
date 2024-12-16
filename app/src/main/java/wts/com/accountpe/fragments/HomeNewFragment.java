package wts.com.accountpe.fragments;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.activities.AddMoneyOnlineActivity;
import wts.com.accountpe.activities.LoginNewActivity;
import wts.com.accountpe.activities.RechargeActivity;
import wts.com.accountpe.activities.SignUpActivity;
import wts.com.accountpe.retrofit.RetrofitClient;
import wts.com.wts_aeps.ui.WtsAepsHome;
import wts.com.wts_aeps.utils.WtsAepsConstants;

public class HomeNewFragment extends Fragment {

    LinearLayout prepaidLayout, dthLayout, aepsLayout, settlementLayout, dmtLayout;
    String balance = "0.0", aepsBalance = "0.0";
    TextView tvBalance, tvAepsBalance, tvAddMoney;
    SharedPreferences sharedPreferences;
    String userId, deviceId, deviceInfo, loginUserName, password, userType;
    ImageSlider imageSlider;
    ArrayList<SlideModel> mySliderList;
    //////////////////////////////////////////////LOCATION
    String pan, merchantCode;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    String lat = "0.0", longi = "0.0";
    //////////////////////////////////////////////LOCATION

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_new, container, false);
        initViews(view);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        loginUserName = sharedPreferences.getString("loginUserName", null);
        password = sharedPreferences.getString("password", null);
        userType = sharedPreferences.getString("usertype", null);
        merchantCode = sharedPreferences.getString("mobileno", null);
        pan = sharedPreferences.getString("pancard", null);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        handleClickListeners();
        if (userId != null)
            getBanner();
        else
            setImageSlider();

        return view;
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

                            mySliderList = new ArrayList<>();
                            mySliderList.add(new SlideModel("http://login.vrepay.com" + banner1, ScaleTypes.FIT));
                            mySliderList.add(new SlideModel("http://login.vrepay.com" + banner2, ScaleTypes.FIT));
                            mySliderList.add(new SlideModel("http://login.vrepay.com" + banner3, ScaleTypes.FIT));

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

    private void setImageSlider() {
        mySliderList = new ArrayList<>();
        mySliderList.add(new SlideModel(R.drawable.slider1, ScaleTypes.FIT));
        mySliderList.add(new SlideModel(R.drawable.slider2, ScaleTypes.FIT));
        mySliderList.add(new SlideModel(R.drawable.slider3, ScaleTypes.FIT));
        mySliderList.add(new SlideModel(R.drawable.slider4, ScaleTypes.FIT));

        imageSlider.setImageList(mySliderList, ScaleTypes.FIT);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (userId!=null)
        {
            getBalance();
            getAePSBalance();
        }

    }

    private void handleClickListeners() {
        tvAddMoney.setOnClickListener(v ->
        {
            if (userId!=null)
            {
                startActivity(new Intent(getContext(), AddMoneyOnlineActivity.class));
            }
            else
            {
                showLoginDialog();
            }
        });


        prepaidLayout.setOnClickListener(v ->
        {
            if (userId!=null)
            {
                Intent intent = new Intent(getContext(), RechargeActivity.class);
                intent.putExtra("service", "Mobile");
                startActivity(intent);
            }
            else
            {
                showLoginDialog();
            }
        });

        dthLayout.setOnClickListener(v ->
        {
            if (userId!=null)
            {
                Intent intent = new Intent(getContext(), RechargeActivity.class);
                intent.putExtra("service", "Dth");
                intent.putExtra("gatewayService", "Dth");
                startActivity(intent);
            }
            else
            {
                showLoginDialog();
            }
        });

        aepsLayout.setOnClickListener(v ->
        {
            if (userId!=null)
            {
                //getLastLocation();
            }
            else
            {
                showLoginDialog();
            }
        });

        settlementLayout.setOnClickListener(v ->
        {
            if (userId!=null)
            {
                //startActivity(new Intent(getContext(), SettlementActivity.class));
            }
            else
            {
                showLoginDialog();
            }
        });

        dmtLayout.setOnClickListener(v ->
        {
            if (userId!=null)
            {
                //startActivity(new Intent(getContext(), SenderValidationActivity.class));
            }
            else
            {
                showLoginDialog();
            }
        });

    }

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
                                launchWTSAEPS();
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

    private void launchWTSAEPS() {

        if (aepsBalance.equalsIgnoreCase("0.00"))
            aepsBalance = "0.0";


        Intent intent = new Intent(getContext(), WtsAepsHome.class);
        intent.putExtra(WtsAepsConstants.AEPS_BALANCE, aepsBalance);
        intent.putExtra(WtsAepsConstants.API_KEY, "CF4FIh5tnuD5lICq/ld7AtaiCwKD7ZjQ1/5e2mU1UiJHO+rTLlss+A==");
        intent.putExtra(WtsAepsConstants.SECURITY_KEY, "yDb4LBYaC7zNSdBrAU6/7Iqjr2ZQcLOUv+yB/ZNvDYs=");
        intent.putExtra(WtsAepsConstants.APP_ID, "185");
        intent.putExtra(WtsAepsConstants.PAN_NO, pan);
        intent.putExtra(WtsAepsConstants.MERCHANT_CODE, merchantCode);
        intent.putExtra(WtsAepsConstants.LATITUDE, lat);
        intent.putExtra(WtsAepsConstants.LONGITUDE, longi);
        startActivity(intent);


    }

    private void getAePSBalance() {
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getAepsBalance(AUTH_KEY, userId, deviceId, deviceInfo, "Login", "0");

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
                            tvAepsBalance.setText("₹ " + aepsBalance);

                        } else {
                            tvAepsBalance.setText("₹ " + "0.0");

                        }

                    } catch (JSONException e) {

                        e.printStackTrace();
                        tvAepsBalance.setText("₹ " + "0.0");

                    }

                } else {
                    tvAepsBalance.setText("₹ " + "0.0");

                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                tvAepsBalance.setText("₹ " + "0.0");

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            lat = mLastLocation.getLatitude() + "";
            longi = mLastLocation.getLongitude() + "";
            launchWTSAEPS();
        }
    };

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

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
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

        imgClose.setOnClickListener(v ->
        {
            builder.dismiss();
        });

        btnLogin.setOnClickListener(v ->
        {
          //  startActivity(new Intent(getContext(), LoginActivity.class));
            startActivity(new Intent(getContext(), LoginNewActivity.class));
            requireActivity().finish();
        });

        tvSignUp.setOnClickListener(v ->
        {
            startActivity(new Intent(getContext(), SignUpActivity.class));
            requireActivity().finish();
        });

    }

    private void initViews(View view) {
        prepaidLayout = view.findViewById(R.id.prepaid_layout);
        dthLayout = view.findViewById(R.id.dth_layout);
        aepsLayout = view.findViewById(R.id.aeps_layout);
        settlementLayout = view.findViewById(R.id.settlement_layout);
        dmtLayout = view.findViewById(R.id.dmt_layout);
        tvBalance = view.findViewById(R.id.tv_balance);
        tvAddMoney = view.findViewById(R.id.tv_add_money);
        tvAepsBalance = view.findViewById(R.id.tv_aeps_balance);
        imageSlider = view.findViewById(R.id.image_slider);

    }

}