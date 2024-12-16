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
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

import wts.com.accountpe.R;
import wts.com.accountpe.activities.RechargeActivity;
import wts.com.accountpe.models.PlansModel;


public class MyPlansAdaper extends ArrayAdapter
{

    ArrayList<PlansModel> myModelArrayList;

    Context context;
    Activity activity;

    public MyPlansAdaper(@NonNull Context context, Activity activity, ArrayList<PlansModel> myModelArrayList) {
        super(context, 0,myModelArrayList);

        this.context=context;
        this.activity=activity;
        this.myModelArrayList=myModelArrayList;

    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.plan_list_layout,null,false);

        final TextView tvDesc,tvValidity,tvRs,textValidity;
        LinearLayout btnRs;
        ConstraintLayout plansLayout;
        btnRs=view.findViewById(R.id.btn_rs);
        tvDesc=view.findViewById(R.id.tv_description);
        tvValidity=view.findViewById(R.id.tv_validity);
        tvRs=view.findViewById(R.id.tv_rs);
        textValidity=view.findViewById(R.id.text_validity);
        plansLayout=view.findViewById(R.id.plans_layout);

        String rs=myModelArrayList.get(position).getRs();
        final String desc=myModelArrayList.get(position).getDesc();
        String validity=myModelArrayList.get(position).getValidity();
        String validityText=myModelArrayList.get(position).getValidityText();

        textValidity.setText(validityText);
        tvRs.setText(rs);
        tvDesc.setText(desc);
        tvValidity.setText(validity);


        plansLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount=tvRs.getText().toString().trim();

                Intent intent=new Intent(getContext(), RechargeActivity.class);
                intent.putExtra("amount",amount);
                intent.putExtra("desc",desc);

                activity.setResult(1,intent);
                activity.finish();


            }
        });
        return view;
    }
}
