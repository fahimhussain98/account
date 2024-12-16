package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.retrofit.RetrofitClient;

public class UserKycNewActivity extends AppCompatActivity {
    ImageView imgBack;
    TextView textAadhar, textPan;
    LinearLayout aadharLayout, panLayout;
    EditText etAadhar, etAadharOtp, etPan;
    ImageView imgVerifiedAadhar, imgVerifiedPan;
    TextView tvUplaodAadharFront, tvUploadAadharBack, tvUploadPan, tvShop1, tvShop2, tvAgreement1, tvAgreement2;
    Button btnSubmit;

    ////  upload image

    String kycStatus = "";
    private static final int GALLERY_REQUEST = 0;
    private static final int CAMERA_REQUEST = 1;
    private static final int FILE_PERMISSION = 2;
    String whichButtonClicked = "Maine kiya";
    boolean isGalleryClicked = false;
    String aadharFrontUrl = "select", aadharBackUrl = "select", panUrl = "select", shop1Url = "select", shop2Url = "select", agreement1Url = "select",
            agreement2Url = "select";

    ///////////////////
    String transactionId;
    String aadharName, panName, fatherName, dateOfBirth, gender, photo_link, fullAddress;
    String userId, deviceId, deviceInfo;
    SharedPreferences sharedPreferences;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_kyc_new);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(UserKycNewActivity.this);
        kycStatus = getIntent().getStringExtra("kycStatus");
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        initViews();

        imgBack.setOnClickListener(view ->
        {
            finish();
        });

        if (kycStatus.equalsIgnoreCase("RJC")) {
            textAadhar.setVisibility(View.GONE);
            aadharLayout.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.VISIBLE);
            tvUplaodAadharFront.setVisibility(View.VISIBLE);
            tvUploadAadharBack.setVisibility(View.VISIBLE);
            tvUploadPan.setVisibility(View.VISIBLE);
            tvShop1.setVisibility(View.VISIBLE);
            tvShop2.setVisibility(View.VISIBLE);
            tvAgreement1.setVisibility(View.VISIBLE);
            tvAgreement2.setVisibility(View.VISIBLE);
        }

        etAadhar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 12) {
                    checkAadhar();
                }
            }
        });

        etAadharOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 6) {
                    verifyAadhar();
                }
            }
        });

        etPan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 10) {
                    verifyPan();
                }
            }
        });

        tvUplaodAadharFront.setOnClickListener(view ->
        {
            whichButtonClicked = "aadharFrontClicked";
            openGalleryCameraDialog();
//            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_MEDIA_IMAGES,Manifest.permission.CAMERA, FILE_PERMISSION);
        });
        tvUploadAadharBack.setOnClickListener(view ->
        {
            whichButtonClicked = "aadharBackClicked";
            openGalleryCameraDialog();
            //  checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES,Manifest.permission.CAMERA,FILE_PERMISSION);
        });
        tvUploadPan.setOnClickListener(view ->
        {
            whichButtonClicked = "panClicked";
            openGalleryCameraDialog();
            // checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_MEDIA_IMAGES,Manifest.permission.CAMERA, FILE_PERMISSION);
        });
        tvShop1.setOnClickListener(view ->
        {
            whichButtonClicked = "shop1Clicked";
        //    openGalleryCameraDialog();
            isGalleryClicked = false;
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA, FILE_PERMISSION);

        });
        tvShop2.setOnClickListener(view ->
        {
            whichButtonClicked = "shop2Clicked";
       //     openGalleryCameraDialog();
            isGalleryClicked = false;
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA, FILE_PERMISSION);
        });
        tvAgreement1.setOnClickListener(view ->
        {
            whichButtonClicked = "agreement1Clicked";
            openGalleryCameraDialog();
            //   checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_MEDIA_IMAGES,Manifest.permission.CAMERA, FILE_PERMISSION);
        });
        tvAgreement2.setOnClickListener(view ->
        {
            whichButtonClicked = "agreement2Clicked";
            openGalleryCameraDialog();
            //    checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_MEDIA_IMAGES,Manifest.permission.CAMERA, FILE_PERMISSION);
        });

        btnSubmit.setOnClickListener(view ->
        {
            if (checkInternetState()) {
                if (checkInput()) {
                    doOfflineUserOnboard();
                }
            } else {
                new AlertDialog.Builder(UserKycNewActivity.this).setMessage("No Internet").show();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("SetTextI18n")
    private void openGalleryCameraDialog() {
        final View serviceSelectionView = getLayoutInflater().inflate(R.layout.service_selection_layout,
                null, false);
        final AlertDialog serviceSelectionDialog = new AlertDialog.Builder(UserKycNewActivity.this).create();
        serviceSelectionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        serviceSelectionDialog.setView(serviceSelectionView);
        serviceSelectionDialog.show();

        LinearLayout galleryLayout = serviceSelectionView.findViewById(R.id.service1);
        ImageView img1 = serviceSelectionView.findViewById(R.id.img1);
        TextView tv1 = serviceSelectionView.findViewById(R.id.tv1);
        LinearLayout cameraLayout = serviceSelectionView.findViewById(R.id.service2);
        ImageView img2 = serviceSelectionView.findViewById(R.id.img2);
        TextView tv2 = serviceSelectionView.findViewById(R.id.tv2);

        img1.setImageResource(R.drawable.gallery);
        img2.setImageResource(R.drawable.camera);
        tv1.setText("GALLERY");
        tv2.setText("CAMERA");

        galleryLayout.setOnClickListener(v ->
        {
            serviceSelectionDialog.dismiss();
            isGalleryClicked = true;
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA, FILE_PERMISSION);

        });

        cameraLayout.setOnClickListener(v ->
        {
            serviceSelectionDialog.dismiss();
            isGalleryClicked = false;
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA, FILE_PERMISSION);
        });
    }

    private void checkAadhar() {
        ProgressDialog progressDialog = new ProgressDialog(UserKycNewActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String aadharNo = etAadhar.getText().toString().trim();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().verifyAadhar(AUTH_KEY, userId, deviceId, deviceInfo, aadharNo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            progressDialog.dismiss();

                            transactionId = responseObject.getString("status");
                            etAadharOtp.setVisibility(View.VISIBLE);

                        } else {
                            String message = responseObject.getString("data");
                            progressDialog.dismiss();
                            new AlertDialog.Builder(UserKycNewActivity.this)
                                    .setMessage(message)
                                    .setCancelable(false)
                                    .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        new AlertDialog.Builder(UserKycNewActivity.this)
                                .setMessage("Please try after sometime")
                                .setCancelable(false)
                                .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                    }
                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(UserKycNewActivity.this)
                            .setMessage("Please try after sometime")
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(UserKycNewActivity.this)
                        .setMessage(t.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss()).show();
            }
        });
    }

    private void verifyAadhar() {
        ProgressDialog progressDialog = new ProgressDialog(UserKycNewActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String aadharNo = etAadhar.getText().toString().trim();
        String otp = etAadharOtp.getText().toString().trim();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().verifyAadharOTP(AUTH_KEY, userId, deviceId, deviceInfo, transactionId, aadharNo, otp);
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            progressDialog.dismiss();

                            JSONObject dataObject = responseObject.getJSONObject("data");
                            aadharName = dataObject.getString("NameOfAadhar");
                            fatherName = dataObject.getString("FatherName");
                            dateOfBirth = dataObject.getString("DateOfBirth");
                            String YearOfBirth = dataObject.getString("YearOfBirth");
                            String email = dataObject.getString("Email");
                            gender = dataObject.getString("Gender");
                            photo_link = dataObject.getString("Photo_link");
                            photo_link = photo_link.replace("\\", "");      //for \ replace
                            fullAddress = dataObject.getString("FullAddress");

                            Dialog dialog = new Dialog(UserKycNewActivity.this);
                            dialog.setContentView(R.layout.aadhar_details_dialog);
                            dialog.setCancelable(false);
                            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            dialog.getWindow().setBackgroundDrawableResource(R.drawable.bordered_back);
                            dialog.show();

                            ImageView imgUser = dialog.findViewById(R.id.imgAadhar);
                            TextView tvName = dialog.findViewById(R.id.tvName);
                            TextView tvFatherName = dialog.findViewById(R.id.tvFatherName);
                            TextView tvDob = dialog.findViewById(R.id.tvBob);
                            TextView tvGender = dialog.findViewById(R.id.tvGender);
                            TextView tvAddress = dialog.findViewById(R.id.tvAddress);

                            Button btnOk = dialog.findViewById(R.id.btnOk);

                            //   Picasso.get().load(photo_link).into(imgUser);

                            String base64Image = photo_link;
                            byte[] imageAsBytes = Base64.decode(base64Image.getBytes(), Base64.DEFAULT);
                            imgUser.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));

                            tvName.setText("Name : " + aadharName);
                            tvFatherName.setText("Father Name : " + fatherName);
                            tvDob.setText("DOB : " + dateOfBirth);
                            tvGender.setText("Gender : " + gender);
                            tvAddress.setText("Address : " + fullAddress);

                            btnOk.setOnClickListener(view ->
                            {
                                dialog.dismiss();
                                etAadharOtp.setVisibility(View.GONE);
                                etAadhar.setEnabled(false);
                                imgVerifiedAadhar.setVisibility(View.VISIBLE);
                                panLayout.setVisibility(View.VISIBLE);
                                textPan.setVisibility(View.VISIBLE);
                            });

                        } else {
                            String message = responseObject.getString("data");
                            progressDialog.dismiss();
                            new AlertDialog.Builder(UserKycNewActivity.this)
                                    .setMessage(message)
                                    .setCancelable(false)
                                    .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        new AlertDialog.Builder(UserKycNewActivity.this)
                                .setMessage("Please try after sometime")
                                .setCancelable(false)
                                .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                    }
                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(UserKycNewActivity.this)
                            .setMessage("Please try after sometime")
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(UserKycNewActivity.this)
                        .setMessage(t.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss()).show();
            }
        });
    }

    private void verifyPan() {
        ProgressDialog progressDialog = new ProgressDialog(UserKycNewActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String pan = etPan.getText().toString().trim();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().verifyPan(AUTH_KEY, userId, deviceId, deviceInfo, pan);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            progressDialog.dismiss();

                            String message = responseObject.getString("data");
                            JSONObject statusOblect = responseObject.getJSONObject("status");
                            panName = statusOblect.getString("NameOfPan");
                            String panNo = statusOblect.getString("PanNumber");
                            String panStatus = statusOblect.getString("PanStatus");
                            String panHolderStatusType = statusOblect.getString("PanHolderStatusType");
                            String FullName = statusOblect.getString("FullName");

                            if (aadharName.equalsIgnoreCase(panName)) {
                                imgVerifiedPan.setVisibility(View.VISIBLE);
                                etPan.setEnabled(false);
                                btnSubmit.setVisibility(View.VISIBLE);
                                tvUplaodAadharFront.setVisibility(View.VISIBLE);
                                tvUploadAadharBack.setVisibility(View.VISIBLE);
                                tvUploadPan.setVisibility(View.VISIBLE);
                                tvShop1.setVisibility(View.VISIBLE);
                                tvShop2.setVisibility(View.VISIBLE);
                                tvAgreement1.setVisibility(View.VISIBLE);
                                tvAgreement2.setVisibility(View.VISIBLE);
                            } else {
                                new AlertDialog.Builder(UserKycNewActivity.this).setMessage("Aadhar Name and Pan Name Mismatched\nPlease provide valid Document").show();
                            }

                        } else {
                            String message = responseObject.getString("data");
                            progressDialog.dismiss();
                            new androidx.appcompat.app.AlertDialog.Builder(UserKycNewActivity.this)
                                    .setTitle("Pan Verification")
                                    .setMessage(message)
                                    .setIcon(R.drawable.cancel)
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    })

                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        new AlertDialog.Builder(UserKycNewActivity.this)
                                .setMessage("Please try after sometime")
                                .setCancelable(false)
                                .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                    }
                } else {
                    progressDialog.dismiss();
                    new AlertDialog.Builder(UserKycNewActivity.this)
                            .setMessage("Please try after sometime")
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                new AlertDialog.Builder(UserKycNewActivity.this)
                        .setMessage(t.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss()).show();
            }
        });
    }

    private void doOfflineUserOnboard() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading....");
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = null;

        String aadharNo = etAadhar.getText().toString();
        String panNo = etPan.getText().toString();

        if (kycStatus.equalsIgnoreCase("RJC")) {
            call = RetrofitClient.getInstance().getApi().doOfflineUploadDocument(AUTH_KEY, userId, deviceId, deviceInfo, "UPLOAD_ONBOARDING_DOCUMENT",
                    aadharFrontUrl, aadharBackUrl, shop1Url, shop2Url, panUrl, agreement1Url, agreement2Url);
        } else {
            call = RetrofitClient.getInstance().getApi().doOfflineUserOnboard(AUTH_KEY, userId, deviceId, deviceInfo, "MERCHANT_ONBOARDING",
                    aadharName, panName, fatherName, dateOfBirth, photo_link, gender, panNo, aadharNo, aadharFrontUrl, aadharBackUrl, shop1Url, shop2Url, panUrl,
                    agreement1Url, agreement2Url);
        }

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {

                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        pDialog.dismiss();
                        String message = responseObject.getString("data");
                        new AlertDialog.Builder(UserKycNewActivity.this)
                                .setTitle("Message !!!")
                                .setMessage(message)
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                })
                                .show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new androidx.appcompat.app.AlertDialog.Builder(UserKycNewActivity.this)
                                .setTitle("Message !!!")
                                .setCancelable(false)
                                .setMessage("Something went wrong")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                })
                                .show();
                    }

                } else {
                    pDialog.dismiss();
                    new androidx.appcompat.app.AlertDialog.Builder(UserKycNewActivity.this)
                            .setTitle("Message !!!")
                            .setMessage("Something went wrong")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(UserKycNewActivity.this)
                        .setTitle("Message !!!")
                        .setMessage("Something went wrong")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .show();
            }
        });
    }

    //  upload image

    public void checkPermission(String writePermission, String readPermission, String mediaFilePermission, String cameraPermission, int requestCode) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(UserKycNewActivity.this, mediaFilePermission) == PackageManager.PERMISSION_DENIED
                    && ContextCompat.checkSelfPermission(UserKycNewActivity.this, cameraPermission) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(UserKycNewActivity.this, new String[]{mediaFilePermission, cameraPermission}, requestCode);
            } else {
                chooseImage();
            }
        } else {
            if (ContextCompat.checkSelfPermission(UserKycNewActivity.this, writePermission) == PackageManager.PERMISSION_DENIED
                    && ContextCompat.checkSelfPermission(UserKycNewActivity.this, readPermission) == PackageManager.PERMISSION_DENIED
                    && ContextCompat.checkSelfPermission(UserKycNewActivity.this, cameraPermission) == PackageManager.PERMISSION_DENIED) {
                // Requesting the permission
                ActivityCompat.requestPermissions(UserKycNewActivity.this, new String[]{writePermission, readPermission, cameraPermission}, requestCode);
            } else {
                chooseImage();
            }
        }

    }

    public void chooseImage() {
        if (isGalleryClicked) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(galleryIntent, GALLERY_REQUEST);
        } else {
            Intent galleryIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            startActivityForResult(galleryIntent, CAMERA_REQUEST);
        }

    }

    private void serverUpload(File myfile) {

        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(UserKycNewActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        RequestBody reqFile;
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        reqFile = RequestBody.create(MediaType.parse("image/*"), myfile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("myFile", timeStamp + myfile.getName(), reqFile);

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().uploadfile(AUTH_KEY, body);
        call.enqueue(new Callback<JsonObject>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        String code = jsonObject.getString("statuscode");

                        if (code.equalsIgnoreCase("TXN")) {
                            if (whichButtonClicked.equalsIgnoreCase("aadharFrontClicked")) {
                                tvUplaodAadharFront.setText("Aadhar Front Image Uploaded");
                                aadharFrontUrl = jsonObject.getString("data");
                            } else if (whichButtonClicked.equalsIgnoreCase("aadharBackClicked")) {
                                tvUploadAadharBack.setText("Aadhar Back Image Uploaded");
                                aadharBackUrl = jsonObject.getString("data");
                            } else if (whichButtonClicked.equalsIgnoreCase("shop1Clicked")) {
                                tvShop1.setText("Shop Image1 Uploaded");
                                shop1Url = jsonObject.getString("data");
                            } else if (whichButtonClicked.equalsIgnoreCase("shop2Clicked")) {
                                tvShop2.setText("Shop Image2 Uploaded");
                                shop2Url = jsonObject.getString("data");
                            } else if (whichButtonClicked.equalsIgnoreCase("agreement1Clicked")) {
                                tvAgreement1.setText("Agreement Image1 Uploaded");
                                agreement1Url = jsonObject.getString("data");
                            } else if (whichButtonClicked.equalsIgnoreCase("agreement2Clicked")) {
                                tvAgreement2.setText("Agreement Image2 Uploaded");
                                agreement2Url = jsonObject.getString("data");
                            } else {
                                tvUploadPan.setText("Pan Image Uploaded");
                                panUrl = jsonObject.getString("data");
                            }

                            pDialog.dismiss();

                        } else if (code.equalsIgnoreCase("ERR")) {

                            new AlertDialog.Builder(UserKycNewActivity.this).setMessage("Try Again").show();
                            pDialog.dismiss();

                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(UserKycNewActivity.this).setMessage("Try Again").show();
                        }

                    } catch (JSONException e) {
                        pDialog.dismiss();
                        e.printStackTrace();
                        new AlertDialog.Builder(UserKycNewActivity.this).setMessage("Try Again").show();
                    }

                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(UserKycNewActivity.this).setMessage("Try Again").show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(UserKycNewActivity.this).setMessage("Try Again").show();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {

                Bitmap selectedImage = (Bitmap) data.getExtras().get("data");

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
                //  File file = new File(Environment.getExternalStorageDirectory() + File.separator + "testimage.jpg");   // not work in android13
                File file = new File(getApplicationContext().getCacheDir(), "testimage.jpg");
                file.createNewFile();
                FileOutputStream fo = new FileOutputStream(file);
                fo.write(bytes.toByteArray());
                fo.close();

                serverUpload(file);

            }

            if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null) {

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String mediaPath = cursor.getString(columnIndex);
                File file = new File(mediaPath);

                serverUpload(file);
                // Set the Image in ImageView for Previewing the Media
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /////////////////////

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (manager != null) {
            networkInfo = manager.getActiveNetworkInfo();
        }

        if (networkInfo != null) {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    private boolean checkInput() {
        if (!aadharFrontUrl.equalsIgnoreCase("select")) {
            if (!aadharBackUrl.equalsIgnoreCase("select")) {
                if (!panUrl.equalsIgnoreCase("select")) {
                    if (!shop1Url.equalsIgnoreCase("select")) {
                        if (!shop2Url.equalsIgnoreCase("select")) {
                            if (!agreement1Url.equalsIgnoreCase("select")) {
                                if (!agreement2Url.equalsIgnoreCase("select")) {
                                    return true;
                                } else {
                                    tvAgreement2.setError("Upload Agreement2 Image");
                                    return false;
                                }
                            } else {
                                tvAgreement1.setError("Upload Agreement1 Image");
                                return false;
                            }
                        } else {
                            tvShop2.setError("Upload Shop2 Image");
                            return false;
                        }
                    } else {
                        tvShop1.setError("Upload Shop1 Image");
                        return false;
                    }
                } else {
                    tvUploadPan.setError("Upload Pan Image");
                    return false;
                }
            } else {
                tvUploadAadharBack.setError("Upload Aadhar Back Image");
                return false;
            }
        } else {
            tvUplaodAadharFront.setError("Upload Aadhar Front Image");
            return false;
        }
    }

    private void initViews() {
        imgBack = findViewById(R.id.imgBack);
        etAadhar = findViewById(R.id.et_aadharNo);
        etAadharOtp = findViewById(R.id.et_otp);
        imgVerifiedAadhar = findViewById(R.id.imgAadharVerified);
        textAadhar = findViewById(R.id.textAadhar);
        textPan = findViewById(R.id.textPan);
        etPan = findViewById(R.id.et_panNo);
        imgVerifiedPan = findViewById(R.id.imgPanVerified);
        aadharLayout = findViewById(R.id.aadharLayout);
        panLayout = findViewById(R.id.panLayout);
        tvUplaodAadharFront = findViewById(R.id.tv_upload_adharFront);
        tvUploadAadharBack = findViewById(R.id.tv_upload_adharBack);
        tvUploadPan = findViewById(R.id.tv_upload_pan);
        tvShop1 = findViewById(R.id.tv_shop1);
        tvShop2 = findViewById(R.id.tv_shop2);
        tvAgreement1 = findViewById(R.id.tv_agreement1);
        tvAgreement2 = findViewById(R.id.tv_agreement2);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

}