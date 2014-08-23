package cats.space.laser.toot_android.api;

import android.content.Context;

import cats.space.laser.toot_android.listener.AsyncTaskCompleteListener;

/**
 * Created by ryanmoore on 8/23/14.
 */
public interface TootService {

    void sendTootOTWAsync(String fromId, String toId, Context context, AsyncTaskCompleteListener listener) throws ApiException;
    void sendTootHereAsync(String fromId, String toId, Context context, AsyncTaskCompleteListener listener) throws ApiException;
}
