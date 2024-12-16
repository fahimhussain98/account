package wts.com.accountpe.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wts.com.accountpe.R;
import wts.com.accountpe.activities.SharePayoutReportActivity;
import wts.com.accountpe.models.SettlementReportModel;

public class SettlementReportAdapter extends RecyclerView.Adapter<SettlementReportAdapter.ViewHolder>
{

    ArrayList<SettlementReportModel> settlementReportModelArrayList;
    boolean isInitialReport;
    Context context;

    public SettlementReportAdapter(ArrayList<SettlementReportModel> settlementReportModelArrayList, boolean isInitialReport, Context context) {
        this.settlementReportModelArrayList = settlementReportModelArrayList;
        this.isInitialReport = isInitialReport;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_settlement_report,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String name = settlementReportModelArrayList.get(position).getName();
        String surcharge = settlementReportModelArrayList.get(position).getSurcharge();
        String amount = settlementReportModelArrayList.get(position).getAmount();
        String paymentType = settlementReportModelArrayList.get(position).getPaymentType();
        String reqDate = settlementReportModelArrayList.get(position).getReqDate();
        String accountNumber = settlementReportModelArrayList.get(position).getAccountNo();
        String status = settlementReportModelArrayList.get(position).getStatus();

        holder.tvReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, SharePayoutReportActivity.class);
                intent.putExtra("transactionId",settlementReportModelArrayList.get(position).getTransactionId());
                intent.putExtra("amount",settlementReportModelArrayList.get(position).getAmount());
                intent.putExtra("comm",settlementReportModelArrayList.get(position).getComm());
                intent.putExtra("balance",settlementReportModelArrayList.get(position).getNewBalance());
                intent.putExtra("dateTime",settlementReportModelArrayList.get(position).getReqDate());
                intent.putExtra("status",settlementReportModelArrayList.get(position).getStatus());
                intent.putExtra("transactionType",settlementReportModelArrayList.get(position).getPaymentType());
                intent.putExtra("oldBalance",settlementReportModelArrayList.get(position).getOldBalance());
                intent.putExtra("accountName",settlementReportModelArrayList.get(position).getName());
                intent.putExtra("accountNo",settlementReportModelArrayList.get(position).getAccountNo());
                intent.putExtra("bankName",settlementReportModelArrayList.get(position).getBankName());
                intent.putExtra("ifsc",settlementReportModelArrayList.get(position).getIfscCode());
                intent.putExtra("surcharge",settlementReportModelArrayList.get(position).getSurcharge());
                intent.putExtra("status",settlementReportModelArrayList.get(position).getStatus());
                intent.putExtra("apiName",settlementReportModelArrayList.get(position).getApiName());
                intent.putExtra("serviceType","payout");
                context.startActivity(intent);
            }
        });

        holder.tvName.setText(name);
        holder.tvSurcharge.setText("₹ " + surcharge);
        holder.tvAmount.setText("₹ " + amount);
        holder.tvPaymentType.setText(paymentType);
        holder.tvReqDate.setText(reqDate);
        holder.tvAccountNumber.setText(accountNumber);
        holder.tvStatus.setText(status);

        if (status.equalsIgnoreCase("Success"))
        {
            holder.tvStatus.setBackgroundResource(R.drawable.button_back_green);
        }
        else if (status.equalsIgnoreCase("Failed"))
        {
            holder.tvStatus.setBackgroundResource(R.drawable.button_back_red);
        }
        else
        {
            holder.tvStatus.setBackgroundResource(R.drawable.yellow_back);
        }

    }

    @Override
    public int getItemCount() {

        return Math.min(settlementReportModelArrayList.size(), 10);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSurcharge, tvAmount, tvPaymentType, tvReqDate, tvAccountNumber, tvStatus,tvReceipt;
        ImageView imgReceipt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvSurcharge = itemView.findViewById(R.id.tv_surcharge);
            tvAmount = itemView.findViewById(R.id.tv_all_report_amount);
            tvPaymentType = itemView.findViewById(R.id.tv_payment_type);
            tvReqDate = itemView.findViewById(R.id.tv_all_report_date_time);
            tvAccountNumber = itemView.findViewById(R.id.tv_account_number);
            tvStatus = itemView.findViewById(R.id.tv_all_report_status);
            imgReceipt=itemView.findViewById(R.id.img_receipt);
            tvReceipt=itemView.findViewById(R.id.tv_receipt);
        }
    }
}
