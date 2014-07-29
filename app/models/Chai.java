package models;

import clients.LetsChaiFacebookClient;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.commons.lang3.Validate;
import play.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by kedar on 7/25/14.
 */
public class Chai {
    // id, name
    private Map<String, HalfChai> halfChais = new HashMap<>();
    private Double score;
    private Friends mutualFriends;

    public Chai () {} // Jackson

    public Chai(User active, User passive, Double score, Friends mutualFriends) {
        halfChais.put(active.getUserId() ,new HalfChai(active.getUserId(), active.getName()));
        this.halfChais.get(active.getUserId()).setAsReceived();
        halfChais.put(passive.getUserId(), new HalfChai(passive.getUserId(), passive.getName()));
        this.score = score;
        this.mutualFriends = mutualFriends;
    }

    public Set<String> getUsers() {
        return halfChais.keySet();
    }

    public boolean hasUser (String userId) {
        return halfChais.containsKey(userId);
    }

    public boolean hasUsers (String userId1, String userId2) {
        return halfChais.containsKey(userId1) && halfChais.containsKey(userId2);
    }

    public Double getScore() {
        return score;
    }

    public Friends getMutualFriends() {
        return mutualFriends;
    }

    public Boolean getDecision (String userId) {
        Validate.isTrue(halfChais.containsKey(userId));
        return halfChais.get(userId).getDecision();
    }

    public static enum Reason {
        BAD_PROFILE, CREEPER
    }

    public Boolean isMatch () {
        for (HalfChai half: halfChais.values()) {
            if (!half.getDecision())
                return false;
        }
        return true;
    }

    // takes thisUser and gives the HalfChai for the other user
    public HalfChai getOtherHalf (String thisUser) {
        Validate.isTrue(hasUser(thisUser));
        for (HalfChai half: halfChais.values()) {
            if (!half.getUserId().equals(thisUser))
                return half;
        }
        return null; // will never reach this point
    }



    public static class HalfChai {

        private Date received;
        private Date decided;
        private Boolean decision = false;
        private String userId;
        private String name;
        private Reason reason;

        public HalfChai (String userId, String name) {
            this.userId = userId;
            this.name = name;
            this.decision = false;
        }

        public HalfChai () {} // for Jackson

        public void setDecision (Boolean decision) {
            this.decision = decision;
        }

        public Date getReceived() {
            return received;
        }

        public void setAsReceived () { received = new Date(); }

        public Date getDecided() {
            return decided;
        }

        public Boolean getDecision() {
            return decision;
        }

        public String getUserId() {
            return userId;
        }

        public String getName() {
            return name;
        }

        public Reason getReason() {
            return reason;
        }

        public String profilePicURL () {
            return LetsChaiFacebookClient.profilePictureURL(userId);
        }
    }
}
