package wts.com.accountpe.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import wts.com.accountpe.R;
import wts.com.accountpe.activities.RechargeActivity;
import wts.com.accountpe.models.DthPlansModel;
public class DthPlansAdapter extends ArrayAdapter {
    ArrayList<DthPlansModel> dthPlansModelArrayList;

    Context context;
    Activity activity;

    public DthPlansAdapter(@NonNull Context context, Activity activity, ArrayList<DthPlansModel> dthPlansModelArrayList) {
        super(context, 0, dthPlansModelArrayList);

        this.context = context;
        this.activity = activity;
        this.dthPlansModelArrayList = dthPlansModelArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        @SuppressLint("ViewHolder") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_dth_plans_list, null, false);

        final TextView tvDesc, tvRs, tvPlanName, tvRsSix, tvRsThree, tvRsOneYear;
        LinearLayout btnRs, btnRsSix, btnRsThree, btnRsOneYear;

        btnRs = view.findViewById(R.id.btn_rs_one);
        btnRsOneYear = view.findViewById(R.id.btn_rs_one_year);
        btnRsThree = view.findViewById(R.id.btn_rs_three);
        btnRsSix = view.findViewById(R.id.btn_rs_six);
        tvDesc = view.findViewById(R.id.tv_description);
        tvPlanName = view.findViewById(R.id.tv_plan_name);
        tvRs = view.findViewById(R.id.tv_rs_one);
        tvRsSix = view.findViewById(R.id.tv_rs_six);
        tvRsThree = view.findViewById(R.id.tv_rs_three);
        tvRsOneYear = view.findViewById(R.id.tv_rs_one_year);


        String rs = dthPlansModelArrayList.get(position).getRs();
        final String desc = dthPlansModelArrayList.get(position).getDescription();
        String planName = dthPlansModelArrayList.get(position).getPlanName();
        String rsSix = dthPlansModelArrayList.get(position).getRsSix();
        String rsThree = dthPlansModelArrayList.get(position).getRsThree();
        String oneRsYear = dthPlansModelArrayList.get(position).getRsOne();


        tvRs.setText(rs);
        tvRsSix.setText(rsSix);
        tvRsThree.setText(rsThree);
        tvRsOneYear.setText(oneRsYear);
        tvDesc.setText(desc);
        tvPlanName.setText(planName);


        btnRs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String amount = tvRs.getText().toString().trim();

                Intent intent = new Intent(getContext(), RechargeActivity.class);
                intent.putExtra("amount", amount);
                intent.putExtra("desc",desc);

                activity.setResult(2, intent);
                activity.finish();
            }
        });

        btnRsSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = tvRsSix.getText().toString().trim();

                Intent intent = new Intent(getContext(), RechargeActivity.class);
                intent.putExtra("amount", amount);
                intent.putExtra("desc", tvPlanName.getText().toString().trim());

                activity.setResult(2, intent);
                activity.finish();
            }
        });

        btnRsThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = tvRsThree.getText().toString().trim();

                Intent intent = new Intent(getContext(), RechargeActivity.class);
                intent.putExtra("amount", amount);

                activity.setResult(2, intent);
                activity.finish();
            }
        });

        btnRsOneYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = tvRsOneYear.getText().toString().trim();

                Intent intent = new Intent(getContext(), RechargeActivity.class);
                intent.putExtra("amount", amount);

                activity.setResult(2, intent);
                activity.finish();
            }
        });


        return view;
    }


}
