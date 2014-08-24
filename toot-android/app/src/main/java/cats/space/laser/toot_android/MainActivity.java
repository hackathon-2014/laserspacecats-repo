package cats.space.laser.toot_android;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;

import cats.space.laser.toot_android.adapter.UserAdapter;
import cats.space.laser.toot_android.api.ApiException;
import cats.space.laser.toot_android.api.Impl.UserServiceImpl;
import cats.space.laser.toot_android.api.UserService;
import cats.space.laser.toot_android.listener.AsyncTaskCompleteListener;
import cats.space.laser.toot_android.model.User;
import cats.space.laser.toot_android.util.DialogUtil;
import cats.space.laser.toot_android.util.SharedPreferencesUtil;

/**
 * Created by Whitney Champion on 8/23/14.
 */
public class MainActivity extends Activity {

    private Context context;
    private UserAdapter userAdapter;
    private ListView userListView;
    private UserService userService;
    private ImageButton addButton;
    private User[] users;
    private User user;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        user = SharedPreferencesUtil.getUser();
        userService = new UserServiceImpl();
        userListView = (ListView) findViewById(R.id.users);

        ImageButton settings = (ImageButton) findViewById(R.id.settings);
        settings.setOnClickListener(new SettingsOnClickListener());

        ImageButton add = (ImageButton) findViewById(R.id.add);
        add.setOnClickListener(new AddOnClickListener());

        dialog = DialogUtil.getProgressDialog(context,"Loading friends...");

    }

    class AddOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context,AddFriendsActivity.class);

            Bundle bundle = new Bundle();
            bundle.putSerializable("currentFriends", users);
            intent.putExtras(bundle);

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

                            LayoutInflater inflater =
                                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View view = inflater.inflate(R.layout.dialog_logout, null);

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);

                            AlertDialog logoutDialog = builder.create();

                            logoutDialog.setView(view, 0, 0, 0, 0);

                            Button cancelButton = (Button) view.findViewById(R.id.cancel);
                            final Dialog finalDialog = logoutDialog;
                            cancelButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finalDialog.dismiss();
                                }
                            });
                            Button logoutButton = (Button) view.findViewById(R.id.logout);
                            logoutButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // log out
                                    SharedPreferencesUtil.clearSharedPrefs();
                                    SharedPreferencesUtil.setLoggedIn(false);
                                    Intent intent = new Intent(context,LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            });

                            logoutDialog.show();

                            break;
                    }
                }

            });

            Dialog dialog = b.create();
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        }
    }

    private class GetFriendsListener implements AsyncTaskCompleteListener<User[]> {

        @Override
        public void onTaskComplete(User[] result) {

            if (result!=null) {
                // get friends
                users = result;

                // populate adapter and attached it to the list view
                userAdapter = new UserAdapter(context, R.layout.user_row, Arrays.asList(users));

                if (users.length != 0) {
                    userListView.setAdapter(userAdapter);
                } else {
                    userListView.setVisibility(View.GONE);
                    TextView empty = (TextView) findViewById(R.id.empty);
                    empty.setVisibility(View.VISIBLE);
                }

                dialog.hide();
            }

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        dialog.show();
        try {
            userService.getFriendsAsynchronous(user, context, new GetFriendsListener());
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }
}
