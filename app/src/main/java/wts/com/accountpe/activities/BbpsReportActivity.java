package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.adapters.AllReportAdapter;
import wts.com.accountpe.models.AllReportsModel;
import wts.com.accountpe.retrofit.RetrofitClient;

public class BbpsReportActivity extends AppCompatActivity {

   
    TextView activityTitle;
    ImageView backButton;
    
    LinearLayout fromDateLayout, toDateLayout;
    Button btnFilter;

    DatePickerDialog fromDatePicker;
    SimpleDateFormat simpleDateFormat, webServiceDateFormat;
    TextView tvFromdate, tvToDate;
    SharedPreferences sharedPreferences;
    String userId, fromDate = "", toDate = "";

    ImageView imgNoDataFound;
    TextView tvNoDataFound;
    RecyclerView allReportsRecycler;

    ArrayList<AllReportsModel> allReportsModelArrayList;

    String deviceId, deviceInfo;

    String transactionId, operatorName, number, amount, commission, cost, balance, dateTime, status, stype, imgUrl, brId;
    String serviceName;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbps_report);
        initViews();

        serviceName = getIntent().getStringExtra("serviceName");
        activityTitle.setText(serviceName+" Report");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        webServiceDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        fromDateLayout.setOnClickListener(new View.OnClickListener() {

            final Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);

            @Override
            public void onClick(View v) {
                fromDatePicker = new DatePickerDialog(BbpsReportActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newDate1 = Calendar.getInstance();
                        newDate1.set(year, month, dayOfMonth);

                        tvFromdate.setText(simpleDateFormat.format(newDate1.getTime()));

                        fromDate = webServiceDateFormat.format(newDate1.getTime());

                    }
                }, year, month, day);

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
                fromDatePicker = new DatePickerDialog(BbpsReportActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newDate1 = Calendar.getInstance();
                        newDate1.set(year, month, dayOfMonth);

                        tvToDate.setText(simpleDateFormat.format(newDate1.getTime()));

                        toDate = webServiceDateFormat.format(newDate1.getTime());
                    }
                }, year, month, day);

                fromDatePicker.show();
            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BbpsReportActivity.this);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        ////////////////////////////////////Select from date, to date and search by

        //////////////////////////////////////////////////////////////////TODAY FROM AND TO DATE

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        Calendar newDate1 = Calendar.getInstance();
        newDate1.set(year, month, day);
        tvFromdate.setText(simpleDateFormat.format(newDate1.getTime()));
        fromDate = webServiceDateFormat.format(newDate1.getTime());
        tvToDate.setText(simpleDateFormat.format(newDate1.getTime()));

        toDate = webServiceDateFormat.format(newDate1.getTime());

        getReports(userId, fromDate, toDate, serviceName);

        //////////////////////////////////////////////////////////////////TODAY FROM AND TO DATE
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetState()) {

                    if (tvFromdate.getText().toString().equalsIgnoreCase("Select Date") ||
                            tvToDate.getText().toString().equalsIgnoreCase("Select Date")) {
                        new AlertDialog.Builder(BbpsReportActivity.this).setMessage("Please select both From date and To Date")
                                .setPositiveButton("Ok", null).show();
                    } else {
                        getReports(userId, fromDate, toDate, serviceName);
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

    private void getReports(final String userId, String fromDate, String toDate, String serviceName) {

        ProgressDialog pDialog = new ProgressDialog(BbpsReportActivity.this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getBBPSReport(AUTH_KEY, userId, deviceId, deviceInfo, fromDate, toDate,
                serviceName);
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
                            allReportsModelArrayList = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                AllReportsModel allReportsModel = new AllReportsModel();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                transactionId = jsonObject.getString("TransactionID");
                                operatorName = jsonObject.getString("OperatorName");
                                number = jsonObject.getString("ConsumerNo");
                                amount = jsonObject.getString("Amount");
                                commission = jsonObject.getString("Commission");
                                cost = jsonObject.getString("PayableAmount");
                                balance = jsonObject.getString("ClosingBalance");
                                dateTime = jsonObject.getString("CreateDate");
                                status = jsonObject.getString("status");
                                String liveId = jsonObject.getString("UniqueID");
                                allReportsModel.setLiveId(liveId);
                                allReportsModel.setUniqueId(liveId);
                                String openingBal = jsonObject.getString("OpeningBalance");
                                String closingBal = jsonObject.getString("ClosingBalance");
                                String pgAmount = jsonObject.getString("PGAMOUNT");
                                //stype = jsonObject.getString("Electricity");
                                //imgUrl = jsonObject.getString("Image");
                                //brId = jsonObject.getString("brid");

                                status = android.text.Html.fromHtml(status).toString();
                                status = status.replace("\\r", "");
                                status = status.replace("\\n", "");
                                status = status.replace("\\t", "");
                                status = status.replace("\\", "");

                                allReportsModel.setTransactionId(transactionId);
                                allReportsModel.setOperatorName(operatorName);
                                allReportsModel.setNumber(number);
                                allReportsModel.setAmount(amount);
                                allReportsModel.setCommission(commission);
                                allReportsModel.setCost(cost);
                                allReportsModel.setBalance(balance);
                                allReportsModel.setsType("Electricity");
                                allReportsModel.setImageUrl(imgUrl);
                                allReportsModel.setDate(dateTime);
                                allReportsModel.setOpeningBalance(openingBal);
                                allReportsModel.setClosingBalance(closingBal);
                                allReportsModel.setPgAmount(pgAmount);

                                @SuppressLint("SimpleDateFormat") DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
                                String[] splitDate = dateTime.split("T");
                                try {
                                    Date date = inputDateFormat.parse(splitDate[0]);
                                    Date time = simpleDateFormat.parse(splitDate[1]);
                                    @SuppressLint("SimpleDateFormat") String outputDate = new SimpleDateFormat("dd MMM yyyy").format(date);
                                    @SuppressLint("SimpleDateFormat") String outputTime = new SimpleDateFormat("hh:mm a").format(time);

                                    allReportsModel.setDate(outputDate);
                                    allReportsModel.setTime(outputTime);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                allReportsModel.setStatus(status);

                                allReportsModelArrayList.add(allReportsModel);

                            }


                            allReportsRecycler.setLayoutManager(new LinearLayoutManager(BbpsReportActivity.this,
                                    RecyclerView.VERTICAL, false));

                            AllReportAdapter allReportAdapter = new AllReportAdapter(allReportsModelArrayList, BbpsReportActivity.this,
                                    userId, BbpsReportActivity.this, serviceName);
                            allReportsRecycler.setAdapter(allReportAdapter);
                            pDialog.dismiss();
                        } else {
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

        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    private void showSnackBar() {
        Toast.makeText(BbpsReportActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
    }

}