package wts.com.accountpe.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import wts.com.accountpe.R;
import wts.com.accountpe.models.AddMoneyReportModel;


public class AddMoneyReportAdapter extends RecyclerView.Adapter<AddMoneyReportAdapter.MyViewHolder> {

    ArrayList<AddMoneyReportModel> addMoneyList;
    Context context;

    public AddMoneyReportAdapter(ArrayList<AddMoneyReportModel> addMoneyList, Context context) {
        this.addMoneyList = addMoneyList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.addmoney_report_item, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String strTransactionID = addMoneyList.get(position).getTxnID();
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


    }

    @Override
    public int getItemCount() {
        return Math.min(addMoneyList.size(), 10);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

      TextView tvTransactionID, tvName, tvOpeningBal, tvAmount, tvCommission, tvSurcharge, tvPayableAmount, tvClosingBal, tvDate, tvTime, tvStatus;
      ImageView imgStatus;

      public MyViewHolder(@NonNull View itemView) {
          super(itemView);

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

      }
  }


}
