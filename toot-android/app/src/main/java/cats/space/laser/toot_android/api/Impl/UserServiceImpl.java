package cats.space.laser.toot_android.api.Impl;

import android.content.Context;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cats.space.laser.toot_android.api.ApiException;
import cats.space.laser.toot_android.api.ApiHelper;
import cats.space.laser.toot_android.api.UserService;
import cats.space.laser.toot_android.listener.AsyncTaskCompleteListener;
import cats.space.laser.toot_android.model.User;
import cats.space.laser.toot_android.model.UserResponse;
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
        String url = ApiHelper.GET_USER;
        try {
            ApiHelper.put(url, userJson, context, UserResponse.class, null);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }
    }

    @Override
    public void getUsersAsynchronous(User user, Context context, AsyncTaskCompleteListener listener)
            throws ApiException {

        //try to make call
        String url = ApiHelper.GET_USER;
        try {
            ApiHelper.get(url, context, UsersList.class, null);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }
    }
}
