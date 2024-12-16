package wts.com.accountpe.activities;

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
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

import wts.com.accountpe.R;

public class ShareAepsReportActivity extends AppCompatActivity {

    TextView tvStatus, tvTransactionId,tvTransactionType,tvOldBalance,tvNewBalance,tvAadharNo,tvPanNo,tvBankName,tvCommission,tvSurcharge,tvAmount,tvTxnAmount, tvApiName,
            tvShopDetails, tvDateTime;
    AppCompatButton btnShare;
    String transactionId,transactionType,oldBalance,newBalance,aadharNo,panNo,bankName,commission,surcharge,amount, apiName, status, dateTime;
    SharedPreferences sharedPreferences;
    ImageView imgClose;
    int FILE_PERMISSION = 45;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_share_aeps_report);

        initViews();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ShareAepsReportActivity.this);
        String ownerName = sharedPreferences.getString("username", null);
        String mobile = sharedPreferences.getString("mobileno", null);
        String role = sharedPreferences.getString("usertype", null);

        tvShopDetails.setText("Name : " + ownerName + "(" + role + ")" + "\n" + "Contact No. : " + mobile);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        transactionId=getIntent().getStringExtra("transactionId");
        transactionType=getIntent().getStringExtra("transactionType");
        oldBalance=getIntent().getStringExtra("oldBalance");
        newBalance=getIntent().getStringExtra("balance");
        aadharNo=getIntent().getStringExtra("aadharNo");
        panNo=getIntent().getStringExtra("panNo");
        bankName=getIntent().getStringExtra("bankName");
        commission=getIntent().getStringExtra("comm");
        surcharge=getIntent().getStringExtra("surcharge");
        amount=getIntent().getStringExtra("amount");
        apiName=getIntent().getStringExtra("apiName");
        status=getIntent().getStringExtra("status");
        dateTime=getIntent().getStringExtra("dateTime");

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, FILE_PERMISSION);
            }
        });

        setViews();
    }

    public void checkPermission(String writePermission, String readPermission, int requestCode) {
        if (ContextCompat.checkSelfPermission(ShareAepsReportActivity.this, writePermission) == PackageManager.PERMISSION_DENIED
                && ContextCompat.checkSelfPermission(ShareAepsReportActivity.this, readPermission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(ShareAepsReportActivity.this, new String[]{writePermission, readPermission}, requestCode);
        } else {
            //takeAndShareScreenShot();
            btnShare.setVisibility(View.GONE);
            Bitmap bitmap = getScreenBitmap();
            shareReceipt(bitmap);

        }
    }

    public Bitmap getScreenBitmap() {
        Bitmap bitmap = null;
        try {
            ScrollView shareReportLayout = findViewById(R.id.share_report_layout);
             bitmap = Bitmap.createBitmap(shareReportLayout.getWidth(),
                    shareReportLayout.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            shareReportLayout.draw(canvas);
            btnShare.setVisibility(View.VISIBLE);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        btnShare.setVisibility(View.VISIBLE);
        return bitmap;
    }

    private void shareReceipt(Bitmap bitmap) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
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
                final AlertDialog.Builder permissionDialog = new AlertDialog.Builder(ShareAepsReportActivity.this);
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


    @SuppressLint("SetTextI18n")
    private void setViews() {
        if (transactionType.equalsIgnoreCase("SAP"))
        {
            tvTransactionType.setText("Mini Statement");
        }
        else if (transactionType.equalsIgnoreCase("WAP"))
        {
            tvTransactionType.setText("Cash withdrawal");
        }
        else if (transactionType.equalsIgnoreCase("BAP"))
        {
            tvTransactionType.setText("Balance Enquiry");
        }
        else if (transactionType.equalsIgnoreCase("MZZ"))
        {
            tvTransactionType.setText("Aadhar Pay");
        }
        else
        {
            tvTransactionType.setText(transactionType);
        }
        tvTransactionId.setText(transactionId);
        tvOldBalance.setText("₹ "+oldBalance);
        tvNewBalance.setText("₹ "+newBalance);
        tvAadharNo.setText(aadharNo);
        tvPanNo.setText(panNo);
        tvBankName.setText(bankName);
        tvCommission.setText("₹ "+commission);
        tvSurcharge.setText("₹ "+surcharge);
        tvAmount.setText("₹ "+amount);
        tvTxnAmount.setText("\u20b9 "+amount);
        tvApiName.setText(apiName);
        tvDateTime.setText(dateTime);
        if (status.equalsIgnoreCase("Success"))
        {
            tvStatus.setText("Transaction Successful");
            tvStatus.setTextColor(getResources().getColor(R.color.green));
        }
        else if (status.equalsIgnoreCase("failed"))
        {
            tvStatus.setText("Transaction Failed");
            tvStatus.setTextColor(getResources().getColor(R.color.red));
        }
        else
        {
            tvStatus.setText("Transaction "+status);
            tvStatus.setTextColor(getResources().getColor(R.color.red));
        }

    }

    private void initViews() {
        tvStatus = findViewById(R.id.tvStatus);
        tvTransactionId=findViewById(R.id.tv_transaction_id);
        tvTransactionType=findViewById(R.id.tv_transaction_type);
        tvOldBalance=findViewById(R.id.tv_old_balance);
        tvNewBalance=findViewById(R.id.tv_new_balance);
        tvAadharNo=findViewById(R.id.tv_aadhar_no);
        tvPanNo=findViewById(R.id.tv_pan_no);
        tvBankName=findViewById(R.id.tv_bank_name);
        tvCommission=findViewById(R.id.tv_all_report_commission);
        tvSurcharge=findViewById(R.id.tv_surcharge);
        tvAmount=findViewById(R.id.tv_amount);
        tvTxnAmount=findViewById(R.id.tv_txnAmount);
        tvApiName=findViewById(R.id.tv_apiName);
        tvShopDetails=findViewById(R.id.tv_shop_details);
        tvDateTime=findViewById(R.id.tv_dateTime);

        btnShare=findViewById(R.id.btn_share);
        imgClose = findViewById(R.id.img_close);

    }
}