package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.retrofit.RetrofitClient;

public class ChangeTpin extends AppCompatActivity {
    private String userId, username, panCard, mobileNo, userType, userTypeId, deviceId, deviceInfo, aadharCard;
    EditText etEmailid;
    AppCompatButton btnSendMailTpin;
    String emailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_tpin);
        initViews();


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Retrieve values
        userId = sharedPreferences.getString("userid", "");
        username = sharedPreferences.getString("username", "");
        panCard = sharedPreferences.getString("pancard", "");
        mobileNo = sharedPreferences.getString("mobileno", "");
        userType = sharedPreferences.getString("usertype", "");
        userTypeId = sharedPreferences.getString("usertypeId", "");
        deviceId = sharedPreferences.getString("deviceId", "");
        deviceInfo = sharedPreferences.getString("deviceInfo", "");
        aadharCard = sharedPreferences.getString("adharcard", "");

        btnSendMailTpin.setOnClickListener(view -> {
            emailId = etEmailid.getText().toString().trim();
            if (emailId.isEmpty()) {
                etEmailid.setError("Please enter your email ID");
                etEmailid.requestFocus();
            } else if (!checkInternetState()) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            } else {
                changeTpin();
            }
        });



    }


   /* private void changeTpin(String emailId) {

        final android.app.AlertDialog progressDialog = new android.app.AlertDialog.Builder(ChangeTpin.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setView(convertView);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().ChangeTpin(AUTH_KEY, userId, deviceId, "1.1.2", mobileNo, aadharCard, emailId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        String responseCode = jsonObject.getString("statuscode");
                        String statusMessage = jsonObject.getString("status");
                        String dataMessage = jsonObject.getString("data");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            // Display the success message in a Toast
                            Toast.makeText(ChangeTpin.this, statusMessage, Toast.LENGTH_LONG).show();
                            showDialog("Message",dataMessage);
                        } else if (responseCode.equalsIgnoreCase("ERR")) {
                            // Display the error message in a dialog
                            showDialog("Error", dataMessage);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ChangeTpin.this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Toast.makeText(ChangeTpin.this, "Request failed with code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ChangeTpin.this, "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }*/
   private void changeTpin() {

       final android.app.AlertDialog progressDialog = new android.app.AlertDialog.Builder(ChangeTpin.this).create();
       LayoutInflater inflater = getLayoutInflater();
       View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
       progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
       progressDialog.setView(convertView);
       progressDialog.setCancelable(false);
       progressDialog.show();


       Call<JsonObject> call = RetrofitClient.getInstance().getApi().ChangeTpin(AUTH_KEY, userId, deviceId,deviceInfo, "1.1.2", mobileNo, aadharCard, emailId);
       Log.d("ChangeTpin", "AUTH_KEY: " + AUTH_KEY);
       Log.d("ChangeTpin", "userId: " + userId);
       Log.d("ChangeTpin", "deviceId: " + deviceId);
       Log.d("ChangeTpin","deviceInfo"+deviceInfo);
       Log.d("ChangeTpin", "Version: 1.1.2");
       Log.d("ChangeTpin", "mobileNo: " + mobileNo);
       Log.d("ChangeTpin", "aadharCard: " + aadharCard);
       Log.d("EmailChangePin","changeMailId  : "+emailId);
       Log.d("ChangeTpin", "emailId1: " + emailId +"\nemailId2 : "+ emailId);

       call.enqueue(new Callback<JsonObject>() {
           @Override
           public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
               Log.d("ResponseTPIN","TPIN"+response.body());
               progressDialog.dismiss();
               if (response.isSuccessful()) {
                   try {
                       JSONObject jsonObject = new JSONObject(response.body().toString());
                       String responseCode = jsonObject.getString("statuscode");
                       String statusMessage = jsonObject.getString("status");
                       String dataMessage = jsonObject.getString("data");

                       if (responseCode.equalsIgnoreCase("TXN")) {
                           // Display the success message in a Toast
                           Toast.makeText(ChangeTpin.this, statusMessage, Toast.LENGTH_LONG).show();
                           showDialog("Message",dataMessage);
                       } else if (responseCode.equalsIgnoreCase("ERR")) {
                           // Display the error message in a dialog
                           showDialog("Error", dataMessage);
                       }
                   } catch (JSONException e) {
                       e.printStackTrace();
                       Toast.makeText(ChangeTpin.this, "Parsing error", Toast.LENGTH_SHORT).show();
                   }
               } else {

                   Toast.makeText(ChangeTpin.this, "Request failed with code: " + response.code(), Toast.LENGTH_SHORT).show();
               }
           }

           @Override
           public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
               progressDialog.dismiss();
               Toast.makeText(ChangeTpin.this, "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
           }
       });
   }
    // Method to show a dialog box
    private void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangeTpin.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void initViews() {
        etEmailid = findViewById(R.id.et_new_tpin);

        btnSendMailTpin = findViewById(R.id.btn_change_mpin);
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

}