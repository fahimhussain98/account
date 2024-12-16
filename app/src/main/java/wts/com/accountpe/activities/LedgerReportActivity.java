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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.adapters.LedgerReportAdapter;
import wts.com.accountpe.models.LedgerReportModel;
import wts.com.accountpe.retrofit.RetrofitClient;

public class LedgerReportActivity extends AppCompatActivity {

    /////////////////////////////////////////toolbar
    String title;
    TextView activityTitle;
    ImageView backButton;
    /////////////////////////////////////////toolbar


    LinearLayout fromDateLayout, toDateLayout;
    Button btnFilter;
    Spinner spinner;
    String[] searchByArray = {"ALL", "Credit", "Debit"};
    String userId, searchBy = "ALL", fromDate = "", toDate = "";
    DatePickerDialog toDatePicker, fromDatePicker;
    SimpleDateFormat simpleDateFormat, webServiceDateFormat;
    TextView tvFromdate, tvToDate;
    SharedPreferences sharedPreferences;
    ProgressDialog pDialog;
    ImageView imgNoDataFound;
    TextView tvNoDataFound;
    RecyclerView ledgerRecycler;
    ArrayList<LedgerReportModel> ledgerReportModelArrayList;
    String balanceId, userid, oldBalance, newBalance, transactionType,
            transactionDate, ipAddress, crDrType, amount, surcharge, tds,commission;

    boolean isInitialReport = true;

    String deviceId, deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledger_report);

        initViews();

        //////////////////////////////////////////////////////////////////Toolbar
        title = getIntent().getStringExtra("title");
        activityTitle.setText(title);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //////////////////////////////////////////////////////////////////Toolbar

