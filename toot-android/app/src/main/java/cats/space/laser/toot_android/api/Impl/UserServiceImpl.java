package com.sparc.stream.Api.Impl;

import android.content.Context;

import com.sparc.stream.Api.ApiException;
import com.sparc.stream.Api.ApiHelper;
import com.sparc.stream.Api.UserService;
import com.sparc.stream.Listener.AsyncTaskCompleteListener;
import com.sparc.stream.Model.NewPassword;
import com.sparc.stream.Model.OAuthTokenResponse;
import com.sparc.stream.Model.SocialTokenResponse;
import com.sparc.stream.Model.SocialUserAuthResponse;
import com.sparc.stream.Model.SubscribeesList;
import com.sparc.stream.Model.SubscribersList;
import com.sparc.stream.Model.SubscribeToUserResponse;
import com.sparc.stream.Model.UnsubscribeToUserResponse;
import com.sparc.stream.Model.UploadPhotoResponse;
import com.sparc.stream.Model.User;
import com.sparc.stream.Model.UserAuthResponse;
import com.sparc.stream.Model.UserList;
import com.sparc.stream.Model.UserUpdateResponse;
import com.sparc.stream.Model.UsersList;
import com.sparc.stream.Utils.GsonUtil;
import com.sparc.stream.Utils.SharedPreferencesUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ryanmoore on 4/22/14.
 */
public class UserServiceImpl implements UserService {

    private static final String EXCEPTION_MESSAGE = "An error occurred.";

    @Override
    public void createUserAsynchronous(User user, Context context, AsyncTaskCompleteListener listener)
            throws ApiException {

        //first, transform User to json
        String userJson = GsonUtil.toJson(user);

        //create headers
        Map<String, String> extraHeaderParameters = new HashMap<String, String>();
        extraHeaderParameters.put("grant_type", "password");
        extraHeaderParameters.put("username", user.getUsername());
        extraHeaderParameters.put("password", user.getPassword());

        //try to make call
        String url = ApiHelper.GET_USER;
        try {
            ApiHelper.post(url, userJson, context, UserAuthResponse.class, listener, extraHeaderParameters);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }
    }

    @Override
    public void updateDeviceTokenAsynchronous(User user, String registrationId, Context context, AsyncTaskCompleteListener listener) throws ApiException {

        // get token for header
        OAuthTokenResponse token = SharedPreferencesUtil.getStreamOAuthToken();

        //create headers
        Map<String, String> extraHeaderParameters = new HashMap<String, String>();
        extraHeaderParameters.put("Authorization", "Bearer " + token.getAccessToken());

        //try to make call
        String url = ApiHelper.GET_USER;

        // device token json
        String json = "{\"androidDeviceTokens\":[\""+registrationId+"\"]}";

        try {
            ApiHelper.put(url+"/"+user.getId(), json, context, UserUpdateResponse.class, listener, extraHeaderParameters);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }
    }

    @Override
    public void updateUserSettingsAsynchronous(User user, User userToUpdate, Context context, AsyncTaskCompleteListener listener)
            throws ApiException {

        // get token for header
        OAuthTokenResponse token = SharedPreferencesUtil.getStreamOAuthToken();

        //first, transform User to json
        String userJson = GsonUtil.toJson(user);

        //create headers
        Map<String, String> extraHeaderParameters = new HashMap<String, String>();
        extraHeaderParameters.put("Authorization", "Bearer " + token.getAccessToken());

        //get user from shared prefs
        User userUpdated = SharedPreferencesUtil.getUser();

        //try to make call
        String url = ApiHelper.GET_USER;
        try {
            ApiHelper.put(url+"/"+userToUpdate.getId(), userJson, context, UserUpdateResponse.class, listener, extraHeaderParameters);

            userUpdated.setSendEmailNotifications(user.getSendEmailNotifications());
            userUpdated.setSendPushNotifications(user.getSendPushNotifications());

            SharedPreferencesUtil.saveUser(userUpdated);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }
    }

