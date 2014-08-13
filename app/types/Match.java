package types;

import models.Chai;
import models.Message;
import org.joda.time.DateTime;

import java.util.Date;
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

    public String getReceiver () {
       return getReceived().getReceiver();
    }

    public String getTarget () {
        return getReceived().getTarget();
    }

    public Date matchDate () {
        DateTime date1 = new DateTime(received.getDecided());
        DateTime date2 = new DateTime(targeted.getDecided());
        return date1.isAfter(date2) ? date1.toDate() : date2.toDate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Match match = (Match) o;

        // true if my match is the same receiver/target or receiver1 == target2 && receiver2 == target2
        if (this.getReceiver().equals(match.getReceiver()) && this.getTarget().equals(match.getTarget()))
            return true;
        if (this.getReceiver().equals(match.getTarget()) && this.getTarget().equals(match.getReceiver()))
            return true;

        return false;
    }

    @Override
    public int hashCode() {
        return getReceiver().hashCode() + getTarget().hashCode();
    }
}
