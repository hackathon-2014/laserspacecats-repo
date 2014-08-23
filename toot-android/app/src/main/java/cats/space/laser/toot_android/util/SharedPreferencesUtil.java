package cats.space.laser.toot_android.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import cats.space.laser.toot_android.application.TootApplication;
import cats.space.laser.toot_android.model.User;

/**
 * Created by Whitney Champion on 8/23/14.
 */
public class SharedPreferencesUtil {

    private static SharedPreferences sharedPreferences;
    private static SharedPreferencesUtil instance = null;

    protected SharedPreferencesUtil() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TootApplication.getAppContext());
    }

    public static SharedPreferencesUtil getInstance() {
        if(instance == null) {
            instance = new SharedPreferencesUtil();
        }

        return instance;
    }

    public static void clearSharedPrefs() {
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.clear();
        sharedPreferencesEditor.commit();
    }

    public static void saveUser(User user) {
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString("username",user.getUsername());
        sharedPreferencesEditor.putString("userId",user.getId());
        sharedPreferencesEditor.putString("password",user.getPassword());
        sharedPreferencesEditor.commit();
    }

    public static void setLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putBoolean("loggedIn",loggedIn);
        sharedPreferencesEditor.commit();
    }

    public static Boolean isLoggedIn() {
        Boolean loggedIn = sharedPreferences.getBoolean("loggedIn",false);
        return loggedIn;
    }

    public static User getUser() {
        if (SharedPreferencesUtil.isLoggedIn()) {
            String username = sharedPreferences.getString("username", null);
            String id = sharedPreferences.getString("userId", null);
            String password = sharedPreferences.getString("password", null);

            User user = new User();
            user.setUsername(username);
            user.setId(id);
            user.setPassword(password);
            return user;

        } else {
            return null;
        }
    }

}
