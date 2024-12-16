package wts.com.accountpe.activities;

import static wts.com.accountpe.retrofit.RetrofitClient.AUTH_KEY;


import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.accountpe.R;
import wts.com.accountpe.retrofit.RetrofitClient;

public class NotificationDetailActivity extends AppCompatActivity {

    TextView tvTitle,tvBody, tvDateTime;
    ImageView imgNotificationImage;

    String title,body,imageUrl,id, dateTime;
    String userId,deviceId,deviceInfo;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);

        initViews();


        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(NotificationDetailActivity.this);
        userId=sharedPreferences.getString("userid",null);
        deviceId=sharedPreferences.getString("deviceId",null);
        deviceInfo=sharedPreferences.getString("deviceInfo",null);

        title=getIntent().getStringExtra("title");
        body=getIntent().getStringExtra("body");
        imageUrl=getIntent().getStringExtra("imageUrl");
        id=getIntent().getStringExtra("id");
        dateTime=getIntent().getStringExtra("dateTime");

        markNotificationAsRead();

        tvTitle.setText(title);
        tvBody.setText(body);
        tvDateTime.setText(dateTime);
        if (!imageUrl.equalsIgnoreCase(""))
        {
            Picasso.get().load(imageUrl).error(R.drawable.noti_icon).into(imgNotificationImage);
        }

    }

  /*  private void markNotificationAsRead() {

        Call<JsonObject> call= RetrofitClient.getInstance().getApi().markNotificationAsRead(AUTH_KEY,userId,deviceId,deviceInfo,id);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
            }
        });
*/

  private void markNotificationAsRead() {

      Call<JsonObject> call = RetrofitClient.getInstance().getApi().markNotificationAsRead(AUTH_KEY, userId, deviceId, deviceInfo, id);
      call.enqueue(new Callback<JsonObject>() {
          @Override
          public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
              if (response.isSuccessful()) {
                  // Notification sound trigger upon success
                  showNotification("Notification", "Marked as read successfully!");
              }
          }

          @Override
          public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
              // You can also trigger a sound notification here if the API fails
              showNotification("Notification", "Failed to mark notification as read!");
          }
      });
  }




    private void showNotification(String title, String message) {
        // Get the default notification sound URI
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "my_channel_id")
                .setSmallIcon(R.drawable.noti_icon)  // Replace with your own icon
                .setContentTitle(title)
                .setContentText(message)
                .setSound(soundUri)  // Set the sound for the notification
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Notify the user
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }



    private void initViews() {
        tvTitle=findViewById(R.id.tv_title);
        tvBody=findViewById(R.id.tv_body);
        tvDateTime =findViewById(R.id.tv_dateTime);
        imgNotificationImage=findViewById(R.id.img_notification);
    }
}