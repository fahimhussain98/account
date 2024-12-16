package wts.com.accountpe.fragments;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.activities.EditProfileActivity;
import wts.com.accountpe.activities.LoginNewActivity;
import wts.com.accountpe.activities.MainAepsWalletToWalletActivity;
import wts.com.accountpe.retrofit.RetrofitClient;

public class ProfileFragment extends Fragment {

    ImageView imgProfile, imgEditMobileNo;
    String userId, deviceId, deviceInfo, name,mobileNumber,userName,userType, panCard,aadharCard,emailId,companyName,dateOfBirth,address,imageUrl;
    SharedPreferences sharedPreferences;
    String aadharNumber,email,mobile;
    TextView tvName,tvMobileNumber,tvUserName,tvUserType,tvPanCard,tvAadharCard,tvEmailId,tvCompanyName,tvDateOfBirth,tvAddress;
    AppCompatButton btnLogout, btnRateUS, btnUpdateProfile, btnWalletToWallet;

    String balance = "00.00",aepsBalance = "00.00";

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view= inflater.inflate(R.layout.fragment_profile, container, false);

        imgProfile = view.findViewById(R.id.profile_image);
        imgEditMobileNo = view.findViewById(R.id.imgEdit);
        tvName=view.findViewById(R.id.tv_username);
        tvMobileNumber=view.findViewById(R.id.tv_mobile);
        tvUserName=view.findViewById(R.id.tv_name);
        tvUserType=view.findViewById(R.id.tv_usertype);
        tvPanCard=view.findViewById(R.id.tv_pancard);
        tvAadharCard=view.findViewById(R.id.tv_aadharcard);
        tvEmailId=view.findViewById(R.id.tv_email);
        tvCompanyName=view.findViewById(R.id.tv_company_name);
        tvDateOfBirth=view.findViewById(R.id.tv_dob);
        tvAddress=view.findViewById(R.id.tv_address);
        btnLogout=view.findViewById(R.id.btn_logout);
        btnRateUS=view.findViewById(R.id.btn_rateUs);
        btnUpdateProfile = view.findViewById(R.id.btn_update_profile);
        btnWalletToWallet = view.findViewById(R.id.btn_walletToWallet);

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getContext());
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        name=sharedPreferences.getString("username",null);
        mobileNumber=sharedPreferences.getString("mobileno",null);
        userName=sharedPreferences.getString("username",null);
        userType=sharedPreferences.getString("usertype",null);
        panCard =sharedPreferences.getString("pancard",null);
        aadharCard=sharedPreferences.getString("adharcard",null);
        emailId=sharedPreferences.getString("email",null);
        companyName=sharedPreferences.getString("firmName",null);
        dateOfBirth=sharedPreferences.getString("dob",null);
        address=sharedPreferences.getString("address",null);
        imageUrl=sharedPreferences.getString("imgUrl",null);
        aadharNumber=sharedPreferences.getString("aadharNumber",null);//***mber
        email=sharedPreferences.getString("email",null);//***mail
        mobile=sharedPreferences.getString("mobileNumber",null);//***ber

