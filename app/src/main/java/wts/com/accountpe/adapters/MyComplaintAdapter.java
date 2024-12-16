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
import wts.com.accountpe.models.MyComplaintModel;


public class MyComplaintAdapter extends RecyclerView.Adapter<MyComplaintAdapter.Viewholder> {


    ArrayList<MyComplaintModel> myComplaintModelArrayList;

    public MyComplaintAdapter(ArrayList<MyComplaintModel> myComplaintModelArrayList) {
        this.myComplaintModelArrayList = myComplaintModelArrayList;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.my_complaint_layout,parent,false);

        return new Viewholder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        String complaintNo=myComplaintModelArrayList.get(position).getCompliantNo();
        String remarks=myComplaintModelArrayList.get(position).getRemarks();
        String serviceName=myComplaintModelArrayList.get(position).getServiceName();
        String status=myComplaintModelArrayList.get(position).getStatus();
        String adminRemark=myComplaintModelArrayList.get(position).getAdminRemark();
        String complainDate = myComplaintModelArrayList.get(position).getComplainDate();
        String txnDate = myComplaintModelArrayList.get(position).getTxnDate();

        holder.tvTransactionId.setText("Complain ID. "+complaintNo);
        holder.tvMobileNumber.setText("Remarks : "+remarks);
        holder.tvOperator.setText("Service : "+serviceName);
        holder.tvComplaintStatus.setText("Status : "+status);
        holder.tvAdminRemarks.setText("Admin Remark : "+adminRemark);
        holder.tvComplainDate.setText("Complain Date : "+complainDate);
        holder.tvUpdateDate.setText("Txn Date : "+txnDate);

    }

    @Override
    public int getItemCount() {
        return Math.min(myComplaintModelArrayList.size(), 10);
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        TextView tvTransactionId,tvMobileNumber,tvOperator,tvComplaintStatus, tvAdminRemarks, tvComplainDate, tvUpdateDate;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            tvTransactionId=itemView.findViewById(R.id.tv_transaction_id);
            tvMobileNumber=itemView.findViewById(R.id.tv_mobile_number);
            tvOperator=itemView.findViewById(R.id.tv_operator_name);
            tvComplaintStatus=itemView.findViewById(R.id.tv_complaint_status);
            tvAdminRemarks=itemView.findViewById(R.id.tv_adminRemarks);
            tvComplainDate=itemView.findViewById(R.id.tv_complainDate);
            tvUpdateDate=itemView.findViewById(R.id.tv_updateDate);
        }
    }
}
