package cats.space.laser.toot_android;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

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
    private ImageButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        addButton = (ImageButton) findViewById(R.id.add);
        addButton.setOnClickListener(new AddFriendOnClickListener());

        User user = SharedPreferencesUtil.getUser();
        UserService userService = new UserServiceImpl();
        userListView = (ListView) findViewById(R.id.users);

        ImageButton settings = (ImageButton) findViewById(R.id.settings);
        settings.setOnClickListener(new SettingsOnClickListener());

        ImageButton add = (ImageButton) findViewById(R.id.add);
        add.setOnClickListener(new AddOnClickListener());

        try {
            userService.getFriendsAsynchronous(user, context, new GetUsersListener());
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    class AddOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context,AddFriendsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }

    class SettingsOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            AlertDialog.Builder b = new AlertDialog.Builder(context);
            b.setTitle("Settings");
            String[] types = {"Logout"};
            b.setItems(types, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                    switch(which){
                        case 0:
                            // log out
                            SharedPreferencesUtil.clearSharedPrefs();
                            SharedPreferencesUtil.setLoggedIn(false);
                            Intent intent = new Intent(context,LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            break;
                    }
                }

            });

            Dialog dialog = b.create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        }
    }

    private class GetUsersListener implements AsyncTaskCompleteListener<ApiBase> {

        @Override
        public void onTaskComplete(ApiBase result) {

            if (result!=null) {
                // get friends
                UsersList users;
                try {
                    users = (UsersList) ApiResponseUtil.parseResponse(result, UsersList.class);
                } catch (ApiException e) {
                    return;
                }

                // populate adapter and attached it to the list view
                userAdapter = new UserAdapter(context, R.layout.user_row, Arrays.asList(users.getUsers()));

                if (users.getUsers().length != 0) {
                    userListView.setAdapter(userAdapter);
                }
            }

        }

    }

    private class AddFriendOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, AddFriendsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
