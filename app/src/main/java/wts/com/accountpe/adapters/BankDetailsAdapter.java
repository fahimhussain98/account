package wts.com.accountpe.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wts.com.accountpe.R;
import wts.com.accountpe.models.BankDetailsModel;

public class BankDetailsAdapter extends RecyclerView.Adapter<BankDetailsAdapter.Viewholder> {


    ArrayList<BankDetailsModel> bankDetailsModelArrayList;

    public BankDetailsAdapter(ArrayList<BankDetailsModel> bankDetailsModelArrayList) {
        this.bankDetailsModelArrayList = bankDetailsModelArrayList;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_bank_list,parent,false);

        return new Viewholder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        String bankName=bankDetailsModelArrayList.get(position).getBankName();
        String accountName=bankDetailsModelArrayList.get(position).getAccountName();
        String accountNumber=bankDetailsModelArrayList.get(position).getAccountNumber();
        String ifsc=bankDetailsModelArrayList.get(position).getIfscCode();
        String branch=bankDetailsModelArrayList.get(position).getBranch();

        holder.tvBankName.setText("Bank : "+bankName);
        holder.tvAccountName.setText("Name : "+accountName);
        holder.tvAccountNumber.setText("Account : "+accountNumber);
        holder.tvIfsc.setText("IFSC : "+ifsc);
        holder.tvBranch.setText("Branch : "+branch);

    }

    @Override
    public int getItemCount() {
        return bankDetailsModelArrayList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        TextView tvBankName,tvAccountName,tvAccountNumber,tvIfsc,tvBranch;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            tvBankName=itemView.findViewById(R.id.tv_bank_name);
            tvAccountName=itemView.findViewById(R.id.tv_account_name);
            tvAccountNumber=itemView.findViewById(R.id.tv_account_number);
            tvIfsc=itemView.findViewById(R.id.tv_ifsc);
            tvBranch=itemView.findViewById(R.id.tv_branch);
        }
    }
}
