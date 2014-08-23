package cats.space.laser.toot_android.api.Impl;

import android.content.Context;

import cats.space.laser.toot_android.api.ApiException;
import cats.space.laser.toot_android.api.ApiHelper;
import cats.space.laser.toot_android.api.UserService;
import cats.space.laser.toot_android.listener.AsyncTaskCompleteListener;
import cats.space.laser.toot_android.model.AddFriend;
import cats.space.laser.toot_android.model.RemoveFriend;
import cats.space.laser.toot_android.model.User;
import cats.space.laser.toot_android.model.UsersList;
import cats.space.laser.toot_android.util.GsonUtil;

/**
 * Created by Whitney Champion on 8/23/14.
 */
public class UserServiceImpl implements UserService {

    private static final String EXCEPTION_MESSAGE = "An error occurred.";

    @Override
    public void createUserAsynchronous(User user, Context context, AsyncTaskCompleteListener listener)
            throws ApiException {

        //first, transform User to json
        String userJson = GsonUtil.toJson(user);

        //try to make call
        String url = ApiHelper.CREATE_USER;
        try {
            ApiHelper.post(url, userJson, context, User.class, listener, null);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }
    }

    @Override
    public void loginAsynchronous(User user, Context context, AsyncTaskCompleteListener listener)
            throws ApiException {

        //first, transform User to json
        String userJson = GsonUtil.toJson(user);

        //try to make call
        String url = ApiHelper.LOGIN;
        try {
            ApiHelper.post(url, userJson, context, User.class, listener, null);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }
    }

    @Override
    public void checkUserAsynchronous(User user, Context context, AsyncTaskCompleteListener listener)
            throws ApiException {

        //try to make call
        String url = ApiHelper.GET_USER+"/"+user.getUsername()+"/exists";
        try {
            ApiHelper.get(url, context, Boolean.class, listener, null);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }
    }

    @Override
    public void getFriendsAsynchronous(User user, Context context, AsyncTaskCompleteListener listener)
            throws ApiException {

        //try to make call
        String url = ApiHelper.GET_USER+"/"+user.getUsername()+"/friends";
        try {
            ApiHelper.get(url, context, User[].class, listener, null);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }
    }

    @Override
    public void getUsersAsynchronous(User user, Context context, AsyncTaskCompleteListener listener)
            throws ApiException {

        //try to make call
        String url = ApiHelper.GET_USERS;
        try {
            ApiHelper.get(url, context, UsersList.class, listener, null);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }
    }

    @Override
    public void addFriendsAsynchronous(AddFriend user, Context context, AsyncTaskCompleteListener listener)
            throws ApiException {

        //first, transform User to json
        String userJson = GsonUtil.toJson(user);

        //try to make call
        String url = ApiHelper.ADD_FRIEND;
        try {
            ApiHelper.post(url, userJson, context, User.class, listener, null);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }
    }

    @Override
    public void removeFriendsAsynchronous(RemoveFriend user, Context context, AsyncTaskCompleteListener listener)
            throws ApiException {

        //first, transform User to json
        String userJson = GsonUtil.toJson(user);

        //try to make call
        String url = ApiHelper.REMOVE_FRIEND;
        try {
            ApiHelper.post(url, userJson, context, User.class, listener, null);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }
    }
}
