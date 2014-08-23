package cats.space.laser.toot_android.api.Impl;

import android.content.Context;

import com.sparc.stream.Api.ApiException;
import com.sparc.stream.Api.ApiHelper;
import com.sparc.stream.Api.TokenService;
import com.sparc.stream.Common.Constants;
import com.sparc.stream.Listener.AsyncTaskCompleteListener;
import com.sparc.stream.Model.ApiBase;
import com.sparc.stream.Model.DeleteTokenResponse;
import com.sparc.stream.Model.OAuthTokenResponse;
import com.sparc.stream.Model.SocialTokenResponse;
import com.sparc.stream.Model.SocialUser;
import com.sparc.stream.Model.SocialUserAuthResponse;
import com.sparc.stream.Model.User;
import com.sparc.stream.Model.UserAuthResponse;
import com.sparc.stream.Utils.ApiResponseUtil;
import com.sparc.stream.Utils.SharedPreferencesUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Whitney Champion on 8/23/14.
 */
public class TokenServiceImpl implements TokenService {

    private static final String EXCEPTION_MESSAGE = "An error occurred.";

    @Override
    public void deleteTokenAsynchronous(Context context, String registrationId, AsyncTaskCompleteListener listener)
            throws ApiException {

        //get token, create headers
        OAuthTokenResponse oAuthTokenResponse = SharedPreferencesUtil.getStreamOAuthToken();
        Map<String, String> extraHeaderParameters = new HashMap<String, String>();
        extraHeaderParameters.put("Authorization", "Bearer " + oAuthTokenResponse.getAccessToken());
        if (!registrationId.isEmpty()) {
            extraHeaderParameters.put("ANDROID_DEVICE_TOKEN", registrationId);
        }

        //try to make call
        String url = ApiHelper.LOGOUT;
        try {
            ApiHelper.delete(url, context, DeleteTokenResponse.class, listener, extraHeaderParameters);
        } catch (Exception e) {
            throw new ApiException(e.getMessage(), null);
        }
    }

    @Override
    public Boolean checkUserTokenExists(){
        return SharedPreferencesUtil.getStreamOAuthToken() != null;
    }

    @Override
    public void obtainAdditionalSocialTokenAsynchronous(SocialUserAuthResponse auth, User user, String json, Context context, AsyncTaskCompleteListener listener) throws ApiException {

        //get our wrapped objects, null check
        SocialTokenResponse socialTokenResponse = auth.getSocialTokenResponse();

        if (socialTokenResponse != null) {

            //create headers
            Map<String, String> extraHeaderParameters = new HashMap<String, String>();
            extraHeaderParameters.put(Constants.GRANT_TYPE_KEY, Constants.SOCIAL_GRANT_TYPE);
            extraHeaderParameters.put("social_provider_token", socialTokenResponse.getToken());

            if (socialTokenResponse.getTokenSecret()!=null) {
                extraHeaderParameters.put("social_provider_token_secret", socialTokenResponse.getTokenSecret());
            }

            //try to make call
            String url = ApiHelper.ADD_SOCIAL;
            try {
                ApiHelper.put(url+"/"+user.getId(), json, context, UserAuthResponse.class, listener, extraHeaderParameters);
            } catch (Exception e) {
                throw new ApiException(EXCEPTION_MESSAGE, null);
            }
        } else {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }
    }

    @Override
    public void removeAdditionalSocialTokenAsynchronous(User user, String json, Context context, AsyncTaskCompleteListener listener) throws ApiException {


        //create headers
        Map<String, String> extraHeaderParameters = new HashMap<String, String>();
        extraHeaderParameters.put(Constants.GRANT_TYPE_KEY, Constants.SOCIAL_GRANT_TYPE);

        //try to make call
        String url = ApiHelper.REMOVE_SOCIAL;
        try {
            ApiHelper.put(url+"/"+user.getId(), json, context, UserAuthResponse.class, listener, extraHeaderParameters);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }

    }

    @Override
    public void obtainTokenAsynchronous(User user, String registrationId, Context context, AsyncTaskCompleteListener listener)
            throws ApiException {

        //create headers
        Map<String, String> extraHeaderParameters = new HashMap<String, String>();
        extraHeaderParameters.put(Constants.GRANT_TYPE_KEY, Constants.PASSWORD_GRANT_TYPE);
        extraHeaderParameters.put("username", user.getUsername());
        extraHeaderParameters.put("password", user.getPassword());
        if (!registrationId.isEmpty()) {
            extraHeaderParameters.put("ANDROID_DEVICE_TOKEN", registrationId);
        }

        //try to make call
        String url = ApiHelper.AUTH;
        try {
            ApiHelper.post(url, null, context, UserAuthResponse.class, listener, extraHeaderParameters);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }
    }

    @Override
    public void obtainSocialTokenAsynchronous(SocialUserAuthResponse auth, String registrationId, Context context, AsyncTaskCompleteListener listener) throws ApiException {

        //get our wrapped objects, null check
        SocialUser socialUser = auth.getSocialUser();
        SocialTokenResponse socialTokenResponse = auth.getSocialTokenResponse();

        if (socialUser != null && socialTokenResponse != null) {

            //create headers
            Map<String, String> extraHeaderParameters = new HashMap<String, String>();
            extraHeaderParameters.put(Constants.GRANT_TYPE_KEY, Constants.SOCIAL_GRANT_TYPE);
            extraHeaderParameters.put("social_provider", socialTokenResponse.getProviderName());
            extraHeaderParameters.put("social_provider_token", socialTokenResponse.getToken());
            extraHeaderParameters.put("social_provider_token_secret", socialTokenResponse.getTokenSecret());
            extraHeaderParameters.put("social_id", socialUser.getSocial_id());
            if (!registrationId.isEmpty()) {
                extraHeaderParameters.put("ANDROID_DEVICE_TOKEN", registrationId);
            }

            //try to make call
            String url = ApiHelper.AUTH;
            try {
                ApiHelper.post(url, null, context, UserAuthResponse.class, listener, extraHeaderParameters);
            } catch (Exception e) {
                throw new ApiException(EXCEPTION_MESSAGE, null);
            }
        } else {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }
    }

    @Override
    public OAuthTokenResponse obtainToken(User user, Context context) throws ApiException {
        return null;
    }

    @Override
    public OAuthTokenResponse refreshToken(Context context)
            throws ApiException {

        //get current token, create headers
        Map<String, String> extraHeaderParameters = new HashMap<String, String>();
        OAuthTokenResponse expiredToken = SharedPreferencesUtil.getStreamOAuthToken();
        extraHeaderParameters.put(Constants.GRANT_TYPE_KEY, "refresh_token");
        extraHeaderParameters.put("refresh_token", expiredToken.getRefreshToken());

        //try to make call
        String url = ApiHelper.AUTH;
        ApiBase result;
        try {
            result = (ApiBase) ApiHelper.post(url, (String)null, context, OAuthTokenResponse.class,
                    extraHeaderParameters);
        } catch (Exception e) {
            throw new ApiException(e.getMessage(), null);
        }

        //do error handling and type checking in util method
        OAuthTokenResponse newOauthToken =
                (OAuthTokenResponse) ApiResponseUtil.parseResponse(result, OAuthTokenResponse.class);

        //store new token, return
        SharedPreferencesUtil.saveStreamOAuthToken(newOauthToken);

        return newOauthToken;
    }

}
