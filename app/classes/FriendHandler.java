package classes;

import clients.LetsChaiFacebookClient;
import models.AreFriends;
import models.User;
import org.apache.commons.lang3.Validate;
import play.libs.F;
import models.Friends;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kedar on 7/25/14.
 */
public class FriendHandler {

    private static FriendHandler INSTANCE;

    private List<AreFriends> friendCache;
    private List<Friends> mutualFriendCache;

    private List<AreFriends> addToFriendCache = new ArrayList<>();
    private List<Friends> addToMutualFriendCache = new ArrayList<>();

    public static FriendHandler getInstance () {
        // singleton
        if (INSTANCE != null)
            return INSTANCE;

        // if not load 'er up
        List<AreFriends> friendList = new ArrayList<>();
        List<Friends> mutualFriendList = new ArrayList<>();

        Iterable<AreFriends> friendsIterable =  PlayJongo.getCollection("friends_cache").find().as(AreFriends.class);
        Iterable<Friends> mutualIterable = PlayJongo.getCollection("mutual_friends_cache").find().as(Friends.class);
        for (AreFriends f: friendsIterable) {
            friendList.add(f);
        }
        for (Friends f: mutualIterable) {
            mutualFriendList.add(f);
        }

        INSTANCE = new FriendHandler(friendList, mutualFriendList);
        return INSTANCE;
    }

    public FriendHandler (List<AreFriends> friendCache, List<Friends> mutualFriendCache) {
        this.friendCache = friendCache;
        this.mutualFriendCache = mutualFriendCache;
    }

    // checks the cache to see if they're friends, gives the fallback if it's not cached
    public Boolean isFriends (String userId1, String userId2, Boolean fallback) {
        try {
            return friendCache.stream().filter(cached -> cached.hasUsers(userId1, userId2)).collect(Collectors.toList()).get(0).isFriends();
        } catch (IndexOutOfBoundsException e) {
            return fallback;
        }
    }

    // returns null if it's not cached, Friends if it is
    public Friends mutualFriends (String userId1, String userId2) {
        try {
            return mutualFriendCache.stream().filter(f -> f.containsUsers(userId1, userId2)).collect(Collectors.toList()).get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    // true if cache exists
    private Boolean isFriendsCached (String userId1, String userId2) {
        return friendCache.stream().filter(cached -> cached.hasUsers(userId1, userId2)).count() > 0;
    }

    // true if cache exists
    private Boolean mutualFriendsCached (String userId1, String userId2) {
        return mutualFriendCache.stream().filter(friends -> friends.containsUsers(userId1, userId2)).count() > 0;
    }

}
