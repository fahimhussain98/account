package wts.com.accountpe.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.adapters.DthPlansAdapter;
import wts.com.accountpe.models.DthPlansModel;
import wts.com.accountpe.retrofit.RetrofitClient;


public class DthPlansActivity extends AppCompatActivity {

    ListView mPlanList;
    ArrayList<DthPlansModel> dthPlansModelArrayList;
    String operator, userId, deviceId, deviceInfo;
    ProgressDialog pDialog;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dth_plans);

        dthPlansModelArrayList = new ArrayList<>();
        mPlanList = findViewById(R.id.m_plan_list);
        toolbar = findViewById(R.id.toolbar);

        operator = getIntent().getStringExtra("operator");
        userId = getIntent().getStringExtra("userId");
        deviceId = getIntent().getStringExtra("deviceId");
        deviceInfo = getIntent().getStringExtra("deviceInfo");
        toolbar.setTitle(operator + " Plans");

        if (checkInternetState()) {
            getPlans(operator);
        } else {
            showSnackBar();
        }

    }

    private void getPlans(String operator) {

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getDthMplan(userId,deviceId,deviceInfo,operator);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        String data=responseObject.getString("data");
                        JSONObject dataObject=new JSONObject(data);

                        String records=dataObject.getString("records");

                        JSONObject recordsObject = new JSONObject(records);

                        JSONArray plansArray = recordsObject.getJSONArray("Plan");

                        for (int i = 0; i < plansArray.length(); i++) {
                            DthPlansModel dthPlansModel = new DthPlansModel();
                            JSONObject plansObject = plansArray.getJSONObject(i);

                            String desc = plansObject.getString("desc");
                            String planName = plansObject.getString("plan_name");
                            dthPlansModel.setDescription(desc);
                            dthPlansModel.setPlanName(planName);

                            JSONObject rsObject = plansObject.getJSONObject("rs");

                            try {
                                String oneMonth = rsObject.getString("1 MONTHS");
                                dthPlansModel.setRs(oneMonth);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                String threeMonths = rsObject.getString("3 MONTHS");
                                dthPlansModel.setRsThree(threeMonths);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                String sixthMonth = rsObject.getString("6 MONTHS");
                                dthPlansModel.setRsSix(sixthMonth);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                String oneYear = rsObject.getString("1 YEAR");
                                dthPlansModel.setRsOne(oneYear);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            dthPlansModelArrayList.add(dthPlansModel);
                        }

                        DthPlansAdapter dthPlansAdapter = new DthPlansAdapter(DthPlansActivity.this, DthPlansActivity.this, dthPlansModelArrayList);
                        mPlanList.setAdapter(dthPlansAdapter);
                        pDialog.dismiss();

                    } catch (JSONException e) {
                        pDialog.dismiss();
                        new AlertDialog.Builder(DthPlansActivity.this)
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
                    new AlertDialog.Builder(DthPlansActivity.this)
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
                new AlertDialog.Builder(DthPlansActivity.this)
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

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    private void showSnackBar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.dth_plans_layout), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}