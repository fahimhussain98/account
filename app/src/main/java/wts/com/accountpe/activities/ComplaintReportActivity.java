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
import wts.com.accountpe.adapters.MyComplaintAdapter;
import wts.com.accountpe.models.MyComplaintModel;
import wts.com.accountpe.retrofit.RetrofitClient;


public class ComplaintReportActivity extends AppCompatActivity {

    /////////////////////////////////////////toolbar
    String title;
    TextView activityTitle;
    ImageView backButton;
    /////////////////////////////////////////toolbar

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

    ArrayList<MyComplaintModel> myComplaintModelArrayList;

    String deviceId,deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_report);

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

        webServiceDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        fromDateLayout.setOnClickListener(new View.OnClickListener() {

            final Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);

            @Override
            public void onClick(View v) {
                fromDatePicker = new DatePickerDialog(ComplaintReportActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                fromDatePicker = new DatePickerDialog(ComplaintReportActivity.this, new DatePickerDialog.OnDateSetListener() {
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

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ComplaintReportActivity.this);
        userId = sharedPreferences.getString("userid", null);
        deviceId=sharedPreferences.getString("deviceId",null);
        deviceInfo=sharedPreferences.getString("deviceInfo",null);
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        webServiceDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

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

        if (checkInternetState())
            getReports(userId,  fromDate, toDate);
        else
            showSnackBar();
        //////////////////////////////////////////////////////////////////TODAY FROM AND TO DATE
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetState()) {

                    if (tvFromdate.getText().toString().equalsIgnoreCase("Select Date") ||
                            tvToDate.getText().toString().equalsIgnoreCase("Select Date")) {
                        new AlertDialog.Builder(ComplaintReportActivity.this).setMessage("Please select both From date and To Date")
                                .setPositiveButton("Ok", null).show();
                    } else {
                        getReports(userId, fromDate, toDate);
                    }

                } else {
                    showSnackBar();
                }
            }
        });

    }

    private void getReports(String userId, String fromDate, String toDate) {
        final ProgressDialog pDialog = new ProgressDialog(ComplaintReportActivity.this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call= RetrofitClient.getInstance().getApi().makeComplaintReport(AUTH_KEY,userId,deviceId,deviceInfo,fromDate,toDate);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful())
                {
                    try {
                        JSONObject responseObject=new JSONObject(String.valueOf(response.body()));
                        String responseCode=responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN"))
                        {
                            myComplaintModelArrayList=new ArrayList<>();
                            JSONArray transactionArray=responseObject.getJSONArray("data");
                            for (int i=0;i<transactionArray.length();i++)
                            {
                                MyComplaintModel myComplaintModel=new MyComplaintModel();

                                JSONObject transactionObject=transactionArray.getJSONObject(i);

                                String complaintNo=transactionObject.getString("complaintNo");
                                String remarks=transactionObject.getString("Remarks");
                                String adminRemark=transactionObject.getString("AdminRemarks");
                                String serviceName=transactionObject.getString("serviceName");
                                String status=transactionObject.getString("Status");
                                String complainDate=transactionObject.getString("CreateDate");
                                String txnDate=transactionObject.getString("TxnDate");

                                @SuppressLint("SimpleDateFormat") DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
                                String[] splitDate = txnDate.split("T");
                                String[] splitDateComplain = complainDate.split("T");
                                try {
                                    Date date = inputDateFormat.parse(splitDate[0]);
                                    Date dateComplain = inputDateFormat.parse(splitDateComplain[0]);
                                    Date time = simpleDateFormat.parse(splitDate[1]);
                                    Date timeComplain = simpleDateFormat.parse(splitDateComplain[1]);
                                    @SuppressLint("SimpleDateFormat") String outputDate = new SimpleDateFormat("dd MMM yyyy").format(date);
                                    @SuppressLint("SimpleDateFormat") String outputDateComplain = new SimpleDateFormat("dd MMM yyyy").format(dateComplain);
                                    @SuppressLint("SimpleDateFormat") String outputTime = new SimpleDateFormat("hh:mm a").format(time);
                                    @SuppressLint("SimpleDateFormat") String outputTimeComplain = new SimpleDateFormat("hh:mm a").format(timeComplain);

                                    myComplaintModel.setTxnDate(outputDate);
                                    myComplaintModel.setComplainDate(outputDateComplain);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                myComplaintModel.setCompliantNo(complaintNo);
                                myComplaintModel.setRemarks(remarks);
                                myComplaintModel.setAdminRemark(adminRemark);
                                myComplaintModel.setServiceName(serviceName);
                                myComplaintModel.setStatus(status);

                                myComplaintModelArrayList.add(myComplaintModel);
                            }

                            allReportsRecycler.setVisibility(View.VISIBLE);
                            tvNoDataFound.setVisibility(View.GONE);
                            imgNoDataFound.setVisibility(View.GONE);
                            allReportsRecycler.setLayoutManager(new LinearLayoutManager(ComplaintReportActivity.this,
                                    RecyclerView.VERTICAL, false));

                            MyComplaintAdapter myComplaintAdapter = new MyComplaintAdapter(myComplaintModelArrayList);
                            allReportsRecycler.setAdapter(myComplaintAdapter);
                            pDialog.dismiss();

                        }
                        else
                        {
                            pDialog.dismiss();

                            allReportsRecycler.setVisibility(View.GONE);
                            tvNoDataFound.setVisibility(View.VISIBLE);
                            imgNoDataFound.setVisibility(View.VISIBLE);
                        }

                    }
                    catch (Exception e)
                    {
                        pDialog.dismiss();

                        allReportsRecycler.setVisibility(View.GONE);
                        tvNoDataFound.setVisibility(View.VISIBLE);
                        imgNoDataFound.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    pDialog.dismiss();

                    allReportsRecycler.setVisibility(View.GONE);
                    tvNoDataFound.setVisibility(View.VISIBLE);
                    imgNoDataFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();

                allReportsRecycler.setVisibility(View.GONE);
                tvNoDataFound.setVisibility(View.VISIBLE);
                imgNoDataFound.setVisibility(View.VISIBLE);
            }
        });

    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    private void showSnackBar() {
        Toast.makeText(ComplaintReportActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
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
}