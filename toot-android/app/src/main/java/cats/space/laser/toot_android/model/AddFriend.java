package cats.space.laser.toot_android.model;

/**
 * Created by Whitney Champion on 8/23/14.
 */
public class AddFriend extends ApiBase {

    private String id;
    private String[] friends;

    public String[] getFriends() {
        return friends;
    }

    public void setFriends(String[] friends) {
        this.friends = friends;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}