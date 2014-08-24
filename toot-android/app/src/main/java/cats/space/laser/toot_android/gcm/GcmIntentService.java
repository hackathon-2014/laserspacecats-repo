package cats.space.laser.toot_android.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.Random;

import cats.space.laser.toot_android.MainActivity;
import cats.space.laser.toot_android.R;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    Context context;

    private static final String OTW = "I'm on the way";
    private static final String ARRIVED = "I'm outside";
    private static final String BRING_BEER = "Bring Beer!!";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        context = this;
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                String message = extras.getString("message");
                String userName = intent.getStringExtra("title");

                sendNotification(message, userName);
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String message, String userName) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);


        Uri stringUri = null;
        if (message.equals(OTW)) {
            stringUri = Uri.parse("android.resource://" + context.getPackageName() + "/"+R.raw.otw);
        } else if (message.equals(ARRIVED)) {
            stringUri = Uri.parse("android.resource://" + context.getPackageName() + "/"+R.raw.sport_air_horn_reverb);
        } else if (message.equals(BRING_BEER)) {
            stringUri = Uri.parse("android.resource://" + context.getPackageName() + "/"+R.raw.homer);
        }
        // build the notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon_small)
                        .setAutoCancel(true)
                        .setLights(0xFF73da66, 1000, 2000)
                        .setContentTitle(userName)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setContentText(message);

        // submit the notification
        mBuilder.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
        mBuilder.setSound(stringUri);
        Notification notification = mBuilder.build();
//        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

        Random r=new Random();
        int notificationInt =r.nextInt(1000000);
        mNotificationManager.notify(notificationInt, mBuilder.build());
    }
}