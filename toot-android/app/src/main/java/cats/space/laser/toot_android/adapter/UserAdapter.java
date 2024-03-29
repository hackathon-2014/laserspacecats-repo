package cats.space.laser.toot_android.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cats.space.laser.toot_android.MainActivity;
import cats.space.laser.toot_android.R;
import cats.space.laser.toot_android.api.ApiException;
import cats.space.laser.toot_android.api.Impl.TootServiceImpl;
import cats.space.laser.toot_android.api.Impl.UserServiceImpl;
import cats.space.laser.toot_android.api.TootService;
import cats.space.laser.toot_android.api.UserService;
import cats.space.laser.toot_android.listener.AsyncTaskCompleteListener;
import cats.space.laser.toot_android.model.AddFriend;
import cats.space.laser.toot_android.model.ApiBase;
import cats.space.laser.toot_android.model.RemoveFriend;
import cats.space.laser.toot_android.model.User;
import cats.space.laser.toot_android.util.ApiResponseUtil;
import cats.space.laser.toot_android.util.DialogUtil;
import cats.space.laser.toot_android.util.SharedPreferencesUtil;

/**
 * Created by Whitney Champion on 8/23/14.
 */
public class UserAdapter extends ArrayAdapter<User> {

    private TootService tootService = new TootServiceImpl();
    int resource;
    Context context;
    List<User> data;

    public UserAdapter(Context context, int resource, List<User> items) {
        super(context, resource, items);
        this.resource = resource;
        this.context = context;
        this.data = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        UserHolder holder = null;
        View row = convertView;

        final User user = data.get(position);
        Typeface type = Typeface.createFromAsset(context.getAssets(),"century_gothic.TTF");

        if ( row == null )
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(resource, parent, false);

            holder = new UserHolder();
            holder.username = (TextView) row.findViewById(R.id.username);

            holder.toot = (FrameLayout) row.findViewById(R.id.button_horn);
            holder.omw = (FrameLayout) row.findViewById(R.id.button_car);
            holder.beer = (FrameLayout) row.findViewById(R.id.button_beer);

            holder.beerButton = (ImageButton) row.findViewById(R.id.beer_icon);
            holder.omwButton = (ImageButton) row.findViewById(R.id.car_icon);
            holder.tootButton = (ImageButton) row.findViewById(R.id.horn_icon);

            row.setTag(holder);
            row.setTag(R.id.button_car, holder.omw);
            row.setTag(R.id.button_horn, holder.toot);
            row.setTag(R.id.button_beer, holder.beer);
            row.setTag(R.id.beer_icon, holder.beerButton);
            row.setTag(R.id.car_icon, holder.omwButton);
            row.setTag(R.id.horn_icon, holder.tootButton);

        } else {
            holder = (UserHolder)row.getTag();
        }

        holder.dialog = DialogUtil.getProgressDialog(context,"Sending toot...");

        holder.username.setText(user.getUsername());
        holder.username.setTypeface(type);
        holder.username.setOnClickListener(new UsernameOnClickListener(user.getUsername(), holder.dialog));

        holder.toot.setOnClickListener(new TootOnClickListener(user.get_id(), holder.dialog));
        holder.omw.setOnClickListener(new OmwOnClickListener(user.get_id(), holder.dialog));
        holder.beer.setOnClickListener(new BeerOnClickListener(user.get_id(), holder.dialog));

        holder.omwButton.setOnClickListener(new OmwOnClickListener(user.get_id(), holder.dialog));
        holder.tootButton.setOnClickListener(new TootOnClickListener(user.get_id(), holder.dialog));
        holder.beerButton.setOnClickListener(new BeerOnClickListener(user.get_id(), holder.dialog));

        holder.username.setOnLongClickListener(new RemoveUserOnClickListener(user.get_id()));
        row.setOnLongClickListener(new RemoveUserOnClickListener(user.get_id()));

        return row;
    }

    static class UserHolder {

        TextView username;
        FrameLayout toot;
        FrameLayout omw;
        FrameLayout beer;
        ImageButton beerButton;
        ImageButton omwButton;
        ImageButton tootButton;
        ProgressDialog dialog;
    }

    private class RemoveUserOnClickListener implements View.OnLongClickListener {

        String id;

        public RemoveUserOnClickListener(String id) {
            this.id = id;
        }

        @Override
        public boolean onLongClick(View v) {

            RemoveFriend userToRemove = new RemoveFriend();
            userToRemove.setFriend(id);
            userToRemove.setId(SharedPreferencesUtil.getUser().get_id());

            UserService userService = new UserServiceImpl();

            ProgressDialog dialog = DialogUtil.getProgressDialog(context, "Removing user...");
            dialog.show();
            try {
                userService.removeFriendsAsynchronous(userToRemove, context, new RemoveFriendsResponseListener(dialog));
            } catch (ApiException e) {
                e.printStackTrace();
            }
            return true;
        }

    }

    private class RemoveFriendsResponseListener implements AsyncTaskCompleteListener<ApiBase> {

        ProgressDialog dialog;

        public RemoveFriendsResponseListener(ProgressDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void onTaskComplete(ApiBase result) {
            dialog.hide();
            User response;
            try {
                response = (User) ApiResponseUtil.parseResponse(result, User.class);
                if (response!=null) {
                    Toast.makeText(context,R.string.remove_success,Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            } catch (ApiException e) {
                return;
            }
        }
    }

    public class UsernameOnClickListener implements View.OnClickListener {

        String username;
        ProgressDialog dialog;

        public UsernameOnClickListener(String username, ProgressDialog dialog) {
            this.dialog = dialog;
            this.username = username;
        }

        @Override
        public void onClick(View view) {


        }
    }

    public class TootOnClickListener implements View.OnClickListener {

        String id;
        ProgressDialog dialog;

        public TootOnClickListener(String id, ProgressDialog dialog) {
            this.dialog = dialog;
            this.id = id;
        }

        @Override
        public void onClick(View view) {
            dialog.show();
            User user = SharedPreferencesUtil.getUser();
            try {
                tootService.sendTootHereAsync(user.get_id(), id, context, new TootResponseListener(dialog));
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    public class BeerOnClickListener implements View.OnClickListener {

        String id;
        ProgressDialog dialog;

        public BeerOnClickListener(String id, ProgressDialog dialog) {
            this.id = id;
            this.dialog = dialog;
        }

        @Override
        public void onClick(View view) {
            dialog.show();
            User user = SharedPreferencesUtil.getUser();
            try {
                tootService.sendTootBeerAsync(user.get_id(), id, context, new TootResponseListener(dialog));
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    public class OmwOnClickListener implements View.OnClickListener {

        String id;
        ProgressDialog dialog;

        public OmwOnClickListener(String id, ProgressDialog dialog) {
            this.id = id;
            this.dialog = dialog;
        }

        @Override
        public void onClick(View view) {
            dialog.show();
            User user = SharedPreferencesUtil.getUser();
            try {
                tootService.sendTootOTWAsync(user.get_id(), id, context, new TootResponseListener(dialog));
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    private class TootResponseListener implements AsyncTaskCompleteListener<ApiBase> {

        ProgressDialog dialog;

        public TootResponseListener(ProgressDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void onTaskComplete(ApiBase result) {
            ApiBase response;
            try {
                response = (ApiBase) ApiResponseUtil.parseResponse(result, ApiBase.class);
                if (response!=null) {
                    dialog.hide();
                }
            } catch (ApiException e) {
                return;
            }

            Log.i("Cat", "toot success, smells bad");
        }
    }
}