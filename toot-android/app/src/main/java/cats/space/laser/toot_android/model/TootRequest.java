package cats.space.laser.toot_android.model;

import java.io.Serializable;

/**
 * Created by ryanmoore on 8/23/14.
 */
public class TootRequest implements Serializable {

    private String destination;
    private String origin;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
}
