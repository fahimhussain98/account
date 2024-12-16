package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.retrofit.RetrofitClient;

public class ShareReportActivity extends AppCompatActivity {

    String number, amount,pgAmount, commission, cost, openingBalance,closingBalance, date,time, status, operator, imgUrl,uniqueId,liveId, serviceName;
    SharedPreferences sharedPreferences;
    TextView tvNumber, tvAmount,tvPgAmount, tvCommission, tvCost, tvClosingBalance,tvOpeningBalance, tvDateTime, tvStatus, tvOperator, tvShopDetails,
            tvTxnStatus,tvTime,tvLiveId, tvTextNumber;
    AppCompatButton btnShare,btnRaiseComplaint;
    int FILE_PERMISSION = 45;
    ImageView imgClose;

    String userId,deviceId,deviceInfo;

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_share_report);

        initViews();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ShareReportActivity.this);
        String ownerName = sharedPreferences.getString("username", null);
        String mobile = sharedPreferences.getString("mobileno", null);
        String role = sharedPreferences.getString("usertype", null);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        number = getIntent().getStringExtra("number");
        amount = getIntent().getStringExtra("amount");
        pgAmount = getIntent().getStringExtra("pgAmount");
        commission = getIntent().getStringExtra("commission");
        cost = getIntent().getStringExtra("cost");
        openingBalance = getIntent().getStringExtra("openingBal");
        closingBalance = getIntent().getStringExtra("closingBal");
        date = getIntent().getStringExtra("date");
        status = getIntent().getStringExtra("status");
        operator = getIntent().getStringExtra("operator");
        imgUrl = getIntent().getStringExtra("imgUrl");
        time = getIntent().getStringExtra("time");
        uniqueId = getIntent().getStringExtra("uniqueId");
        liveId = getIntent().getStringExtra("liveId");
        serviceName = getIntent().getStringExtra("serviceName");

        tvNumber.setText(number);
        tvAmount.setText("₹ " + amount);
        tvPgAmount.setText("\u20b9"+pgAmount);
        tvCommission.setText("₹ " + commission);
        tvCost.setText("₹ " + cost);
        tvOpeningBalance.setText("₹ " + openingBalance);
        tvClosingBalance.setText("₹ " + closingBalance);
        tvDateTime.setText(date);
        tvStatus.setText(status);
        tvTxnStatus.setText(status);
        tvOperator.setText(operator);
        tvTime.setText(time);
        tvLiveId.setText(liveId);

        if (serviceName.equalsIgnoreCase("Recharge") || serviceName.equalsIgnoreCase("Postpaid"))
        {
            tvTextNumber.setText("Number");
        }
        else if (serviceName.equalsIgnoreCase("Fastag"))
        {
            tvTextNumber.setText("Vehicle No");
        }
        else
        {
            tvTextNumber.setText("ConsumerNo.");
        }

        if((status.equalsIgnoreCase("Success") || status.equalsIgnoreCase("Successful")))
        {
            tvStatus.setBackground(getResources().getDrawable(R.drawable.button_back_green));
        }
        else if (status.equalsIgnoreCase("Failed") || status.equalsIgnoreCase("failure"))
        {
            tvStatus.setBackground(getResources().getDrawable(R.drawable.button_back_red));
        }
        else
        {
            tvStatus.setBackground(getResources().getDrawable(R.drawable.button_back));
        }

        tvShopDetails.setText("Name  :  " + ownerName + "(" + role + ")" + "\n" + "Contact No.  :  " + mobile);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, FILE_PERMISSION);

            }
        });

        btnRaiseComplaint.setOnClickListener(v->
        {
            final android.app.AlertDialog complaintDialog = new android.app.AlertDialog.Builder(ShareReportActivity.this).create();
            LayoutInflater inflater = getLayoutInflater();
            View convertView = inflater.inflate(R.layout.complaint_dialog, null);
            complaintDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            complaintDialog.setView(convertView);
            complaintDialog.setCancelable(false);
            complaintDialog.show();

            ImageView imgClose=convertView.findViewById(R.id.img_close);
            EditText etRemarks=convertView.findViewById(R.id.et_remarks);
            AppCompatButton btnMakeComplaint=convertView.findViewById(R.id.btn_make_complaint);

            imgClose.setOnClickListener(v1->
            {
                complaintDialog.dismiss();
            });

            btnMakeComplaint.setOnClickListener(v1->
            {
                if (!TextUtils.isEmpty(etRemarks.getText()))
                {
                    String remarks=etRemarks.getText().toString().trim();
                    raiseComplaint(remarks);
                    complaintDialog.dismiss();
                }
                else
                    etRemarks.setError("Required");
            });
        });
    }

    private void raiseComplaint(String remarks) {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(ShareReportActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call= RetrofitClient.getInstance().getApi().makeComplaint(AUTH_KEY,userId,deviceId,deviceInfo,uniqueId,remarks,
                "NA",liveId,serviceName, date);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful())
                {
                    try {
                        JSONObject responseObject=new JSONObject(String.valueOf(response.body()));
                        String message=responseObject.getString("data");
                        pDialog.dismiss();
                        new AlertDialog.Builder(ShareReportActivity.this)
                                .setMessage(message)
                                .setTitle("Complain Status")
                                .setPositiveButton("ok",null)
                                .show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        Toast.makeText(ShareReportActivity.this, "Failed to raise complain.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    pDialog.dismiss();
                    Toast.makeText(ShareReportActivity.this, "Failed to raise complain.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                Toast.makeText(ShareReportActivity.this, "Failed to raise complain.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkPermission(String writePermission, String readPermission, int requestCode) {
        if (ContextCompat.checkSelfPermission(ShareReportActivity.this, writePermission) == PackageManager.PERMISSION_DENIED
                && ContextCompat.checkSelfPermission(ShareReportActivity.this, readPermission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(ShareReportActivity.this, new String[]{writePermission, readPermission}, requestCode);
        } else {
            //takeAndShareScreenShot();
            btnShare.setVisibility(View.GONE);
            btnRaiseComplaint.setVisibility(View.GONE);
            Bitmap bitmap = getScreenBitmap();
            shareReceipt(bitmap);

        }
    }

    public Bitmap getScreenBitmap() {
        Bitmap b = null;
        try {
            ScrollView shareReportLayout = findViewById(R.id.share_report_layout);
            Bitmap bitmap = Bitmap.createBitmap(shareReportLayout.getWidth(),
                    shareReportLayout.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            shareReportLayout.draw(canvas);
            btnShare.setVisibility(View.VISIBLE);
            btnRaiseComplaint.setVisibility(View.VISIBLE);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return b;
    }

    private void shareReceipt(Bitmap bitmap) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title"+System.currentTimeMillis(), null);
            Uri imageUri = Uri.parse(path);
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/*");
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            share.putExtra(Intent.EXTRA_STREAM, imageUri);
            startActivity(Intent.createChooser(share, "Share link!"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == FILE_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                final AlertDialog.Builder permissionDialog = new AlertDialog.Builder(ShareReportActivity.this);
                permissionDialog.setTitle("Permission Required");
                permissionDialog.setMessage("You can set permission manually." + "\n" + "Settings-> App Permission -> Allow Storage permission.");
                permissionDialog.setCancelable(false);
                permissionDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                permissionDialog.show();

            }
        }
    }

    private void initViews() {
        tvNumber = findViewById(R.id.tv_all_report_number);
        tvAmount = findViewById(R.id.tv_all_report_amount);
        tvPgAmount = findViewById(R.id.tv_all_report_pgAmount);
        tvCommission = findViewById(R.id.tv_all_report_commission);
        tvCost = findViewById(R.id.tv_all_report_cost);
        tvOpeningBalance = findViewById(R.id.tv_opening_balance);
        tvClosingBalance = findViewById(R.id.tv_closing_balance);
        tvDateTime = findViewById(R.id.tv_all_report_date_time);
        tvTime = findViewById(R.id.tv_time);
        tvLiveId = findViewById(R.id.tv_liveId);
        tvTextNumber = findViewById(R.id.textNumber);
        tvStatus = findViewById(R.id.tv_status);
        tvOperator = findViewById(R.id.tv_all_report_operator_name);
        tvShopDetails = findViewById(R.id.tv_shop_details);
        btnShare = findViewById(R.id.btn_share);
        imgClose = findViewById(R.id.img_close);
        tvTxnStatus = findViewById(R.id.tv_txn_status);
        btnRaiseComplaint = findViewById(R.id.btn_raise_complaint);

    }
}