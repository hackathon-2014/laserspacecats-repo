package cats.space.laser.toot_android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import cats.space.laser.toot_android.R;
import cats.space.laser.toot_android.model.User;

/**
 * Created by Whitney Champion on 8/23/14.
 */
public class UserAdapter extends ArrayAdapter<User> {

    int resource;
    Context context;
    List<User> data;
    String[] urls;

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

        User user = data.get(position);

        if ( row == null )
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(resource, parent, false);

            holder = new UserHolder();
            holder.username = (TextView) row.findViewById(R.id.username);
            holder.toot = (FrameLayout) row.findViewById(R.id.button_horn);
            holder.omw = (FrameLayout) row.findViewById(R.id.button_car);

            row.setTag(holder);
            row.setTag(R.id.button_car, holder.omw);
            row.setTag(R.id.button_horn, holder.toot);

        } else {
            holder = (UserHolder)row.getTag();
        }

        holder.username.setText(user.getUsername());
        holder.username.setOnClickListener(new UsernameOnClickListener(user.getUsername()));
        holder.toot.setOnClickListener(new TootOnClickListener(user.getId()));
        holder.omw.setOnClickListener(new OmwOnClickListener(user.getId()));

        return row;
    }

    static class UserHolder {
        TextView username;
        FrameLayout toot;
        FrameLayout omw;
    }

    public class UsernameOnClickListener implements View.OnClickListener {

        String username;

        public UsernameOnClickListener(String username) {
            this.username = username;
        }

        @Override
        public void onClick(View view) {

        }
    }

    public class TootOnClickListener implements View.OnClickListener {

        String id;

        public TootOnClickListener(String id) {
            this.id = id;
        }

        @Override
        public void onClick(View view) {

        }
    }

    public class OmwOnClickListener implements View.OnClickListener {

        String id;

        public OmwOnClickListener(String id) {
            this.id = id;
        }

        @Override
        public void onClick(View view) {

        }
    }

}