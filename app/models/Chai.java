package models;

import types.Decision;
import types.Friends;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kedar on 7/25/14.
 */
public class Chai {
    // id, name
    private Map<String, String> users = new HashMap<>();
    private Map<String, Decision> decisions = new HashMap<>();
    private Date date;
    private Double score;
    private Friends mutualFriends;

    public Chai(User user1, User user2, Double score, Friends mutualFriends) {
        users.put(user1.getUserId(), user1.getName());
        users.put(user2.getUserId(), user2.getName());
        decisions.put(user1.getUserId(), new Decision(user1.getUserId(), false));
        decisions.put(user2.getUserId(), new Decision(user2.getUserId(), false));
        this.score = score;
        this.mutualFriends = mutualFriends;
        date = new Date();
    }

    public Map<String, String> getUsers() {
        return users;
    }

    public Map<String, Decision> getDecisions() {
        return decisions;
    }

    public Decision getDecision(String userId) {
        return decisions.get(userId);
    }

    public void setDecision(Decision decision) {
        decisions.put(decision.getUserId(), decision);
    }

    public boolean hasUser (String userId) {
        return users.containsKey(userId);
    }

    public boolean hasUsers (String userId1, String userId2) {
        return users.containsKey(userId1) && users.containsKey(userId2);
    }

    public Date getDate() {
        return date;
    }

    public Double getScore() {
        return score;
    }

    public Friends getMutualFriends() {
        return mutualFriends;
    }
}
