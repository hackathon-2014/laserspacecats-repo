package cats.space.laser.toot_android.api.Impl;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import cats.space.laser.toot_android.api.ApiException;
import cats.space.laser.toot_android.api.ApiHelper;
import cats.space.laser.toot_android.api.TootService;
import cats.space.laser.toot_android.listener.AsyncTaskCompleteListener;
import cats.space.laser.toot_android.model.ApiBase;

/**
 * Created by ryanmoore on 8/23/14.
 */
public class TootServiceImpl implements TootService {

    private static final String EXCEPTION_MESSAGE = "An error occurred.";

    @Override
    public void sendTootOTWAsync(String fromId, String id, Context context, AsyncTaskCompleteListener listener) throws ApiException {

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("id", id);
        headers.put("origin", fromId);
        headers.put("classification", "otw");


        String url = ApiHelper.TOOT;
        try {
            ApiHelper.post(url, "", context, ApiBase.class, listener, headers);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }
    }

    @Override
    public void sendTootHereAsync(String fromId, String id, Context context, AsyncTaskCompleteListener listener) throws ApiException {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("id", id);
        headers.put("origin", fromId);
        headers.put("classification", "arrival");

        String url = ApiHelper.TOOT;
        try {
            ApiHelper.post(url, "", context, ApiBase.class, listener, headers);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }
    }

    @Override
    public void sendTootBeerAsync(String fromId, String toId, Context context, AsyncTaskCompleteListener listener) throws ApiException {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("id", toId);
        headers.put("origin", fromId);
        headers.put("classification", "arrival");
        String url = ApiHelper.TOOT_BEER;
        try {
            ApiHelper.post(url, "", context, ApiBase.class, listener, null);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }
    }
}
