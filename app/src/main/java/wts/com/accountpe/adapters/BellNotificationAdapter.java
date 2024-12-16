package wts.com.accountpe.adapters;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.activities.NotificationDetailActivity;
import wts.com.accountpe.models.BellNotificationModel;
import wts.com.accountpe.retrofit.RetrofitClient;


public class BellNotificationAdapter extends RecyclerView.Adapter<BellNotificationAdapter.MyViewHolder> {

    String userId,deviceId,deviceInfo;
    Context context;
    Activity activity;
    ArrayList<BellNotificationModel> bellNotificationModelArrayList;

    public BellNotificationAdapter(Context context, Activity activity,
                                   ArrayList<BellNotificationModel> bellNotificationModelArrayList, String userId, String deviceId, String deviceInfo) {
        this.context = context;
        this.activity = activity;
        this.bellNotificationModelArrayList = bellNotificationModelArrayList;
        this.userId = userId;
        this.deviceId = deviceId;
        this.deviceInfo = deviceInfo;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notification,parent,false);
        return new MyViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String title=bellNotificationModelArrayList.get(position).getTitle();
        String body=bellNotificationModelArrayList.get(position).getBody();
        String image=bellNotificationModelArrayList.get(position).getImage();
        String isVerify=bellNotificationModelArrayList.get(position).getIsVerify();
        String dateTime=bellNotificationModelArrayList.get(position).getDateTime();

        if (isVerify.equalsIgnoreCase("1"))
        {
            holder.notificationLayout.setBackground(context.getResources().getDrawable(R.drawable.read_notification));
        }
        else
        {
            holder.notificationLayout.setBackground(context.getResources().getDrawable(R.drawable.unread_notification));
        }

        if (!image.equalsIgnoreCase(""))
        {
            Picasso.get().load(image).error(R.drawable.noti_icon).into(holder.imgNotification);
        }
        holder.tvTitle.setText(title);
        holder.tvBody.setText(body);
        holder.tvDateTime.setText(dateTime);

        holder.imgDelete.setOnClickListener(v->
        {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(250, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                vibrator.vibrate(250);
            }

            new AlertDialog.Builder(context)
                    .setMessage("Do you want to delete this notification ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String id=bellNotificationModelArrayList.get(position).getId();
                            deleteNotification(id,position);
                        }
                    }).setNeutralButton("No",null)
                    .show();
        });

        holder.notificationLayout.setOnClickListener(v->
        {
            Intent intent=new Intent(context, NotificationDetailActivity.class);
            intent.putExtra("title",bellNotificationModelArrayList.get(position).getTitle());
            intent.putExtra("body",bellNotificationModelArrayList.get(position).getBody());
            intent.putExtra("imageUrl",bellNotificationModelArrayList.get(position).getImage());
            intent.putExtra("id",bellNotificationModelArrayList.get(position).getId());
            intent.putExtra("dateTime",bellNotificationModelArrayList.get(position).getDateTime());
            activity.startActivity(intent);
        });

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void deleteNotification(String id,int position) {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(context).create();
        LayoutInflater inflater =activity.getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call= RetrofitClient.getInstance().getApi().deleteNotification(AUTH_KEY,userId,deviceId,deviceInfo,id);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful())
                {
                    try {
                        JSONObject responseObject=new JSONObject(String.valueOf(response.body()));
                        String message=responseObject.getString("data");
                        new AlertDialog.Builder(context)
                                .setMessage(message)
                                .setTitle("Message")
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @SuppressLint("NotifyDataSetChanged")
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //context.startActivity(activity.getIntent());
                                        //notifyItemRemoved(position);
                                        bellNotificationModelArrayList.remove(position);
                                        notifyDataSetChanged();
                                    }
                                })
                                .show();
                        pDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
            }
        });

    }

    @Override
    public int getItemCount() {
        return bellNotificationModelArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle,tvBody, tvDateTime;
        ImageView imgNotification,imgDelete;
        ConstraintLayout notificationLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgNotification=itemView.findViewById(R.id.img_notification);
            tvTitle=itemView.findViewById(R.id.tv_title);
            tvBody=itemView.findViewById(R.id.tv_body);
            tvDateTime=itemView.findViewById(R.id.tv_dateTime);
            imgDelete=itemView.findViewById(R.id.img_delete);
            notificationLayout=itemView.findViewById(R.id.notification_layout);
        }
    }
}
