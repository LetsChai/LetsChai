package classes;

import clients.LetsChaiFacebookClient;
import models.AreFriends;
import models.Friends;
import models.User;
import play.libs.F;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kedar on 7/28/14.
 */
public class FriendCacher {

    private static FriendHandler INSTANCE;

    private List<AreFriends> friendCache;
    private List<Friends> mutualFriendCache;
    private List<AreFriends> addToFriendCache = new ArrayList<>();
    private List<Friends> addToMutualFriendCache = new ArrayList<>();
    private LetsChaiFacebookClient fb;
    private User user;

    public static FriendCacher forUser (User user) {

        String userId = user.getUserId();
        List<AreFriends> friendList = new ArrayList<>();
        List<Friends> mutualFriendList = new ArrayList<>();

        Iterable<AreFriends> friendsIterable =  PlayJongo.getCollection("friends_cache").find("{'users': '#'}", userId).as(AreFriends.class);
        Iterable<Friends> mutualIterable = PlayJongo.getCollection("mutual_friends_cache").find("{'users': '#'}", userId).as(Friends.class);
        for (AreFriends f: friendsIterable) {
            friendList.add(f);
        }
        for (Friends f: mutualIterable) {
            mutualFriendList.add(f);
        }

        return new FriendCacher(friendList, mutualFriendList, user);
    }

    public FriendCacher (List<AreFriends> areFriendsList, List<Friends> mutualFriendsList, User user) {
        friendCache = areFriendsList;
        mutualFriendCache = mutualFriendsList;
        fb = new LetsChaiFacebookClient(user.getAccessToken().getAccessToken());
        this.user = user;
    }

    public F.Promise<Boolean> update (List<User> otherUsers) {
        String userId = user.getUserId();
        List<F.Promise<Boolean>> promises = new ArrayList<>();

        for (User otherUser: otherUsers) {
            String otherId = otherUser.getUserId();

            // cache AreFriends
            if (!isFriendsCached(userId, otherId)) {
                F.Promise<Boolean> friendPromise = fb.areFriends(userId, otherId)
                        .map(bool -> new AreFriends(Arrays.asList(userId, otherId), bool))
                        .map(isFriend -> {
                            friendCache.add(isFriend);
                            addToFriendCache.add(isFriend);
                            return true;
                        });
                promises.add(friendPromise);
            }

            // cache mutual Friends
            if (!mutualFriendsCached(userId, otherId)) {
                F.Promise<Boolean> mutualPromise = fb.getMutualFriends(userId, otherId)
                        .map(friends -> {
                            mutualFriendCache.add(friends);
                            addToMutualFriendCache.add(friends);
                            return true;
                        });
                promises.add(mutualPromise);
            }
        }

        return combinePromises(promises).map(list -> {
            PlayJongo.getCollection("friends_cache").insert(addToFriendCache.toArray());
            PlayJongo.getCollection("mutual_friends_cache").insert(addToMutualFriendCache.toArray());
            return true;
        });
    }

    // true if cache exists
    private Boolean isFriendsCached (String userId1, String userId2) {
        return friendCache.stream().filter(cached -> cached.hasUsers(userId1, userId2)).count() > 0;
    }

    // true if cache exists
    private Boolean mutualFriendsCached (String userId1, String userId2) {
        return mutualFriendCache.stream().filter(friends -> friends.containsUsers(userId1, userId2)).count() > 0;
    }

    @SuppressWarnings("unchecked")
    private F.Promise<List<Boolean>> combinePromises(List<F.Promise<Boolean>> promiseList) {
        F.Promise<Boolean>[] promiseArray = new F.Promise[promiseList.size()];
        for (int i=0; i<promiseList.size(); i++) {
            promiseArray[i] = promiseList.get(i);
        }
        return F.Promise.sequence(promiseArray);
    }

}
