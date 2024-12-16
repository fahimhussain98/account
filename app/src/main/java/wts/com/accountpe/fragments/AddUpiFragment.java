package wts.com.accountpe.fragments;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.activities.NewMoneyTransferActivity;
import wts.com.accountpe.activities.ScannerViewActivity;
import wts.com.accountpe.retrofit.RetrofitClient;


public class AddUpiFragment extends Fragment {

    EditText etName, etMobileNumber, etUpiId;
    Button btnVerify, btnSubmit,btnScan;
    String userId;
    SharedPreferences sharedPreferences;
    String deviceId, deviceInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_upi, container, false);
        initViews(view);



        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        btnVerify.setOnClickListener(v ->
        {
            if (!TextUtils.isEmpty(etMobileNumber.getText()) && !TextUtils.isEmpty(etUpiId.getText())) {
                verifyUpiId();
            } else {
                Toast.makeText(getContext(), "All fields are mandatory", Toast.LENGTH_SHORT).show();
            }
        });

        btnSubmit.setOnClickListener(v ->
        {
            if (!TextUtils.isEmpty(etName.getText()) && !TextUtils.isEmpty(etMobileNumber.getText()) && !TextUtils.isEmpty(etUpiId.getText())) {
                addUpiId();
            } else {
                Toast.makeText(getContext(), "All fields are mandatory", Toast.LENGTH_SHORT).show();
            }
        });

        btnScan.setOnClickListener(v ->
        {
            startActivityForResult(new Intent(getActivity(), ScannerViewActivity.class), 1);

        });


        return view;
    }

    private void addUpiId() {
        ProgressDialog pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        pDialog.setMessage("Please wait...");
        pDialog.show();

        String name = etName.getText().toString().trim();
        String mobileNo = etMobileNumber.getText().toString().trim();
        String upiId = etUpiId.getText().toString().trim();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().addDmtUpi(AUTH_KEY, userId, deviceId, deviceInfo,
                NewMoneyTransferActivity.remitterId, name, mobileNo, "NA", upiId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject responseObject = null;
                    try {
                        responseObject = new JSONObject(String.valueOf(response.body()));
                        String message = responseObject.getString("data");
                        pDialog.dismiss();
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Message")
                                .setMessage(message)
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        requireActivity().finish();
                                    }
                                }).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Message")
                                .setMessage("Please try after sometime.")
                                .setPositiveButton("Ok", null)
                                .show();
                    }

                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Message")
                            .setMessage("Please try after sometime.")
                            .setPositiveButton("Ok", null)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(requireContext())
                        .setTitle("Message")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }

    private void verifyUpiId() {
        ProgressDialog pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        pDialog.setMessage("Please wait...");
        pDialog.show();

        String mobileNo = etMobileNumber.getText().toString().trim();
        String upiId = etUpiId.getText().toString().trim();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().verifyUpiId(AUTH_KEY, userId, deviceId, deviceInfo,
                NewMoneyTransferActivity.remitterId, "NA", mobileNo, upiId, NewMoneyTransferActivity.senderMobileNumber,
                NewMoneyTransferActivity.senderName);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            String name = responseObject.getString("data");
                            etName.setText(name);
                            pDialog.dismiss();

                        } else {
                            String message = responseObject.getString("data");
                            pDialog.dismiss();
                            new AlertDialog.Builder(requireContext())
                                    .setTitle("Message")
                                    .setMessage(message)
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Message")
                                .setMessage("Please try after sometime.")
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Message")
                            .setMessage("Please try after sometime.")
                            .setPositiveButton("Ok", null)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(requireContext())
                        .setTitle("Message")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        NewMoneyTransferActivity.shouldRefreshActivity = false;

        if (requestCode == 1) {
            String upiId = null;
            if (data != null) {
                upiId = data.getStringExtra("upiId");
                etUpiId.setText(upiId);
            }

        } else {
            Toast.makeText(getActivity(), "Try after some time", Toast.LENGTH_SHORT).show();
        }

    }

    private void initViews(View view) {
        etName = view.findViewById(R.id.et_recipient_name);
        etMobileNumber = view.findViewById(R.id.et_recipient_number);
        etUpiId = view.findViewById(R.id.et_upi_id);
        btnVerify = view.findViewById(R.id.btn_verify);
        btnSubmit = view.findViewById(R.id.btn_submit);
        btnScan = view.findViewById(R.id.btn_scan);
    }
}