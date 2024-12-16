package wts.com.accountpe.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wts.com.accountpe.R;
import wts.com.accountpe.models.CreditDebitModel;


public class CreditDebitAdapter extends RecyclerView.Adapter<CreditDebitAdapter.ViewHolder> {

    ArrayList<CreditDebitModel> creditDebitModelArrayList;
    String drUser, crUser, id, amount, paymentType, date,time, remarks;
    boolean isInitialReport,isCreditReport;
    Context context;

    public CreditDebitAdapter(ArrayList<CreditDebitModel> creditDebitModelArrayList, boolean isInitialReport, boolean isCreditReport, Context context) {
        this.creditDebitModelArrayList = creditDebitModelArrayList;
        this.isInitialReport = isInitialReport;
        this.isCreditReport=isCreditReport;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_credit_debit_report, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        //drUser = creditDebitModelArrayList.get(position).getDrUser();
        crUser = creditDebitModelArrayList.get(position).getCrUser();
        amount = creditDebitModelArrayList.get(position).getAmount();
        paymentType = creditDebitModelArrayList.get(position).getPaymentType();
        date = creditDebitModelArrayList.get(position).getDate();
        time = creditDebitModelArrayList.get(position).getTime();
        remarks = creditDebitModelArrayList.get(position).getRemarks();

        holder.setData(drUser, crUser, amount, paymentType, date,time, remarks);

        holder.tvMore.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                final View view1 = LayoutInflater.from(context).inflate(R.layout.credit_debit_report_dialog, null, false);
                final AlertDialog builder = new AlertDialog.Builder(context).create();
                builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                builder.setView(view1);
                builder.show();

                TextView tvDrUser=view1.findViewById(R.id.tv_dr_user);
                TextView tvCrUser=view1.findViewById(R.id.tv_cr_user);
                TextView tvAmount=view1.findViewById(R.id.tv_amount);
                TextView tvPaymentType=view1.findViewById(R.id.tv_payment_type);
                TextView tvPaymentDate=view1.findViewById(R.id.tv_payment_date);
                TextView tvPaymentTime=view1.findViewById(R.id.tv_payment_time);
                TextView tvRemarks=view1.findViewById(R.id.tv_remarks);

                drUser = creditDebitModelArrayList.get(position).getDrUser();
                crUser = creditDebitModelArrayList.get(position).getCrUser();
                amount = creditDebitModelArrayList.get(position).getAmount();
                paymentType = creditDebitModelArrayList.get(position).getPaymentType();
                date = creditDebitModelArrayList.get(position).getDate();
                time = creditDebitModelArrayList.get(position).getTime();
                remarks = creditDebitModelArrayList.get(position).getRemarks();

                tvDrUser.setText(drUser);
                tvCrUser.setText(crUser);
                tvAmount.setText("₹ "+amount);
                tvPaymentType.setText(paymentType);
                tvPaymentDate.setText(date);
                tvPaymentTime.setText(time);
                tvRemarks.setText(remarks);

                if (isCreditReport)
                {
                    tvAmount.setTextColor(context.getResources().getColor(R.color.green));
                }
                else
                {
                    tvAmount.setTextColor(context.getResources().getColor(R.color.red));
                }

            }
        });

        if (isCreditReport)
        {
            holder.imgCreditDebit.setImageResource(R.drawable.img_credit_balance);
        }
        else
        {
            holder.imgCreditDebit.setImageResource(R.drawable.img_debit_balance);
        }
    }

    @Override
    public int getItemCount() {
        if (isInitialReport) {
            if (creditDebitModelArrayList.size()<=10)
                return creditDebitModelArrayList.size();
            else  {
                return 10;
            }
        } else {
            return creditDebitModelArrayList.size();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCrUser, tvAmount,tvTime,tvDate,tvMore;
        ImageView imgCreditDebit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCrUser = itemView.findViewById(R.id.tv_cr_user);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvMore = itemView.findViewById(R.id.btn_view_more);
            imgCreditDebit = itemView.findViewById(R.id.img_credit_debit);
        }

        @SuppressLint("SetTextI18n")
        public void setData(String drUser, String crUser, String amount, String paymentType, String date, String time, String remarks) {
            tvCrUser.setText(crUser);
            tvAmount.setText("₹ "+amount);
            tvTime.setText(time);
            tvDate.setText(date);
        }
    }
}
