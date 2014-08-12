package types;

import models.Chai;
import models.Message;

import java.util.List;
import java.util.Map;

/**
 * Created by kedar on 8/4/14.
 */
public class Match {
    private Chai received;
    private Chai targeted;
    private String targetName;

    public Match (Chai received, Chai targeted) {
        this.received = received;
        this.targeted = targeted;
    }

    public Chai getReceived() {
        return received;
    }

    public Chai getTargeted() {
        return targeted;
    }

    public String getTargetName() {
        return targetName;
    }

    public String getTargetFirstName () {
        return targetName.split(" ")[0];
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }
}
