package cats.space.laser.toot_android.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import cats.space.laser.toot_android.Constants;
import cats.space.laser.toot_android.R;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    Context context;

    private static final String OTW = "I'm outside";
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
                String url = extras.getString("url");

                sendNotification(message, url);
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String message, String url) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
//
//
//        if (message.equals())
//        if(toot.classification === 'arrival') {
//            message.addData('message','I\'m outside');
//        } else if (toot.classification === 'otw') {
//            message.addData('message','I\'m on the way');
//        } else if (toot.classification === 'beer') {
//            message.addData('message','Bring Beer!!');
//
            // build a correct, finalized url
            String finalizedUrl = Constants.API_URL+"/"+message.split(" ")[0];

            // build the pending activity
            Intent actionIntent = new Intent(Intent.ACTION_VIEW);
            actionIntent.setData(Uri.parse(finalizedUrl));
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, actionIntent, 0);

            // build the notification
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setAutoCancel(true)
                            .setLights(0xFF73da66, 1000, 2000)
                            .setContentTitle("Cat Notification")
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(message))
                            .setContentText(message);

            // submit the notification
            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/"+R.raw.sport_air_horn_reverb));
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}