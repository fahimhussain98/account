package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import android.app.DatePickerDialog;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.adapters.PaymentGatewayReportAdapter;
import wts.com.accountpe.models.AddMoneyReportModel;
import wts.com.accountpe.retrofit.RetrofitClient;


public class PaymentGatewayReportActivity extends AppCompatActivity {

    TextView activityTitle;
    ImageView backButton;

    LinearLayout fromDateLayout, toDateLayout;
    Button btnFilter;

    DatePickerDialog fromDatePicker;
    SimpleDateFormat simpleDateFormat, webServiceDateFormat;
    TextView tvFromdate, tvToDate;
    SharedPreferences sharedPreferences;
    String userName,userId, fromDate = "", toDate = "";

    ImageView imgNoDataFound;
    TextView tvNoDataFound;
    RecyclerView addMoneyReportRecycler;

    ArrayList<AddMoneyReportModel> addMoneyList;

    String transactionId, uniqueTxnId, mobileNo,openingBal, amount, commission,surcharge,  closingBal, dateTime, status, stype, payableAmount, aadharNumber, name, bankRefNo;

    String deviceId,deviceInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_gateway_report);


        initViews();

        activityTitle.setText("AddMoney Report");
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(PaymentGatewayReportActivity.this);
        userId = sharedPreferences.getString("userid", null);
        userName = sharedPreferences.getString("username", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        webServiceDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        fromDateLayout.setOnClickListener(new View.OnClickListener() {

            final Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);
            @Override
            public void onClick(View v) {
                fromDatePicker = new DatePickerDialog(PaymentGatewayReportActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                fromDatePicker = new DatePickerDialog(PaymentGatewayReportActivity.this, new DatePickerDialog.OnDateSetListener() {
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

        getAddMoneyReport( fromDate, toDate, "ALL");

        //////////////////////////////////////////////////////////////////TODAY FROM AND TO DATE
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetState()) {

                    if (tvFromdate.getText().toString().equalsIgnoreCase("Select Date") ||
                            tvToDate.getText().toString().equalsIgnoreCase("Select Date")) {
                        new AlertDialog.Builder(PaymentGatewayReportActivity.this).setMessage("Please select both From date and To Date")
                                .setPositiveButton("Ok", null).show();
                    } else {
                        getAddMoneyReport(fromDate, toDate, "DATE");
                    }

                } else {
                    showSnackBar();
                }
            }
        });

    }

    private void getAddMoneyReport(String fDate, String tDate, String searchBy) {

        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(PaymentGatewayReportActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getPaymentGatewayReport(AUTH_KEY, userId, deviceId, deviceInfo,  fDate, tDate, searchBy);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;

                    try {

                        jsonObject1 = new JSONObject(String.valueOf(response.body()));
                        String response_code = jsonObject1.getString("statuscode");

                        if (response_code.equalsIgnoreCase("TXN")) {
                            addMoneyReportRecycler.setVisibility(View.VISIBLE);
                            tvNoDataFound.setVisibility(View.GONE);
                            imgNoDataFound.setVisibility(View.GONE);


                            JSONArray jsonArray = jsonObject1.getJSONArray("data");
                            addMoneyList = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                AddMoneyReportModel addMoneyReportModel = new AddMoneyReportModel();

                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                transactionId = jsonObject.getString("TransactionId");
                                uniqueTxnId = jsonObject.getString("UniqueTransaction");
                                name = jsonObject.getString("Name");
                                openingBal = jsonObject.getString("OpeningBal");
                                amount = jsonObject.getString("Amount");
                                commission = jsonObject.getString("Commission");
                                surcharge = jsonObject.getString("Surcharge");
                              //  closingBal = jsonObject.getString("ClosingBal");
                                dateTime = jsonObject.getString("CreateDate");
                                status = jsonObject.getString("Status");

                                addMoneyReportModel.setTxnID(transactionId);
                                addMoneyReportModel.setUniqueTxnId(uniqueTxnId);
                                addMoneyReportModel.setName(name);
                                addMoneyReportModel.setOpeningBal(openingBal);
                                addMoneyReportModel.setAmount(amount);
                                addMoneyReportModel.setCommission(commission);
                                addMoneyReportModel.setSurcharge(surcharge);
                                addMoneyReportModel.setClosingBal(closingBal);
                                addMoneyReportModel.setCreatedOn(dateTime);
                                addMoneyReportModel.setStatus(status);
                                addMoneyList.add(addMoneyReportModel);

                            }


                            addMoneyReportRecycler.setLayoutManager(new LinearLayoutManager(PaymentGatewayReportActivity.this,
                                    RecyclerView.VERTICAL, false));

                            PaymentGatewayReportAdapter addMoneyReportAdapter = new PaymentGatewayReportAdapter(addMoneyList, PaymentGatewayReportActivity.this);
                            addMoneyReportRecycler.setAdapter(addMoneyReportAdapter);
                            pDialog.dismiss();
                        } else if (response_code.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();

                            addMoneyReportRecycler.setVisibility(View.GONE);
                            tvNoDataFound.setVisibility(View.VISIBLE);
                            imgNoDataFound.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        imgNoDataFound.setVisibility(View.VISIBLE);
                        tvNoDataFound.setVisibility(View.VISIBLE);
                        addMoneyReportRecycler.setVisibility(View.GONE);
                    }
                } else {
                    pDialog.dismiss();
                    addMoneyReportRecycler.setVisibility(View.GONE);
                    tvNoDataFound.setVisibility(View.VISIBLE);
                    imgNoDataFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                imgNoDataFound.setVisibility(View.VISIBLE);
                tvNoDataFound.setVisibility(View.VISIBLE);
                addMoneyReportRecycler.setVisibility(View.GONE);
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

        addMoneyReportRecycler = findViewById(R.id.all_report_recycler);
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
        Toast.makeText(PaymentGatewayReportActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
    }


}