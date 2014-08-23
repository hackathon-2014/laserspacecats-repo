package cats.space.laser.toot_android.model;

/**
 * Created by Whitney Champion on 8/23/14.
 */
public class RemoveFriend extends ApiBase {

    private String id;
    private String friend;

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}