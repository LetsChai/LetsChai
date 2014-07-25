package classes;

import clients.LetsChaiFacebookClient;
import models.User;
import play.libs.F;
import types.Friends;
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
    public Boolean isFriendsCached (String userId1, String userId2, Boolean fallback) {
        Boolean cached = isFriendsCached(userId1, userId2);
        if (cached != null)
            return cached;
        return fallback;
    }

    // returns null if it's not cached
    private Boolean isFriendsCached (String userId1, String userId2) {
        try {
            return friendCache.stream().filter(cached -> cached.hasUsers(userId1, userId2)).collect(Collectors.toList()).get(0).isFriends();
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public F.Promise<Boolean> isFriends (User user1, User user2) {
        String userId1 = user1.getUserId();
        String userId2 = user2.getUserId();

        Boolean cached = isFriendsCached(userId1, userId2);
        if (cached != null)
            return F.Promise.promise(() -> cached);

        LetsChaiFacebookClient fb = new LetsChaiFacebookClient(user1.getAccessToken().getAccessToken());
        F.Promise<Boolean> fresh = fb.areFriends(userId1, userId2);
        fresh.onRedeem(bool -> {
            AreFriends a = new AreFriends(Arrays.asList(userId1, userId2), bool);
            addToFriendCache.add(a);
            friendCache.add(a);
        });

        return fresh;
    }

    // returns null if it's not cached
    public Friends mutualFriendsCached (String userId1, String userId2) {
        try {
            return mutualFriendCache.stream().filter(f -> f.containsUsers(userId1, userId2)).collect(Collectors.toList()).get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public F.Promise<Friends> mutualFriends (User user1, User user2) {
        String userId1 = user1.getUserId();
        String userId2 = user2.getUserId();

        Friends cached = mutualFriendsCached(userId1, userId2);
        if (cached != null)
            return F.Promise.promise(() -> cached);

        LetsChaiFacebookClient fb = new LetsChaiFacebookClient(user1.getAccessToken().getAccessToken());
        F.Promise<Friends> fresh = fb.getMutualFriends(userId2);
        fresh.onRedeem(f -> {
            addToMutualFriendCache.add(f);
            mutualFriendCache.add(f);
        });

        return fresh;
    }

    /**
     * Created by kedar on 7/25/14.
     */
    public static class AreFriends {
        private List<String> users;
        private Boolean friends;

        public AreFriends (List<String> users, Boolean areFriends) {
            this.users = users;
            this.friends = areFriends;
        }

        public List<String> getUsers() {
            return users;
        }

        public Boolean isFriends () {
            return friends;
        }

        public Boolean hasUsers (String userId1, String userId2) {
            return users.contains(userId1) && users.contains(userId2);
        }
    }
}
