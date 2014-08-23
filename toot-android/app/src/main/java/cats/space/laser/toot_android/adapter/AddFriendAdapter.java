package cats.space.laser.toot_android.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import cats.space.laser.toot_android.R;
import cats.space.laser.toot_android.api.ApiException;
import cats.space.laser.toot_android.api.Impl.TootServiceImpl;
import cats.space.laser.toot_android.api.TootService;
import cats.space.laser.toot_android.listener.AsyncTaskCompleteListener;
import cats.space.laser.toot_android.model.ApiBase;
import cats.space.laser.toot_android.model.User;
import cats.space.laser.toot_android.util.ApiResponseUtil;
import cats.space.laser.toot_android.util.DialogUtil;

/**
 * Created by Whitney Champion on 8/23/14.
 */
public class AddFriendAdapter extends ArrayAdapter<User> {

    private TootService tootService = new TootServiceImpl();
    int resource;
    Context context;
    List<User> data;
    String[] urls;

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
            holder.dialog = DialogUtil.getProgressDialog(context, "Adding user...");

            row.setTag(holder);

        } else {
            holder = (UserHolder)row.getTag();
        }

        holder.username.setText(user.getUsername());
        holder.username.setOnClickListener(new UsernameOnClickListener(user.getUsername(), holder.dialog));

        return row;
    }

    static class UserHolder {
        TextView username;
        ProgressDialog dialog;
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
//            User user = SharedPreferencesUtil.getUser();
//            try {
////                tootService.sendTootHereAsync(user.get_id(), id, context, new TootResponseListener());
//            } catch (ApiException e) {
//                e.printStackTrace();
//            }
        }
    }

    private class AddUserResponseListener implements AsyncTaskCompleteListener<ApiBase> {

        ProgressDialog dialog;

        public AddUserResponseListener(ProgressDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void onTaskComplete(ApiBase result) {
            dialog.hide();
            ApiBase response;
            try {
                response = (ApiBase) ApiResponseUtil.parseResponse(result, ApiBase.class);
            } catch (ApiException e) {
                return;
            }

            Log.i("Cat", "toot success, smells bad");
        }
    }
}