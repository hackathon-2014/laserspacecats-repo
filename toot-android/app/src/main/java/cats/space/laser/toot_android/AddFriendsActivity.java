package cats.space.laser.toot_android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

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
    private Object[] existingUsers;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        context = this;

        Intent intent=this.getIntent();
        Bundle bundle=intent.getExtras();

        existingUsers = (Object[]) bundle.getSerializable("currentFriends");
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

            List<User> filteredUsers = new ArrayList<User>();
            for (User user: users.getUsers()) {
                boolean found = false;

                for(Object existingFriend:existingUsers) {
                    if (existingFriend instanceof User) {
                        if (user.get_id().equals(((User)existingFriend).get_id())) {
                            found = true;
                        }
                    }
                }

                if (!found) {
                    filteredUsers.add(user);
                }
            }

            // populate adapter and attached it to the list view
            userAdapter = new AddFriendAdapter(context, R.layout.add_user_row, filteredUsers);

            if (users.getUsers().length!=0) {
                userListView.setAdapter(userAdapter);
                userListView.setVisibility(View.VISIBLE);
                TextView empty = (TextView) findViewById(R.id.empty);
                empty.setVisibility(View.GONE);
            } else {
                userListView.setVisibility(View.GONE);
                TextView empty = (TextView) findViewById(R.id.empty);
                empty.setVisibility(View.VISIBLE);
            }
        }

    }
}
