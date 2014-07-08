package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import play.Logger;
import play.api.libs.json.JsPath;
import play.libs.Json;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Date;

/**
 * Created by kedar on 6/12/14.
 */
public class Chai {
    private String userId;
    private Date date;
    private boolean decision = false;    // like, pass -> true, false
    private Date decisionTimestamp;
    private RejectReason reason = null;  // if rejected, the reason
    private double chaiScore;
    private boolean match = false;

    public Chai(String userId, double chaiScore) {
        this.userId = userId;
        this.chaiScore = chaiScore;
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

    public boolean getDecision() {
        return decision;
    }

    public String getUserId() {
        return userId;
    }

    public void setMatch (boolean isMatch) {
        this.match = isMatch;
    }

    public boolean isMatch() {
        return match;
    }

    public ObjectNode toJson () {
        Gson gson = new Gson();
        String jsonString = gson.toJson(this);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = null;
        try {
            json = mapper.readTree(jsonString);
        } catch (IOException e) {
            Logger.error("Error parsing JSON in Chai.toJson()");
            e.printStackTrace();
        }

        if (!json.isObject()) {
            throw new ClassCastException("chai.toJSON(): JsonNode cannot be cast to ObjectNode, the node contains a value instead of an object");
        }

        return (ObjectNode) json;
    }
}
