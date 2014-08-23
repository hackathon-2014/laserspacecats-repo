package cats.space.laser.toot_android.model;

import java.io.Serializable;

/**
 * Created by Whitney Champion on 8/23/14.
 */
public class ApiBase implements Serializable{

    private Boolean success;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
