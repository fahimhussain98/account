package wts.com.accountpe.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wts.com.accountpe.R;
import wts.com.accountpe.models.WalletToWalletReportModel;

public class WalletToWalletRecyclerviewAdapter extends RecyclerView.Adapter<WalletToWalletRecyclerviewAdapter.MyViewHolder> {

    ArrayList<WalletToWalletReportModel> arrayList;
    Context context;

    public WalletToWalletRecyclerviewAdapter(ArrayList<WalletToWalletReportModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.wallet_to_wallet_report_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvRefNo.setText(arrayList.get(position).getRefNo());
        holder.tvTransferTo.setText(arrayList.get(position).getReceivedBy());
        holder.tvAmount.setText(arrayList.get(position).getAmount());
        holder.tvDate.setText(arrayList.get(position).getTxnDate());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvRefNo, tvTransferTo, tvAmount, tvDate;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvRefNo = itemView.findViewById(R.id.tvRefNo);
            tvTransferTo = itemView.findViewById(R.id.tvTransferTo);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDate = itemView.findViewById(R.id.tvDate);

        }
    }
}
