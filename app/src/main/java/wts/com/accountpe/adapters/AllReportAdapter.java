package wts.com.accountpe.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import wts.com.accountpe.R;
import wts.com.accountpe.activities.ShareReportActivity;
import wts.com.accountpe.models.AllReportsModel;

public class AllReportAdapter extends RecyclerView.Adapter<AllReportAdapter.ViewHolder> {

    ArrayList<AllReportsModel> allReportsModelArrayList;
    String transactionId, operatorName, imageUrl, number, amount, commission, cost, balance, date, time, status, stype;
    Context context;
    String userId;
    String deviceId, deviceInfo;
    String serviceName;
    SharedPreferences sharedPreferences;
    Activity activity;

    public AllReportAdapter(ArrayList<AllReportsModel> allReportsModelArrayList, Context context, String userId, Activity activity, String serviceName) {
        this.allReportsModelArrayList = allReportsModelArrayList;
        this.context = context;
        this.userId = userId;
        this.activity = activity;
        this.serviceName = serviceName;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recharge_report2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        transactionId = allReportsModelArrayList.get(position).getTransactionId();
        operatorName = allReportsModelArrayList.get(position).getOperatorName();
        number = allReportsModelArrayList.get(position).getNumber();
        amount = allReportsModelArrayList.get(position).getAmount();
        commission = allReportsModelArrayList.get(position).getCommission();
        cost = allReportsModelArrayList.get(position).getCost();
        balance = allReportsModelArrayList.get(position).getBalance();
        date = allReportsModelArrayList.get(position).getDate();
        time = allReportsModelArrayList.get(position).getTime();
        status = allReportsModelArrayList.get(position).getStatus();
        stype = allReportsModelArrayList.get(position).getsType();
        imageUrl = allReportsModelArrayList.get(position).getImageUrl();
        String openingBalance = allReportsModelArrayList.get(position).getOpeningBalance();
        String closingBalance = allReportsModelArrayList.get(position).getClosingBalance();
        String liveId = allReportsModelArrayList.get(position).getLiveId();


        holder.tvNumber.setText(number);
        holder.tvStatus.setText(status);
        holder.tvDateTime.setText(date + " , " + time);
        holder.tvAmount.setText("₹ " + amount);

        if (status.equalsIgnoreCase("SUCCESS")) {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.red));

        }

        Picasso.get().load(imageUrl).into(holder.imgOperator);

        holder.tvMore.setOnClickListener(v ->
        {
            Intent intent = new Intent(context, ShareReportActivity.class);
            intent.putExtra("number", allReportsModelArrayList.get(position).getNumber());
            intent.putExtra("amount", allReportsModelArrayList.get(position).getAmount());
            intent.putExtra("pgAmount", allReportsModelArrayList.get(position).getPgAmount());
            intent.putExtra("commission", allReportsModelArrayList.get(position).getCommission());
            intent.putExtra("cost", allReportsModelArrayList.get(position).getCost());
            intent.putExtra("openingBal", allReportsModelArrayList.get(position).getOpeningBalance());
            intent.putExtra("closingBal", allReportsModelArrayList.get(position).getClosingBalance());
            intent.putExtra("date", allReportsModelArrayList.get(position).getDate());
            intent.putExtra("time", allReportsModelArrayList.get(position).getTime());
            intent.putExtra("status", allReportsModelArrayList.get(position).getStatus());
            intent.putExtra("operator", allReportsModelArrayList.get(position).getOperatorName());
            intent.putExtra("uniqueId", allReportsModelArrayList.get(position).getUniqueId());
            intent.putExtra("liveId", allReportsModelArrayList.get(position).getLiveId());
            intent.putExtra("serviceName", serviceName);
            context.startActivity(intent);

        });


    }

    @Override
    public int getItemCount() {

        return Math.min(allReportsModelArrayList.size(), 10);


    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        /*TextView tvTransactionId, tvDate, tvNumber, tvAmount, tvOpeningBalance, tvComm, tvClosingBalance, tvStatus,tvLiveId;
        ImageView imgOperator, imgShare;*/
        TextView tvNumber, tvAmount, tvStatus, tvDateTime, tvMore;
        ImageView imgOperator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            /*tvTransactionId = itemView.findViewById(R.id.tv_transaction_id);
            tvDate = itemView.findViewById(R.id.tv_date_time);
            tvNumber = itemView.findViewById(R.id.tv_number);
            tvNumber = itemView.findViewById(R.id.tv_number);
            tvOpeningBalance = itemView.findViewById(R.id.tv_opening_balance);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvComm = itemView.findViewById(R.id.tv_commission);
            tvClosingBalance = itemView.findViewById(R.id.tv_closing_balance);
            tvStatus = itemView.findViewById(R.id.tv_status);
            imgShare = itemView.findViewById(R.id.img_share);
            imgOperator = itemView.findViewById(R.id.img_operator);
            tvLiveId = itemView.findViewById(R.id.tv_live_id);*/

            tvNumber = itemView.findViewById(R.id.tv_number);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvDateTime = itemView.findViewById(R.id.tv_date_time);
            imgOperator = itemView.findViewById(R.id.img_operator);
            tvMore = itemView.findViewById(R.id.tv_more);

        }
    }
}
