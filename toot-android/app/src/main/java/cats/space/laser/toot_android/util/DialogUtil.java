package cats.space.laser.toot_android.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import cats.space.laser.toot_android.MainActivity;
import cats.space.laser.toot_android.R;

/**
 * Created by whitneychampion on 8/23/14.
 */
public class DialogUtil {

    public static AlertDialog.Builder getNeutralDialog(Context context, String title, String message,
                                                       DialogInterface.OnClickListener listener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNeutralButton("OK", listener);
        return builder;
    }

    public static ProgressDialog getProgressDialog(Context context, String message) {

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(message);
        return progressDialog;
    }

}