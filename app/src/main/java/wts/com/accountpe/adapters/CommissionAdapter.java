package wts.com.accountpe.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import wts.com.accountpe.R;
import wts.com.accountpe.models.MyCommissionModel;

public class CommissionAdapter extends ArrayAdapter {
    ArrayList<MyCommissionModel> myCommissionModelArrayList;
    Context context;
    Activity activity;

    public CommissionAdapter(@NonNull Context context, Activity activity, ArrayList<MyCommissionModel> myCommissionModelArrayList) {
        super(context, 0,myCommissionModelArrayList);

        this.myCommissionModelArrayList = myCommissionModelArrayList;
        this.context = context;
        this.activity = activity;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        @SuppressLint("ViewHolder") View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.comission_row_list,null,false);

        TextView tvOperator=view.findViewById(R.id.tv_operator_name);
        TextView tvCommPer=view.findViewById(R.id.tv_comm_per);
        TextView tvChargePer=view.findViewById(R.id.tv_charge_per);

        String operator=myCommissionModelArrayList.get(position).getOperator();
        String commPer=myCommissionModelArrayList.get(position).getCommPer();
        String chargePer=myCommissionModelArrayList.get(position).getChargePer();

        tvOperator.setText(operator);
        tvCommPer.setText(commPer);
        tvChargePer.setText(chargePer);

        return view;
    }
}

