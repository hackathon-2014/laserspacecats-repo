package cats.space.laser.toot_android.application;

import android.app.Application;
import android.content.Context;

import cats.space.laser.toot_android.util.SharedPreferencesUtil;

/**
 * Created by Whitney Champion on 3/19/14.
 */
public class TootApplication extends Application {

    private static TootApplication application;
    private static Context context;

    public void onCreate(){
        super.onCreate();

        application = this;

        // Assign the context to the Application Scope
        context = getApplicationContext();

        // Instantiate the SharedPreferencesUtil Singleton
        SharedPreferencesUtil.getInstance();

    }

    public static Context getAppContext() {
        return TootApplication.context;
    }

    public static TootApplication getApplication() {
        if (application == null) {
            throw new IllegalStateException("Application not initialized");
        }
        return application;
    }


}