//       if (!imageUrl.equalsIgnoreCase(""))
        if(imageUrl != null && !imageUrl.equalsIgnoreCase(""))
       {
           String base64Image = imageUrl;
           byte[] imageAsBytes = Base64.decode(base64Image.getBytes(), Base64.DEFAULT);
           imgProfile.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
       }
       else
       {
           imgProfile.setImageResource(R.drawable.profile_img);
       }
        tvName.setText(name);
       // tvMobileNumber.setText(mobileNumber);
        tvMobileNumber.setText(mobile);
        tvUserName.setText(userName);
        tvUserType.setText(userType);
        tvPanCard.setText(panCard);
        tvAadharCard.setText(aadharNumber);
       // tvAadharCard.setText(aadharCard);
       // tvEmailId.setText(emailId);
        tvEmailId.setText(email);
        tvCompanyName.setText(companyName);
        tvDateOfBirth.setText(dateOfBirth);
        tvAddress.setText(address);

        btnLogout.setOnClickListener(v-> {
            final View view1 = LayoutInflater.from(getContext()).inflate(R.layout.logout_dialog_layout, null, false);
            final AlertDialog logoutDialog = new AlertDialog.Builder(requireContext()).create();
            logoutDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            logoutDialog.setCancelable(false);
            logoutDialog.setView(view1);
            logoutDialog.show();

            final Button btnCancel = logoutDialog.findViewById(R.id.btn_cancel);
            Button btnYes = logoutDialog.findViewById(R.id.btn_yes);

            assert btnCancel != null;
            btnCancel.setOnClickListener(v1 -> logoutDialog.dismiss());

            assert btnYes != null;
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                    editor.clear();
                    editor.apply();
                    //    startActivity(new Intent(getContext(), LoginActivity.class));
                    startActivity(new Intent(getContext(), LoginNewActivity.class));
                    requireActivity().finish();
                }
            });
        });

        btnUpdateProfile.setOnClickListener(v ->
        {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            intent.putExtra("changeMobileNo", false);
            startActivity(intent);
        });

        btnWalletToWallet.setOnClickListener(view1 ->
        {
         //   startActivity(new Intent(requireActivity(), WalletToWalletActivity.class));  //  for single wallet

            Intent in = new Intent(requireActivity(),MainAepsWalletToWalletActivity.class);     //  for double wallet
            in.putExtra("mainBalance", balance);
            in.putExtra("aepsBalance",aepsBalance );
            startActivity(in);

        });

        imgEditMobileNo.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            intent.putExtra("changeMobileNo", false);
            startActivity(intent);
        });

        btnRateUS.setOnClickListener(view1 ->
        {
            showRatingBarDialog("");
        });

        return view;
    }

    private void showRatingBarDialog(String whichButtonClicked) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.rate_us_dialog);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.home_dash_back_white);
        dialog.getWindow().setLayout((int)(getResources().getDisplayMetrics().widthPixels*0.95), ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        Button btnSubmit = dialog.findViewById(R.id.btnSubmit);
        Button btnNotNow = dialog.findViewById(R.id.btnCancel);

        btnNotNow.setOnClickListener(view ->
        {
            dialog.dismiss();
        });

        btnSubmit.setOnClickListener(view -> {
            Intent in = null;
            try {
                in = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+requireActivity().getPackageName()));
            }
            catch (Exception e)
            {
                in = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+getActivity().getPackageName()));
            }
            startActivity(in);
        });
    }

    private void getBalance() {

        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(requireContext()).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getBalance(AUTH_KEY,userId,deviceId,deviceInfo,"Login","0");

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {

                    JSONObject jsonObject1 = null;
                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));

                        String statusCode = jsonObject1.getString("statuscode");

                        if (statusCode.equalsIgnoreCase("TXN")) {

                            JSONObject jsonObject = jsonObject1.getJSONObject("data");
                            balance = jsonObject.getString("userBalance");

                            pDialog.dismiss();
                        } else if (statusCode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();

                        } else {
                            pDialog.dismiss();

                        }

                    } catch (JSONException e) {
                        pDialog.dismiss();

                        e.printStackTrace();
                    }

                } else {
                    pDialog.dismiss();

                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();

            }
        });
    }

    private void getAePSBalance() {

        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(requireContext()).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getAepsBalance(AUTH_KEY,userId,deviceId,deviceInfo,"Login","");

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;
                    try {
                        jsonObject1 = new JSONObject(String.valueOf(response.body()));

                        String statusCode = jsonObject1.getString("statuscode");

                        if (statusCode.equalsIgnoreCase("TXN")) {
                            JSONObject jsonObject = jsonObject1.getJSONObject("data");
                            aepsBalance = jsonObject.getString("userBalance");

                            pDialog.dismiss();
                        } else if (statusCode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();

                        } else {
                            pDialog.dismiss();

                        }

                    } catch (JSONException e) {
                        pDialog.dismiss();

                        e.printStackTrace();
                    }

                } else {
                    pDialog.dismiss();
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getBalance();
        getAePSBalance();
    }
}