    @Override
    public void updateUserAsynchronous(User user, User userToUpdate, Context context, AsyncTaskCompleteListener listener)
            throws ApiException {

        // get token for header
        OAuthTokenResponse token = SharedPreferencesUtil.getStreamOAuthToken();

        //first, transform User to json
        String userJson = GsonUtil.toJson(user);

        //create headers
        Map<String, String> extraHeaderParameters = new HashMap<String, String>();
        extraHeaderParameters.put("Authorization", "Bearer " + token.getAccessToken());

        //get user from shared prefs
        User userUpdated = SharedPreferencesUtil.getUser();

        //try to make call
        String url = ApiHelper.GET_USER;
        try {
            ApiHelper.put(url + "/" + userToUpdate.getId(), userJson, context, UserUpdateResponse.class, listener, extraHeaderParameters);

            userUpdated.setUsername(user.getUsername());
            userUpdated.setAboutMe(user.getAboutMe());
            userUpdated.setLocation(user.getLocation());
            userUpdated.setEmail(user.getEmail());
            userUpdated.setProfilePicUrl(user.getProfilePicUrl());

        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }
    }

    @Override
    public void uploadProfilePicAsynchronous(User userToUpdate, File profilePic, Context context, AsyncTaskCompleteListener listener)
            throws ApiException {

        // get token for header
        OAuthTokenResponse token = SharedPreferencesUtil.getStreamOAuthToken();

        //create headers
        Map<String, String> extraHeaderParameters = new HashMap<String, String>();
        extraHeaderParameters.put("Authorization", "Bearer " + token.getAccessToken());

        //try to make call
        String url = ApiHelper.GET_USER;
        try {
            ApiHelper.multipartPut(url + "/photo/" + userToUpdate.getId(), profilePic, context, UploadPhotoResponse.class, listener, extraHeaderParameters);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }
    }

    @Override
    public void addUserPasswordAsynchronous(NewPassword password, User userToUpdate, Context context, AsyncTaskCompleteListener listener)
            throws ApiException {

        // get token for header
        OAuthTokenResponse token = SharedPreferencesUtil.getStreamOAuthToken();

        //first, transform Password to json
        String passwordJson = GsonUtil.toJson(password);

        //create headers
        Map<String, String> extraHeaderParameters = new HashMap<String, String>();
        extraHeaderParameters.put("Authorization", "Bearer " + token.getAccessToken());

        //try to make call
        String url = ApiHelper.GET_USER;
        try {
            ApiHelper.put(url + "/" + userToUpdate.getId() + "/password", passwordJson, context, UserUpdateResponse.class, listener, extraHeaderParameters);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }
    }

    @Override
    public void getSocialUsers(User user, String json, Context context, AsyncTaskCompleteListener listener)
            throws ApiException {

        // get token for header
        OAuthTokenResponse token = SharedPreferencesUtil.getStreamOAuthToken();

        //create headers
        Map<String, String> extraHeaderParameters = new HashMap<String, String>();
        extraHeaderParameters.put("Authorization", "Bearer " + token.getAccessToken());

        //try to make call
        String url = ApiHelper.SOCIAL;
        try{
            ApiHelper.post(url, json, context, UsersList.class, listener, extraHeaderParameters);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }

    }

    @Override
    public void getPopularUsers(Integer limit, Integer offset, Context context, AsyncTaskCompleteListener listener)
            throws ApiException {

        // get token for header
        OAuthTokenResponse token = SharedPreferencesUtil.getStreamOAuthToken();

        //create headers
        Map<String, String> extraHeaderParameters = new HashMap<String, String>();
        extraHeaderParameters.put("Authorization", "Bearer " + token.getAccessToken());

        //try to make call
        String url = ApiHelper.GET_USER + "?limit="+limit+"&offset="+offset+"&sortField=subscribedBy&sortOrder=-1";
        try{
            ApiHelper.get(url, context, UsersList.class, listener, extraHeaderParameters);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }

    }

    @Override
    public void getSubscribers(User user, Integer offset, Integer limit, Context context, AsyncTaskCompleteListener listener)
            throws ApiException {

        // get token for header
        OAuthTokenResponse token = SharedPreferencesUtil.getStreamOAuthToken();

        //create headers
        Map<String, String> extraHeaderParameters = new HashMap<String, String>();
        extraHeaderParameters.put("Authorization", "Bearer " + token.getAccessToken());

        //try to make call
        String url = ApiHelper.GET_USER + "/subscribedBy?username=" + user.getUsername() + "&limit=" + limit + "&offset=" + offset;
        try{
            ApiHelper.get(url, context, SubscribersList.class, listener, extraHeaderParameters);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }

    }

    @Override
    public void getSubscriptions(User user, Integer offset, Integer limit, Context context, AsyncTaskCompleteListener listener)
            throws ApiException {

        // get token for header
        OAuthTokenResponse token = SharedPreferencesUtil.getStreamOAuthToken();

        //create headers
        Map<String, String> extraHeaderParameters = new HashMap<String, String>();
        extraHeaderParameters.put("Authorization", "Bearer " + token.getAccessToken());

        //try to make call
        String url = ApiHelper.GET_USER + "/subscribedTo?username=" + user.getUsername() + "&limit=" + limit + "&offset=" + offset;
        try{
            ApiHelper.get(url, context, SubscribeesList.class, listener, extraHeaderParameters);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }

    }

