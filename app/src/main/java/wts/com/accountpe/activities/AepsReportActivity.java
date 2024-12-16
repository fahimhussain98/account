package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.adapters.AepsReportAdapter;
import wts.com.accountpe.models.AepsModel;
import wts.com.accountpe.retrofit.RetrofitClient;

public class AepsReportActivity extends AppCompatActivity {

    TextView activityTitle;
    ImageView backButton;

    LinearLayout fromDateLayout, toDateLayout;
    Button btnFilter;

    DatePickerDialog fromDatePicker;
    SimpleDateFormat simpleDateFormat, webServiceDateFormat;
    TextView tvFromdate, tvToDate;
    SharedPreferences sharedPreferences;
    ProgressDialog pDialog;
    String userId, fromDate = "", toDate = "";

    ImageView imgNoDataFound;
    TextView tvNoDataFound;
    RecyclerView allReportsRecycler;

    ArrayList<AepsModel> allReportsModelArrayList;

    String deviceId,deviceInfo;

    boolean isInitialReport = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aeps_report);

        initViews();

        activityTitle.setText("AePS Report");
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        ////////////////////////////////////Select from date, to date and search by

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        webServiceDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);


        fromDateLayout.setOnClickListener(new View.OnClickListener() {

            final Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);

            @Override
            public void onClick(View v) {
                fromDatePicker = new DatePickerDialog(AepsReportActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newDate1 = Calendar.getInstance();
                        newDate1.set(year, month, dayOfMonth);

                        tvFromdate.setText(simpleDateFormat.format(newDate1.getTime()));

                        fromDate = webServiceDateFormat.format(newDate1.getTime());

                    }
                }, year, month, day);
                fromDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                fromDatePicker.show();

            }
        });


        toDateLayout.setOnClickListener(new View.OnClickListener() {
            final Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);

            @Override
            public void onClick(View v) {
                fromDatePicker = new DatePickerDialog(AepsReportActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newDate1 = Calendar.getInstance();
                        newDate1.set(year, month, dayOfMonth);

                        tvToDate.setText(simpleDateFormat.format(newDate1.getTime()));

                        toDate = webServiceDateFormat.format(newDate1.getTime());
                    }
                }, year, month, day);
                fromDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
                fromDatePicker.show();

            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AepsReportActivity.this);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);


        ////////////////////////////////////Select from date, to date and search by

        //////////////////////////////////////////////////////////////////
        Calendar fromCalender = Calendar.getInstance();
        fromDate = webServiceDateFormat.format(fromCalender.getTime());
        tvFromdate.setText(simpleDateFormat.format(fromCalender.getTime()));

        Calendar toCalender = Calendar.getInstance();
        toDate = webServiceDateFormat.format(toCalender.getTime());
        tvToDate.setText(simpleDateFormat.format(toCalender.getTime()));



        if (checkInternetState()) {
            getReports(userId,  fromDate, toDate, isInitialReport);

        } else {
            showSnackBar();
        }
        //////////////////////////////////////////////////////////////////
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetState()) {

                    if (tvFromdate.getText().toString().equalsIgnoreCase("Select Date") ||
                            tvToDate.getText().toString().equalsIgnoreCase("Select Date")) {
                        new AlertDialog.Builder(AepsReportActivity.this).setMessage("Please select both From date and To Date")
                                .setPositiveButton("Ok", null).show();
                    } else {
                        isInitialReport = false;
                        getReports(userId,  fromDate, toDate, isInitialReport);
                    }

                } else {
                    showSnackBar();
                }
            }
        });

    }

    private void initViews() {

        backButton = findViewById(R.id.back_button);
        activityTitle = findViewById(R.id.activity_title);

        fromDateLayout = findViewById(R.id.from_date_layout);
        toDateLayout = findViewById(R.id.to_date_layout);
        btnFilter = findViewById(R.id.btn_filter);

        tvFromdate = findViewById(R.id.tv_from_date);
        tvToDate = findViewById(R.id.tv_to_date);

        imgNoDataFound = findViewById(R.id.img_no_data_found);
        tvNoDataFound = findViewById(R.id.tv_no_data_found);

        allReportsRecycler = findViewById(R.id.all_report_recycler);
    }

    private void getReports(String userId, String fromDate, String toDate, final boolean isInitialReport) {

        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(AepsReportActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getAepsReport(AUTH_KEY,userId,deviceId,deviceInfo,fromDate,toDate);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;

                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));
                        String statusCode = jsonObject1.getString("statuscode");

                        if (statusCode.equalsIgnoreCase("TXN")) {
                            allReportsRecycler.setVisibility(View.VISIBLE);
                            tvNoDataFound.setVisibility(View.GONE);
                            imgNoDataFound.setVisibility(View.GONE);

                            JSONArray jsonArray = jsonObject1.getJSONArray("data");
                            allReportsModelArrayList = new ArrayList<AepsModel>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                AepsModel aepsModel = new AepsModel();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String transactionId=jsonObject.getString("TransactionId");
                                String amount=jsonObject.getString("Amount");
                                String comm=jsonObject.getString("Commission");
                                String newBalance=jsonObject.getString("ClosingBal");
                                String dateTime=jsonObject.getString("CreatedOn");
                                String status=jsonObject.getString("Status");
                                status = status.replaceAll("\\<.*?\\>", "");
                                String transactionType=jsonObject.getString("TransactionType");
                                String oldBalance=jsonObject.getString("OpeningBal");
                                String aadharNo=jsonObject.getString("AadharNo");
                                String panNo=jsonObject.getString("Pancard");
                                String bankName=jsonObject.getString("BankName");
                                String surcharge=jsonObject.getString("Surcharge");
                                String apiName=jsonObject.getString("APINAME");

                                @SuppressLint("SimpleDateFormat") DateFormat inputDate = new SimpleDateFormat("yyyy-MM-dd");
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat inputTime = new SimpleDateFormat("hh:mm:ss");
                                String[] splitDate = dateTime.split("T");
                                try {
                                    Date date = inputDate.parse(splitDate[0]);
                                    Date time = inputTime.parse(splitDate[1]);
                                    assert date != null;
                                    @SuppressLint("SimpleDateFormat") String outPutDate = new SimpleDateFormat("dd MMM yyyy").format(date);
                                    assert time != null;
                                    @SuppressLint("SimpleDateFormat") String outPutTime = new SimpleDateFormat("hh:mm:a").format(time);
                                    aepsModel.setTimestamp(outPutDate+", "+outPutTime);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                aepsModel.setTransactionId(transactionId);
                                aepsModel.setAmount(amount);
                                aepsModel.setComm(comm);
                                aepsModel.setNewbalance(newBalance);
                                aepsModel.setTxnStatus(status);
                                aepsModel.setTransactionType(transactionType);
                                aepsModel.setOldbalance(oldBalance);
                                aepsModel.setAadharNo(aadharNo);
                                aepsModel.setPanNo(panNo);
                                aepsModel.setBankName(bankName);
                                aepsModel.setSurcharge(surcharge);
                                aepsModel.setApiName(apiName);

                                allReportsModelArrayList.add(aepsModel);

                            }

                            allReportsRecycler.setLayoutManager(new LinearLayoutManager(AepsReportActivity.this,
                                    RecyclerView.VERTICAL, false));

                            AepsReportAdapter allReportAdapter = new AepsReportAdapter(allReportsModelArrayList, isInitialReport
                                    ,AepsReportActivity.this);
                            allReportsRecycler.setAdapter(allReportAdapter);
                            pDialog.dismiss();
                        } else if (statusCode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();

                            allReportsRecycler.setVisibility(View.GONE);
                            tvNoDataFound.setVisibility(View.VISIBLE);
                            imgNoDataFound.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        imgNoDataFound.setVisibility(View.VISIBLE);
                        tvNoDataFound.setVisibility(View.VISIBLE);
                        allReportsRecycler.setVisibility(View.GONE);
                    }
                } else {
                    pDialog.dismiss();
                    allReportsRecycler.setVisibility(View.GONE);
                    tvNoDataFound.setVisibility(View.VISIBLE);
                    imgNoDataFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                imgNoDataFound.setVisibility(View.VISIBLE);
                tvNoDataFound.setVisibility(View.VISIBLE);
                allReportsRecycler.setVisibility(View.GONE);
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

    private void showSnackBar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.aeps_reports_layout), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}