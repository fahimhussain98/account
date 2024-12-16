package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.BuildConfig;
import wts.com.accountpe.R;
import wts.com.accountpe.retrofit.RetrofitClient;


public class ReferEarnActivity extends AppCompatActivity {

    ImageView imgBack;
    TextView tvTitle, tvWallet;
    TextView tvReferralCode, tvReceiverCommission, tvSenderCommission;
    LinearLayout btnRefer;

    String deviceId, deviceInfo, userid, mobileNo;
    String strReceiverCommission, strSenderCommission;
    String referralCode;
    SharedPreferences sharedPreferences;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_earn);

        tvTitle = findViewById(R.id.activity_title);
        tvWallet = findViewById(R.id.tvWallet);

        imgBack = findViewById(R.id.back_button);
        tvReferralCode = findViewById(R.id.tvReferralCode);
        tvReceiverCommission = findViewById(R.id.tvReceiverCommission);
        btnRefer = findViewById(R.id.layout_btnRefer);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        userid = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        mobileNo = sharedPreferences.getString("mobileno", null);


        getCodeForReferEarn();

        tvTitle.setText("Refer&Earn");


        imgBack.setOnClickListener(view -> {
            finish();
        });

        btnRefer.setOnClickListener(view -> {

            String referMessage = "call : "+mobileNo+" \tacntpe@gmail.com\n\nDear friend, I am sending you the India's highest CashBack platform & best mobile recharge, Postpaid, DTH, BBPS, AEPS, services application link. Download it and get gift voucher up to Rs. 100, on your first successful payment to your wallet, Hurry Up..!\nACCOUNTPE\n";

            Bitmap imgBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.logojpg);
            String imgBitmapPath = MediaStore.Images.Media.insertImage(ReferEarnActivity.this.getContentResolver(),imgBitmap,"title"+ System.currentTimeMillis(),null);
            Uri imgBitmapUri = Uri.parse(imgBitmapPath);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            // shareIntent.setPackage("com.whatsapp");      // for only on whatsapp sharing .....
            //   shareIntent.setPackage("com.google.android.gm");    // for only on gmail sharing  .....
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shareIntent.putExtra(Intent.EXTRA_STREAM,imgBitmapUri);
            shareIntent.setType("image/*");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Account Pe");
            String shareUrl = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"&referrer="+referralCode+"\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, referMessage+"\n" + shareUrl+"\nYour Refer Code is "+referralCode);
            //    shareIntent.putExtra(Intent.EXTRA_TEXT, "Use this Code on SignUp :\n" + referralCode);
            startActivity(Intent.createChooser(shareIntent, "Share this"));


        });

    }

    private void getCodeForReferEarn() {

        ProgressDialog pDialog = new ProgressDialog(ReferEarnActivity.this);
        pDialog.setMessage("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getReferEarnCode(AUTH_KEY, userid, deviceId, deviceInfo, "1343");

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    pDialog.dismiss();
                    JSONObject jsonObject1 = null;
                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));

                        String statusCode = jsonObject1.getString("statuscode");

                        if (statusCode.equalsIgnoreCase("TXN")) {
                           referralCode = jsonObject1.getString("data");
                            tvReferralCode.setText(referralCode);
                            getReferCommission();

                        }

                        else if (statusCode.equalsIgnoreCase("ERR"))
                        {
                            new AlertDialog.Builder(ReferEarnActivity.this)
                                    .setCancelable(false)
                                    .setMessage(jsonObject1.getString("data"))
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    })
                                    .show();
                        }

                        else {

                            new AlertDialog.Builder(ReferEarnActivity.this)
                                    .setMessage("Something went wrong")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    }).show();

                        }


                    } catch (JSONException e) {

                        new AlertDialog.Builder(ReferEarnActivity.this)
                                .setMessage("Something went wrong")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                }).show();
                    }

                } else {

                    pDialog.dismiss();
                    new AlertDialog.Builder(ReferEarnActivity.this)
                            .setMessage("Something went wrong")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).show();

                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(ReferEarnActivity.this)
                        .setMessage("Something went wrong")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).show();

            }
        });
    }

    private void getReferCommission() {

        ProgressDialog pDialog = new ProgressDialog(ReferEarnActivity.this);
        pDialog.setMessage("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getReferCommission(AUTH_KEY, userid, deviceId, deviceInfo,"39");

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    pDialog.dismiss();
                    JSONObject jsonObject1 = null;
                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));

                        String response_code = jsonObject1.getString("statuscode");

                        if (response_code.equalsIgnoreCase("TXN")) {

                            JSONArray jsonArray = jsonObject1.getJSONArray("data");
                            JSONObject dataObject = jsonArray.getJSONObject(0);
                            strReceiverCommission = dataObject.getString("recieverComm");
                            strSenderCommission = dataObject.getString("senderComm");
                            tvReceiverCommission.setText(getResources().getString(R.string.refer_earn_text3)+" "+strReceiverCommission);
                            tvWallet.setText(getResources().getText(R.string.share_earn_text1)+" "+strReceiverCommission);

                        }
                        else {
                            new AlertDialog.Builder(ReferEarnActivity.this)
                                    .setMessage(jsonObject1.optString("data"))
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    }).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        new AlertDialog.Builder(ReferEarnActivity.this)
                                .setMessage("Something went wrong")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                }).show();
                    }

                }
                else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(ReferEarnActivity.this)
                            .setMessage("Something went wrong")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).show();
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(ReferEarnActivity.this)
                        .setMessage("Something went wrong")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).show();
            }
        });
    }

}