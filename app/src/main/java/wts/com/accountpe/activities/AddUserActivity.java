package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
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
import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.retrofit.RetrofitClient;

public class AddUserActivity extends AppCompatActivity {

    Spinner spinnerUserType, spinnerSchemeName;
    EditText etOwnerName, etFirmName, etAddress, etEmail, etMobileNumber, etPanCard,
            etAadharCard, etPassword, etCapBalance;
    TextView tvState, tvCity, tvDob;
    Button btnProceed;
    String selectedUserType = "select", selectedSchemeId = "select", selectedSchemeAmount, selectedState, selectedStateId = "select",
            selectedCityId, selectedCity = "select", selectedDob = "select";
    ArrayList<String> userTypeList;
    TextView tvTitle;
    ImageView imgBack;
    String userTypeId, userId, schemeUserType;
    String usertypeId;
    SharedPreferences sharedPreferences;
    ArrayList<String> schemeNameArrayList, schemeIdList, schemeAmountList, stateList, stateIdList, cityList, cityIdList;
    Call<JsonObject> call;
    SimpleDateFormat simpleDateFormat, webServiceDateFormat;

    String deviceId, deviceInfo;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        initViews();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AddUserActivity.this);
    //    userTypeId = sharedPreferences.getString("userTypeId", null);
        userTypeId = sharedPreferences.getString("usertypeId", null);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        tvTitle.setText("Add User");
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getState();

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        webServiceDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        tvDob.setOnClickListener(new View.OnClickListener() {

            final Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);

            @Override
            public void onClick(View v) {
                DatePickerDialog fromDatePicker = new DatePickerDialog(AddUserActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newDate1 = Calendar.getInstance();
                        newDate1.set(year, month, dayOfMonth);

                        tvDob.setText(simpleDateFormat.format(newDate1.getTime()));

                        selectedDob = webServiceDateFormat.format(newDate1.getTime());

                    }
                }, year, month, day);
                fromDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                fromDatePicker.show();

            }
        });

       /* if (userTypeId.equalsIgnoreCase("3")) {
            userTypeList = new ArrayList<>();
            userTypeList.add("MD");
            userTypeList.add("AD");
            userTypeList.add("Retailer");
        }*/
        /*if ("3".equalsIgnoreCase(userTypeId)) {
            userTypeList = new ArrayList<>();
            userTypeList.add("MD");
            userTypeList.add("AD");
            userTypeList.add("Retailer");
        }
        Log.d("userTypeId","userTypeId 1  : " + userTypeId +"\nusertypeId 2 ::"+ usertypeId);


        if ("4".equalsIgnoreCase(userTypeId)) {
            userTypeList = new ArrayList<>();
            userTypeList.add("AD");
            userTypeList.add("Retailer");
        }

        if ("5".equalsIgnoreCase(userTypeId)) {
            userTypeList = new ArrayList<>();
            userTypeList.add("Retailer");
        }

        HintSpinner<String> hintSpinner = new HintSpinner<>(
                spinnerUserType,
                new HintAdapter<String>(AddUserActivity.this, "User Type", userTypeList),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        selectedUserType = userTypeList.get(position);
                        getScheme();
                    }
                });
        hintSpinner.init();*/
//__________________________________________________________________
        userTypeList = new ArrayList<>();

        if ("3".equalsIgnoreCase(userTypeId)) {
            userTypeList.add("MD");
            userTypeList.add("AD");
            userTypeList.add("Retailer");
        } else if ("4".equalsIgnoreCase(userTypeId)) {
            userTypeList.add("AD");
            userTypeList.add("Retailer");
        } else if ("5".equalsIgnoreCase(userTypeId)) {
            userTypeList.add("Retailer");
        }

//        Log.d("userTypeId", "userTypeId: " + userTypeId);
//        Log.d("userTypeList", "userTypeList: " + userTypeList);

        HintSpinner<String> hintSpinner = new HintSpinner<>(
                spinnerUserType,
                new HintAdapter<>(AddUserActivity.this, "User Type", userTypeList),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        selectedUserType = userTypeList.get(position);
                        getScheme();
                    }
                });
        hintSpinner.init();

