package types;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

/**
 * Created by kedar on 7/17/14.
 */
public class Decision {
    private boolean like = false;
    private Date timestamp;
    private Reason reason;
    private String userId;

    private Decision () {} // Jackson

    public Decision (String userId, boolean likes) {
        this.userId = userId;
        this.like = likes;
        this.timestamp = new Date();
    }

    public Decision (String userId, boolean likes, Reason reason) {
        this(userId, likes);
        this.reason = reason;
    }

    public enum Reason {
        BAD_PROFILE, BAD_PICTURE, UGLY_FACE, IS_FRIEND, DATING_HIS_BEST_FRIEND;
    }

    @JsonIgnore // not sure why this is necessary but it is, bug in Jackson???
    public boolean isYes() {
        return like;
    }

    @JsonIgnore
    public boolean isNo () {
        return !like;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Reason getReason() {
        return reason;
    }

    public String getUserId() {
        return userId;
    }
}
