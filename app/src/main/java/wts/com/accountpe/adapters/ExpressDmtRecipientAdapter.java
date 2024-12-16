package wts.com.accountpe.adapters;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.activities.ExpressDmtGetSenderActivity;
import wts.com.accountpe.activities.SenderValidationActivity;
import wts.com.accountpe.activities.ShareExpressDmtActivity;
import wts.com.accountpe.fragments.ExpressDmtAddBeneFragment;
import wts.com.accountpe.models.RecipientModel;
import wts.com.accountpe.retrofit.RetrofitClient;

public class ExpressDmtRecipientAdapter extends RecyclerView.Adapter<ExpressDmtRecipientAdapter.Viewholder> {
    Context context;
    ArrayList<RecipientModel> recipientModelArrayList;
    String mobileNumber, userId, userName;
    Activity activity;

    String deviceId, deviceInfo;
    String selectedTransactionMode = "IMPS";

    public ExpressDmtRecipientAdapter(Context context, Activity activity, ArrayList<RecipientModel> recipientModelArrayList,
                            String mobileNumber, String userId, String userName, String deviceId, String deviceInfo) {
        this.context = context;
        this.activity = activity;
        this.recipientModelArrayList = recipientModelArrayList;
        this.mobileNumber = mobileNumber;
        this.userId = userId;
        this.userName = userName;
        this.deviceId = deviceId;
        this.deviceInfo = deviceInfo;
    }


    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_recipient_list, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        final String recipientName = recipientModelArrayList.get(position).getRecipientName();
        final String bankName = recipientModelArrayList.get(position).getBankName();
        final String recipientId = recipientModelArrayList.get(position).getRecipientId();
        final String bankAccountNumber = recipientModelArrayList.get(position).getBankAccountNumber();
        final String ifsc = recipientModelArrayList.get(position).getIfsc();
        final String benificaryMobileNumber = recipientModelArrayList.get(position).getMobileNumber();

