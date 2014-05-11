package models;

import com.restfb.types.User;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by kedar on 4/20/14.
 */
public class FacebookFriends {

    private HashSet<String> friends = new HashSet<String>();
    private String userId;

    public FacebookFriends () {}

    public FacebookFriends(String userId, Collection<User> users) {
        this.userId = userId;
        for (User user: users) {
            friends.add(user.getId());
        }
    }

}
