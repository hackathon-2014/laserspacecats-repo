package cats.space.laser.toot_android.api.Impl;

import android.content.Context;

import cats.space.laser.toot_android.api.ApiException;
import cats.space.laser.toot_android.api.ApiHelper;
import cats.space.laser.toot_android.api.TootService;
import cats.space.laser.toot_android.listener.AsyncTaskCompleteListener;
import cats.space.laser.toot_android.model.ApiBase;
import cats.space.laser.toot_android.model.TootRequest;
import cats.space.laser.toot_android.util.GsonUtil;

/**
 * Created by ryanmoore on 8/23/14.
 */
public class TootServiceImpl implements TootService {

    private static final String EXCEPTION_MESSAGE = "An error occurred.";

    @Override
    public void sendTootOTWAsync(String fromId, String toId, Context context, AsyncTaskCompleteListener listener) throws ApiException {

        TootRequest tootRequest = new TootRequest();
        tootRequest.setDestination(toId);
        tootRequest.setOrigin(fromId);

        String tootJson = GsonUtil.toJson(tootRequest);

        String url = ApiHelper.TOOT_OTW;
        try {
            ApiHelper.post(url, tootJson, context, ApiBase.class, listener, null);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }
    }

    @Override
    public void sendTootHereAsync(String fromId, String toId, Context context, AsyncTaskCompleteListener listener) throws ApiException {
        TootRequest tootRequest = new TootRequest();
        tootRequest.setDestination(toId);
        tootRequest.setOrigin(fromId);

        String tootJson = GsonUtil.toJson(tootRequest);

        String url = ApiHelper.TOOT_HERE;
        try {
            ApiHelper.post(url, tootJson, context, ApiBase.class, listener, null);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }
    }
}
