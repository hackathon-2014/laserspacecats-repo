package cats.space.laser.toot_android.api;

import android.content.Context;


import java.io.File;

import cats.space.laser.toot_android.api.ApiException;
import cats.space.laser.toot_android.listener.AsyncTaskCompleteListener;
import cats.space.laser.toot_android.model.User;

/**
 * Created by Whitney Champion on 8/23/14.
 */
public interface UserService {

    void createUserAsynchronous(User user, Context context, AsyncTaskCompleteListener listener)
            throws ApiException;

    void addFriendsAsynchronous(User user, Context context, AsyncTaskCompleteListener listener)
            throws ApiException;

    void getUsersAsynchronous(User user, Context context, AsyncTaskCompleteListener listener)
            throws ApiException;
}
