package wts.com.accountpe.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import wts.com.accountpe.R;
import wts.com.accountpe.myInterface.CircleInterface;

public class CircleAdapter extends RecyclerView.Adapter<CircleAdapter.MyViewHolder>
{
    String[] stateNameArray;
    CircleInterface circleInterface;


    public CircleAdapter(String[] stateNameArray) {
        this.stateNameArray = stateNameArray;
    }

    public void setMyInterface(CircleInterface circleInterface)
    {
        this.circleInterface = circleInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_circle_list, null, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tvCircleName.setText(stateNameArray[position]);
        holder.circleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //dialog.dismiss();


                String circleName= stateNameArray[position];
                //String circleId= circleModelArrayList.get(position).getCircleId();

                circleInterface.circleData(circleName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stateNameArray.length;
    }


    public static class MyViewHolder extends  RecyclerView.ViewHolder {
        ConstraintLayout circleLayout;
        TextView tvCircleName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            circleLayout=itemView.findViewById(R.id.circle_layout);
            tvCircleName=itemView.findViewById(R.id.tv_circle_name);
        }
    }
}