    @Override
    public void findUserByUsername(User user, Context context, AsyncTaskCompleteListener listener)
            throws ApiException {

        // get token for header
        OAuthTokenResponse token = SharedPreferencesUtil.getStreamOAuthToken();

        //create headers
        Map<String, String> extraHeaderParameters = new HashMap<String, String>();
        extraHeaderParameters.put("Authorization", "Bearer " + token.getAccessToken());

        //try to make call
        String url = ApiHelper.GET_USER + "?limit=1&username=" + user.getUsername();
        try{
            ApiHelper.get(url, context, UserList.class, listener, extraHeaderParameters);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }

    }

    @Override
    public void findUserById(String userId, Context context,
                             AsyncTaskCompleteListener listener) throws ApiException {

        // get token for header
        OAuthTokenResponse token = SharedPreferencesUtil.getStreamOAuthToken();

        // create headers
        Map<String, String> extraHeaderParameters = new HashMap<String, String>();
        extraHeaderParameters.put("Authorization", "Bearer " + token.getAccessToken());

        // try to make call
        String url = ApiHelper.GET_USER + "?limit=1&id=" + userId;
        try{
            ApiHelper.get(url, context, UserList.class, listener, extraHeaderParameters);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }

    }

    @Override
    public void forgotPasswordAsynchronous(String email, Context context, AsyncTaskCompleteListener listener)
            throws ApiException {

        String url = ApiHelper.GET_USER + "/" + email + "/forgotpassword";

        try{
            ApiHelper.put(url, null, context, UserList.class, listener, null);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }
    }

    @Override
    public void userNameAvailabilityCheckAsynchronous(String userName, Context context,
                                       AsyncTaskCompleteListener listener) throws ApiException {

        String url = ApiHelper.GET_USER + "/" + userName;
        try{
            ApiHelper.head(url, context, Boolean.class, listener, null);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }
    }

    @Override
    public void createSocialUserAsynchronous(SocialUserAuthResponse socialUserAuthResponse,
                                             Context context, AsyncTaskCompleteListener listener)
            throws ApiException {

        //first, transform SocialUser to json
        SocialTokenResponse token = socialUserAuthResponse.getSocialTokenResponse();
        String userJson = GsonUtil.toJson(socialUserAuthResponse.getSocialUser());

        //create headers
        Map<String, String> extraHeaderParameters = new HashMap<String, String>();
        extraHeaderParameters.put("grant_type", "social");
        extraHeaderParameters.put("social_provider_token", token.getToken());
        extraHeaderParameters.put("social_provider_token_secret", token.getTokenSecret());
        extraHeaderParameters.put("social_provider", token.getProviderName());

        //try to make call
        String url = ApiHelper.GET_USER;
        try {
            ApiHelper.post(url, userJson, context, UserAuthResponse.class, listener, extraHeaderParameters);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }
    }

    @Override
    public void subscribeToUserAsynchronous(String userId, Context context, AsyncTaskCompleteListener listener)
            throws ApiException {

        //get token for header
        OAuthTokenResponse token = SharedPreferencesUtil.getStreamOAuthToken();

        //create headers
        Map<String, String> extraHeaderParameters = new HashMap<String, String>();
        extraHeaderParameters.put("Authorization", "Bearer " + token.getAccessToken());

        //try to make call
        String url = String.format(ApiHelper.GET_USER + "/subscribe/%s", userId);
        try {
            ApiHelper.put(url, null, context, SubscribeToUserResponse.class, listener, extraHeaderParameters);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }
    }

    @Override
    public void unsubscribeToUserAsynchronous(String userId, Context context, AsyncTaskCompleteListener listener)
            throws ApiException {
        //get token for header
        OAuthTokenResponse token = SharedPreferencesUtil.getStreamOAuthToken();

        //create headers
        Map<String, String> extraHeaderParameters = new HashMap<String, String>();
        extraHeaderParameters.put("Authorization", "Bearer " + token.getAccessToken());

        //try to make call
        String url = String.format(ApiHelper.GET_USER + "/unsubscribe/%s", userId);
        try {
            ApiHelper.put(url, null, context, UnsubscribeToUserResponse.class, listener, extraHeaderParameters);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }
    }
}
