package models;

import types.Decision;
import types.Friends;

import java.util.Date;

/**
 * Created by kedar on 6/12/14.
 */
public class Chai {

    private Date date;
    private Decision myDecision;
    private Decision otherDecision;
    private double score;
    private Friends mutualFriends;

    private Chai () {} // for Jackson

    public Chai(String myId, String otherId, double score, Friends mutualFriends) {
        this.myDecision = new Decision(myId, false);
        this.otherDecision = new Decision(otherId, false);
        this.score = score;
        this.mutualFriends = mutualFriends;
        date = new Date();
    }

    public String getMyUserId () {
        return myDecision.getUserId();
    }

    public String getOtherUserId () {
        return otherDecision.getUserId();
    }

    public Date getDate() {
        return date;
    }

    public Decision getMyDecision() {
        return myDecision;
    }

    public Decision getOtherDecision() {
        return otherDecision;
    }

    public double getScore() {
        return score;
    }

    public Friends getMutualFriends() {
        return mutualFriends;
    }

    public void setMyDecision (Decision decision) {
        myDecision = decision;
    }

    public void setMyDecision (boolean likes) {
        myDecision = new Decision(getMyUserId(), likes);
    }

    public void setOtherDecision (Decision decision) {
        otherDecision = decision;
    }

    public void setOtherDecision (boolean likes) {
        otherDecision = new Decision(getOtherUserId(), likes);
    }

    public boolean isMatch () {
        return myDecision.isYes() && otherDecision.isYes();
    }
}
