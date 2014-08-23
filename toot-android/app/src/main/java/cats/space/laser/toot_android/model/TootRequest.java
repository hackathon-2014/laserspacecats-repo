package cats.space.laser.toot_android.model;

import java.io.Serializable;

/**
 * Created by ryanmoore on 8/23/14.
 */
public class TootRequest implements Serializable {


    private String userName;
    private String origin;
    private String classification;

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
}
