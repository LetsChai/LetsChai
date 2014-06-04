package models;

import org.apache.commons.lang3.Validate;

import java.util.*;

/**
 * Created by kedar on 6/3/14.
 */
public class Chai {
    // there can only be 2 users in a match
    private Map<String, Boolean> userChoices = new HashMap<>(); // null: no choice made yet, true: like, false: pass
    private RejectReason reason;
    private Date date;

    private Chai () {}  // for Jackson

    public Chai (String user1, String user2) {
        Validate.notNull(user1, "user1 can't be null");
        Validate.notNull(user2, "user2 can't be null");
        userChoices.put(user1, false);
        userChoices.put(user2, false);
        date = new Date();
    }

    public Chai (String user1, String user2, RejectReason reason) {
        this(user1, user2);
        this.reason = reason;
    }

    public boolean contains (String userId) {
        if (userChoices.containsKey(userId))
            return true;
        return false;
    }

    public boolean contains (String user1, String user2) {
        if (userChoices.containsKey(user1) && userChoices.containsKey(user2))
            return true;
        return false;
    }

    public boolean isMatch () {
        for (Boolean choice: userChoices.values()) {
            if (!choice)
                return false;
        }
        return true;
    }

    public Boolean getChoice (String userId) {
        if (!contains(userId))
            throw new IllegalArgumentException("Chai does not contain userId");
        return userChoices.get(userId);
    }
}
