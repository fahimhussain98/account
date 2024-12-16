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
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

import wts.com.accountpe.R;

public class ShareDmtReportActivity extends AppCompatActivity {

    TextView tvTransactionId, tvBankRRN, tvServiceType, tvOldBalance, tvNewBalance, tvBeniName, tvAccountNo, tvBankName, tvCommission, tvSurcharge, tvAmount, tvCost,
            tvShopDetails, tvTransactionAmount, tvStatus, tvDateTime, tvIfsc;
    AppCompatButton btnShare;
    String transactionId, bankRRN, oldBalance, newBalance, name, accountNo, bankName, commission, surcharge, amount, cost, status, dateTime, ifscCode;
    SharedPreferences sharedPreferences;
    ImageView imgClose;
    int FILE_PERMISSION = 45;

    String serviceType;
    LinearLayout transactionTypeContainer, oldBalanceContainer, transactionAmountContainer;

    TextView tvTitle;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_share_dmt_report);

        initViews();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ShareDmtReportActivity.this);
        String ownerName = sharedPreferences.getString("username", null);
        String mobile = sharedPreferences.getString("mobileno", null);
        String role = sharedPreferences.getString("usertype", null);
        tvShopDetails.setText("Name " + ownerName + "(" + role + ")" + "\n" + "Contact No. " + mobile);

        transactionId = getIntent().getStringExtra("transactionId");
        bankRRN = getIntent().getStringExtra("bankRRn");
        serviceType = getIntent().getStringExtra("serviceType");
        oldBalance = getIntent().getStringExtra("openingBal");
        newBalance = getIntent().getStringExtra("balance");
        name = getIntent().getStringExtra("beniName");
        accountNo = getIntent().getStringExtra("accountNumber");
        bankName = getIntent().getStringExtra("bank");
        commission = getIntent().getStringExtra("commission");
        surcharge = getIntent().getStringExtra("surcharge");
        amount = getIntent().getStringExtra("amount");
        cost = getIntent().getStringExtra("cost");
        status = getIntent().getStringExtra("status");
        dateTime = getIntent().getStringExtra("date");
        ifscCode = getIntent().getStringExtra("ifsc");

        tvTransactionId.setText(transactionId);
        tvBankRRN.setText(bankRRN);
        tvOldBalance.setText("₹ " + oldBalance);
        tvNewBalance.setText("₹ " + newBalance);
        tvDateTime.setText(dateTime);
        tvBeniName.setText(name);
        tvAccountNo.setText(accountNo);
        tvBankName.setText(bankName);
        tvIfsc.setText(ifscCode);
        tvCommission.setText("₹ " + commission);
        tvSurcharge.setText("₹ " + surcharge);
        tvTransactionAmount.setText(amount);
        tvAmount.setText(cost);
        tvStatus.setText(status);
        tvServiceType.setText(serviceType);

        if (status.equalsIgnoreCase("Success")) {
            tvStatus.setTextColor(getResources().getColor(R.color.green));
        } else if (status.equalsIgnoreCase("Failed")) {
            tvStatus.setTextColor(Color.RED);
        } else {
            tvStatus.setTextColor(Color.YELLOW);
        }

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
    }

    public void checkPermission(String writePermission, String readPermission, int requestCode) {
        if (ContextCompat.checkSelfPermission(ShareDmtReportActivity.this, writePermission) == PackageManager.PERMISSION_DENIED
                && ContextCompat.checkSelfPermission(ShareDmtReportActivity.this, readPermission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(ShareDmtReportActivity.this, new String[]{writePermission, readPermission}, requestCode);
        } else {
            //takeAndShareScreenShot();
            btnShare.setVisibility(View.GONE);
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
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        btnShare.setVisibility(View.VISIBLE);
        return b;
    }

    private void shareReceipt(Bitmap bitmap) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title" + System.currentTimeMillis(), null);
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
                final AlertDialog.Builder permissionDialog = new AlertDialog.Builder(ShareDmtReportActivity.this);
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

        tvTransactionId = findViewById(R.id.tv_transaction_id);
        tvBankRRN = findViewById(R.id.tv_bankRRN);
        tvServiceType = findViewById(R.id.tv_txnType);
        tvOldBalance = findViewById(R.id.tv_old_balance);
        tvNewBalance = findViewById(R.id.tv_new_balance);
        tvBeniName = findViewById(R.id.tv_account_name);
        tvAccountNo = findViewById(R.id.tv_account_no);
        tvBankName = findViewById(R.id.tv_bank_name);
        tvIfsc = findViewById(R.id.tv_ifscCode);
        tvCommission = findViewById(R.id.tv_all_report_commission);
        tvSurcharge = findViewById(R.id.tv_surcharge);
        tvAmount = findViewById(R.id.tv_amount);
        tvShopDetails = findViewById(R.id.tv_shop_details);
        tvTransactionAmount = findViewById(R.id.tv_transaction_amount);
        tvStatus = findViewById(R.id.tv_status);
        tvDateTime = findViewById(R.id.tv_date_time);
        tvTitle = findViewById(R.id.text_transaction_slip);

        transactionTypeContainer = findViewById(R.id.transaction_type_container);
        oldBalanceContainer = findViewById(R.id.old_balance_container);
        transactionAmountContainer = findViewById(R.id.transaction_amount_container);

        btnShare = findViewById(R.id.btn_share);
        imgClose = findViewById(R.id.img_close);
    }
}