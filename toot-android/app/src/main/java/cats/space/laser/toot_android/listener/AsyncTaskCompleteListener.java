package cats.space.laser.toot_android.listener;

/**
 * Created by Whitney Champion on 8/23/14.
 */
public interface AsyncTaskCompleteListener<T> {

    public void onTaskComplete(T result);
}