package cats.space.laser.toot_android.adapter;

import android.content.Context;
import android.graphics.Typeface;
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
import cats.space.laser.toot_android.util.SharedPreferencesUtil;

/**
 * Created by Whitney Champion on 8/23/14.
 */
public class UserAdapter extends ArrayAdapter<User> {

    private TootService tootService = new TootServiceImpl();
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

            row.setTag(holder);
            row.setTag(R.id.button_car, holder.omw);
            row.setTag(R.id.button_horn, holder.toot);
            row.setTag(R.id.button_beer, holder.beer);

        } else {
            holder = (UserHolder)row.getTag();
        }

        holder.username.setText(user.getUsername());
        holder.username.setTypeface(type);
        holder.username.setOnClickListener(new UsernameOnClickListener(user.getUsername()));
        holder.toot.setOnClickListener(new TootOnClickListener(user.get_id()));
        holder.omw.setOnClickListener(new OmwOnClickListener(user.get_id()));
        holder.beer.setOnClickListener(new BeerOnClickListener(user.get_id()));

        return row;
    }

    static class UserHolder {
        TextView username;
        FrameLayout toot;
        FrameLayout omw;
        FrameLayout beer;
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
            User user = SharedPreferencesUtil.getUser();
            try {
                tootService.sendTootHereAsync(user.get_id(), id, context, new TootResponseListener());
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    public class BeerOnClickListener implements View.OnClickListener {

        String id;

        public BeerOnClickListener(String id) {
            this.id = id;
        }

        @Override
        public void onClick(View view) {
            User user = SharedPreferencesUtil.getUser();
            try {
                tootService.sendTootBeerAsync(user.get_id(), id, context, new TootResponseListener());
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    public class OmwOnClickListener implements View.OnClickListener {

        String id;

        public OmwOnClickListener(String id) {
            this.id = id;
        }

        @Override
        public void onClick(View view) {
            User user = SharedPreferencesUtil.getUser();
            try {
                tootService.sendTootHereAsync(user.get_id(), id, context, new TootResponseListener());
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    private class TootResponseListener implements AsyncTaskCompleteListener<ApiBase> {

        @Override
        public void onTaskComplete(ApiBase result) {
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