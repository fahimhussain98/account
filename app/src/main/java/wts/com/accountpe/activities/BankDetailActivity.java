package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.adapters.BankDetailsAdapter;
import wts.com.accountpe.models.BankDetailsModel;
import wts.com.accountpe.retrofit.RetrofitClient;

public class BankDetailActivity extends AppCompatActivity {
    String userId, deviceId, deviceInfo;
    SharedPreferences sharedPreferences;

    ArrayList<BankDetailsModel> bankDetailsModelArrayList;
    RecyclerView recyclerView;
    ImageView qrImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_detail);


        recyclerView = findViewById(R.id.recycler_view);
        qrImage = findViewById(R.id.qrImage);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        getBankList();
        getQRImage();

    }

    private void getBankList() {
        final ProgressDialog pDialog = new ProgressDialog(BankDetailActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getBankList(AUTH_KEY, userId, deviceId, deviceInfo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {

                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");
                        if (responseCode.equalsIgnoreCase("TXN")) {
                            JSONArray transactionArray = responseObject.getJSONArray("data");
                            bankDetailsModelArrayList = new ArrayList<>();
                            for (int i = 0; i < transactionArray.length(); i++) {

                                BankDetailsModel bankDetailsModel = new BankDetailsModel();

                                JSONObject transactionObject = transactionArray.getJSONObject(i);
                                String bankName = transactionObject.getString("BankName");
                                String accountName = transactionObject.getString("AccountName");
                                String accountNumber = transactionObject.getString("AccountNumber");
                                String ifsc = transactionObject.getString("IFSC");
                                String branch = transactionObject.getString("Branch");

                                bankDetailsModel.setBankName(bankName);
                                bankDetailsModel.setAccountName(accountName);
                                bankDetailsModel.setAccountNumber(accountNumber);
                                bankDetailsModel.setIfscCode(ifsc);
                                bankDetailsModel.setBranch(branch);

                                bankDetailsModelArrayList.add(bankDetailsModel);
                            }

                            BankDetailsAdapter bankDetailsAdapter = new BankDetailsAdapter(bankDetailsModelArrayList);
                            recyclerView.setLayoutManager(new LinearLayoutManager(BankDetailActivity.this,
                                    RecyclerView.VERTICAL, false));
                            recyclerView.setAdapter(bankDetailsAdapter);

                            pDialog.dismiss();
                        } else {
                            pDialog.dismiss();

                        }

                    } catch (Exception e) {
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
    private void getQRImage() {
        final ProgressDialog pDialog = new ProgressDialog(BankDetailActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getQRImage(AUTH_KEY);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {

                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");
                        if (responseCode.equalsIgnoreCase("TXN")) {
                            pDialog.dismiss();
                            String image = responseObject.getString("data");
                            Picasso.get().load(image).into(qrImage);


                        } else {
                            pDialog.dismiss();

                        }

                    } catch (Exception e) {
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