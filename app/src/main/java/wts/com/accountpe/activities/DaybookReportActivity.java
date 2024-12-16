package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.retrofit.RetrofitClient;

public class DaybookReportActivity extends AppCompatActivity {

    LinearLayout fromDateLayout, toDateLayout;
    AppCompatButton btnFilter;
    DatePickerDialog fromDatePicker;
    TextView tvFromdate, tvToDate;

    TextView tvSuccess,tvSuccessCount,tvFailed,tvFailedCount,tvPending,tvPendingCount,tvOpeningBalance,
            tvBalanceCredited,tvBalanceDebited,tvClosingBalance;

    SimpleDateFormat simpleDateFormat, webServiceDateFormat;
    String userId, fromDate = "", toDate = "";
    String deviceId, deviceInfo;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daybook_report);

        initViews();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        webServiceDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(DaybookReportActivity.this);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        Calendar fromCalender = Calendar.getInstance();
        fromDate = webServiceDateFormat.format(fromCalender.getTime());
        tvFromdate.setText(simpleDateFormat.format(fromCalender.getTime()));



        Calendar toCalender = Calendar.getInstance();
        toDate = webServiceDateFormat.format(toCalender.getTime());
        tvToDate.setText(simpleDateFormat.format(toCalender.getTime()));


        if (checkInternetState()) {
            getReports();

        } else {
            showSnackBar();
        }

        fromDateLayout.setOnClickListener(new View.OnClickListener() {

            final Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);

            @Override
            public void onClick(View v) {
                fromDatePicker = new DatePickerDialog(DaybookReportActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                fromDatePicker = new DatePickerDialog(DaybookReportActivity.this, new DatePickerDialog.OnDateSetListener() {
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

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetState()) {

                    if (tvFromdate.getText().toString().equalsIgnoreCase("Select Date") ||
                            tvToDate.getText().toString().equalsIgnoreCase("Select Date")) {
                        new AlertDialog.Builder(DaybookReportActivity.this).setMessage("Please select both From date and To Date")
                                .setPositiveButton("Ok", null).show();
                    } else {
                        getReports();
                    }

                } else {
                    showSnackBar();
                }
            }
        });

    }

    private void getReports() {

        ProgressDialog pDialog=new ProgressDialog(DaybookReportActivity.this);
        pDialog.setMessage("Please wait");
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getCommissionReport(AUTH_KEY, userId, deviceId, deviceInfo, "all", fromDate, toDate);
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;

                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));


                        JSONArray jsonArray = jsonObject1.getJSONArray("data");
                        JSONObject jsonObject = jsonArray.getJSONObject(0);

                        String success = jsonObject.getString("SUCCESS");
                        String successCount = jsonObject.getString("SUCCESSCOUNT");
                        String failed = jsonObject.getString("FAILED");
                        String failedCount = jsonObject.getString("FAILEDCOUNT");
                        String pending = jsonObject.getString("PENDING");
                        String pendingCount = jsonObject.getString("PENDINGCOUNT");
                        String openingBalance=jsonObject.getString("OPENINGBAL");
                        String creditBalance=jsonObject.getString("FUNDCREDIT");
                        String debitBalance=jsonObject.getString("FUNDDEBIT");
                        String closingBalance=jsonObject.getString("CLOSINGBAL");

                        tvSuccess.setText("Balance ₹ "+success);
                        tvFailed.setText("Balance ₹ "+failed);
                        tvPending.setText("Balance ₹ "+pending);
                        tvSuccessCount.setText("Count : "+successCount);
                        tvPendingCount.setText("Count : "+pendingCount);
                        tvFailedCount.setText("Count : "+failedCount);

                        tvOpeningBalance.setText("Balance ₹ "+openingBalance);
                        tvBalanceCredited.setText("Balance ₹ "+creditBalance);
                        tvBalanceDebited.setText("Balance ₹ "+debitBalance);
                        tvClosingBalance.setText("Balance ₹ "+closingBalance);

                        pDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
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

    private void initViews() {
        fromDateLayout = findViewById(R.id.from_date_layout);
        toDateLayout = findViewById(R.id.to_date_layout);
        btnFilter = findViewById(R.id.btn_filter);

        tvFromdate = findViewById(R.id.tv_from_date);

        tvToDate = findViewById(R.id.tv_to_date);

        tvSuccess=findViewById(R.id.tv_success);
        tvSuccessCount=findViewById(R.id.tv_success_count);
        tvFailed=findViewById(R.id.tv_failed);
        tvFailedCount=findViewById(R.id.tv_failed_count);
        tvPending=findViewById(R.id.tv_pending);
        tvPendingCount=findViewById(R.id.tv_pending_count);
        tvOpeningBalance=findViewById(R.id.tv_opening_balance);
        tvBalanceCredited=findViewById(R.id.tv_balance_credited);
        tvBalanceDebited=findViewById(R.id.tv_balance_debited);
        tvClosingBalance=findViewById(R.id.tv_closing_balance);
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
        Snackbar snackbar = Snackbar.make(findViewById(R.id.commission_report), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}