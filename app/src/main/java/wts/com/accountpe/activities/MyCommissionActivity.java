package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import wts.com.accountpe.commFragments.AepsAadharPayFragment;
import wts.com.accountpe.commFragments.AepsCommissionFragment;
import wts.com.accountpe.commFragments.DmtCommissionFragment;
import wts.com.accountpe.commFragments.DthFragment;
import wts.com.accountpe.commFragments.ElectricityCommissionFragment;
import wts.com.accountpe.commFragments.LoanRepaymentCommissionFragment;
import wts.com.accountpe.commFragments.LpgGasCommissionFragment;
import wts.com.accountpe.commFragments.MobileFragment;
import wts.com.accountpe.commFragments.PayoutCommissionFragment;
import wts.com.accountpe.commFragments.PostpaidCommissionFragment;
import wts.com.accountpe.commFragments.ReferEarnFragment;
import wts.com.accountpe.commFragments.UserObnoardCommissionFragment;
import wts.com.accountpe.models.MyCommissionModel;
import wts.com.accountpe.retrofit.RetrofitClient;

public class MyCommissionActivity extends AppCompatActivity {
    String userId;
    SharedPreferences sharedPreferences;
    public static ArrayList<MyCommissionModel> mobileCommissionList, dthCommissionList,postpaidCommissionList, dmtCommissionList,lpgGasCommissionList,electricityCommissionList,
            aepsCommissionList, loanRepaymentCommissionList, payoutCommissionList, userOnboardChargeCommList, aepsAadharPayChargeCommList, referEarnChargeCommList;
    ViewPager viewPager;
    TabLayout tabLayout;
    MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
    String deviceId, deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_commission);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyCommissionActivity.this);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        if (checkInternetState()) {
            getMyCommission();
        } else {
            showSnackBar();
        }

    }

    private void getMyCommission() {

        ProgressDialog pDialog = new ProgressDialog(MyCommissionActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Getting Data...");
        pDialog.show();


        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getMyCommission(AUTH_KEY, userId, deviceId, deviceInfo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {

                            mobileCommissionList = new ArrayList<>();
                            dthCommissionList = new ArrayList<>();
                            dmtCommissionList = new ArrayList<>();
                            electricityCommissionList = new ArrayList<>();
                            lpgGasCommissionList = new ArrayList<>();
                            loanRepaymentCommissionList = new ArrayList<>();
                            postpaidCommissionList = new ArrayList<>();
                            aepsCommissionList = new ArrayList<>();
                            payoutCommissionList = new ArrayList<>();
                            userOnboardChargeCommList = new ArrayList<>();
                            aepsAadharPayChargeCommList = new ArrayList<>();
                            referEarnChargeCommList = new ArrayList<>();

                            myPagerAdapter.addFragment(new MobileFragment(), "Mobile");
                            myPagerAdapter.addFragment(new DthFragment(), "DTH");
                            myPagerAdapter.addFragment(new PostpaidCommissionFragment(), "PostPaid");
                            myPagerAdapter.addFragment(new ElectricityCommissionFragment(), "Electricity");
                            myPagerAdapter.addFragment(new LpgGasCommissionFragment(), "LPG GAS");
                            myPagerAdapter.addFragment(new LoanRepaymentCommissionFragment(), "Loan Emi");
                            myPagerAdapter.addFragment(new DmtCommissionFragment(), "Money Transfer");
                            myPagerAdapter.addFragment(new AepsCommissionFragment(), "AePS");
                            myPagerAdapter.addFragment(new PayoutCommissionFragment(), "Payout");
                            myPagerAdapter.addFragment(new UserObnoardCommissionFragment(), "User Onboard");
                            myPagerAdapter.addFragment(new AepsAadharPayFragment(), "AEPS AADHAR PE");
                            myPagerAdapter.addFragment(new ReferEarnFragment(), "Refer & Earn");

                            JSONArray transactionArray = responseObject.getJSONArray("data");
                            for (int i = 0; i < transactionArray.length(); i++) {

                                JSONObject transactionObject = transactionArray.getJSONObject(i);

                                String service = transactionObject.getString("ServiceName");
                                String operator = transactionObject.getString("OperatorName");
                                String commPer = transactionObject.getString("Commission");
                                String chargePer = transactionObject.getString("Surcharge");
                                boolean chargePerType = transactionObject.getBoolean("SurchargeType");
                                boolean commPerType = transactionObject.getBoolean("CommissionType");

                                if (commPerType)
                                    commPer = commPer+"%";
                                else commPer ="\u20b9"+ commPer;
                                if (chargePerType)
                                    chargePer = chargePer+"%";
                                else chargePer = "\u20b9"+chargePer;

                                if (service.equalsIgnoreCase("MOBILE")) {

                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    mobileCommissionList.add(myCommissionModel);
                                }

                                if (service.equalsIgnoreCase("DTH")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    dthCommissionList.add(myCommissionModel);
                                }

                                if (service.equalsIgnoreCase("PostPaid")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    postpaidCommissionList.add(myCommissionModel);
                                }

                                if (service.equalsIgnoreCase("Money Transfer")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    dmtCommissionList.add(myCommissionModel);
                                }
                                if (service.equalsIgnoreCase("Electricity")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    electricityCommissionList.add(myCommissionModel);
                                }
                                if (service.equalsIgnoreCase("LPG GAS")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    lpgGasCommissionList.add(myCommissionModel);
                                }
                                if (service.equalsIgnoreCase("Loan Repayment")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    loanRepaymentCommissionList.add(myCommissionModel);
                                }
                                if (service.equalsIgnoreCase("AEPS")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    aepsCommissionList.add(myCommissionModel);
                                }

                                if (service.equalsIgnoreCase("Payouts")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    payoutCommissionList.add(myCommissionModel);
                                }
                                if (service.equalsIgnoreCase("User Onboard Charge")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    userOnboardChargeCommList.add(myCommissionModel);
                                }
                                if (service.equalsIgnoreCase("AEPS AADHAR-PE")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    aepsAadharPayChargeCommList.add(myCommissionModel);
                                }

                                if (service.equalsIgnoreCase("Refer & Earn")) {
                                    MyCommissionModel myCommissionModel = new MyCommissionModel();
                                    myCommissionModel.setOperator(operator);
                                    myCommissionModel.setChargePer(chargePer);
                                    myCommissionModel.setCommPer(commPer);

                                    referEarnChargeCommList.add(myCommissionModel);
                                }

                                viewPager.setAdapter(myPagerAdapter);
                                tabLayout.setupWithViewPager(viewPager);

                            }
                        } else {
                            new AlertDialog.Builder(MyCommissionActivity.this)
                                    .setMessage("Something went wrong.")
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                        }
                        pDialog.dismiss();


                    } catch (JSONException e) {
                        pDialog.dismiss();
                        new AlertDialog.Builder(MyCommissionActivity.this)
                                .setMessage("Something went wrong.")
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                        e.printStackTrace();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(MyCommissionActivity.this)
                            .setMessage("Something went wrong.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(MyCommissionActivity.this)
                        .setMessage("Something went wrong.")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
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

    private void showSnackBar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.my_commission_layout), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}