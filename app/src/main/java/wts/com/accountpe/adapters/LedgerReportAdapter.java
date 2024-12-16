package wts.com.accountpe.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wts.com.accountpe.R;
import wts.com.accountpe.models.LedgerReportModel;


public class LedgerReportAdapter extends RecyclerView.Adapter<LedgerReportAdapter.ViewHolder> {

    ArrayList<LedgerReportModel> ledgerReportModelArrayList;
    Context context;
    boolean isIntialReport;


    public LedgerReportAdapter(ArrayList<LedgerReportModel> ledgerReportModelArrayList, Context context, boolean isInitialReport) {
        this.ledgerReportModelArrayList = ledgerReportModelArrayList;
        this.context = context;
        this.isIntialReport = isInitialReport;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_ledger_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final String balanceId, userId, oldBalance, newBalance, transactionType,
                transactionDate, ipAddress, crDrType, amount, surcharge, tds, commission;

        balanceId = ledgerReportModelArrayList.get(position).getBalanceId();
        userId = ledgerReportModelArrayList.get(position).getUserId();
        oldBalance = ledgerReportModelArrayList.get(position).getOldBalance();
        newBalance = ledgerReportModelArrayList.get(position).getNewBalance();
        transactionType = ledgerReportModelArrayList.get(position).getTransactionType();
        transactionDate = ledgerReportModelArrayList.get(position).getTransactionDate();
        ipAddress = ledgerReportModelArrayList.get(position).getIpAddress();
        crDrType = ledgerReportModelArrayList.get(position).getCrDrType();
        amount = ledgerReportModelArrayList.get(position).getAmount();
        surcharge = ledgerReportModelArrayList.get(position).getSurcharge();
        tds = ledgerReportModelArrayList.get(position).getTds();
        commission = ledgerReportModelArrayList.get(position).getCommission();
        String pgAmount = ledgerReportModelArrayList.get(position).getPgAmount();


        holder.setdata(balanceId, userId, oldBalance, newBalance, transactionType, transactionDate, ipAddress, crDrType,
                amount);

        holder.btnViewMore.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                final View view1 = LayoutInflater.from(context).inflate(R.layout.view_more_dialog_layout, null, false);
                final AlertDialog builder = new AlertDialog.Builder(context).create();
                builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                builder.setCancelable(false);
                builder.setView(view1);
                builder.show();

                Button btnGoToBack = view1.findViewById(R.id.btn_dialog_back_button);

                TextView tvDialogBalanceId = view1.findViewById(R.id.tv_dialog_balance_id);
                TextView tvDialogTransType = view1.findViewById(R.id.tv_dialog_trans_type);
                TextView tvDialogCrDrType = view1.findViewById(R.id.tv_dialog_cr_dr_type);
                TextView tvDialogTransDate = view1.findViewById(R.id.tv_dialog_trans_date);
                TextView tvDialogTransAmount = view1.findViewById(R.id.tv_dialog_trans_amount);
                TextView tvDialogOldbal = view1.findViewById(R.id.tv_dialog_old_bal);
                TextView tvDialogNewbal = view1.findViewById(R.id.tv_dialog_new_bal);
                TextView tvSurcharge = view1.findViewById(R.id.tv_surcharge);
                TextView tvTds = view1.findViewById(R.id.tv_tds);
                TextView tvCommission = view1.findViewById(R.id.tv_commission);
                TextView tvPgAmount = view1.findViewById(R.id.tv_dialog_pg_amount);

                tvDialogBalanceId.setText(balanceId);
                tvDialogTransType.setText(transactionType);
                tvDialogCrDrType.setText(crDrType);
                tvDialogTransDate.setText(transactionDate);
                tvDialogTransAmount.setText("₹ " + amount);
                tvDialogOldbal.setText("₹ " + oldBalance);
                tvDialogNewbal.setText("₹ " + newBalance);
                tvSurcharge.setText("₹ " + surcharge);
                tvTds.setText("₹ " + tds);
                tvCommission.setText("₹ " + commission);
                tvPgAmount.setText("₹ " + pgAmount);


                btnGoToBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return Math.min(ledgerReportModelArrayList.size(), 10);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTransactionId, tvTransactionType, tvTransactionDate, tvOldBalance, tvNewBalance, tvTransactionAmount;
        Button btnViewMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTransactionId = itemView.findViewById(R.id.tv_transaction_id);
            tvTransactionType = itemView.findViewById(R.id.tv_transaction_type);
            tvTransactionDate = itemView.findViewById(R.id.tv_transaction_date);
            tvOldBalance = itemView.findViewById(R.id.tv_old_balance);
            tvNewBalance = itemView.findViewById(R.id.tv_new_balance);
            tvTransactionAmount = itemView.findViewById(R.id.tv_transaction_amount);
            btnViewMore = itemView.findViewById(R.id.btn_view_more);
        }


        public void setdata(String balanceId, String userId, String oldBalance, String newBalance,
                            String transactionType, String transactionDate, String ipAddress,
                            String crDrType, String amount) {
            tvTransactionId.setText(balanceId);
            tvTransactionType.setText(transactionType);
            tvTransactionDate.setText(transactionDate);
            tvOldBalance.setText(oldBalance);
            tvNewBalance.setText(newBalance);
            tvTransactionAmount.setText(amount);

        }
    }
}
