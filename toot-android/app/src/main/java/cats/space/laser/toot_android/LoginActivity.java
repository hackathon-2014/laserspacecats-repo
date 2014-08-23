package cats.space.laser.toot_android;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import cats.space.laser.toot_android.util.Util;

/**
 * Created by ryanmoore on 8/23/14.
 */
public class LoginActivity extends Activity {

    private Context context;
    private GoogleCloudMessaging gcm;
    private String regId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = getApplicationContext();

        // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        if (Util.checkPlayServices(this)) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regId = Util.getRegistrationId(context);

            if (regId.isEmpty()) {
                registerInBackground();
            } else {
                startLogin();
            }

        } else {
            Log.i("Cat", "No valid Google Play Services APK found.");
        }
    }

    private void registerInBackground() {
        new AsyncTask() {

            @Override
            protected void onPostExecute(Object result) {
                startLogin();
            }

            @Override
            protected Object doInBackground(Object[] params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcm.register(Constants.GCM_SENDER_ID);
                    msg = "Device registered, registration ID=" + regId;

                    // Persist the regID - no need to register again.
                    Log.e("RegId", regId);
                    Util.storeRegistrationId(context, regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }
        }.execute(null, null, null);
    }

    private void startLogin() {
    }

}
