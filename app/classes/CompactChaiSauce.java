package classes;

import models.Chai;
import models.Friends;
import models.User;
import org.joda.time.DateTime;
import play.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by kedar on 5/24/14.
 */

public class CompactChaiSauce implements MatchingAlgo {

    private PincodeHandler pincodeHandler;
    private LetsChaiScorer scorer;
    private LetsChaiBooleanChecker checker;
    private Query query;

    public CompactChaiSauce(PincodeHandler pincodeHandler) {
        this.pincodeHandler = pincodeHandler;
        scorer = new LetsChaiScorer();
        checker = new LetsChaiBooleanChecker(pincodeHandler);
        query = new Query();
    }

    // returns null if there's no chai, should be changed to an exception later
    public Chai nextChai (User user) {
        String userId = user.getUserId();
        Logger.info("Getting next chai for " + userId);

        // got to be valid
        if (!checker.individuals(user)) {
            Logger.info("Invalid user");
            return null;
        }

        // queries
        List<Chai> chais = query.chais(userId);
        List<Friends> friends = query.friends(userId);

        // check for repeat chai (22 hour minimum between chais)
        DateTime yesterday = new DateTime().minusHours(22);
        if (chais.stream().filter(chai -> chai.getReceiver().equals(userId) && new DateTime(chai.getReceived()).isAfter(yesterday)).count() > 0) {
            Logger.info("already has chai");
            return null;
        }

        List<User> candidates = filtered(user, query.users(), friends, chais);
        List<String> usersLikeMe = chais.stream().filter(Chai::getDecision)
                .map(Chai::getReceiver).collect(Collectors.toList());
        try {
            UserScore winner = candidates.stream()
                    .map(cand -> new UserScore(cand, score(friends, user, cand, usersLikeMe.contains(cand.getUserId()))))
                    .max((t1, t2) -> Double.compare(t1.score, t2.score)).get();
            return new Chai(userId, winner.user.getUserId(), winner.score);
        } catch (NoSuchElementException e) {
            Logger.info("No matches for " + userId);
            return null;
        }
    }

    // filter candidates with the boolean checker
    public List<User> filtered (User user, List<User> unfiltered, List<Friends> friends, List<Chai> chais) {
        return unfiltered.stream().filter(candidate -> checker.associatives(user, candidate, friends)
            && checker.nonAssociatives(user, candidate, chais))
            .collect(Collectors.toList());
    }

    // scoring convenience method
    private double score (List<Friends> friendList, User user1, User user2, boolean hasMatch) {
        double distance = pincodeHandler.distance(user1.getPincode(), user2.getPincode());
        Friends eqFriend = new Friends(Arrays.asList(user1.getUserId(), user2.getUserId()));
        for (Friends f: friendList) {
            if (f.equals(eqFriend)) {
                return scorer.score(f, distance, hasMatch);
            }

        }
        return scorer.partialScore(distance, hasMatch);
    }

    // convenience tuple
    private class UserScore {
        public User user;
        public double score;

        public UserScore(User user, double score) {
            this.user = user;
            this.score = score;
        }
    }
}