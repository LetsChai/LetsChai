package models;

import java.util.Date;

/**
 * Created by kedar on 6/12/14.
 */
public class Chai2 {
    public String userId;
    public Date date;
    public boolean decision = false;    // like, pass -> true, false
    public Date decisionTimestamp;
    public RejectReason reason = null;  // if rejected, the reason
    private double chaiScore;

    public Chai2 (String userId) {
        this.userId = userId;
        date = new Date();
    }

    public void setDecision (boolean choice) {
        this.decision = choice;
        decisionTimestamp = new Date();
    }

    public void setDecisionWithReason (boolean choice, RejectReason reason) {
        setDecision(choice);
        this.reason = reason;
    }
}
