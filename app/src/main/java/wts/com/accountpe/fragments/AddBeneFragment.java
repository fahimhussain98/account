package wts.com.accountpe.fragments;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.activities.NewMoneyTransferActivity;
import wts.com.accountpe.activities.SenderValidationActivity;
import wts.com.accountpe.retrofit.RetrofitClient;

public class AddBeneFragment extends Fragment {

    ArrayList<String> bankNameArrayList, bankCodeArrayList, bankIdArrayList, ifscArrayList;
    EditText etRecipientName, etRecipientNumber, etAccountNumber, tvIfsc;
    AutoCompleteTextView etBankName;
    Button btnSubmit, btnVerify;
    String mobileNumber, selectedBankCode = "select", selectedIfsc, selectedBankName;
    String userId;
    SharedPreferences sharedPreferences;
    String deviceId, deviceInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_add_bene, container, false);
        initViews(view);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);


        mobileNumber = NewMoneyTransferActivity.senderMobileNumber;

        if (checkInternetState()) {
            getBanks();
        } else {
            showSnackBar(view);
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etRecipientName.getText()) && !TextUtils.isEmpty(etRecipientName.getText()) && !TextUtils.isEmpty(etRecipientNumber.getText())
                        && !selectedBankCode.equalsIgnoreCase("select"))
                    addBeneficiary();
                else {
                    new AlertDialog.Builder(requireContext())
                            .setMessage("Select Above Details.")
                            .show();
                }

            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetState()) {
                    verifyAccount();
                } else {
                    showSnackBar(view);
                }
            }
        });

        return view;

    }

    private void verifyAccount() {
        ProgressDialog pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();


        String recipientName = etRecipientName.getText().toString().trim();
        String recipientNumber = etRecipientNumber.getText().toString().trim();
        String accountNumber = etAccountNumber.getText().toString().trim();
        selectedIfsc=tvIfsc.getText().toString().trim();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().verifyAccount(AUTH_KEY, userId, deviceId, deviceInfo, SenderValidationActivity.senderId,
                selectedBankName, recipientName, "", mobileNumber, selectedIfsc, accountNumber, SenderValidationActivity.senderMobileNumber,
                "NA", "NA", "NA", SenderValidationActivity.sendername, "ifs");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObject = new JSONObject(String.valueOf(response.body()));
                        String responseCode = responseObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {

                            String beniName = responseObject.getString("data");
                            etRecipientName.setText(beniName);

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


                    } catch (Exception e) {
                        pDialog.dismiss();
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Alert!!!")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Alert!!!")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(requireContext())
                        .setTitle("Alert!!!")
                        .setMessage("Something went wrong.")
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });

    }

    private void initViews(View view) {
        etBankName = view.findViewById(R.id.et_bank_name);
        etRecipientName = view.findViewById(R.id.et_recipient_name);
        etRecipientNumber = view.findViewById(R.id.et_recipient_number);
        etAccountNumber = view.findViewById(R.id.et_account_number);
        tvIfsc = view.findViewById(R.id.tv_ifsc);
        btnSubmit = view.findViewById(R.id.btn_submit);
        btnVerify = view.findViewById(R.id.btn_verify);

    }

    private void addBeneficiary() {

        ProgressDialog pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();

        String recipientName = etRecipientName.getText().toString().trim();
        String recipientNumber = etRecipientNumber.getText().toString().trim();
        String accountNumber = etAccountNumber.getText().toString().trim();
        String ifsc = tvIfsc.getText().toString().trim();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().addBeneficiary(AUTH_KEY, userId, deviceId, deviceInfo, SenderValidationActivity.senderId,
                selectedBankName, recipientName, "", recipientNumber, ifsc, accountNumber, mobileNumber, "NA", "NA", "NA");

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;

                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = jsonObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {


                            String message = jsonObject.getString("data");

                            pDialog.dismiss();
                            new AlertDialog.Builder(requireContext())
                                    .setTitle("Status")
                                    .setCancelable(false)
                                    .setMessage(message)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            //getActivity().finish();
                                            startActivity(requireActivity().getIntent());

                                        }
                                    })
                                    .show();
                        } else if (responseCode.equalsIgnoreCase("ERR")) {
                            String responseMessage = jsonObject.getString("data");
                            pDialog.dismiss();
                            new AlertDialog.Builder(requireContext())
                                    .setTitle("Alert!!!")
                                    .setMessage(responseMessage)
                                    .setPositiveButton("Ok", null)
                                    .show();
                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(requireContext())
                                    .setTitle("Alert!!!")
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Alert!!!")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Alert!!!")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(requireContext())
                        .setTitle("Alert!!!")
                        .setMessage("Something went wrong.")
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });

    }

    private void getBanks() {
        ProgressDialog pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().getBankDmt(AUTH_KEY, deviceId, deviceInfo, userId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;

                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));


                        String responseCode = jsonObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            JSONArray transactionArray = jsonObject.getJSONArray("data");

                            bankNameArrayList = new ArrayList<>();
                            bankCodeArrayList = new ArrayList<>();
                            bankIdArrayList = new ArrayList<>();
                            ifscArrayList = new ArrayList<>();

                            for (int i = 0; i < transactionArray.length(); i++) {
                                JSONObject transactionObject = transactionArray.getJSONObject(i);

                                String responsebankName = transactionObject.getString("BankName");
                                String responseBankCode = transactionObject.getString("BankCode");
                                String responseBankId = transactionObject.getString("BankID");
                                String responseIfsc = transactionObject.getString("IFSC");

                                bankNameArrayList.add(responsebankName);
                                bankCodeArrayList.add(responseBankCode);
                                bankIdArrayList.add(responseBankId);
                                ifscArrayList.add(responseIfsc);

                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, bankNameArrayList);
                            etBankName.setAdapter(adapter);
                            etBankName.setThreshold(1);

                            etBankName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    int index = bankNameArrayList.indexOf(etBankName.getText().toString());
                                    selectedBankCode = bankCodeArrayList.get(index);
                                    selectedIfsc = ifscArrayList.get(index);
                                    selectedBankName = bankNameArrayList.get(index);
                                    tvIfsc.setText(selectedIfsc);

                                }
                            });


                            pDialog.dismiss();
                        } else if (responseCode.equalsIgnoreCase("ERR")) {
                            pDialog.dismiss();
                            new AlertDialog.Builder(requireContext())
                                    .setTitle("Alert!!!")
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("Ok", null)
                                    .show();
                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(requireContext())
                                    .setTitle("Alert!!!")
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Alert!!!")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Alert!!!")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null)
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(requireContext())
                        .setTitle("Alert!!!")
                        .setMessage("Something went wrong.")
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }

    private boolean checkInternetState() {

        ConnectivityManager manager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }

    private void showSnackBar(View view) {
        Snackbar snackbar = Snackbar.make(view.findViewById(R.id.add_bene_fragment), "No Internet", Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}