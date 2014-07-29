package models;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.Validate;
import play.Logger;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kedar on 6/3/14
 */
public class Friends {

    private List<String> users;
    private HashMap<String, String> friends = new HashMap<>();  // userId, name
    private int count;
    private Date timestamp;

    public static Iterable<Friends> getFullCache () {
        return PlayJongo.getCollection("mutual_friends_cache").find().as(Friends.class);
    }

    private Friends () {}

    // from a Facebook mutual friends call
    public Friends (JsonNode fbResponse, List<String> users) {
        Validate.notNull(fbResponse);
        Logger.info(fbResponse.toString());

        for(JsonNode j: fbResponse.path("context").path("mutual_friends").path("data")) {
            addFriend(j);
        }
        setCount(fbResponse.path("context").path("mutual_friends").path("summary").path("total_count").asInt());
        this.users = users;
    }

    // from a Facebook User json
    public void addFriend (JsonNode j) {
        friends.put(j.path("id").asText(), j.path("name").asText());
        timestamp = new Date();
    }

    // returns the count field if it's set, the size of the friends map otherwise
    public Integer getCount () {
        if (count != 0)
            return count;
        return friends.size();
    }

    public Integer unnamedFriendCount () {
        return count - friends.size();
    }

    public void setCount(int count) {
        this.count = count;
    }

    public HashMap<String, String> getFriends() {
        return friends;
    }

    public String toString () {
        return friends.toString() + ", count:" + count;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public Boolean containsUsers (String userId1, String userId2) {
        return users.contains(userId1) && users.contains(userId2);
    }
}
