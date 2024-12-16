package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.retrofit.RetrofitClient;

public class FundTransferActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1;
    private static final int FILE_PERMISSION = 2;
    String title;
    TextView activityTitle;
    ImageView backButton;

    String[] paymentModeArr = {"BY IMPS", "BY NEFT", "BY RTGS", "BY WALLET", "BY CASH IN BANK", "UPI PAYMENT", "BY ATM TRANSFER"};
    String[] requestToArr = {"PARENT", "ADMIN"};
    Spinner spinner, spinnerBank, spinnerRequestTo;
    String selectedPaymentMode = "Select Payment Mode", selectedBankName = "select", selectRequestTo = "select";

    EditText etAmount, etRefrenceNumber, etRemarks;
    TextView tvDate, tvUploadReceipt;
    Button btnSubmit;
    SharedPreferences sharedPreferences;
    String userId;
    ArrayList<String> bankList;
    String amount, comment, bankRefNo, date = "select";
    ArrayList<String> photoPaths;
    private String fileUrl = "NA";
    String deviceId, deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_transfer);

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
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(FundTransferActivity.this);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        getBankList();

        HintSpinner<String> hintSpinner = new HintSpinner<>(
                spinner,
                new HintAdapter<String>(FundTransferActivity.this, "Select Payment Mode", Arrays.asList(paymentModeArr)),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        selectedPaymentMode = paymentModeArr[position];
                    }
                });
        hintSpinner.init();

        HintSpinner<String> hintSpinnerRequestTo = new HintSpinner<>(
                spinnerRequestTo,
                new HintAdapter<String>(FundTransferActivity.this, "Request To", Arrays.asList(requestToArr)),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        selectRequestTo = requestToArr[position];
                    }
                });
        hintSpinnerRequestTo.init();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        /*String date = day + "-" + (month + 1) + "-" + year + "";
        tvDate.setText(date);*/


        tvDate.setOnClickListener(v ->
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            SimpleDateFormat webServiceDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            DatePickerDialog datePickerDialog = new DatePickerDialog(FundTransferActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Calendar date1 = Calendar.getInstance();
                    date1.set(year, month, dayOfMonth);

                    tvDate.setText(simpleDateFormat.format(date1.getTime()));
                    date = webServiceDateFormat.format(date1.getTime());

                }
            }, year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        btnSubmit.setOnClickListener(v -> {
            if (checkInternetState()) {
                if (checkInputs()) {
                    amount = etAmount.getText().toString().trim();
                    comment = etRemarks.getText().toString().trim();
                    bankRefNo = etRefrenceNumber.getText().toString().trim();
                    doPaymentRequest(userId, amount, comment, bankRefNo, selectedPaymentMode);
                }
            } else {
                showSnackBar("No Internet");
            }
        });

        tvUploadReceipt.setOnClickListener(v ->
        {
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, FILE_PERMISSION);
        });

    }

    public void checkPermission(String writePermission, String readPermission, int requestCode) {

        if (ContextCompat.checkSelfPermission(FundTransferActivity.this, writePermission) == PackageManager.PERMISSION_DENIED
                && ContextCompat.checkSelfPermission(FundTransferActivity.this, readPermission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(FundTransferActivity.this, new String[]{writePermission, readPermission}, requestCode);
        } else {
            chooseImage();
        }
    }

    public void chooseImage() {

       /* photoPaths = new ArrayList<>();
        FilePickerBuilder.getInstance()
                //.setSelectedFiles(photoPaths)
                .setActivityTitle("Please select media")
                .enableVideoPicker(true)
                .enableCameraSupport(true)
                .showGifs(false)
                .showFolderView(true)
                .enableSelectAll(false)
                .enableImagePicker(true)
                .setCameraPlaceholder(R.drawable.camera)
                .withOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .pickPhoto(this, PICK_IMAGE_REQUEST);*/

        /*photoPaths = new ArrayList<>();
        FilePickerBuilder.getInstance()
                .setSelectedFiles(photoPaths)
                .setActivityTitle("Please select media")
                .enableVideoPicker(true)
                .enableCameraSupport(true)
                .showGifs(true)
                .setMaxCount(1)
                .showFolderView(true)
                .enableSelectAll(false)
                .enableImagePicker(true)
                .setCameraPlaceholder(R.drawable.ic_camera)
                .withOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .pickPhoto(this, CAMERA_REQUEST);*/

        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, CAMERA_REQUEST);



    }

    private void getBankList() {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(FundTransferActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getBankList(AUTH_KEY, userId, deviceId, deviceInfo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {

                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");
                        if (responseCode.equalsIgnoreCase("TXN")) {
                            JSONArray transactionArray = responseObject.getJSONArray("data");
                            bankList = new ArrayList<>();
                            for (int i = 0; i < transactionArray.length(); i++) {
                                JSONObject transactionObject = transactionArray.getJSONObject(i);
                                String bankName = transactionObject.getString("BankName");

                                bankList.add(bankName);

                            }

                            HintSpinner<String> hintSpinner = new HintSpinner<>(
                                    spinnerBank,
                                    new HintAdapter<String>(FundTransferActivity.this, "Select Bank", bankList),
                                    new HintSpinner.Callback<String>() {
                                        @Override
                                        public void onItemSelected(int position, String itemAtPosition) {
                                            selectedBankName = bankList.get(position);
                                        }
                                    });
                            hintSpinner.init();

                            pDialog.dismiss();
                        } else {
                            pDialog.dismiss();
                            String message=responseObject.getString("data");
                            new android.app.AlertDialog.Builder(FundTransferActivity.this)
                                    .setMessage(message)
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }).show();
                        }

                    } catch (Exception e) {
                        pDialog.dismiss();
                        new AlertDialog.Builder(FundTransferActivity.this)
                                .setMessage("Something went wrong.")
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(FundTransferActivity.this)
                            .setMessage("Something went wrong.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(FundTransferActivity.this)
                        .setMessage("Something went wrong.")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
        });
    }

    private void doPaymentRequest(String userid, String amount, String comment, String bankRefNo, String selectedPaymentMode) {

        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(FundTransferActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().doPaymentRequest(AUTH_KEY,userId,deviceId,deviceInfo,date,bankRefNo,selectedBankName,
                selectedPaymentMode,comment,fileUrl,selectRequestTo,amount);

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;

                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));

                        String statusCode = jsonObject1.getString("statuscode");
                        String responseMessage = jsonObject1.getString("data");
                        String transactions = jsonObject1.getString("status");

                        if (statusCode.equalsIgnoreCase("TXN")) {

                            final View view = LayoutInflater.from(FundTransferActivity.this).inflate(R.layout.recharge_status_layout, null, false);
                            final AlertDialog rechargeStatusDialog = new AlertDialog.Builder(FundTransferActivity.this).create();
                            rechargeStatusDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            rechargeStatusDialog.setCancelable(false);
                            rechargeStatusDialog.setView(view);
                            rechargeStatusDialog.show();

                            ImageView imgRechargeDialogIcon = rechargeStatusDialog.findViewById(R.id.img_recharge_dialog_icon);
                            TextView tvRechargeDialogNumber = rechargeStatusDialog.findViewById(R.id.tv_recharge_dialogue_number);
                            TextView tvRechargeDialogTitle = rechargeStatusDialog.findViewById(R.id.tv_recharge_dialog_title);
                            TextView tvRechargeDialogStatus = rechargeStatusDialog.findViewById(R.id.tv_recharge_dialogue_status);
                            TextView tvRechargeDialogAmount = rechargeStatusDialog.findViewById(R.id.tv_recharge_dialogue_amount);
                            Button btnRechargeDialog = rechargeStatusDialog.findViewById(R.id.btn_recharge_dialog);


                            tvRechargeDialogNumber.setVisibility(View.INVISIBLE);
                            tvRechargeDialogAmount.setVisibility(View.INVISIBLE);

                            imgRechargeDialogIcon.setImageResource(R.drawable.successicon);
                            tvRechargeDialogStatus.setText("Status :" + responseMessage);
                            tvRechargeDialogTitle.setText("Status");

                            btnRechargeDialog.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    rechargeStatusDialog.dismiss();
                                    finish();
                                }
                            });
                            pDialog.dismiss();
                        } else if (statusCode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();
                            String status = jsonObject1.getString("data");
                            final Dialog rechargeStatusDialog = new Dialog(FundTransferActivity.this);
                            rechargeStatusDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            rechargeStatusDialog.setCancelable(false);
                            rechargeStatusDialog.setContentView(R.layout.recharge_status_layout);
                            rechargeStatusDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            rechargeStatusDialog.show();

                            ImageView imgRechargeDialogIcon = rechargeStatusDialog.findViewById(R.id.img_recharge_dialog_icon);
                            TextView tvRechargeDialogNumber = rechargeStatusDialog.findViewById(R.id.tv_recharge_dialogue_number);
                            TextView tvRechargeDialogStatus = rechargeStatusDialog.findViewById(R.id.tv_recharge_dialogue_status);
                            TextView tvRechargeDialogAmount = rechargeStatusDialog.findViewById(R.id.tv_recharge_dialogue_amount);
                            Button btnRechargeDialog = rechargeStatusDialog.findViewById(R.id.btn_recharge_dialog);

                            tvRechargeDialogAmount.setVisibility(View.INVISIBLE);
                            tvRechargeDialogNumber.setVisibility(View.INVISIBLE);

                            tvRechargeDialogStatus.setTextColor(Color.RED);
                            tvRechargeDialogStatus.setText("Status : " + status);
                            imgRechargeDialogIcon.setImageResource(R.drawable.failureicon);
                            btnRechargeDialog.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    rechargeStatusDialog.dismiss();
                                    finish();
                                }
                            });
                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(FundTransferActivity.this)
                                    .setTitle("Alert")
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("Ok", null).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(FundTransferActivity.this)
                                .setTitle("Alert")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Ok", null).show();
                    }

                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(FundTransferActivity.this)
                            .setTitle("Alert")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(FundTransferActivity.this)
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null).show();
            }
        });
    }

    private boolean checkInputs() {

        if (!selectedPaymentMode.equalsIgnoreCase("Select Payment Mode")) {
            if (!TextUtils.isEmpty(etAmount.getText())) {
                if (!TextUtils.isEmpty(etRefrenceNumber.getText())) {
                    if (!TextUtils.isEmpty(etRemarks.getText())) {
                        if (!date.equalsIgnoreCase("select")) {
                            if (!selectedBankName.equalsIgnoreCase("select")) {
                                if (!selectRequestTo.equalsIgnoreCase("select")) {

                                    return true;
                                } else {
                                    showSnackBar("select Request To");
                                    return false;
                                }
                            } else {
                                showSnackBar("select bank");
                                return false;
                            }
                        } else {
                            showSnackBar("select date");
                            return false;
                        }

                    } else {
                        etRemarks.setError("Enter Remarks");
                        return false;
                    }
                } else {
                    etRefrenceNumber.setError("Enter Reference Number");
                    return false;
                }
            } else {
                etAmount.setError("Enter Amount");
                return false;
            }
        } else {
            new AlertDialog.Builder(FundTransferActivity.this).setMessage("Select Payment Mode")
                    .setPositiveButton("OK", null).show();
            return false;
        }

    }

    private void initViews() {
        backButton = findViewById(R.id.back_button);
        activityTitle = findViewById(R.id.activity_title);
        etAmount = findViewById(R.id.et_amount);
        etRefrenceNumber = findViewById(R.id.et_reference_number);
        etRemarks = findViewById(R.id.et_remarks);
        tvDate = findViewById(R.id.tv_date);
        tvUploadReceipt = findViewById(R.id.tv_upload_receipt);
        spinner = findViewById(R.id.spinner);
        spinnerBank = findViewById(R.id.spinner_bank);
        spinnerRequestTo = findViewById(R.id.spinner_request_to);
        btnSubmit = findViewById(R.id.btn_submit);
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.fund_transfer_layout), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    private void serverUpload(File myfile) {

        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(FundTransferActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        RequestBody reqFile;
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        /*if (myfile.toString().contains(".pdf")) {
            reqFile = RequestBody.create(MediaType.parse("application/pdf"), myfile);
        }

        else if (myfile.toString().contains(".jpg") || myfile.toString().contains(".jpeg") || myfile.toString().contains(".png")) {
            reqFile = RequestBody.create(MediaType.parse("image/*"), myfile);
        } else {
            pDialog.dismiss();
            Toast.makeText(this, "Please select only image file.", Toast.LENGTH_SHORT).show();
            return;
        }*/

        reqFile = RequestBody.create(MediaType.parse("image/*"), myfile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("myFile", timeStamp + myfile.getName(), reqFile);


        Call<JsonObject> call = RetrofitClient.getInstance().getApi().uploadfile(AUTH_KEY,body);
        call.enqueue(new Callback<JsonObject>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        String code = jsonObject.getString("statuscode");

                        if (code.equalsIgnoreCase("TXN")) {
                            tvUploadReceipt.setText("File Uploaded");
                            fileUrl = jsonObject.getString("data");
                            pDialog.dismiss();

                        } else if (code.equalsIgnoreCase("ERR")) {
                            showSnackBar("Try again.");
                            pDialog.dismiss();

                        } else {
                            pDialog.dismiss();
                            showSnackBar("Try again.");
                        }

                    } catch (JSONException e) {
                        pDialog.dismiss();
                        e.printStackTrace();
                        showSnackBar("Try again.");
                    }

                } else {
                    pDialog.dismiss();
                    showSnackBar("Try again.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                showSnackBar("Try again.");
            }
        });

    }

    @SuppressLint("SetTextI18n")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (CAMERA_REQUEST == requestCode && resultCode == RESULT_OK && data != null) {
               /* photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                String path = photoPaths.get(0);*/
               /* Uri filepath=data.getData();
                String path=filepath.toString();

                File file = new File(filepath.getPath());
                fileUrl = "NA";
                serverUpload(file);*/

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String mediaPath = cursor.getString(columnIndex);
                File file=new File(mediaPath);
                serverUpload(file);
                // Set the Image in ImageView for Previewing the Media
                cursor.close();
            } else {
                fileUrl = "NA";
                tvUploadReceipt.setText("Upload Receipt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}