package cats.space.laser.toot_android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;

import cats.space.laser.toot_android.adapter.UserAdapter;
import cats.space.laser.toot_android.api.ApiException;
import cats.space.laser.toot_android.api.Impl.UserServiceImpl;
import cats.space.laser.toot_android.api.UserService;
import cats.space.laser.toot_android.listener.AsyncTaskCompleteListener;
import cats.space.laser.toot_android.model.ApiBase;
import cats.space.laser.toot_android.model.User;
import cats.space.laser.toot_android.model.UsersList;
import cats.space.laser.toot_android.util.ApiResponseUtil;
import cats.space.laser.toot_android.util.SharedPreferencesUtil;

/**
 * Created by Whitney Champion on 8/23/14.
 */
public class MainActivity extends Activity {

    private Context context;
    private UserAdapter userAdapter;
    private ListView userListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        User user = SharedPreferencesUtil.getUser();
        UserService userService = new UserServiceImpl();
        userListView = (ListView) findViewById(R.id.users);

        try {
            userService.getUsersAsynchronous(user, context, new GetUsersListener());
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }
    private class GetUsersListener implements AsyncTaskCompleteListener<ApiBase> {

        @Override
        public void onTaskComplete(ApiBase result) {

            // get subscriptions
            UsersList users;
            try {
                users = (UsersList) ApiResponseUtil.parseResponse(result, UsersList.class);
            } catch (ApiException e) {
                return;
            }

            // populate adapter and attached it to the list view
            userAdapter = new UserAdapter(context, R.layout.user_row, Arrays.asList(users.getUsers()));

            if (users.getUsers().length!=0) {
                userListView.setAdapter(userAdapter);
            }

        }

    }




}
