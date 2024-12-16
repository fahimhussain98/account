package wts.com.accountpe.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;

import wts.com.accountpe.R;


public class AepsTransactionSecondActivity extends AppCompatActivity {

    LinearLayout amountContainer, accountBalContainer;
    TextView tvStatusLine,tvTransactionId,tvType,tvAadharNo,tvBankName,tvMobileNumber,tvAmount,tvDateTime,tvAccountBalance;
    ImageView imgStatus,imgShare;
    AppCompatButton btnDone;

    String transactionId,type,aadharNo,bankName,mobileNumber,amount,status,date,time,message,accountBalance;
    int FILE_PERMISSION = 45;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aeps_transaction_second);
        initViews();

        transactionId=getIntent().getStringExtra("transactionId");
        type=getIntent().getStringExtra("transactionType");
        aadharNo=getIntent().getStringExtra("responseAadharNumber");
        bankName=getIntent().getStringExtra("bankName");
        amount=getIntent().getStringExtra("responseAmount");
        mobileNumber=getIntent().getStringExtra("responseMobileNumber");
        status=getIntent().getStringExtra("status");
        date=getIntent().getStringExtra("outputDate");
        time=getIntent().getStringExtra("outputTime");
        message=getIntent().getStringExtra("message");
        accountBalance=getIntent().getStringExtra("accountBalance");

        tvDateTime.setText(date+"\n"+time);
        tvTransactionId.setText(transactionId);
        tvType.setText(type);
        tvBankName.setText(bankName);
        tvAadharNo.setText(aadharNo);
        tvMobileNumber.setText(mobileNumber);
        tvAmount.setText("₹ "+amount);
        tvAccountBalance.setText("₹ "+accountBalance);

       if (type.equalsIgnoreCase("Balance Inquiry") || type.equalsIgnoreCase("Balance Enquiry")) {
           amountContainer.setVisibility(View.GONE);
       }

        if (status.equalsIgnoreCase("Success")) {
            tvStatusLine.setText("Congratulations!\n");
            imgStatus.setImageResource(R.drawable.bg_success_ic);
        }
        else {
            tvStatusLine.setTextColor(getResources().getColor(R.color.red));
            tvStatusLine.setText(message);
            imgStatus.setImageResource(R.drawable.bg_failed_ic);
        }

        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, FILE_PERMISSION);

            }
        });

        btnDone.setOnClickListener(v-> {
            finish();
        });

    }

    public void checkPermission(String writePermission, String readPermission, int requestCode) {
        if (ContextCompat.checkSelfPermission(AepsTransactionSecondActivity.this, writePermission) == PackageManager.PERMISSION_DENIED
                && ContextCompat.checkSelfPermission(AepsTransactionSecondActivity.this, readPermission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(AepsTransactionSecondActivity.this, new String[]{writePermission, readPermission}, requestCode);
        } else {
            //takeAndShareScreenShot();
            imgShare.setVisibility(View.GONE);
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
            imgShare.setVisibility(View.VISIBLE);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        imgShare.setVisibility(View.VISIBLE);
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
                final AlertDialog.Builder permissionDialog = new AlertDialog.Builder(AepsTransactionSecondActivity.this);
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
        amountContainer = findViewById(R.id.amountContainer);
        accountBalContainer = findViewById(R.id.accountBalanceContainer);
        tvStatusLine=findViewById(R.id.tv_status_line);
        tvTransactionId=findViewById(R.id.tv_transaction_id);
        tvType=findViewById(R.id.tv_type);
        tvAccountBalance=findViewById(R.id.tv_account_balance);
        tvAadharNo=findViewById(R.id.tv_aadhar_no);
        tvBankName=findViewById(R.id.tv_bank_name);
        tvMobileNumber=findViewById(R.id.tv_mobile_number);
        tvAmount=findViewById(R.id.tv_amount);
        tvDateTime=findViewById(R.id.tv_date_time);
        imgStatus=findViewById(R.id.img_status);
        imgShare=findViewById(R.id.img_share);
        btnDone=findViewById(R.id.btn_done);
    }
}