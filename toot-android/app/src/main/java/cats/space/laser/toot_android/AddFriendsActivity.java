package cats.space.laser.toot_android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;

import java.util.Arrays;

import cats.space.laser.toot_android.adapter.AddFriendAdapter;
import cats.space.laser.toot_android.api.ApiException;
import cats.space.laser.toot_android.api.Impl.UserServiceImpl;
import cats.space.laser.toot_android.api.UserService;
import cats.space.laser.toot_android.listener.AsyncTaskCompleteListener;
import cats.space.laser.toot_android.model.ApiBase;
import cats.space.laser.toot_android.model.User;
import cats.space.laser.toot_android.model.UsersList;
import cats.space.laser.toot_android.util.ApiResponseUtil;
import cats.space.laser.toot_android.util.DialogUtil;
import cats.space.laser.toot_android.util.SharedPreferencesUtil;

/**
 * Created by ryanmoore on 8/23/14.
 */
public class AddFriendsActivity extends Activity {


    private Context context;
    private AddFriendAdapter userAdapter;
    private ListView userListView;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        context = this;

        User user = SharedPreferencesUtil.getUser();
        UserService userService = new UserServiceImpl();
        userListView = (ListView) findViewById(R.id.users);

        dialog = DialogUtil.getProgressDialog(context,"Getting users...");
        dialog.show();
        try {
            userService.getUsersAsynchronous(user, context, new GetUsersListener());
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }
    private class GetUsersListener implements AsyncTaskCompleteListener<ApiBase> {

        @Override
        public void onTaskComplete(ApiBase result) {

            // get friends
            UsersList users;
            try {
                users = (UsersList) ApiResponseUtil.parseResponse(result, UsersList.class);
                dialog.hide();
            } catch (ApiException e) {
                return;
            }

            // populate adapter and attached it to the list view
            userAdapter = new AddFriendAdapter(context, R.layout.add_user_row, Arrays.asList(users.getUsers()));

            if (users.getUsers().length!=0) {
                userListView.setAdapter(userAdapter);
            }
        }

    }
}