////////////////////////////////////Select from date, to date and search by
        HintSpinner<String> hintSpinner = new HintSpinner<>(spinner, new HintAdapter<String>(LedgerReportActivity.this,
                "Select", Arrays.asList(searchByArray)), new HintSpinner.Callback<String>() {
            @Override
            public void onItemSelected(int position, String itemAtPosition) {
                searchBy = itemAtPosition;

            }
        });
        hintSpinner.init();

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        webServiceDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);


        fromDateLayout.setOnClickListener(new View.OnClickListener() {

            final Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);

            @Override
            public void onClick(View v) {
                fromDatePicker = new DatePickerDialog(LedgerReportActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                fromDatePicker = new DatePickerDialog(LedgerReportActivity.this, new DatePickerDialog.OnDateSetListener() {
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


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LedgerReportActivity.this);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        ////////////////////////////////////////////////////////////////////////////
        Calendar fromCalender = Calendar.getInstance();
        //fromCalender.add(Calendar.MONTH, -1);
        fromDate = webServiceDateFormat.format(fromCalender.getTime());
        tvFromdate.setText(simpleDateFormat.format(fromCalender.getTime()));

        Calendar toCalender = Calendar.getInstance();
        toDate = webServiceDateFormat.format(toCalender.getTime());
        tvToDate.setText(simpleDateFormat.format(fromCalender.getTime()));


        if (checkInternetState()) {
            getReport("", "", isInitialReport);

        } else {
            showSnackBar();
        }
        ////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////Select from date, to date and search by

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkInternetState()) {
                    if (tvFromdate.getText().toString().equalsIgnoreCase("Select date") ||
                            tvToDate.getText().toString().equalsIgnoreCase("Select date") ||
                            searchBy.equalsIgnoreCase("Select")) {
                        new AlertDialog.Builder(LedgerReportActivity.this).setTitle("Select Details")
                                .setMessage("Please select all the above details")
                                .setPositiveButton("Ok", null).show();
                    } else {
                        isInitialReport = false;
                        getReport(fromDate, toDate, isInitialReport);
                    }
                } else {
                    showSnackBar();
                }
            }
        });
    }

    private void getReport(String fromDate, String toDate, final boolean isInitialReport) {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = null;

        if (title.equalsIgnoreCase("Ledger Report")) {
            call = RetrofitClient.getInstance().getApi().getLedgerReport(AUTH_KEY,userId,deviceId,deviceInfo,"",fromDate,toDate);
        } else {
            call = RetrofitClient.getInstance().getApi().getAepsLedgerReport(AUTH_KEY,userId,deviceId,deviceInfo,"",fromDate,toDate);
        }

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;

                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));
                        String statusCode = jsonObject1.getString("statuscode");

                        if (statusCode.equalsIgnoreCase("TXN")) {
                            ledgerRecycler.setVisibility(View.VISIBLE);
                            tvNoDataFound.setVisibility(View.GONE);
                            imgNoDataFound.setVisibility(View.GONE);

                            JSONArray jsonArray = jsonObject1.getJSONArray("data");
                            ledgerReportModelArrayList = new ArrayList<LedgerReportModel>();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                LedgerReportModel ledgerReportModel = new LedgerReportModel();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                balanceId = jsonObject.getString("ID");
                                userid = jsonObject.getString("UserID");
                                oldBalance = jsonObject.getString("OLDBAL");
                                newBalance = jsonObject.getString("NEWBAL");
                                transactionType = jsonObject.getString("TRANSACTIONTYPE");
                                transactionDate = jsonObject.getString("CreatedOn");
                                ipAddress = jsonObject.getString("IPAddress");
                                crDrType = jsonObject.getString("CRDRTYPE");
                                amount = jsonObject.getString("AMOUNT");
                                surcharge = jsonObject.getString("surcharge");
                                tds = jsonObject.getString("TDS");
                                commission = jsonObject.getString("Commission");
                               String pgAmount = jsonObject.getString("PGAMOUNT");

                                crDrType = android.text.Html.fromHtml(crDrType).toString();
                                crDrType = crDrType.replace("\\r", "");
                                crDrType = crDrType.replace("\\n", "");
                                crDrType = crDrType.replace("\\t", "");
                                crDrType = crDrType.replace("\\", "");

                                @SuppressLint("SimpleDateFormat") DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
                                String[] splitDate = transactionDate.split("T");
                                try {
                                    Date date = inputDateFormat.parse(splitDate[0]);
                                    Date time = simpleDateFormat.parse(splitDate[1]);
                                    @SuppressLint("SimpleDateFormat") String outputDate = new SimpleDateFormat("dd MMM yyyy").format(date);
                                    @SuppressLint("SimpleDateFormat") String outputTime = new SimpleDateFormat("hh:mm a").format(time);

                                    ledgerReportModel.setTransactionDate(outputDate+" "+outputTime);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                ledgerReportModel.setBalanceId(balanceId);
                                ledgerReportModel.setUserId(userId);
                                ledgerReportModel.setOldBalance(oldBalance);
                                ledgerReportModel.setNewBalance(newBalance);
                                ledgerReportModel.setTransactionType(transactionType);

                                ledgerReportModel.setIpAddress(ipAddress);
                                ledgerReportModel.setCrDrType(crDrType);
                                ledgerReportModel.setAmount(amount);
                                ledgerReportModel.setSurcharge(surcharge);
                                ledgerReportModel.setTds(tds);
                                ledgerReportModel.setCommission(commission);
                                ledgerReportModel.setPgAmount(pgAmount);

                                ledgerReportModelArrayList.add(ledgerReportModel);

                            }

                            LedgerReportAdapter ledgerReportAdapter = new LedgerReportAdapter(ledgerReportModelArrayList,
                                    LedgerReportActivity.this, isInitialReport);
                            ledgerRecycler.setLayoutManager(new LinearLayoutManager(LedgerReportActivity.this,
                                    RecyclerView.VERTICAL, false));

                            ledgerRecycler.setAdapter(ledgerReportAdapter);

                            pDialog.dismiss();
                        } else  {
                            pDialog.dismiss();

                            ledgerRecycler.setVisibility(View.GONE);
                            tvNoDataFound.setVisibility(View.VISIBLE);
                            imgNoDataFound.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        pDialog.dismiss();
                        ledgerRecycler.setVisibility(View.GONE);
                        tvNoDataFound.setVisibility(View.VISIBLE);
                        imgNoDataFound.setVisibility(View.VISIBLE);
                        e.printStackTrace();
                    }
                } else {
                    pDialog.dismiss();
                    ledgerRecycler.setVisibility(View.GONE);
                    tvNoDataFound.setVisibility(View.VISIBLE);
                    imgNoDataFound.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(LedgerReportActivity.this).setTitle("Error")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null).show();
                ledgerRecycler.setVisibility(View.GONE);
                tvNoDataFound.setVisibility(View.VISIBLE);
                imgNoDataFound.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initViews() {
        backButton = findViewById(R.id.back_button);
        activityTitle = findViewById(R.id.activity_title);

        fromDateLayout = findViewById(R.id.from_date_layout);
        toDateLayout = findViewById(R.id.to_date_layout);
        spinner = findViewById(R.id.spinner);
        btnFilter = findViewById(R.id.btn_filter);

        tvFromdate = findViewById(R.id.tv_from_date);
        tvToDate = findViewById(R.id.tv_to_date);

        imgNoDataFound = findViewById(R.id.img_no_data_found);
        tvNoDataFound = findViewById(R.id.tv_no_data_found);

        ledgerRecycler = findViewById(R.id.ledger_recycler);
    }

    private void showSnackBar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.ledger_report_layout), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }
}