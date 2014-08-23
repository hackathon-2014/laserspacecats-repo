package cats.space.laser.toot_android.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cats.space.laser.toot_android.R;
import cats.space.laser.toot_android.api.ApiException;
import cats.space.laser.toot_android.api.Impl.UserServiceImpl;
import cats.space.laser.toot_android.api.UserService;
import cats.space.laser.toot_android.listener.AsyncTaskCompleteListener;
import cats.space.laser.toot_android.model.AddFriend;
import cats.space.laser.toot_android.model.ApiBase;
import cats.space.laser.toot_android.model.User;
import cats.space.laser.toot_android.util.ApiResponseUtil;
import cats.space.laser.toot_android.util.DialogUtil;
import cats.space.laser.toot_android.util.SharedPreferencesUtil;

/**
 * Created by Whitney Champion on 8/23/14.
 */
public class AddFriendAdapter extends ArrayAdapter<User> {

    private UserService userService = new UserServiceImpl();
    int resource;
    Context context;
    List<User> data;

    public AddFriendAdapter(Context context, int resource, List<User> items) {
        super(context, resource, items);
        this.resource = resource;
        this.context = context;
        this.data = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        UserHolder holder = null;
        View row = convertView;

        User user = data.get(position);
        Typeface type = Typeface.createFromAsset(context.getAssets(), "century_gothic.TTF");

        if ( row == null )
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(resource, parent, false);

            FrameLayout hornlayout = (FrameLayout) row.findViewById(R.id.button_horn);
            FrameLayout carlayout = (FrameLayout) row.findViewById(R.id.button_car);
            FrameLayout beerlayout = (FrameLayout) row.findViewById(R.id.button_beer);

            hornlayout.setVisibility(View.GONE);
            carlayout.setVisibility(View.GONE);
            beerlayout.setVisibility(View.GONE);

            holder = new UserHolder();
            holder.username = (TextView) row.findViewById(R.id.username);

            row.setTag(holder);

        } else {
            holder = (UserHolder)row.getTag();
        }

        holder.dialog = DialogUtil.getProgressDialog(context, "Adding user...");

        holder.username.setText(user.getUsername());
        holder.username.setTypeface(type);
        holder.username.setOnClickListener(new UsernameOnClickListener(user.get_id(), holder.dialog));

        return row;
    }

    static class UserHolder {
        TextView username;
        ProgressDialog dialog;
    }

    public class UsernameOnClickListener implements View.OnClickListener {

        String id;
        ProgressDialog dialog;

        public UsernameOnClickListener(String id, ProgressDialog dialog) {
            this.dialog = dialog;
            this.id = id;
        }

        @Override
        public void onClick(View view) {

            AddFriend user = new AddFriend();

            List<String> friends = new ArrayList<String>();
            friends.add(id);

            String[] newFriends = new String[friends.size()];
            friends.toArray(newFriends);

            user.setFriends(newFriends);
            user.setId(SharedPreferencesUtil.getUser().get_id());

            dialog.show();
            try {
                userService.addFriendsAsynchronous(user, context, new AddFriendsResponseListener(dialog));
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    private class AddFriendsResponseListener implements AsyncTaskCompleteListener<ApiBase> {

        ProgressDialog dialog;

        public AddFriendsResponseListener(ProgressDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void onTaskComplete(ApiBase result) {
            dialog.hide();
            User response;
            try {
                response = (User) ApiResponseUtil.parseResponse(result, User.class);
                if (response!=null) {
                    Toast.makeText(context,R.string.add_success,Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
                return;
            }
        }
    }
}