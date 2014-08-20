package classes;

import clients.LetsChaiFacebookClient;
import models.Friends;
import models.User;
import org.joda.time.DateTime;
import play.Logger;
import play.libs.F;
import types.Permission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kedar on 7/28/14.
 */
public class FriendCacher {
    LetsChaiBooleanChecker checker;
    Query query;

    public FriendCacher (PincodeHandler pincodeHandler) {
        checker = new LetsChaiBooleanChecker(pincodeHandler);
        query = new Query();
    }

    // I'm a recursive boss
    public F.Promise<Boolean> cache (List<User> users) {
        if (users.size() == 0)
            return F.Promise.pure(true);

        User user = users.get(0);

        List<User> clone = new ArrayList<>(users);
        clone.remove(0);
        return cache(user).flatMap(bool -> cache(clone));
    }

    public F.Promise<Boolean> cache (User user) {
        String userId = user.getUserId();

        // check to see if the access token is valid
        if(new DateTime(user.getAccessToken().getExpires()).isBeforeNow()) {
            Logger.info("Access token for " + userId + " is expired");
            return F.Promise.pure(false);
        }
        // check to see if user is valid
        if (!checker.individuals(user)) {
            Logger.info("Invalid user " + userId);
            return F.Promise.pure(false);
        }
        // check to see if user has given friends permission
        if (!user.getPermissions().contains(Permission.USER_FRIENDS)) {
            Logger.info("No friends permission: " + userId);
            return F.Promise.pure(false);
        }

        LetsChaiFacebookClient fb = new LetsChaiFacebookClient(user.getAccessToken().getAccessToken());
        List<Friends> cache = query.friends(userId);
        List<F.Promise<Friends>> promises = new ArrayList<>();

        Logger.info("Caching user " + userId);

        List<User> users = query.users();
        List<User> toCache = users.stream().filter(candidate -> checker.associatives(user, candidate, cache))
                .collect(Collectors.toList());
        for (User candidate: toCache) {
            String candidateId = candidate.getUserId();

            // update repeats if they're more than 3 days old, then continue on regardless
            Friends compare = new Friends(Arrays.asList(userId, candidateId));
            List<Friends> cacheMatch = cache.stream().filter(item -> item.equals(compare))
                    .collect(Collectors.toList());
            if (cacheMatch.size() > 0) {
                Friends inCache = cacheMatch.get(0);

                // check how long ago the data was cached, update it if it's too old
                DateTime cutoff = new DateTime().minusDays(3);
                if (new DateTime(inCache.getTimestamp()).isBefore(cutoff)) {
                    Logger.info("Renewing for friend " + candidateId);
                    fb.getMutualFriendsJSON(candidateId)
                            .zip(fb.areFriends(userId, candidate.getUserId()))
                            .onRedeem(t -> {
                                inCache.setMutualFriends(t._1);
                                inCache.setFriends(t._2);
                                query.updateFriends(inCache);
                            });
                }

                // continue on all cached items
                continue;
            }

            // API calls
            Logger.info("Getting fresh for friend " + candidateId);
            F.Promise<Friends> promise = fb.getMutualFriends(userId, candidateId)
                    .zip(fb.areFriends(userId, candidate.getUserId()))
                    .map(t -> {
                        t._1.setFriends(t._2);
                        return t._1;
                    });
            promises.add(promise);
        }

        return combinePromises(promises).map(list -> {
            query.insertFriends(list);
            Logger.info("Finished caching user " + userId);
            return true;
        });
    }

    @SuppressWarnings("unchecked")
    private F.Promise<List<Friends>> combinePromises(List<F.Promise<Friends>> promiseList) {
        F.Promise<Friends>[] promiseArray = new F.Promise[promiseList.size()];
        for (int i=0; i<promiseList.size(); i++) {
            promiseArray[i] = promiseList.get(i);
        }
        return F.Promise.sequence(promiseArray);
    }

}
