package models;

import java.util.Date;
import java.util.List;

/**
 * Created by kedar on 7/25/14.
 */
public class AreFriends {
    private List<String> users;
    private Boolean friends;
    private Date timestamp;

    private AreFriends () {} // Jackson

    public AreFriends(List<String> users, Boolean areFriends) {
        this.users = users;
        this.friends = areFriends;
        timestamp = new Date();
    }

    public List<String> getUsers() {
        return users;
    }

    public Boolean isFriends () {
        return friends;
    }

    public Boolean hasUsers (String userId1, String userId2) {
        return users.contains(userId1) && users.contains(userId2);
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
