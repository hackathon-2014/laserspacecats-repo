package cats.space.laser.toot_android.model;

/**
 * Created by Whitney Champion on 8/23/14.
 */
public class UserResponse extends ApiBase {

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
