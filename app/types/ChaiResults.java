package types;

import models.Chai;

/**
 * Created by kedar on 8/18/14.
 */
public class ChaiResults {

    private String userId;
    private int targets = 0;
    private int likes = 0;
    private int passes = 0;
    private int noDecisions = 0;

    public ChaiResults(String userId) {
        this.userId = userId;
    }

    public void addChai (Chai chai) {
        targets++;
        if (!chai.hasDecided()) noDecisions++;
        else if (chai.getDecision()) likes++;
        else passes++;
    }

    public double average () {
        return (double) likes / (double) (passes + likes);
    }

    public double weighted () {
        return (double) likes / (double) targets;
    }

    public int noDecisions () {
        return targets - likes - passes;
    }

    public int passPlusND () {
        return targets - likes;
    }

    public String getUserId() {
        return userId;
    }

    public int getTargets() {
        return targets;
    }

    public int getLikes() {
        return likes;
    }

    public int getPasses() {
        return passes;
    }

    public int getNoDecisions() {
        return noDecisions;
    }
}
