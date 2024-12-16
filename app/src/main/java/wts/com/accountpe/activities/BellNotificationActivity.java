package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.adapters.BellNotificationAdapter;
import wts.com.accountpe.models.BellNotificationModel;
import wts.com.accountpe.retrofit.RetrofitClient;


public class BellNotificationActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    String userId,deviceId,deviceInfo;

    RecyclerView notificationRecycler;
    TextView tvNoNotification;

    ArrayList<BellNotificationModel> bellNotificationModelArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bell_notification);

        notificationRecycler=findViewById(R.id.notification_recycler);
        tvNoNotification=findViewById(R.id.text_notifications);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(BellNotificationActivity.this);
        userId=sharedPreferences.getString("userid",null);
        deviceId=sharedPreferences.getString("deviceId",null);
        deviceInfo=sharedPreferences.getString("deviceInfo",null);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllNotification();
    }

    private void getAllNotification() {
        final AlertDialog pDialog = new AlertDialog.Builder(BellNotificationActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call= RetrofitClient.getInstance().getApi().getAllNotification(AUTH_KEY,userId,deviceId,deviceInfo);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful())
                {
                    try {
                        JSONObject responseObject=new JSONObject(String.valueOf(response.body()));

                        String statusCode=responseObject.getString("statuscode");

                        if (statusCode.equalsIgnoreCase("TXN"))
                        {

                            bellNotificationModelArrayList=new ArrayList<>();
                            JSONArray dataArray= responseObject.getJSONArray("data");
                            for (int position=0;position<dataArray.length();position++)
                            {
                                BellNotificationModel bellNotificationModel=new BellNotificationModel();

                                JSONObject dataObject=dataArray.getJSONObject(position);
                                String title= dataObject.getString("Title");
                                String body= dataObject.getString("Body");
                                String image= dataObject.getString("Image");
                                String id= dataObject.getString("Id");
                                String isVerify= dataObject.getString("IsVerify");
                                String dateTime= dataObject.getString("CreatedDate");

                                @SuppressLint("SimpleDateFormat") DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
                                String[] splitDate = dateTime.split("T");

                                try {
                                    Date date = inputDateFormat.parse(splitDate[0]);
                                    Date time = simpleDateFormat.parse(splitDate[1]);

                                    assert date != null;
                                    @SuppressLint("SimpleDateFormat") String outputDate = new SimpleDateFormat("dd MMM yyyy").format(date);
                                    assert time != null;
                                    @SuppressLint("SimpleDateFormat") String outputTime = new SimpleDateFormat("hh:mm a").format(time);

                                    bellNotificationModel.setDateTime(outputDate+" "+outputTime);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                bellNotificationModel.setTitle(title);
                                bellNotificationModel.setBody(body);
                                bellNotificationModel.setImage(image);
                                bellNotificationModel.setId(id);
                                bellNotificationModel.setIsVerify(isVerify);

                                bellNotificationModelArrayList.add(bellNotificationModel);

                            }

                            notificationRecycler.setLayoutManager(new LinearLayoutManager(BellNotificationActivity.this,
                                    RecyclerView.VERTICAL, false));

                            BellNotificationAdapter bellNotificationAdapter = new BellNotificationAdapter(BellNotificationActivity.this,
                                    BellNotificationActivity.this,bellNotificationModelArrayList,userId,deviceId,deviceInfo);
                            notificationRecycler.setAdapter(bellNotificationAdapter);

                            pDialog.dismiss();
                        }
                        else
                        {
                            pDialog.dismiss();
                            notificationRecycler.setVisibility(View.GONE);
                            tvNoNotification.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        notificationRecycler.setVisibility(View.GONE);
                        tvNoNotification.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    pDialog.dismiss();
                    notificationRecycler.setVisibility(View.GONE);
                    tvNoNotification.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                pDialog.dismiss();
                notificationRecycler.setVisibility(View.GONE);
                tvNoNotification.setVisibility(View.VISIBLE);
            }
        });

    }
}