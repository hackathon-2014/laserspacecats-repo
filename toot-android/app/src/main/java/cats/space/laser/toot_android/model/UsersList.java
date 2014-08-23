package cats.space.laser.toot_android.model;

/**
 * Created by Whitney Champion on 8/23/14.
 */
public class UsersList extends ApiBase {

    private User[] users;

    public User[] getUsers() {
        return users;
    }

    public void setUsers(User[] users) {
        this.users = users;
    }

}