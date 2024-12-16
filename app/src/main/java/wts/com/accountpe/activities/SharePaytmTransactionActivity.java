package wts.com.accountpe.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import wts.com.accountpe.R;

public class SharePaytmTransactionActivity extends AppCompatActivity {

    TextView tvStatus, tvBankName, tvBankTxnId, tvOrderId, tvMessage, tvAmount, tvDate, tvTxnId;
    String status, bankName, bankTxnId, orderId, message, amount, date, txnId;
    String response;
    Button btnFinish, btnShare;

    int FILE_PERMISSION = 45;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_paytm_transaction);

        initViews();

        response = getIntent().getStringExtra("response");

        try {
            JSONObject responseObject = new JSONObject(response);

            if (responseObject.has("STATUS"))
                status = responseObject.getString("STATUS");

            if (responseObject.has("BANKNAME"))
                bankName = responseObject.getString("BANKNAME");

            if (responseObject.has("BANKTXNID"))
                bankTxnId = responseObject.getString("BANKTXNID");

            if (responseObject.has("ORDERID"))
                orderId = responseObject.getString("ORDERID");

            if (responseObject.has("RESPMSG"))
                message = responseObject.getString("RESPMSG");

            if (responseObject.has("TXNAMOUNT"))
                amount = responseObject.getString("TXNAMOUNT");

            if (responseObject.has("TXNDATE"))
                date = responseObject.getString("TXNDATE");

            if (responseObject.has("TXNID"))
                txnId = responseObject.getString("TXNID");

            setData();

        } catch (Exception e) {
            new AlertDialog.Builder(SharePaytmTransactionActivity.this)
                    .setCancelable(false)
                    .setMessage("There are some issues please contact to your admin")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .show();
        }

        btnFinish.setOnClickListener(v ->
        {
            finish();
        });

        btnShare.setOnClickListener(v ->
        {
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, FILE_PERMISSION);

        });

    }

    public void checkPermission(String writePermission, String readPermission, int requestCode) {
        if (ContextCompat.checkSelfPermission(SharePaytmTransactionActivity.this, writePermission) == PackageManager.PERMISSION_DENIED
                && ContextCompat.checkSelfPermission(SharePaytmTransactionActivity.this, readPermission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(SharePaytmTransactionActivity.this, new String[]{writePermission, readPermission}, requestCode);
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
            ConstraintLayout shareReportLayout = findViewById(R.id.share_report_layout);
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
                final AlertDialog.Builder permissionDialog = new AlertDialog.Builder(SharePaytmTransactionActivity.this);
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

    private void setData() {
        tvStatus.setText(status);
        tvBankName.setText(bankName);
        tvBankTxnId.setText(bankTxnId);
        tvOrderId.setText(orderId);
        tvMessage.setText(message);
        tvAmount.setText(amount);
        tvDate.setText(date);
        tvTxnId.setText(txnId);
    }

    private void initViews() {
        tvStatus = findViewById(R.id.tv_status);
        tvBankName = findViewById(R.id.tv_bank_name);
        tvBankTxnId = findViewById(R.id.tv_bank_txn_id);
        tvOrderId = findViewById(R.id.tv_order_id);
        tvMessage = findViewById(R.id.tv_message);
        tvAmount = findViewById(R.id.tv_amount);
        tvDate = findViewById(R.id.tv_date);
        tvTxnId = findViewById(R.id.tv_txn_id);
        btnFinish = findViewById(R.id.btn_finish);
        btnShare = findViewById(R.id.btn_share);
    }
}