package classes;

import clients.LetsChaiFacebookClient;
import models.Friends;
import models.User;
import play.Logger;
import play.libs.F;

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
        users.remove(0);
        return cache(user).flatMap(bool -> cache(users));
    }

    public F.Promise<Boolean> cache (User user) {
        String userId = user.getUserId();
        LetsChaiFacebookClient fb = new LetsChaiFacebookClient(user.getAccessToken().getAccessToken());
        List<Friends> cache = query.friends(userId);
        List<F.Promise<Friends>> promises = new ArrayList<>();

        Logger.info("Caching user " + userId);

        List<User> users = query.users();
        List<User> toCache = users.stream().filter(candidate -> checker.associatives(user, candidate, cache))
                .collect(Collectors.toList());

        for (User candidate: toCache) {
            String candidateId = candidate.getUserId();

            // ignore repeats for now
            if (cache.contains(new Friends(Arrays.asList(userId, candidateId))))
                continue;

            // API calls
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
