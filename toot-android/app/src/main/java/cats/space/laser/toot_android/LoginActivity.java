package cats.space.laser.toot_android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import cats.space.laser.toot_android.api.ApiException;
import cats.space.laser.toot_android.api.Impl.UserServiceImpl;
import cats.space.laser.toot_android.api.UserService;
import cats.space.laser.toot_android.listener.AsyncTaskCompleteListener;
import cats.space.laser.toot_android.model.ApiBase;
import cats.space.laser.toot_android.model.User;
import cats.space.laser.toot_android.util.ApiResponseUtil;
import cats.space.laser.toot_android.util.SharedPreferencesUtil;
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
        Button signIn = (Button) findViewById(R.id.sign_in);
        signIn.setOnClickListener(new LoginOnClickListener());

        // if user already saved, go to main screen
        if (SharedPreferencesUtil.isLoggedIn()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        if (Util.checkPlayServices(this)) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regId = Util.getRegistrationId(context);

            if (regId.isEmpty()) {
                registerInBackground();
            }

        } else {
            Log.i("Cat", "No valid Google Play Services APK found.");
        }
    }

    public class LoginOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            startLogin();
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

        UserService userService = new UserServiceImpl();


        // log in
        try {
            userService.createUserAsynchronous(getUserFromScreen(), context, new LoginResponseListener());
        } catch (ApiException e) {
            e.printStackTrace();
        }

    }

    private User getUserFromScreen() {

        EditText username = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);
        User user = new User();
        user.setUsername(username.getText().toString());
        user.setRegistrationId(regId);
        user.setPassword(password.getText().toString());

        return user;
    }

    private class LoginResponseListener implements AsyncTaskCompleteListener<ApiBase> {

        @Override
        public void onTaskComplete(ApiBase result) {

            User response;
            try {
                response = (User) ApiResponseUtil.parseResponse(result, User.class);
                if (response!=null) {
                    SharedPreferencesUtil.setLoggedIn(true);
                    SharedPreferencesUtil.saveUser(response);
                    Intent intent = new Intent(context,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            } catch (ApiException e) { //something bad happened
            }
        }
    }

}
