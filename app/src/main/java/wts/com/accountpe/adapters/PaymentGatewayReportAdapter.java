package wts.com.accountpe.adapters;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.models.AddMoneyReportModel;
import wts.com.accountpe.retrofit.RetrofitClient;

public class PaymentGatewayReportAdapter extends RecyclerView.Adapter<PaymentGatewayReportAdapter.Viewholder> {

    ArrayList<AddMoneyReportModel> addMoneyList;
    Context context;

    String userId,deviceId,deviceInfo;
    SharedPreferences sharedPreferences;

    public PaymentGatewayReportAdapter(ArrayList<AddMoneyReportModel> addMoneyList, Context context) {
        this.addMoneyList = addMoneyList;
        this.context = context;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.addmoney_report_item,parent,false);

        return new Viewholder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

        String strTransactionID = addMoneyList.get(position).getTxnID();
        String strUnique = addMoneyList.get(position).getUniqueTxnId();
        String strName = addMoneyList.get(position).getName();
        String strOpeningBal = addMoneyList.get(position).getOpeningBal();
        String strAmount = addMoneyList.get(position).getAmount();
        String strCommission = addMoneyList.get(position).getCommission();
        String strSurcharge = addMoneyList.get(position).getSurcharge();
        String strPayableAmount = addMoneyList.get(position).getPayAbleAmount();
        String strClosingBal = addMoneyList.get(position).getClosingBal();
        String strCreatedOn = addMoneyList.get(position).getCreatedOn();
        String strStatus = addMoneyList.get(position).getStatus();

        if (strStatus.equalsIgnoreCase("Success") || strStatus.equalsIgnoreCase("TXN"))
        {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.green));
            holder.imgStatus.setImageResource(R.drawable.success);
        }
        else if (strStatus.equalsIgnoreCase("Failure") || strStatus.equalsIgnoreCase("Failed") || strStatus.equalsIgnoreCase("ERR"))
        {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.red));
            holder.imgStatus.setImageResource(R.drawable.failed);
        }

        else if (strStatus.equalsIgnoreCase("PENDING"))
        {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.yellow));
            holder.imgStatus.setImageResource(R.drawable.pending);
        }

        @SuppressLint("SimpleDateFormat") DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
        String[] splitDate = strCreatedOn.split("T");
        try {
            Date date = inputDateFormat.parse(splitDate[0]);
            Date time = simpleDateFormat.parse(splitDate[1]);
            @SuppressLint("SimpleDateFormat") String outputDate = new SimpleDateFormat("dd MMM yyyy").format(date);
            @SuppressLint("SimpleDateFormat") String outputTime = new SimpleDateFormat("hh:mm a").format(time);

            holder.tvDate.setText(outputDate);
            holder.tvTime.setText(outputTime);

        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.tvTransactionID.setText(strTransactionID);
        holder.tvName.setText(strName);
        holder.tvOpeningBal.setText(strOpeningBal);
        holder.tvAmount.setText(strAmount);
        holder.tvCommission.setText(strCommission);
        holder.tvSurcharge.setText(strSurcharge);
        holder.tvPayableAmount.setText(strPayableAmount);
        holder.tvClosingBal.setText(strClosingBal);
        holder.tvStatus.setText(strStatus);

        holder.closingBalanceLayout.setVisibility(View.GONE);

        holder.btnComplain.setOnClickListener(v->
        {
            final android.app.AlertDialog complaintDialog = new android.app.AlertDialog.Builder(context).create();
            LayoutInflater inflater = LayoutInflater.from(context);
            View convertView = inflater.inflate(R.layout.complaint_dialog, null);
            complaintDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            complaintDialog.setView(convertView);
            complaintDialog.setCancelable(false);
            complaintDialog.show();

            ImageView imgClose=convertView.findViewById(R.id.img_close);
            EditText etRemarks=convertView.findViewById(R.id.et_remarks);
            AppCompatButton btnMakeComplaint=convertView.findViewById(R.id.btn_make_complaint);

            imgClose.setOnClickListener(v1->
            {
                complaintDialog.dismiss();
            });

            btnMakeComplaint.setOnClickListener(v1->
            {
                if (!TextUtils.isEmpty(etRemarks.getText()))
                {
                    String remarks=etRemarks.getText().toString().trim();
                    raiseComplaint(remarks, strUnique, strCreatedOn);
                    complaintDialog.dismiss();
                }
                else
                    etRemarks.setError("Required");
            });
        });

    }

    private void raiseComplaint(String remarks, String uniqueId, String txnDate) {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(context).create();
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call= RetrofitClient.getInstance().getApi().makeComplaint(AUTH_KEY,userId,deviceId,deviceInfo,uniqueId,remarks,
                "NA", "NA","Add Money PG", txnDate);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful())
                {
                    try {
                        JSONObject responseObject=new JSONObject(String.valueOf(response.body()));
                        String message=responseObject.getString("data");
                        pDialog.dismiss();
                        new AlertDialog.Builder(context)
                                .setMessage(message)
                                .setTitle("Complain Status")
                                .setPositiveButton("ok",null)
                                .show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        Toast.makeText(context, "Failed to raise complain.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    pDialog.dismiss();
                    Toast.makeText(context, "Failed to raise complain.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(context, "Failed to raise complain.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {

        return Math.min(addMoneyList.size(), 10);

    }

    public static class Viewholder extends RecyclerView.ViewHolder{
        LinearLayout closingBalanceLayout;
        TextView tvTransactionID, tvName, tvOpeningBal, tvAmount, tvCommission, tvSurcharge, tvPayableAmount, tvClosingBal, tvDate, tvTime, tvStatus;
        ImageView imgStatus;

        Button btnComplain;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            closingBalanceLayout = itemView.findViewById(R.id.closingBalanceLayout);

            tvTransactionID = itemView.findViewById(R.id.tv_all_report_transaction_id);
            tvName = itemView.findViewById(R.id.tv_all_report_userName);
            tvOpeningBal = itemView.findViewById(R.id.tv_all_report_openingBal);
            tvAmount = itemView.findViewById(R.id.tv_all_report_amount);
            tvCommission = itemView.findViewById(R.id.tv_all_report_commission);
            tvSurcharge = itemView.findViewById(R.id.tv_all_report_surcharge);
            tvPayableAmount = itemView.findViewById(R.id.tv_all_report_cost);
            tvClosingBal = itemView.findViewById(R.id.tv_all_report_balance);
            tvDate = itemView.findViewById(R.id.tv_all_report_date);
            tvTime = itemView.findViewById(R.id.tv_all_report_time);
            tvStatus = itemView.findViewById(R.id.tv_all_report_status);
            imgStatus = itemView.findViewById(R.id.img_status);

            btnComplain = itemView.findViewById(R.id.btn_make_complaint);
        }
    }
}
