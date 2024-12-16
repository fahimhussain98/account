package wts.com.accountpe.fcm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.Html;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import wts.com.accountpe.R;
import wts.com.accountpe.activities.SplashScreenActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("fcmToken", token);
        editor.apply();

        sendNotification("AccountPe", "Welcome to AccountPe", "");

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
       /* if (remoteMessage.getData().size() != 0 && remoteMessage.getNotification() != null)
        {
            JSONObject jsonObject=new JSONObject(remoteMessage.getData());

            String title=jsonObject.optString("title");
            String body=jsonObject.optString("body");
        }*/

       /* if (remoteMessage.getData().size() > 0) {
            Log.e("mj4", "" + remoteMessage.getData());
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e("mj5", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }*/

//        String button =   Objects.requireNonNull(remoteMessage.getNotification()).getClickAction();
//        Toast.makeText(this, button, Toast.LENGTH_SHORT).show();

        try {
            JSONObject jsonObject = new JSONObject(remoteMessage.getData());
            String title = jsonObject.optString("title");
            String body = jsonObject.optString("body");
            String image = jsonObject.optString("Image");
            sendNotification(title, body, image);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "notification exception", Toast.LENGTH_SHORT).show();
        }

    }

    private void sendNotification(String title, String body, String image) {

        NotificationCompat.BigPictureStyle bigPictureStyle = null;

        Intent intent = new Intent(this, SplashScreenActivity.class);

        PendingIntent pendingIntent;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        }

        String channelId = "1";

        Bitmap remote_picture = null;
        try {
            URL url = new URL(image);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream in = connection.getInputStream();
            remote_picture = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.bigPicture(remote_picture)
                .bigLargeIcon(null).build();

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(MyFirebaseMessagingService.this, channelId)
                        .setContentTitle(title)
                        .setContentText(Html.fromHtml(body))
                        .setAutoCancel(true)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setContentIntent(pendingIntent)
                        .setPriority(Notification.PRIORITY_MAX).setStyle(bigPictureStyle)
                        .setSound(notificationSound)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setColor(getResources().getColor(R.color.purple_700));
        notificationBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        notificationBuilder.setLights(Color.RED, 3000, 3000);
        NotificationManager notificationManager =
                (NotificationManager) MyFirebaseMessagingService.this.getSystemService(Context.NOTIFICATION_SERVICE);

// Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(channelId,
                    "Notification channel",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(1, notificationBuilder.build());
    }
}
