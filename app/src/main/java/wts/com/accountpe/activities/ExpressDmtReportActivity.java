package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import android.annotation.SuppressLint;
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
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import wts.com.accountpe.adapters.MoneyReportAdapter;
import wts.com.accountpe.models.MoneyReportModel;
import wts.com.accountpe.retrofit.RetrofitClient;

public class ExpressDmtReportActivity extends AppCompatActivity {

    TextView tvTitle;
    ImageView backButton;
    LinearLayout fromDateLayout, toDateLayout;
    Button btnFilter;
    Spinner spinner;
    String[] searchByArray = {"ALL", "DATE"};
    String userId, searchBy = "ALL", fromDate = "", toDate = "";
    DatePickerDialog fromDatePicker;
    SimpleDateFormat simpleDateFormat, webServiceDateFormat;
    TextView tvFromDate, tvToDate;
    SharedPreferences sharedPreferences;
    ArrayList<MoneyReportModel> moneyReportModelArrayList;
    RecyclerView creditDebitRecycler;
    ImageView imgNoDataFound;
    TextView tvNoDataFound;
    String deviceId,deviceInfo;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_express_dmt_report);

        initViews();

        tvTitle.setText("Express DMT Report");
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        HintSpinner<String> hintSpinner = new HintSpinner<>(spinner, new HintAdapter<String>(ExpressDmtReportActivity.this,
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
                fromDatePicker = new DatePickerDialog(ExpressDmtReportActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar newDate1 = Calendar.getInstance();
                        newDate1.set(year, month, dayOfMonth);

                        tvFromDate.setText(simpleDateFormat.format(newDate1.getTime()));

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
                fromDatePicker = new DatePickerDialog(ExpressDmtReportActivity.this, new DatePickerDialog.OnDateSetListener() {
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

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ExpressDmtReportActivity.this);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        Calendar newDate1 = Calendar.getInstance();
        newDate1.set(year, month, day);
        tvFromDate.setText(simpleDateFormat.format(newDate1.getTime()));
        fromDate = webServiceDateFormat.format(newDate1.getTime());
        tvToDate.setText(simpleDateFormat.format(newDate1.getTime()));

        toDate = webServiceDateFormat.format(newDate1.getTime());

        if (checkInternetState()) {
            getReport();
        }
        else {
            showSnackBar();
        }

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetState()) {
                    getReport();
                }
                else {
                    showSnackBar();
                }
            }
        });
    }

    private void getReport() {

        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(ExpressDmtReportActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();
        Call<JsonObject> call= RetrofitClient.getInstance().getApi().expressDmtReport(AUTH_KEY,userId,deviceId,deviceInfo,fromDate,toDate,"");

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;

                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));
                        String statusCode = jsonObject1.getString("statuscode");

                        if (statusCode.equalsIgnoreCase("TXN")) {
                            creditDebitRecycler.setVisibility(View.VISIBLE);
                            tvNoDataFound.setVisibility(View.GONE);
                            imgNoDataFound.setVisibility(View.GONE);

                            JSONArray jsonArray = jsonObject1.getJSONArray("data");
                            moneyReportModelArrayList = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                MoneyReportModel moneyReportModel = new MoneyReportModel();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String amount=jsonObject.getString("Amount");
                                String cost =jsonObject.getString("Payableamount");
                                String comm=jsonObject.getString("Commission");
                                String surcharge=jsonObject.getString("Surcharge");
                                String openingBal=jsonObject.getString("OpeningBal");
                                String balance=jsonObject.getString("ClosingBal");
                                String tDateTime=jsonObject.getString("CreatedOn");
                                String accountNo=jsonObject.getString("AccountNo");
                                String beniName=jsonObject.getString("BeneficiaryName");
                                String bank=jsonObject.getString("BankName");
                                String ifsc=jsonObject.getString("IfscCode");
                                String status=jsonObject.getString("Status");
                                String transactionId=jsonObject.getString("TransactionId");
                                String senderMobileNo=jsonObject.getString("SenderMobileNo");
                                String uniqueId=jsonObject.getString("UniqueId");
                                String serviceType=jsonObject.getString("ServiceType");
                                String bankRRN=jsonObject.getString("BankRrnNo");

                                status = android.text.Html.fromHtml(status).toString();
                                status = status.replace("\\r", "");
                                status = status.replace("\\n", "");
                                status = status.replace("\\t", "");
                                status = status.replace("\\", "");

                                @SuppressLint("SimpleDateFormat") DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
                                String[] splitDate = tDateTime.split("T");
                                try {
                                    Date date = inputDateFormat.parse(splitDate[0]);
                                    Date time = simpleDateFormat.parse(splitDate[1]);
                                    @SuppressLint("SimpleDateFormat") String outputDate = new SimpleDateFormat("dd MMM yyyy").format(date);
                                    @SuppressLint("SimpleDateFormat") String outputTime = new SimpleDateFormat("hh:mm a").format(time);

                                    moneyReportModel.setDate(outputDate+" "+outputTime);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                moneyReportModel.setAmount(amount);
                                moneyReportModel.setCost(cost);
                                moneyReportModel.setOpeningBal(openingBal);
                                moneyReportModel.setBalance(balance);
                                moneyReportModel.setAccountNumber(accountNo);
                                moneyReportModel.setBeniName(beniName);
                                moneyReportModel.setBank(bank);
                                moneyReportModel.setIfsc(ifsc);
                                moneyReportModel.setStatus(status);
                                moneyReportModel.setTransactionId(transactionId);
                                moneyReportModel.setBankRRN(bankRRN);
                                moneyReportModel.setCommission(comm);
                                moneyReportModel.setSurcharge(surcharge);
                                moneyReportModel.setSenderMobileNo(senderMobileNo);
                                moneyReportModel.setUniqueId(uniqueId);
                                moneyReportModel.setServiceType(serviceType);

                                moneyReportModelArrayList.add(moneyReportModel);
                            }

                            creditDebitRecycler.setLayoutManager(new LinearLayoutManager(ExpressDmtReportActivity.this,
                                    RecyclerView.VERTICAL, false));

                            MoneyReportAdapter moneyReportAdapter = new MoneyReportAdapter(moneyReportModelArrayList,ExpressDmtReportActivity.this,
                                    ExpressDmtReportActivity.this);
                            creditDebitRecycler.setAdapter(moneyReportAdapter);
                            pDialog.dismiss();
                        }
                        else {
                            pDialog.dismiss();

                            creditDebitRecycler.setVisibility(View.GONE);
                            tvNoDataFound.setVisibility(View.VISIBLE);
                            imgNoDataFound.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        pDialog.dismiss();
                        creditDebitRecycler.setVisibility(View.GONE);
                        tvNoDataFound.setVisibility(View.VISIBLE);
                        imgNoDataFound.setVisibility(View.VISIBLE);
                        e.printStackTrace();
                    }

                } else {
                    pDialog.dismiss();
                    creditDebitRecycler.setVisibility(View.GONE);
                    tvNoDataFound.setVisibility(View.VISIBLE);
                    imgNoDataFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                creditDebitRecycler.setVisibility(View.GONE);
                tvNoDataFound.setVisibility(View.VISIBLE);
                imgNoDataFound.setVisibility(View.VISIBLE);
            }
        });
    }
    private boolean checkInternetState() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }

        }
        return false;
    }

    private void showSnackBar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.money_report_layout), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void initViews() {

        backButton = findViewById(R.id.back_button);
        tvTitle = findViewById(R.id.activity_title);

        fromDateLayout = findViewById(R.id.from_date_layout);
        toDateLayout = findViewById(R.id.to_date_layout);
        spinner = findViewById(R.id.spinner);
        btnFilter = findViewById(R.id.btn_filter);

        tvFromDate = findViewById(R.id.tv_from_date);
        tvToDate = findViewById(R.id.tv_to_date);

        imgNoDataFound=findViewById(R.id.img_no_data_found);
        tvNoDataFound=findViewById(R.id.tv_no_data_found);

        creditDebitRecycler = findViewById(R.id.credit_debit_recycler);
    }

}