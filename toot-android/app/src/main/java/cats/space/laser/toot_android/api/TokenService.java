package cats.space.laser.toot_android.api;

import android.content.Context;

import com.sparc.stream.Listener.AsyncTaskCompleteListener;
import com.sparc.stream.Model.OAuthTokenResponse;
import com.sparc.stream.Model.SocialUserAuthResponse;
import com.sparc.stream.Model.User;

/**
 * Created by Whitney Champion on 8/23/14.
 */
public interface TokenService {

    void deleteTokenAsynchronous(Context context, String registrationId, AsyncTaskCompleteListener listener) throws ApiException;
    Boolean checkUserTokenExists();
    void obtainTokenAsynchronous(User user, String registrationId, Context context, AsyncTaskCompleteListener listener) throws ApiException;
    void removeAdditionalSocialTokenAsynchronous(User user, String json, Context context, AsyncTaskCompleteListener listener) throws ApiException;
    void obtainAdditionalSocialTokenAsynchronous(SocialUserAuthResponse auth, User user, String json, Context context, AsyncTaskCompleteListener listener) throws ApiException;
    void obtainSocialTokenAsynchronous(SocialUserAuthResponse auth, String registrationId, Context context, AsyncTaskCompleteListener listener) throws ApiException;

}