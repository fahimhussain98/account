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
import wts.com.accountpe.models.ViewUsersModel;


public class ViewCustomersAdapter extends RecyclerView.Adapter<ViewCustomersAdapter.Viewholder> {


    ArrayList<ViewUsersModel> viewUsersModelArrayList;



    public ViewCustomersAdapter(ArrayList<ViewUsersModel> viewUsersModelArrayList) {
        this.viewUsersModelArrayList = viewUsersModelArrayList;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_users_list,parent,false);

        return new Viewholder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        String name=viewUsersModelArrayList.get(position).getName();
        String userId=viewUsersModelArrayList.get(position).getUserid();
        String number=viewUsersModelArrayList.get(position).getMobileNo();
        String userType=viewUsersModelArrayList.get(position).getUserType();
        String date=viewUsersModelArrayList.get(position).getDate();
        String balance=viewUsersModelArrayList.get(position).getWalletBalance();


        holder.tvName.setText(name);
        holder.tvMobileNumber.setText("Mobile No. :- "+number);
        holder.tvDate.setText("Date :- "+date);
        holder.tvUserType.setText("Role :- "+userType);
        holder.tvBalance.setText("Balance : \u20b9"+balance);


    }

    @Override
    public int getItemCount() {
        return viewUsersModelArrayList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {

        TextView tvName,tvDate,tvUserType,tvMobileNumber, tvBalance;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            tvName=itemView.findViewById(R.id.tv_name);
            tvDate=itemView.findViewById(R.id.tv_date);
            tvUserType=itemView.findViewById(R.id.tv_user_tpe);
            tvMobileNumber=itemView.findViewById(R.id.tv_mobile_number);
            tvBalance = itemView.findViewById(R.id.tv_balance);
        }
    }
}
