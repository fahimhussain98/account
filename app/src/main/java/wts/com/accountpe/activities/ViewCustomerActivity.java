package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

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
import wts.com.accountpe.adapters.ViewCustomersAdapter;
import wts.com.accountpe.models.ViewUsersModel;
import wts.com.accountpe.retrofit.RetrofitClient;

public class ViewCustomerActivity extends AppCompatActivity {
    ImageView imgClose;
    RecyclerView recyclerView;
    String  userid;
    SharedPreferences sharedPreferences;
    ArrayList<ViewUsersModel> viewUsersModelArrayList;
    String deviceId,deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_customer);

        initViews();

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ViewCustomerActivity.this);
        userid = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        if (checkInternetState()) {
            getUsers();

        } else {
            showSnackBar("No Internet");
        }

    }

    private void getUsers() {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(ViewCustomerActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getUsers(AUTH_KEY,deviceId,deviceInfo,userid,"ALL");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = jsonObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            viewUsersModelArrayList=new ArrayList<>();
                            JSONArray transactionArray=jsonObject.getJSONArray("data");

                            for (int i=0;i<transactionArray.length();i++)
                            {
                                ViewUsersModel viewUsersModel=new ViewUsersModel();

                                JSONObject transactionObject=transactionArray.getJSONObject(i);

                                String name=transactionObject.getString("UserName");
                                String userId=transactionObject.getString("ID");
                                String userType=transactionObject.getString("UserType1");
                                String number=transactionObject.getString("MobileNo");
                                String date=transactionObject.getString("CreatedDate");
                                String balance=transactionObject.getString("mainwalletbalance");


                                String[] dateArr=date.split("T");
                                date=dateArr[0];

                                viewUsersModel.setName(name);
                                viewUsersModel.setUserid(userId);
                                viewUsersModel.setMobileNo(number);
                                viewUsersModel.setDate(date);
                                viewUsersModel.setUserType(userType);
                                viewUsersModel.setWalletBalance(balance);

                                viewUsersModelArrayList.add(viewUsersModel);

                            }

                            pDialog.dismiss();

                            recyclerView.setLayoutManager(new LinearLayoutManager(ViewCustomerActivity.this,
                                    RecyclerView.VERTICAL, false));

                            ViewCustomersAdapter viewCustomersAdapter = new ViewCustomersAdapter(viewUsersModelArrayList);
                            recyclerView.setAdapter(viewCustomersAdapter);


                        } else
                        {
                            pDialog.dismiss();
                            showSnackBar("No User Found!!!");
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        showSnackBar("No User Found!!!");

                    }
                } else {
                    pDialog.dismiss();
                    showSnackBar("No User Found!!!");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                showSnackBar("No User Found!!!");
            }
        });

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

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.view_users_layout), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void initViews() {
        imgClose = findViewById(R.id.img_close);
        recyclerView = findViewById(R.id.recycler_view);
    }
}