package wts.com.accountpe.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.adapters.MyPagerAdapter;
import wts.com.accountpe.models.PlansModel;
import wts.com.accountpe.plansFragments.ComboFragment;
import wts.com.accountpe.plansFragments.Data2Fragment;
import wts.com.accountpe.plansFragments.DataFragment;
import wts.com.accountpe.plansFragments.FullTTFragment;
import wts.com.accountpe.plansFragments.RateCutterFragment;
import wts.com.accountpe.plansFragments.RoamingFragment;
import wts.com.accountpe.plansFragments.SMSFragment;
import wts.com.accountpe.plansFragments.TopUpFragment;
import wts.com.accountpe.retrofit.RetrofitClient;


public class PlansActivity extends AppCompatActivity {

    String operator, commcircle, userId, deviceId, deviceInfo;

    public static ArrayList<PlansModel> topUpArrayList;
    public static ArrayList<PlansModel> dataArrayList;
    public static ArrayList<PlansModel> data2ArrayList;
    public static ArrayList<PlansModel> smsArrayList;
    public static ArrayList<PlansModel> comboArrayList;
    public static ArrayList<PlansModel> rateCutterArrayList;
    public static ArrayList<PlansModel> roamingArrayList;
    public static ArrayList<PlansModel> fullTTArrayList;


    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;

    ProgressDialog pDialog;

    MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans);

        //////CHANGE COLOR OF STATUS BAR
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(PlansActivity.this, R.color.purple_700));
        //////CHANGE COLOR OF STATUS BAR

        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        operator = getIntent().getStringExtra("operator");
        commcircle = getIntent().getStringExtra("commcircle");
        userId = getIntent().getStringExtra("userId");
        deviceId = getIntent().getStringExtra("deviceId");
        deviceInfo = getIntent().getStringExtra("deviceInfo");

        topUpArrayList = new ArrayList<>();
        dataArrayList = new ArrayList<>();
        data2ArrayList = new ArrayList<>();
        smsArrayList = new ArrayList<>();
        comboArrayList = new ArrayList<>();
        rateCutterArrayList = new ArrayList<>();
        roamingArrayList = new ArrayList<>();
        fullTTArrayList = new ArrayList<>();

        if (checkInternetState()) {
            getPlans();
        } else {
            showSnackBar();
        }


    }

    private void getPlans() {

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getPlans(userId, deviceId, deviceInfo, operator, commcircle);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {


                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        String data = responseObject.getString("data");
                        JSONObject dataObject = new JSONObject(data);


                        JSONObject recordsObject = dataObject.getJSONObject("records");

                        if (recordsObject.has("TOPUP")) {

                            JSONArray topUPArray = recordsObject.getJSONArray("TOPUP");
                            myPagerAdapter.addFragment(new TopUpFragment(), "Top Up");
                            for (int i = 0; i < topUPArray.length(); i++) {
                                PlansModel plansModel = new PlansModel();

                                JSONObject topUpData = topUPArray.getJSONObject(i);
                                String rs = topUpData.getString("rs");
                                String desc = topUpData.getString("desc");
                                String validity = topUpData.getString("validity");


                                plansModel.setRs(rs);
                                plansModel.setValidityText("Validity");
                                plansModel.setDesc(desc);
                                plansModel.setValidity(validity);

                                topUpArrayList.add(plansModel);

                            }
                        }


                        if (recordsObject.has("3G/4G")) {
                            JSONArray dataArray = recordsObject.getJSONArray("3G/4G");
                            myPagerAdapter.addFragment(new DataFragment(), "3G/4G");
                            for (int i = 0; i < dataArray.length(); i++) {
                                PlansModel plansModel = new PlansModel();

                                JSONObject topUpData = dataArray.getJSONObject(i);
                                String rs = topUpData.getString("rs");
                                String desc = topUpData.getString("desc");
                                String validity = topUpData.getString("validity");

                                plansModel.setRs(rs);
                                plansModel.setDesc(desc);
                                plansModel.setValidityText("Validity");
                                plansModel.setValidity(validity);

                                dataArrayList.add(plansModel);

                            }
                        }


                        if (recordsObject.has("2G")) {
                            myPagerAdapter.addFragment(new Data2Fragment(), "2G");
                            JSONArray data2Array = recordsObject.getJSONArray("2G");
                            for (int i = 0; i < data2Array.length(); i++) {
                                PlansModel plansModel = new PlansModel();

                                JSONObject topUpData = data2Array.getJSONObject(i);
                                String rs = topUpData.getString("rs");
                                String desc = topUpData.getString("desc");
                                String validity = topUpData.getString("validity");

                                plansModel.setRs(rs);
                                plansModel.setDesc(desc);
                                plansModel.setValidityText("Validity");
                                plansModel.setValidity(validity);

                                data2ArrayList.add(plansModel);

                            }
                        }


                        if (recordsObject.has("RATE CUTTER")) {
                            myPagerAdapter.addFragment(new RateCutterFragment(), "Rate Cutter");
                            JSONArray rateCutterArray = recordsObject.getJSONArray("RATE CUTTER");
                            for (int i = 0; i < rateCutterArray.length(); i++) {
                                PlansModel plansModel = new PlansModel();

                                JSONObject topUpData = rateCutterArray.getJSONObject(i);
                                String rs = topUpData.getString("rs");
                                String desc = topUpData.getString("desc");
                                String validity = topUpData.getString("validity");

                                plansModel.setRs(rs);
                                plansModel.setDesc(desc);
                                plansModel.setValidityText("Validity");
                                plansModel.setValidity(validity);

                                rateCutterArrayList.add(plansModel);
                            }
                        }


                        if (recordsObject.has("SMS")) {
                            myPagerAdapter.addFragment(new SMSFragment(), "SMS");
                            JSONArray smsArray = recordsObject.getJSONArray("SMS");
                            for (int i = 0; i < smsArray.length(); i++) {
                                PlansModel plansModel = new PlansModel();

                                JSONObject topUpData = smsArray.getJSONObject(i);
                                String rs = topUpData.getString("rs");
                                String desc = topUpData.getString("desc");
                                String validity = topUpData.getString("validity");

                                plansModel.setRs(rs);
                                plansModel.setValidityText("Validity");
                                plansModel.setDesc(desc);
                                plansModel.setValidity(validity);

                                smsArrayList.add(plansModel);
                            }
                        }


                        if (recordsObject.has("Romaing")) {
                            myPagerAdapter.addFragment(new RoamingFragment(), "Roaming");
                            JSONArray roamingArray = recordsObject.getJSONArray("Romaing");
                            for (int i = 0; i < roamingArray.length(); i++) {
                                PlansModel plansModel = new PlansModel();

                                JSONObject topUpData = roamingArray.getJSONObject(i);
                                String rs = topUpData.getString("rs");
                                String desc = topUpData.getString("desc");
                                String validity = topUpData.getString("validity");

                                plansModel.setRs(rs);
                                plansModel.setDesc(desc);
                                plansModel.setValidityText("Validity");
                                plansModel.setValidity(validity);

                                roamingArrayList.add(plansModel);
                            }
                        }


                        if (recordsObject.has("COMBO")) {
                            myPagerAdapter.addFragment(new ComboFragment(), "Combo Offer");
                            JSONArray comboArray = recordsObject.getJSONArray("COMBO");
                            for (int i = 0; i < comboArray.length(); i++) {
                                PlansModel plansModel = new PlansModel();

                                JSONObject topUpData = comboArray.getJSONObject(i);
                                String rs = topUpData.getString("rs");
                                String desc = topUpData.getString("desc");
                                String validity = topUpData.getString("validity");

                                plansModel.setRs(rs);
                                plansModel.setDesc(desc);
                                plansModel.setValidityText("Validity");
                                plansModel.setValidity(validity);

                                comboArrayList.add(plansModel);
                            }
                        }


                        if (recordsObject.has("FULLTT")) {
                            myPagerAdapter.addFragment(new FullTTFragment(), "Full TT");
                            JSONArray fullTTArray = recordsObject.getJSONArray("FULLTT");
                            for (int i = 0; i < fullTTArray.length(); i++) {
                                PlansModel plansModel = new PlansModel();

                                JSONObject topUpData = fullTTArray.getJSONObject(i);
                                String rs = topUpData.getString("rs");
                                String desc = topUpData.getString("desc");
                                String validity = topUpData.getString("validity");

                                plansModel.setRs(rs);
                                plansModel.setDesc(desc);
                                plansModel.setValidityText("Validity");
                                plansModel.setValidity(validity);

                                fullTTArrayList.add(plansModel);
                            }
                        }


                        viewPager.setAdapter(myPagerAdapter);


                        tabLayout.setupWithViewPager(viewPager);

                        pDialog.dismiss();


                    } catch (JSONException e) {
                        pDialog.dismiss();
                        new AlertDialog.Builder(PlansActivity.this)
                                .setCancelable(false)
                                .setMessage("No Plans Found")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
                        e.printStackTrace();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(PlansActivity.this)
                            .setCancelable(false)
                            .setMessage("No Plans Found")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                new AlertDialog.Builder(PlansActivity.this)
                        .setCancelable(false)
                        .setMessage("No Plans Found")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
        });
    }

    private void showSnackBar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.plans_layout), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }
}