        holder.tvRecipientName.setText(recipientName);
        holder.tvAccountNumber.setText(bankAccountNumber);
        holder.tvBankName.setText(bankName);
        holder.tvIfsc.setText(ifsc);


        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context).setTitle("Confirmation")
                        .setMessage("Do you want to delete this beneficiary ?\n\n"+recipientName+"\n"+bankName+"\n"+bankAccountNumber+"\n"+ifsc)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteBene(recipientId);
                            }
                        }).setNegativeButton("Cancel", null)
                        .show();
            }
        });

        holder.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final View addSenderOTPDialogView = activity.getLayoutInflater().inflate(R.layout.pay_benificary_layout,
                        null, false);
                final AlertDialog addSenderOTPDialog = new AlertDialog.Builder(context).create();
                addSenderOTPDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                addSenderOTPDialog.setCancelable(false);
                addSenderOTPDialog.setView(addSenderOTPDialogView);
                addSenderOTPDialog.show();

                final EditText etAmount = addSenderOTPDialog.findViewById(R.id.et_amount);
             //   final EditText etTpin = addSenderOTPDialog.findViewById(R.id.et_tpin);
                Button btnProceed = addSenderOTPDialog.findViewById(R.id.btn_proceed);
                Button btnCancel = addSenderOTPDialog.findViewById(R.id.btn_cancel);
                TextView tvBeniName = addSenderOTPDialog.findViewById(R.id.tv_beni_name);
                TextView tvBankName = addSenderOTPDialog.findViewById(R.id.tv_bank_name);
                TextView tvAccountNumber = addSenderOTPDialog.findViewById(R.id.tv_account_number);
                TextView tvIfsc = addSenderOTPDialog.findViewById(R.id.tv_ifsc);
                RadioButton rdImps = addSenderOTPDialog.findViewById(R.id.rd_imps);
                RadioButton rdNeft = addSenderOTPDialog.findViewById(R.id.rd_neft);
                EditText etTpin  = addSenderOTPDialog.findViewById(R.id.et_tpin);

                tvBeniName.setText(recipientName);
                tvBankName.setText(bankName);
                tvAccountNumber.setText(bankAccountNumber);
                tvIfsc.setText(ifsc);

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addSenderOTPDialog.dismiss();
                    }
                });

                btnProceed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(etAmount.getText())) {
                            if (!TextUtils.isEmpty(etTpin.getText())) {

                                addSenderOTPDialog.dismiss();
                                String amount = etAmount.getText().toString().trim();
                                String tpin = etTpin.getText().toString().trim();

                                if (rdImps.isChecked()) {
                                    selectedTransactionMode = "IMPS";

                                } else  {
                                    selectedTransactionMode = "neft";

                                }

                                payBenificiaryExpress(recipientId, amount, bankAccountNumber, bankName, ifsc, recipientName, ExpressDmtGetSenderActivity.senderMobileNumber);
                            } else {
                                etTpin.setError("Required");
                            }


                        } else {
                            etAmount.setError("Enter Amount");
                        }
                    }
                });

            }
        });

    }

    private void deleteBene(String recipientId) {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(context).create();
        LayoutInflater inflater = activity.getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();
        Call<JsonObject> call = RetrofitClient.getInstance().getApi().deleteBeneficiaryExpress(AUTH_KEY, deviceId, deviceInfo, userId, recipientId);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;

                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));

                        String responseCode = jsonObject.getString("statuscode");

                        if (responseCode.equalsIgnoreCase("TXN")) {
                            pDialog.dismiss();

                            String transaction = jsonObject.getString("data");

                            new AlertDialog.Builder(context)
                                    .setTitle("Status")
                                    .setMessage(transaction)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            activity.finish();
                                        }
                                    })
                                    .show();
                        } else {
                            pDialog.dismiss();
                            new AlertDialog.Builder(context)
                                    .setTitle("Alert!!!")
                                    .setMessage("Something went wrong.")
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(context)
                                .setTitle("Alert!!!")
                                .setMessage("Something went wrong.")
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(context)
                            .setTitle("Alert!!!")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(context)
                        .setTitle("Alert!!!")
                        .setMessage("Something went wrong.")
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }

    private void payBenificiaryExpress(String recipientId, String amount, String bankAccountNumber, String bankName, String ifsc, String benificaryName, String benificaryMobileNumber) {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(context).create();
        LayoutInflater inflater = activity.getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call = RetrofitClient.getInstance().getApi().payBeneficiaryExpress(AUTH_KEY, userId, deviceId, deviceInfo, amount, recipientId,
                ExpressDmtGetSenderActivity.sendername, ExpressDmtGetSenderActivity.senderMobileNumber, selectedTransactionMode, benificaryMobileNumber,
                ExpressDmtGetSenderActivity.lat,ExpressDmtGetSenderActivity.longi);

        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;

                    try {
                        jsonObject = new JSONObject(String.valueOf(response.body()));

                        String respoonseCode = jsonObject.getString("statuscode");

                        if (respoonseCode.equalsIgnoreCase("TXN")) {

                            JSONArray dataArray = jsonObject.getJSONArray("data");
                            JSONObject dataObject = dataArray.getJSONObject(0);

                            String accountNumber = dataObject.getString("AccountNo");
                            String beniName = dataObject.getString("BeneficiaryName");
                            String responseBankName = dataObject.getString("BankName");
                            String responseIfsc = dataObject.getString("IfscCode");
                            String date = dataObject.getString("CreatedOn");
                            String transactionId = dataObject.getString("TransactionId");
                            String responseAmount = dataObject.getString("Amount");
                            String status = dataObject.getString("Status");
                            String bankRefId = dataObject.getString("BankRrnNo");
                            String comm = dataObject.getString("Commission");
                            String surcharge = dataObject.getString("Surcharge");

                            Intent intent = new Intent(context, ShareExpressDmtActivity.class);
                            intent.putExtra("amount", amount);
                            intent.putExtra("accountNumber", accountNumber);
                            intent.putExtra("beniName", beniName);
                            intent.putExtra("bank", responseBankName);
                            intent.putExtra("ifsc", responseIfsc);
                            intent.putExtra("date", date);
                            intent.putExtra("transactionId", transactionId);
                            intent.putExtra("responseAmount", responseAmount);
                            intent.putExtra("status", status);
                            intent.putExtra("bankRefId", bankRefId);
                            intent.putExtra("comm", comm);
                            intent.putExtra("surcharge", surcharge);
                            intent.putExtra("isMoneyReport", false);

                            context.startActivity(intent);

                            pDialog.dismiss();

                        } else {
                            pDialog.dismiss();
                            String message = jsonObject.getString("data");
                            new AlertDialog.Builder(context).setTitle("Message")
                                    .setMessage(message)
                                    .setPositiveButton("Ok", null)
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(context).setTitle("Message")
                                .setMessage(e.getMessage())
                                .setPositiveButton("Ok", null)
                                .show();
                    }
                } else {
                    pDialog.dismiss();
                    new AlertDialog.Builder(context).setTitle("Message")
                            .setMessage("Something went wrong.")
                            .setPositiveButton("Ok", null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(context).setTitle("Message")
                        .setMessage(t.getMessage())
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipientModelArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    public static class Viewholder extends RecyclerView.ViewHolder {
        TextView tvRecipientName, tvAccountNumber, tvBankName, tvIfsc;
        Button btnPay, btnDelete,btnPayUpi;

        ImageView imgBank, imgIfsc;
        EditText etTpin;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            tvRecipientName = itemView.findViewById(R.id.tv_recipient_name);
            tvAccountNumber = itemView.findViewById(R.id.tv_account_number);
            tvBankName = itemView.findViewById(R.id.tv_bank_name);
            btnPay = itemView.findViewById(R.id.btn_pay);
            btnPayUpi = itemView.findViewById(R.id.btn_upiPay);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            tvIfsc = itemView.findViewById(R.id.tv_ifsc);
            imgBank = itemView.findViewById(R.id.img_bank);
            imgIfsc = itemView.findViewById(R.id.img_ifsc);

            btnPayUpi.setVisibility(View.GONE);

        }
    }
}
