package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.restfb.json.JsonException;

import java.util.HashMap;

/**
 * Created by kedar on 6/3/14
 */
public class Friends {
    // userId, name
    private HashMap<String, String> friends = new HashMap<>();
    private int count;

    public Friends () {}

    // from a Facebook User json
    public void addFriend (JsonNode j) {
        friends.put(j.path("id").asText(), j.path("name").asText());
    }

    // returns the count field if it's set, the size of the friends map otherwise
    public Integer getCount () {
        if (count != 0)
            return count;
        return friends.size();
    }

    public void setCount(int count) {
        this.count = count;
    }

    public HashMap<String, String> getFriends() {
        return friends;
    }
}
