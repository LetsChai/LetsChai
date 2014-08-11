package models;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.Validate;
import org.jongo.marshall.jackson.oid.ObjectId;
import play.Logger;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.util.*;

/**
 * Created by kedar on 6/3/14
 */
public class Friends {
    @ObjectId
    private Object _id;

    private List<String> users;
    private Map<String, String> mutualFriends = new HashMap<>();  // userId, name
    private boolean friends;    // whether they are facebook friends
    private int count;  // total number of mutual friends
    private Date timestamp; // gets set every time mutual friends is updated

    private Friends () {}   // Jackson

    // from a Facebook mutual friends call
    public Friends (List<String> users) {
        this.users = users;
    }

    public void setMutualFriends (JsonNode fbResponse) {
        Validate.notNull(fbResponse);
        Logger.info(fbResponse.toString());

        for(JsonNode j: fbResponse.path("context").path("mutual_friends").path("data")) {
            mutualFriends.put(j.path("id").asText(), j.path("name").asText());
        }
        count = fbResponse.path("context").path("mutual_friends").path("summary").path("total_count").asInt();
        timestamp = new Date();
    }

    public void setFriends (boolean friends) {
        this.friends = friends;
    }

    public Integer unnamedFriendCount () {
        return count - mutualFriends.size();
    }

    public String toString () {
        return "users:" + users.toString() + ", mutualFriends: " + mutualFriends.toString() + ", count:" + count + ", friends:" + String.valueOf(friends);
    }

    public List<String> getUsers() {
        return users;
    }

    public boolean isFriends() {
        return friends;
    }

    // Friends are equal if they refer to the same 2 users
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Friends friends = (Friends) o;

        // To normalize the orders
        Collections.sort(users);
        Collections.sort(friends.users);

        if (!users.equals(friends.users)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        Collections.sort(users); // To normalize the order
        return users.hashCode();
    }

    public Map<String, String> getMutualFriends() {
        return mutualFriends;
    }

    public int getCount() {
        return count;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public de.undercouch.bson4jackson.types.ObjectId getObjectId () {
        return (de.undercouch.bson4jackson.types.ObjectId) _id;
    }

    // pass in one user, returns the other, throws an Exception if the user is not one of the two
    public String getOtherUser (String userId) {
        Validate.notNull(userId);
        Validate.isTrue(users.contains(userId));
        return users.get(0).equals(userId) ? users.get(1) : users.get(0);
    }

    // Friends are comparable
    // The greater Friends is the one with more named mutual friends
    // The tiebreaker is count
    public static int compare (Friends friends1, Friends friends2) {
        if (friends1.getMutualFriends().size() > friends2.getMutualFriends().size())
            return 1;
        if (friends2.getMutualFriends().size() > friends1.getMutualFriends().size())
            return -1;
        // tiebreaker
        return Integer.compare(friends1.getCount(), friends2.getCount());
    }
}
