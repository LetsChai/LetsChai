package models;

import clients.LetsChaiFacebookClient;

import java.util.*;

/**
 * Created by kedar on 8/1/14.
 */
public class Chai implements Comparable<Chai> {

    private String receiver;
    private String target;
    private boolean decision;
    private Date received;
    private Date decided;
    private Double score;

    public Chai () {} // Jackson

    public Chai(String receiver, String target, double score) {
        this.receiver = receiver;
        this.target = target;
        this.received = new Date();
        this.score = score;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getTarget() {
        return target;
    }

    public boolean getDecision() {
        return decision;
    }

    public Date getReceived() {
        return received;
    }

    public Date getDecided() {
        return decided;
    }

    public Double getScore() {
        return score;
    }

    public List<String> getUsers () {
        return Arrays.asList(receiver, target);
    }

    public void setDecision(boolean decision) {
        this.decision = decision;
        this.decided = new Date();
    }

    public boolean hasDecided () {
        return decided != null;
    }

    // a chai is equal if the receiver and target are the same
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chai chai2 = (Chai) o;

        if (!receiver.equals(chai2.receiver)) return false;
        if (!target.equals(chai2.target)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = receiver.hashCode();
        result = 31 * result + target.hashCode();
        return result;
    }

    public int compareTo(Chai chai2) {
        return Chai.compare(this, chai2);
    }

    public static int compare (Chai chai1, Chai chai2) {
        return Double.compare(chai1.getScore(), chai2.getScore());
    }
}
