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
import wts.com.accountpe.activities.ShareAepsReportActivity;
import wts.com.accountpe.models.AepsModel;


public class AepsReportAdapter extends RecyclerView.Adapter<AepsReportAdapter.ViewHolder>
{
    ArrayList<AepsModel> aepsModelArrayList;
    boolean isInitialReport;
    Context context;

    public AepsReportAdapter(ArrayList<AepsModel> aepsModelArrayList, boolean isInitialReport, Context context) {
        this.aepsModelArrayList = aepsModelArrayList;
        this.isInitialReport = isInitialReport;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_aeps_report,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String transactionId=aepsModelArrayList.get(position).getTransactionId();
        String amount=aepsModelArrayList.get(position).getAmount();
        String comm=aepsModelArrayList.get(position).getComm();
        String balance=aepsModelArrayList.get(position).getNewbalance();
        String dateTime=aepsModelArrayList.get(position).getTimestamp();
        String status=aepsModelArrayList.get(position).getTxnStatus();


        holder.tvTransactionId.setText(transactionId);
        holder.tvAmount.setText("₹ "+amount);
        holder.tvComm.setText("₹ "+comm);
        holder.tvBalance.setText("₹ "+balance);
        holder.tvDateTime.setText(dateTime);
        holder.tvStatus.setText(status);

        if (status.equalsIgnoreCase("Success"))
        {
            holder.tvStatus.setBackgroundResource(R.drawable.button_back_green);
        }
        else if (status.equalsIgnoreCase("failed"))
        {
            holder.tvStatus.setBackgroundResource(R.drawable.button_back_red);
        }
        else
        {
            holder.tvStatus.setBackgroundResource(R.drawable.button_back);
        }

        holder.imgReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(context, ShareAepsReportActivity.class);
                intent.putExtra("transactionId",aepsModelArrayList.get(position).getTransactionId());
                intent.putExtra("amount",aepsModelArrayList.get(position).getAmount());
                intent.putExtra("comm",aepsModelArrayList.get(position).getComm());
                intent.putExtra("balance",aepsModelArrayList.get(position).getNewbalance());
                intent.putExtra("dateTime",aepsModelArrayList.get(position).getTimestamp());
                intent.putExtra("status",aepsModelArrayList.get(position).getTxnStatus());
                intent.putExtra("transactionType",aepsModelArrayList.get(position).getTransactionType());
                intent.putExtra("oldBalance",aepsModelArrayList.get(position).getOldbalance());
                intent.putExtra("aadharNo",aepsModelArrayList.get(position).getAadharNo());
                intent.putExtra("panNo",aepsModelArrayList.get(position).getPanNo());
                intent.putExtra("bankName",aepsModelArrayList.get(position).getBankName());
                intent.putExtra("surcharge",aepsModelArrayList.get(position).getSurcharge());
                intent.putExtra("apiName",aepsModelArrayList.get(position).getApiName());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return aepsModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTransactionId,tvAmount,tvComm,tvCost,tvBalance,tvDateTime,tvStatus;
        ImageView imgReceipt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTransactionId=itemView.findViewById(R.id.tv_all_report_transaction_id);
            tvAmount=itemView.findViewById(R.id.tv_all_report_amount);
            tvComm=itemView.findViewById(R.id.tv_all_report_commission);
            tvCost=itemView.findViewById(R.id.tv_all_report_cost);
            tvBalance=itemView.findViewById(R.id.tv_new_balance);
            tvDateTime=itemView.findViewById(R.id.tv_all_report_date_time);
            tvStatus=itemView.findViewById(R.id.tv_all_report_status);
            imgReceipt=itemView.findViewById(R.id.img_receipt);
        }
    }
}
