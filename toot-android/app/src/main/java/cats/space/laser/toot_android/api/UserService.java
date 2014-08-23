package cats.space.laser.toot_android

import android.content.Context;


import java.io.File;

/**
 * Created by Whitney Champion on 8/23/14.
 */
public interface UserService {

    void createUserAsynchronous(User user, Context context, AsyncTaskCompleteListener listener)
            throws ApiException;

    void updateDeviceTokenAsynchronous(User user, String registrationId, Context context, AsyncTaskCompleteListener listener)
            throws ApiException;

    void updateUserSettingsAsynchronous(User user, User userToUpdate, Context context, AsyncTaskCompleteListener listener)
            throws ApiException;

    void updateUserAsynchronous(User user, User userToUpdate, Context context, AsyncTaskCompleteListener listener)
            throws ApiException;

    void uploadProfilePicAsynchronous(User userToUpdate, File profilePic, Context context, AsyncTaskCompleteListener listener)
            throws ApiException;

    void addUserPasswordAsynchronous(NewPassword password, User userToUpdate, Context context, AsyncTaskCompleteListener listener)
            throws ApiException;

    void userNameAvailabilityCheckAsynchronous(String userName, Context context,
                                       AsyncTaskCompleteListener listener) throws ApiException;

    void findUserById(String userId, Context context,
                      AsyncTaskCompleteListener listener) throws ApiException;

    void createSocialUserAsynchronous(SocialUserAuthResponse socialUserAuthResponse, Context context,
                                      AsyncTaskCompleteListener listener) throws ApiException;

    void getPopularUsers(Integer limit, Integer offset, Context context, AsyncTaskCompleteListener listener)
            throws ApiException;

    void getSocialUsers(User user, String json, Context context, AsyncTaskCompleteListener listener)
            throws ApiException;

    void getSubscribers(User user, Integer offset, Integer limit, Context context, AsyncTaskCompleteListener listener)
            throws ApiException;

    void getSubscriptions(User user, Integer offset, Integer limit, Context context, AsyncTaskCompleteListener listener)
            throws ApiException;

    void findUserByUsername(User user, Context context, AsyncTaskCompleteListener listener)
            throws ApiException;

    void forgotPasswordAsynchronous(String emailAddress, Context context, AsyncTaskCompleteListener listener)
            throws ApiException;

    void subscribeToUserAsynchronous(String userId, Context context, AsyncTaskCompleteListener listener)
            throws ApiException;

    void unsubscribeToUserAsynchronous(String userId, Context context, AsyncTaskCompleteListener listener)
            throws ApiException;

}
