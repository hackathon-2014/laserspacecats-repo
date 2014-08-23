package cats.space.laser.toot_android.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Whitney Champion on 8/23/14.
 */
public class User extends ApiBase {

    private String id;
    private String username;
    private String registrationId;
    private String password;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}