//__________________________________________________________________


        btnProceed.setOnClickListener(v -> {
            if (checkInternetState()) {
                checkAddUser();
            } else {
                showSnackBar("No Internet");
            }
        });

    }

    private void checkAddUser() {
        if (checkInputs()) {
           if (etMobileNumber.getText().toString().length() == 10)
           {
               String ownerName = etOwnerName.getText().toString().trim();
               String firmName = etFirmName.getText().toString().trim();
               String address = etAddress.getText().toString().trim();
               String mobile = etMobileNumber.getText().toString().trim();
               String emailId = etEmail.getText().toString().trim();
               String panCard = etPanCard.getText().toString().trim();
               String aadharCard = etAadharCard.getText().toString().trim();
               String password = etPassword.getText().toString().trim();
               String capBalance = etCapBalance.getText().toString().trim();

               call = RetrofitClient.getInstance().getApi().addUser(AUTH_KEY, userId, deviceId, deviceInfo, mobile, password, ownerName, schemeUserType,
                       emailId, selectedDob, firmName, address, selectedStateId, selectedCityId, panCard, aadharCard, selectedSchemeId, capBalance);
               addUser();
           }
           else
           {
               etMobileNumber.setError("Invalid Mobile No");
           }

        } else {
            showSnackBar("All fields are mandatory.");
        }
    }

    private void getState() {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(AddUserActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getState(AUTH_KEY, userId, deviceId, deviceInfo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {

                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            stateList = new ArrayList<>();
                            stateIdList = new ArrayList<>();
                            JSONArray transactionArray = responseObject.getJSONArray("data");
                            for (int i = 0; i < transactionArray.length(); i++) {
                                JSONObject transactionObject = transactionArray.getJSONObject(i);

                                String stateName = transactionObject.getString("StateName");
                                String stateId = transactionObject.getString("StateId");
                                stateList.add(stateName);
                                stateIdList.add(stateId);
                            }

                            tvState.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SpinnerDialog operatorDialog = new SpinnerDialog(AddUserActivity.this, stateList, "Select State", R.style.DialogAnimations_SmileWindow, "Close  ");// With 	Animation
                                    operatorDialog.setCancellable(true); // for cancellable
                                    operatorDialog.setShowKeyboard(false);// for open keyboard by default
                                    operatorDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                                        @Override
                                        public void onClick(String item, int position) {
                                            tvState.setText(item);
                                            selectedStateId = stateIdList.get(position);
                                            selectedState = stateList.get(position);
                                            getCity();
                                        }
                                    });

                                    operatorDialog.showSpinerDialog();
                                }
                            });
                            pDialog.dismiss();

                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(AddUserActivity.this)
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
                        new AlertDialog.Builder(AddUserActivity.this)
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
                    new AlertDialog.Builder(AddUserActivity.this)
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
                new AlertDialog.Builder(AddUserActivity.this)
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
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(AddUserActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getCity(AUTH_KEY, userId, deviceId, deviceInfo, selectedStateId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {

                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            cityIdList = new ArrayList<>();
                            cityList = new ArrayList<>();
                            JSONArray transactionArray = responseObject.getJSONArray("data");
                            for (int i = 0; i < transactionArray.length(); i++) {
                                JSONObject transactionObject = transactionArray.getJSONObject(i);

                                String cityName = transactionObject.getString("CityName");
                                String cityId = transactionObject.getString("CityId");
                                cityIdList.add(cityId);
                                cityList.add(cityName);
                            }

                            tvCity.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SpinnerDialog operatorDialog = new SpinnerDialog(AddUserActivity.this, cityList, "Select City", R.style.DialogAnimations_SmileWindow, "Close  ");// With 	Animation
                                    operatorDialog.setCancellable(true); // for cancellable
                                    operatorDialog.setShowKeyboard(false);// for open keyboard by default
                                    operatorDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                                        @Override
                                        public void onClick(String item, int position) {
                                            tvCity.setText(item);
                                            selectedCity = cityList.get(position);
                                            selectedCityId = cityIdList.get(position);
                                        }
                                    });

                                    operatorDialog.showSpinerDialog();
                                }
                            });
                            pDialog.dismiss();

                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(AddUserActivity.this)
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
                        new AlertDialog.Builder(AddUserActivity.this)
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
                    new AlertDialog.Builder(AddUserActivity.this)
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
                new AlertDialog.Builder(AddUserActivity.this)
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

    private void addUser() {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(AddUserActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();
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
                            new AlertDialog.Builder(AddUserActivity.this)
                                    .setTitle("Status")
                                    .setMessage(responseMessage)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(AddUserActivity.this)
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
                        new AlertDialog.Builder(AddUserActivity.this)
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
                    new AlertDialog.Builder(AddUserActivity.this)
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
                new AlertDialog.Builder(AddUserActivity.this)
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

    private boolean checkInputs() {
        return !TextUtils.isEmpty(etOwnerName.getText()) && !TextUtils.isEmpty(etFirmName.getText())
                && !TextUtils.isEmpty(etAddress.getText()) && !TextUtils.isEmpty(etEmail.getText()) && !TextUtils.isEmpty(etMobileNumber.getText())
                && !selectedSchemeId.equalsIgnoreCase("select") && !selectedUserType.equalsIgnoreCase("select");
    }

    private void getScheme() {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(AddUserActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        if (selectedUserType.equalsIgnoreCase("MD"))
            schemeUserType = "4";
        else if (selectedUserType.equalsIgnoreCase("AD"))
            schemeUserType = "5";
        else if (selectedUserType.equalsIgnoreCase("RETAILER"))
            schemeUserType = "6";

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getScheme(AUTH_KEY, userId, deviceId, deviceInfo,
                "ADDUSER", schemeUserType, "0");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {

                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            schemeNameArrayList = new ArrayList<>();
                            schemeIdList = new ArrayList<>();
                            schemeAmountList = new ArrayList<>();
                            JSONArray transactionArray = responseObject.getJSONArray("data");
                            for (int i = 0; i < transactionArray.length(); i++) {
                                JSONObject transactionObject = transactionArray.getJSONObject(i);

                                String schemeName = transactionObject.getString("SchemeName");
                                String schemeId = transactionObject.getString("ID");
                                String schemeAmount = transactionObject.getString("SchemeAmount");
                                schemeNameArrayList.add(schemeName);
                                schemeIdList.add(schemeId);
                                schemeAmountList.add(schemeAmount);
                            }

                            HintSpinner<String> hintSpinner = new HintSpinner<>(
                                    spinnerSchemeName,
                                    new HintAdapter<>(AddUserActivity.this, "Select scheme", schemeNameArrayList),
                                    new HintSpinner.Callback<String>() {
                                        @Override
                                        public void onItemSelected(int position, String itemAtPosition) {
                                            selectedSchemeId = schemeIdList.get(position);
                                            selectedSchemeAmount = schemeAmountList.get(position);
                                        }
                                    });
                            hintSpinner.init();
                            pDialog.dismiss();

                        } else {
                            pDialog.dismiss();
                            String message=responseObject.getString("data");
                            new AlertDialog.Builder(AddUserActivity.this)
                                    .setMessage(message)
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
                        new AlertDialog.Builder(AddUserActivity.this)
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
                    new AlertDialog.Builder(AddUserActivity.this)
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
                new AlertDialog.Builder(AddUserActivity.this)
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

    private void initViews() {
        spinnerUserType = findViewById(R.id.spinner_user_type);
        spinnerSchemeName = findViewById(R.id.spinner_scheme_name);
        etOwnerName = findViewById(R.id.et_owner_name);
        etFirmName = findViewById(R.id.et_firm_name);
        etAddress = findViewById(R.id.et_address);
        etEmail = findViewById(R.id.et_email);
        etMobileNumber = findViewById(R.id.et_mobile_number);
        etPanCard = findViewById(R.id.et_pan_number);
        etAadharCard = findViewById(R.id.et_aadhar_number);
        btnProceed = findViewById(R.id.btn_proceed);
        tvTitle = findViewById(R.id.activity_title);
        imgBack = findViewById(R.id.back_button);
        tvState = findViewById(R.id.tv_state_name);
        tvCity = findViewById(R.id.tv_city_name);
        tvDob = findViewById(R.id.tv_dob);
        etPassword = findViewById(R.id.et_password);
        etCapBalance = findViewById(R.id.et_cap_balance);
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
        Snackbar snackbar = Snackbar.make(findViewById(R.id.add_user_layout), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}