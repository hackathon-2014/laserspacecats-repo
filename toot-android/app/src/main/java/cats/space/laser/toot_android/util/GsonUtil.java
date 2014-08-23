package cats.space.laser.toot_android.util;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Whitney Champion on 8/23/14.
 */
public class GsonUtil {

    public static String toJson(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }
}
