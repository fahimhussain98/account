package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.BuildConfig;
import wts.com.accountpe.R;
import wts.com.accountpe.databinding.ActivitySignUpBinding;
import wts.com.accountpe.retrofit.RetrofitClient;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    String versionCodeStr;
    SimpleDateFormat simpleDateFormat, webServiceDateFormat;
    String selectedDob = "",selectedStateId="",selectedCityId="";

    String referralCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_sign_up);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_sign_up);
        versionCodeStr = BuildConfig.VERSION_NAME;

        binding.etMobileNumber.setText(getIntent().getStringExtra("mobileNo"));

        getReferralCode();

        binding.loginLayout.setOnClickListener(view ->
        {
            onBackPressed();
        });

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        webServiceDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        binding.tvDob.setOnClickListener(v->
        {
            final Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog fromDatePicker = new DatePickerDialog(SignUpActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Calendar newDate1 = Calendar.getInstance();
                    newDate1.set(year, month, dayOfMonth);

                    binding.tvDob.setText(simpleDateFormat.format(newDate1.getTime()));

                    selectedDob = webServiceDateFormat.format(newDate1.getTime());

                }
            }, year, month, day);
            fromDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            fromDatePicker.show();
        });

        getState();

        binding.btnSignUp.setOnClickListener(v -> {
            if (checkInternetState()) {
                if (checkInputs()) {
                  //  addUser();
                    verifyOtp();
                } else {
                    showSnackBar("All fields are mandatory.");
                }
            } else {
                showSnackBar("No Internet");
            }
        });

    }

    private void getState() {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(SignUpActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getState(AUTH_KEY, "userId", "deviceId", "deviceInfo");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {

                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            ArrayList<String> stateList = new ArrayList<>();
                            ArrayList<String> stateIdList = new ArrayList<>();
                            JSONArray transactionArray = responseObject.getJSONArray("data");
                            for (int i = 0; i < transactionArray.length(); i++) {
                                JSONObject transactionObject = transactionArray.getJSONObject(i);

                                String stateName = transactionObject.getString("StateName");
                                String stateId = transactionObject.getString("StateId");
                                stateList.add(stateName);
                                stateIdList.add(stateId);
                            }

                            binding.tvState.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SpinnerDialog operatorDialog = new SpinnerDialog(SignUpActivity.this, stateList, "Select State", R.style.DialogAnimations_SmileWindow, "Close  ");// With 	Animation
                                    operatorDialog.setCancellable(true); // for cancellable
                                    operatorDialog.setShowKeyboard(false);// for open keyboard by default
                                    operatorDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                                        @Override
                                        public void onClick(String item, int position) {
                                            binding.tvState.setText(item);
                                            selectedStateId = stateIdList.get(position);

                                            getCity();

                                        }
                                    });

                                    operatorDialog.showSpinerDialog();
                                }
                            });
                            pDialog.dismiss();

                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(SignUpActivity.this)
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(SignUpActivity.this)
                                .setMessage("Something went wrong.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
                    }

                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(SignUpActivity.this)
                            .setMessage("Something went wrong.")
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
                new AlertDialog.Builder(SignUpActivity.this)
                        .setMessage("Something went wrong.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
        });
    }

    private void getCity() {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(SignUpActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getCity(AUTH_KEY, "userId","deviceId",
                "deviceInfo",selectedStateId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {

                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            ArrayList<String> cityIdList = new ArrayList<>();
                            ArrayList<String> cityList = new ArrayList<>();
                            JSONArray transactionArray = responseObject.getJSONArray("data");
                            for (int i = 0; i < transactionArray.length(); i++) {
                                JSONObject transactionObject = transactionArray.getJSONObject(i);

                                String cityName = transactionObject.getString("CityName");
                                String cityId = transactionObject.getString("CityId");
                                cityIdList.add(cityId);
                                cityList.add(cityName);
                            }

                            binding.tvCity.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SpinnerDialog operatorDialog = new SpinnerDialog(SignUpActivity.this, cityList, "Select City", R.style.DialogAnimations_SmileWindow, "Close  ");// With 	Animation
                                    operatorDialog.setCancellable(true); // for cancellable
                                    operatorDialog.setShowKeyboard(false);// for open keyboard by default
                                    operatorDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                                        @Override
                                        public void onClick(String item, int position) {
                                            binding.tvCity.setText(item);
                                            selectedCityId = cityIdList.get(position);
                                        }
                                    });

                                    operatorDialog.showSpinerDialog();
                                }
                            });
                            pDialog.dismiss();

                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(SignUpActivity.this)
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(SignUpActivity.this)
                                .setMessage("Something went wrong.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
                    }

                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(SignUpActivity.this)
                            .setMessage("Something went wrong.")
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
                new AlertDialog.Builder(SignUpActivity.this)
                        .setMessage("Something went wrong.")
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
        NetworkInfo networkInfo = null;
        if (manager != null) {
            networkInfo = manager.getActiveNetworkInfo();
        }

        if (networkInfo != null) {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    private void addUser() {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(SignUpActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        String ownerName = binding.etName.getText().toString().trim();
        String mobile = binding.etMobileNumber.getText().toString().trim();
        String emailId = binding.etEmail.getText().toString().trim();
       String shopName = binding.etShopName.getText().toString();
        String address = binding.etAddress.getText().toString();
        String pinCode = binding.etPincode.getText().toString();
        String remarks = binding.etRemarks.getText().toString();
        String pancard = binding.etPancard.getText().toString();
        String aadharcard = binding.etAadharcard.getText().toString();

        referralCode = binding.etReferCode.getText().toString().trim();

//        Call<JsonObject> call = RetrofitClient.getInstance().getApi().newUserSignUp(AUTH_KEY,ownerName,selectedDob,shopName,
//                mobile,emailId, address, pinCode,selectedStateId,selectedCityId, "Account Pe",pancard, aadharcard,versionCodeStr, referralCode
//        );

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().newUserSignUp(AUTH_KEY,ownerName,"NA","NA",
                mobile,emailId, "NA", "NA","0","0", "Account Pe","NA", "NA","1.1.2", referralCode);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            String responseMessage = responseObject.getString("data");
                            pDialog.dismiss();
                            new AlertDialog.Builder(SignUpActivity.this)
                                    .setTitle("Status")
                                    .setCancelable(false)
                                    .setMessage(responseMessage)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                         //   startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                                            startActivity(new Intent(SignUpActivity.this,LoginNewActivity.class));
                                            finish();
                                        }
                                    }).show();
                        }

                        else if (responseCode.equalsIgnoreCase("ERR"))
                        {
                            pDialog.dismiss();
                            new AlertDialog.Builder(SignUpActivity.this)
                                    .setTitle("Status !!!")
                                    .setMessage(responseObject.getString("data"))
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }

                        else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(SignUpActivity.this)
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(SignUpActivity.this)
                                .setMessage("Something went wrong.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(SignUpActivity.this)
                            .setMessage("Something went wrong.")
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
                new AlertDialog.Builder(SignUpActivity.this)
                        .setMessage("Something went wrong.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
        });
    }

    private void verifyOtp() {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(SignUpActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        String mobileNo = binding.etMobileNumber.getText().toString().trim();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().sendOTPWithMobileNo(AUTH_KEY, mobileNo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {

                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                         String otp = responseObject.getString("status");
                            showOtpDialog(otp);
                            pDialog.dismiss();

                        }

                        else if (responseCode.equalsIgnoreCase("ERR"))
                        {
                            pDialog.dismiss();
                            String message = responseObject.getString("data");
                            new AlertDialog.Builder(SignUpActivity.this)
                                    .setMessage(message)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }

                        else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(SignUpActivity.this)
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(SignUpActivity.this)
                                .setMessage("Something went wrong.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
                    }

                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(SignUpActivity.this)
                            .setMessage("Something went wrong.")
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
                new AlertDialog.Builder(SignUpActivity.this)
                        .setMessage("Something went wrong.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showOtpDialog(final String Otp) {
        View addSenderDialogView = getLayoutInflater().inflate(R.layout.add_sender_otp_dialog_layout, null, false);
        final androidx.appcompat.app.AlertDialog addSenderDialog = new androidx.appcompat.app.AlertDialog.Builder(SignUpActivity.this).create();
        addSenderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addSenderDialog.setCancelable(false);
        addSenderDialog.setView(addSenderDialogView);
        addSenderDialog.setCancelable(false);
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
                verifyOtp();
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
                        addSenderDialog.dismiss();
                        addUser();
                    } else {
                        new androidx.appcompat.app.AlertDialog.Builder(SignUpActivity.this)
                                .setMessage("Wrong Otp")
                                .show();
                    }
                } else {
                    etOtp.setError("Required");
                }
            }
        });
    }

    public void getReferralCode() {
        ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setMessage("Getting details...");
        progressDialog.show();

        InstallReferrerClient mReferrerClient;
        mReferrerClient = InstallReferrerClient.newBuilder(SignUpActivity.this).build();
        mReferrerClient.startConnection(new InstallReferrerStateListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                switch (responseCode) {
                    case InstallReferrerClient.InstallReferrerResponse.OK:
                        //Connection established
                        try {
                            ReferrerDetails response = mReferrerClient.getInstallReferrer();
                            referralCode =  response.getInstallReferrer();

                            if (!referralCode.equalsIgnoreCase("utm_source=google-play&utm_medium=organic") && !referralCode.contains("not%20set")) {

                                binding.etReferCode.setText(referralCode);
                                binding.etReferCode.setEnabled(false);
                            }

                         //   Toast.makeText(SignUpActivity.this, referralCode, Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        } catch (Exception e) {
                            Toast.makeText(SignUpActivity.this, "In Catch...", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                        mReferrerClient.endConnection();
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        // API not available on the current Play Store app
                        Toast.makeText(SignUpActivity.this, "Case 1", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        Toast.makeText(SignUpActivity.this, "Case 2", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        // Connection could not be established
                        break;
                    default:
                        Toast.makeText(SignUpActivity.this, "In Default", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        break;
                }
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Toast.makeText(SignUpActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }

    private void showSnackBar(String message) {
        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean checkInputs() {
        return !TextUtils.isEmpty(binding.etName.getText())
                && !TextUtils.isEmpty(binding.etEmail.getText())
                && !TextUtils.isEmpty(binding.etMobileNumber.getText())
              //  && !TextUtils.isEmpty(binding.etShopName.getText())
              //  && !TextUtils.isEmpty(binding.etAddress.getText())
              //  && !TextUtils.isEmpty(binding.etPincode.getText())
              //  && !selectedDob.equalsIgnoreCase("")
              //  && !selectedCityId.equalsIgnoreCase("")
              //  && !selectedStateId.equalsIgnoreCase("")
             //   && !TextUtils.isEmpty(binding.etPancard.getText())
             //   && !TextUtils.isEmpty(binding.etAadharcard.getText())
                ;

    }

    @Override
    protected void onResume() {
        super.onResume();
        getReferralCode();
    }
}