package wts.com.accountpe.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import wts.com.accountpe.activities.ShareDmtReportActivity;
import wts.com.accountpe.models.MoneyReportModel;

public class MoneyReportAdapter extends RecyclerView.Adapter<MoneyReportAdapter.ViewHolder> {

    ArrayList<MoneyReportModel> moneyReportModelArrayList;
    Context context;
    Activity activity;

    public MoneyReportAdapter(ArrayList<MoneyReportModel> moneyReportModelArrayList, Context context, Activity activity) {
        this.moneyReportModelArrayList = moneyReportModelArrayList;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_money_report_layout,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        String amount=moneyReportModelArrayList.get(position).getAmount();
        final String accountNumber=moneyReportModelArrayList.get(position).getAccountNumber();
        final String beniName=moneyReportModelArrayList.get(position).getBeniName();
        final String bank=moneyReportModelArrayList.get(position).getBank();
        final String ifsc=moneyReportModelArrayList.get(position).getIfsc();
        String cost=moneyReportModelArrayList.get(position).getCost();
        String balance=moneyReportModelArrayList.get(position).getBalance();
        String openingBal=moneyReportModelArrayList.get(position).getOpeningBal();
        String date=moneyReportModelArrayList.get(position).getDate();
        final String transactionId=moneyReportModelArrayList.get(position).getTransactionId();
        String status=moneyReportModelArrayList.get(position).getStatus();
        String bankRRn = moneyReportModelArrayList.get(position).getBankRRN();
        String commission = moneyReportModelArrayList.get(position).getCommission();
        String surcharge = moneyReportModelArrayList.get(position).getSurcharge();
        String serviceType = moneyReportModelArrayList.get(position).getServiceType();

        if (status.equalsIgnoreCase("SUCCESS") || status.equalsIgnoreCase("Successful"))
        {
            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.button_back_green));
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.white));
        }
        else if (status.equalsIgnoreCase("pending"))
        {
            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.yellow_back));
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.white));
        }

        else if (status.equalsIgnoreCase("failure") || status.equalsIgnoreCase("Failed"))
        {
            holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.button_back_red));
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.white));
        }
        holder.tvAmount.setText("₹ "+amount);
        holder.tvAccountNumber.setText(accountNumber);
        holder.tvBeniName.setText(beniName);
        holder.tvBank.setText(bank);
        holder.tvIfsc.setText(ifsc);
        holder.tvCost.setText("₹ "+cost);
        holder.tvBalance.setText("₹ "+balance);
        holder.tvDate.setText(date);
        holder.tvTransactionId.setText(transactionId);
        holder.tvStatus.setText(status);

        holder.imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShareDmtReportActivity.class);
                intent.putExtra("amount",amount);
                intent.putExtra("cost",cost);
                intent.putExtra("accountNumber",accountNumber);
                intent.putExtra("beniName",beniName);
                intent.putExtra("bank",bank);
                intent.putExtra("ifsc",ifsc);
                intent.putExtra("date",date);
                intent.putExtra("transactionId",transactionId);
                intent.putExtra("status",status);
                intent.putExtra("bankRRn",bankRRn);
                intent.putExtra("openingBal",openingBal);
                intent.putExtra("balance",balance);
                intent.putExtra("commission",commission);
                intent.putExtra("surcharge",surcharge);
                intent.putExtra("serviceType",serviceType);

                context.startActivity(intent);
                //checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, FILE_PERMISSION);
            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {

        return Math.min(moneyReportModelArrayList.size(), 10);



    }

    public static class ViewHolder extends  RecyclerView.ViewHolder {
        TextView tvAmount,tvAccountNumber,tvBeniName,tvBank,tvIfsc,tvCost,tvBalance,tvDate,tvTransactionId,tvStatus;
        ImageView imgShare;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvAmount=itemView.findViewById(R.id.tv_amount);
            tvAccountNumber=itemView.findViewById(R.id.tv_account_number);
            tvBeniName=itemView.findViewById(R.id.tv_beni_name);
            tvBank=itemView.findViewById(R.id.tv_bank_name);
            tvIfsc=itemView.findViewById(R.id.tv_ifsc);
            tvCost=itemView.findViewById(R.id.tv_all_report_cost);
            tvBalance=itemView.findViewById(R.id.tv_all_report_balance);
            tvDate=itemView.findViewById(R.id.tv_all_report_date_time);
            tvTransactionId=itemView.findViewById(R.id.tv_transaction_id);
            tvStatus=itemView.findViewById(R.id.tv_all_report_status);
            imgShare = itemView.findViewById(R.id.img_share);
        }
    }
}
