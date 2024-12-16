package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.retrofit.RetrofitClient;


public class EditProfileActivity extends AppCompatActivity {

    EditText etName, etMail, etMobile, etAddress;
    AppCompatButton btnProceed;

    SharedPreferences sharedPreferences;
    String deviceId, deviceInfo, userId;
    String oldMobileNo, oldEmailId, name, address;
    boolean isChangeMobileNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initViews();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(EditProfileActivity.this);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        userId = sharedPreferences.getString("userid", null);
        oldMobileNo = sharedPreferences.getString("mobileno", null);
       // oldEmailId = sharedPreferences.getString("email", null);
        oldEmailId = sharedPreferences.getString("emailId", null);
        name = sharedPreferences.getString("username", null);
        address = sharedPreferences.getString("address", null);
        isChangeMobileNo = getIntent().getBooleanExtra("changeMobileNo", false);


        etMobile.setText(oldMobileNo);
        etMail.setText(oldEmailId);
        etName.setText(name);
        etAddress.setText(address);

        if (isChangeMobileNo) {
            etName.setEnabled(false);
            etMail.setEnabled(false);
            etAddress.setEnabled(false);
        }


        btnProceed.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(etName.getText().toString()) && !TextUtils.isEmpty(etMobile.getText().toString())
                    && !TextUtils.isEmpty(etMail.getText().toString()) && !TextUtils.isEmpty(etAddress.getText().toString())) {
                checkOtp();
            } else {
                Toast.makeText(EditProfileActivity.this, "All Fields Are Mandatory", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void editProfile() {
        final AlertDialog pDialog = new AlertDialog.Builder(EditProfileActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        String mobileNo = etMobile.getText().toString().trim();
        String mailId = etMail.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().editProfile(AUTH_KEY, userId, deviceId, deviceInfo, mobileNo, oldMobileNo,
                mailId, oldEmailId, address, name);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String message = responseObject.getString("data");

                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(EditProfileActivity.this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", name);
                        editor.putString("mobileno", mobileNo);
                        editor.putString("email", mailId);
                        editor.putString("address", address);
                        editor.apply();

                        new androidx.appcompat.app.AlertDialog.Builder(EditProfileActivity.this)
                                .setTitle("Message")
                                .setMessage(message)
                                .setCancelable(false)
                                .setPositiveButton("Got it!!!", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                })
                                .show();

                        pDialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(EditProfileActivity.this)
                                .setTitle("Message")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Got it!!!", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(EditProfileActivity.this)
                            .setTitle("Message")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Got it!!!", null)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(EditProfileActivity.this)
                        .setMessage(t.getMessage())
                        .setPositiveButton("Got it!!!", null)
                        .show();
            }
        });
    }

    private void checkOtp() {

        final AlertDialog progressDialog = new AlertDialog.Builder(EditProfileActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setView(convertView);
        progressDialog.setCancelable(false);
        progressDialog.show();


        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getOtp(AUTH_KEY, userId, deviceId, deviceInfo, oldEmailId, oldMobileNo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            progressDialog.dismiss();
                            String otp = responseObject.getString("data");
                            showOtpDialog(otp);
                        }

                        else if (responseCode.equalsIgnoreCase("NPA"))
                        {
                            progressDialog.dismiss();
                            editProfile();
                        }

                        else {
                            progressDialog.dismiss();
                            String message = responseObject.getString("data");
                            new androidx.appcompat.app.AlertDialog.Builder(EditProfileActivity.this)
                                    .setMessage(message)
                                    .show();
                        }

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(EditProfileActivity.this)
                                .setMessage("Something went wrong")
                                .show();
                    }
                } else {
                    progressDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(EditProfileActivity.this)
                            .setMessage("Something went wrong")
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(EditProfileActivity.this)
                        .setMessage("Something went wrong")
                        .show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showOtpDialog(final String Otp) {
        View addSenderDialogView = getLayoutInflater().inflate(R.layout.add_sender_otp_dialog_layout, null, false);
        final androidx.appcompat.app.AlertDialog addSenderDialog = new androidx.appcompat.app.AlertDialog.Builder(EditProfileActivity.this).create();
        addSenderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addSenderDialog.setCancelable(false);
        addSenderDialog.setView(addSenderDialogView);
        addSenderDialog.show();

        ImageView imgClose = addSenderDialogView.findViewById(R.id.img_close);
        TextView tvTitle = addSenderDialogView.findViewById(R.id.text_user_registration);
        final EditText etOtp = addSenderDialogView.findViewById(R.id.et_otp);
        Button btnCancel = addSenderDialogView.findViewById(R.id.btn_cancel);
        Button btnSubmit = addSenderDialogView.findViewById(R.id.btn_submit);
        Button btnResendOtp = addSenderDialogView.findViewById(R.id.btn_resend_otp);

        btnResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOtp();
                addSenderDialog.dismiss();
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSenderDialog.dismiss();
            }
        });
        tvTitle.setText("OTP Verification");
        etOtp.setHint("OTP");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSenderDialog.dismiss();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etOtp.getText())) {
                    String userOtp = etOtp.getText().toString().trim();
                    if (userOtp.equals(Otp)) {
                        editProfile();


                    } else {
                        new androidx.appcompat.app.AlertDialog.Builder(EditProfileActivity.this)
                                .setMessage("Wrong Otp")
                                .show();
                    }
                } else {
                    etOtp.setError("Required");
                }
            }
        });
    }

    private void initViews() {
        etName = findViewById(R.id.et_name);
        etMail = findViewById(R.id.et_email);
        etMobile = findViewById(R.id.et_mobile_number);
        etAddress = findViewById(R.id.et_address);
        btnProceed = findViewById(R.id.btn_proceed);
    }
}