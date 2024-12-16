package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.databinding.ActivityHomeDashBinding;
import wts.com.accountpe.fragments.AllServicesFragment;
import wts.com.accountpe.fragments.HomeFragment;
import wts.com.accountpe.fragments.OthersFragment;
import wts.com.accountpe.fragments.ProfileFragment;
import wts.com.accountpe.fragments.ReportsFragment;
import wts.com.accountpe.retrofit.RetrofitClient;

public class HomeDashActivity extends AppCompatActivity {
    ActivityHomeDashBinding binding;
    String userId, deviceId, deviceInfo, loginUserName, password;
    String balance, aepsBalance;
    SharedPreferences sharedPreferences;
    TextView tvNotificationSize;

    // in app update

    AppUpdateManager appUpdateManager;
    int request_code = 1;

    ////////////////////////

    public static String kycStatus = "santosh";
    boolean isSubmitButtonClicked = false;
    int FILE_PERMISSION = 45;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_dash);

        In_app_update();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(HomeDashActivity.this);
        userId            = sharedPreferences.getString("userid", null);
        deviceId          = sharedPreferences.getString("deviceId", null);
        deviceInfo        = sharedPreferences.getString("deviceInfo", null);
        loginUserName     = sharedPreferences.getString("loginUserName", null);
        password          = sharedPreferences.getString("password", null);

        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_MEDIA_IMAGES, FILE_PERMISSION);

        loadFragment(new HomeFragment());
        if (userId != null) {
            getPopUpBanner();
        }

        binding.bottomNavBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_home:
                        if (userId != null) {
                            loadFragment(new HomeFragment());
                        } else {
                            showLoginDialog();
                        }

                        return true;
                    case R.id.bottom_more:
                        if (userId != null) {
                            loadFragment(new OthersFragment());
                        } else {
                            showLoginDialog();
                        }
                        return true;
                    case R.id.bottom_bank:
                        if (userId != null) {
                            loadFragment(new AllServicesFragment());
                        } else {
                            showLoginDialog();
                        }
                        return true;

                    case R.id.bottom_report:
                        if (userId != null) {
                            loadFragment(new ReportsFragment());
                        } else {
                            showLoginDialog();
                        }
                        return true;

                    case R.id.bottom_profile:
                        if (userId != null) {
                            loadFragment(new ProfileFragment());
                        } else {
                            showLoginDialog();
                        }
                        return true;

                    default:
                        return true;
                }
            }
        });

        binding.fabButton.setOnClickListener(v -> {
            if (userId != null) {
                loadFragment(new AllServicesFragment());
                binding.bottomNavBar.setSelectedItemId(R.id.bottom_bank);
            } else {
                showLoginDialog();
            }

        });

        binding.imgLogout.setOnClickListener(v -> {
            if (userId != null) {
                final View view1 = LayoutInflater.from(HomeDashActivity.this).inflate(R.layout.logout_dialog_layout, null, false);
                final AlertDialog logoutDialog = new AlertDialog.Builder(HomeDashActivity.this).create();
                logoutDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                logoutDialog.setCancelable(false);
                logoutDialog.setView(view1);
                logoutDialog.show();

                final Button btnCancel = logoutDialog.findViewById(R.id.btn_cancel);
                Button btnYes = logoutDialog.findViewById(R.id.btn_yes);

                assert btnCancel != null;
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        logoutDialog.dismiss();
                    }
                });

                assert btnYes != null;
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(HomeDashActivity.this).edit();
                        editor.clear();
                        editor.apply();
                        //  startActivity(new Intent(HomeDashActivity.this, LoginActivity.class));
                        startActivity(new Intent(HomeDashActivity.this, LoginNewActivity.class));
                        finish();
                    }
                });
            } else {
                showLoginDialog();
            }
        });

        binding.imgWhatsapp.setOnClickListener(view -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=+919973299911&text=Hello I am"));
                startActivity(intent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        binding.imgBell.setOnClickListener(view -> {
            if (userId != null) {
                startActivity(new Intent(HomeDashActivity.this, BellNotificationActivity.class));
            } else {
                showLoginDialog();
            }
        });

        binding.imgWalletTransfer.setOnClickListener(view -> {
            //   startActivity(new Intent(requireActivity(), WalletToWalletActivity.class));  //  for single wallet

            Intent in = new Intent(HomeDashActivity.this, MainAepsWalletToWalletActivity.class);     //  for double wallet
            in.putExtra("mainBalance", balance);
            in.putExtra("aepsBalance", aepsBalance);
            startActivity(in);
        });

    }

    public void checkPermission(String writePermission, String readPermission,String mediaFilePermission, int requestCode) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S)
        {
            if (ContextCompat.checkSelfPermission(HomeDashActivity.this, mediaFilePermission) == PackageManager.PERMISSION_DENIED)
            {
                ActivityCompat.requestPermissions(HomeDashActivity.this, new String[]{mediaFilePermission}, requestCode);
            }

        }
        else
        {
            if (ContextCompat.checkSelfPermission(HomeDashActivity.this, writePermission) == PackageManager.PERMISSION_DENIED
                    && ContextCompat.checkSelfPermission(HomeDashActivity.this, readPermission) == PackageManager.PERMISSION_DENIED) {
                // Requesting the permission
                ActivityCompat.requestPermissions(HomeDashActivity.this, new String[]{writePermission, readPermission}, requestCode);
            }
        }

    }

    public void showPopUpDialog(String popUpBanner) {
        Dialog dialog = new Dialog(HomeDashActivity.this);
        dialog.setContentView(R.layout.popupbanner_dialog);
        ImageView imgPopUp = dialog.findViewById(R.id.imgPopUpBanner);
        ImageView btnCancel = dialog.findViewById(R.id.imgCancel);
        dialog.getWindow().setLayout((int) (getResources().getDisplayMetrics().widthPixels * 0.9), ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.transparent_background);
        View v = dialog.getWindow().getDecorView();
        ObjectAnimator scaleUp = ObjectAnimator.ofPropertyValuesHolder(v, PropertyValuesHolder.ofFloat("scaleX", 0.0f, 1.0f), PropertyValuesHolder.ofFloat("scaleY", 0.0f, 1.0f), PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f));
        scaleUp.setDuration(300);
        scaleUp.start();

        if (popUpBanner.contains("Upload")) {
            Picasso.get().load("http://login.accountpe.in" + popUpBanner).into(imgPopUp);
        } else {
            imgPopUp.setBackgroundResource(R.drawable.logo);
        }

        btnCancel.setOnClickListener(view -> {
            ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(v, PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.0f), PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.0f), PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f));
            scaleDown.setDuration(300);
            scaleDown.start();
            scaleDown.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();

                }
            });

        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void getPopUpBanner() {
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getPopupBanner(AUTH_KEY, userId, deviceId, deviceInfo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");
                        if (responseCode.equalsIgnoreCase("TXN")) {

                            JSONObject dataObject = responseObject.getJSONObject("data");

                            String popUpBanner = dataObject.getString("Banner");
                            popUpBanner = popUpBanner.replace("~", "");
                            showPopUpDialog(popUpBanner);
                        } else if (responseCode.equalsIgnoreCase("ERR")) {
//                            String message = responseObject.getString("data");
//                            Toast.makeText(HomeDashActivity.this, message, Toast.LENGTH_SHORT).show();

                        } else {
                            //   Toast.makeText(HomeDashActivity.this, "Popup Error", Toast.LENGTH_SHORT).show();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        //  Toast.makeText(HomeDashActivity.this, "Popup Error", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    //  Toast.makeText(HomeDashActivity.this, "Popup Error", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                //  Toast.makeText(HomeDashActivity.this, "Popup Error", Toast.LENGTH_SHORT).show();

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
                            balance = jsonObject.getString("userBalance");
                            binding.tvBalance.setText("₹" + balance);

                        } else {
                            binding.tvBalance.setText("₹00.00");
                        }

                    } catch (JSONException e) {
                        binding.tvBalance.setText("₹00.00");
                        e.printStackTrace();
                    }

                } else {
                    binding.tvBalance.setText("₹00.00");
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

                binding.tvBalance.setText("₹" + "00.00");

            }
        });
    }

 /*   private void getSalesReport() {
        SimpleDateFormat webServiceDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        Calendar newDate1 = Calendar.getInstance();
        newDate1.set(year, month, day);

        String strFromDate = webServiceDateFormat.format(newDate1.getTime());
        String strToDate = webServiceDateFormat.format(newDate1.getTime());

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getTotalEarning(AUTH_KEY, userId, deviceId, deviceInfo, strFromDate, strToDate);

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
                            JSONObject dataObject = dataArray.getJSONObject(0);

                            String totalEarning = dataObject.getString("TotalCommission");

                            binding.tvEarning.setText(" \u20b9" + totalEarning);

                        } else {
                            binding.tvEarning.setText(" \u20b9" + 0.00);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        binding.tvEarning.setText(" \u20b9" + 0.00);
                    }

                } else {
                    binding.tvEarning.setText(" \u20b9" + 0.00);
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                binding.tvEarning.setText("\u20b9" + 0.00);
            }
        });
    }*/

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

                        } else if (statusCode.equalsIgnoreCase("ERR")) {
                            String message = jsonObject1.getString("data");
                            Toast.makeText(HomeDashActivity.this, message, Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(HomeDashActivity.this, "aeps balance error", Toast.LENGTH_SHORT).show();

                        }

                    } catch (JSONException e) {
                        Toast.makeText(HomeDashActivity.this, "aeps balance error", Toast.LENGTH_SHORT).show();

                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(HomeDashActivity.this, "aeps balance error", Toast.LENGTH_SHORT).show();
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Toast.makeText(HomeDashActivity.this, "aeps balance error", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showLoginDialog() {
        final View view1 = LayoutInflater.from(HomeDashActivity.this).inflate(R.layout.login_dialog, null, false);
        final AlertDialog builder = new AlertDialog.Builder(HomeDashActivity.this).create();
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
            //  startActivity(new Intent(HomeDashActivity.this, LoginActivity.class));
            startActivity(new Intent(HomeDashActivity.this, LoginNewActivity.class));
            finish();
        });

        tvSignUp.setOnClickListener(v -> {
            startActivity(new Intent(HomeDashActivity.this, SignUpActivity.class));
            finish();
        });

    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, fragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkInternetState()) {
            if (userId != null) {
                checkOfflineUserOnboard();
                checkCredentials();
                getAllNotification();
                getBalance();
               // getSalesReport();
                getAePSBalance();
                In_app_update();
            }
        } else {
            new AlertDialog.Builder(HomeDashActivity.this)
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

    private boolean checkInternetState() {
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert manager != null;
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
//        showRatingBarDialog();
//       if (isSubmitButtonClicked)
//       {
//          super.onBackPressed();
//       }

        Dialog dialog = new Dialog(HomeDashActivity.this);
        dialog.setContentView(R.layout.rate_us_dialog);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.home_dash_back);
        dialog.getWindow().setLayout((int) (getResources().getDisplayMetrics().widthPixels * 0.95), ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        Button btnSubmit = dialog.findViewById(R.id.btnSubmit);
        Button btnNotNow = dialog.findViewById(R.id.btnCancel);

        btnNotNow.setOnClickListener(view -> {
            super.onBackPressed();
            dialog.dismiss();
        });

        btnSubmit.setOnClickListener(view -> {

            dialog.dismiss();
            Intent in = null;
            try {
                in = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName()));
            } catch (Exception e) {
                in = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
            }
            startActivity(in);
        });

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
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(HomeDashActivity.this);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userid", userId);
                            editor.apply();

                        } else {
                            pDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(HomeDashActivity.this)
                                    .setCancelable(false)
                                    .setMessage("You have to login again.")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(HomeDashActivity.this).edit();
                                            editor.clear();
                                            editor.apply();
                                            //  startActivity(new Intent(HomeDashActivity.this, LoginActivity.class));
                                            startActivity(new Intent(HomeDashActivity.this, LoginNewActivity.class));
                                            finish();
                                        }
                                    }).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(HomeDashActivity.this)
                                .setCancelable(false)
                                .setMessage("You have to login again.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(HomeDashActivity.this).edit();
                                        editor.clear();
                                        editor.apply();
                                        //    startActivity(new Intent(HomeDashActivity.this, LoginActivity.class));
                                        startActivity(new Intent(HomeDashActivity.this, LoginNewActivity.class));
                                        finish();
                                    }
                                }).show();
                    }

                } else {
                    pDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(HomeDashActivity.this)
                            .setCancelable(false)
                            .setMessage("You have to login again.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(HomeDashActivity.this).edit();
                                    editor.clear();
                                    editor.apply();
                                    //  startActivity(new Intent(HomeDashActivity.this, LoginActivity.class));
                                    startActivity(new Intent(HomeDashActivity.this, LoginNewActivity.class));
                                    finish();
                                }
                            }).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(HomeDashActivity.this)
                        .setCancelable(false)
                        .setMessage("You have to login again.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(HomeDashActivity.this).edit();
                                editor.clear();
                                editor.apply();
                                //   startActivity(new Intent(HomeDashActivity.this, LoginActivity.class));
                                startActivity(new Intent(HomeDashActivity.this, LoginNewActivity.class));
                                finish();
                            }
                        }).show();
            }
        });
    }

    private void checkOfflineUserOnboard() {
        ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().checkOfflineUserOnboard(AUTH_KEY, userId, deviceId, deviceInfo, "MERCHANT_STATUS_ONBOARD");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {

                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            pDialog.dismiss();
                            kycStatus = responseObject.getString("status");

                        } else {
                            pDialog.dismiss();
                            String message = responseObject.getString("data");
                            new androidx.appcompat.app.AlertDialog.Builder(HomeDashActivity.this)
                                    .setMessage(message)
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(HomeDashActivity.this)
                                .setMessage("User OnBoard Error")
                                .show();
                    }

                } else {
                    pDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(HomeDashActivity.this)
                            .setMessage("User OnBoard Error")
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(HomeDashActivity.this)
                        .setMessage("User OnBoard Error")
                        .show();
            }
        });
    }

    private void getAllNotification() {

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getAllNotification(AUTH_KEY, userId, deviceId, deviceInfo);
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        String statusCode = responseObject.getString("statuscode");

                        if (statusCode.equalsIgnoreCase("TXN")) {
                            int size = 0;
                            JSONArray dataArray = responseObject.getJSONArray("data");

                            for (int position = 0; position < dataArray.length(); position++) {

                                JSONObject dataObject = dataArray.getJSONObject(position);
                                String isVerify = dataObject.getString("IsVerify");

                                if (isVerify.equalsIgnoreCase("0")) {
                                    size++;
                                }
                            }

                            if (size > 9) {
                                binding.tvNotificationSize.setText("9+");
                            } else {
                                binding.tvNotificationSize.setText("" + size);
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        binding.tvNotificationSize.setText("0");

                    }
                } else {
                    binding.tvNotificationSize.setText("0");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                binding.tvNotificationSize.setText("0");
            }
        });

    }

    //  in app update ..................................................

    public void In_app_update() {
        appUpdateManager = AppUpdateManagerFactory.create(this);
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if ((result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE)
                        && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, HomeDashActivity.this, request_code);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        appUpdateManager.registerListener(installStateUpdatedListener);
    }

    InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(@NonNull InstallState state) {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "App is ready to install", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Install App", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appUpdateManager.completeUpdate();
                        Toast.makeText(HomeDashActivity.this, "App updated successfully", Toast.LENGTH_SHORT).show();
                    }
                });
                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.purple_700));
                snackbar.setTextColor(Color.WHITE);
                snackbar.setActionTextColor(Color.WHITE);

                snackbar.show();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == request_code && resultCode != RESULT_OK) {
            Toast.makeText(this, "update failed" + resultCode, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        In_app_update();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (appUpdateManager != null) {
            appUpdateManager.unregisterListener(installStateUpdatedListener);
        }
    }
   /* @Override
    protected void onStop() {
        super.onStop();
        // Clear login data to force user to re-login
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
*/

/////////////////////////////////////////////////////////////////////////////

}