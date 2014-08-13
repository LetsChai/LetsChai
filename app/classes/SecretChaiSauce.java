package classes;

import com.google.common.collect.Lists;
import models.Chai;
import models.User;
import org.joda.time.DateTime;
import play.Logger;
import play.libs.F;
import models.Friends;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by kedar on 5/24/14.
 */

public class SecretChaiSauce implements MatchingAlgo {

    private PincodeHandler pincodeHandler;
    private LetsChaiScorer scorer;
    private LetsChaiBooleanChecker checker;
    private Query query;
    private Random random = new Random();

    public SecretChaiSauce (PincodeHandler pincodeHandler) {
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

        // do you have a lover out there waiting for you?
        List<String> unfilteredLoverIds = chais.stream().filter(chai -> chai.getTarget().equals(userId) && chai.getDecision()
                && !chais.contains(new Chai(chai.getTarget(), chai.getReceiver(), 0.0)))
                .map(Chai::getReceiver)
                .collect(Collectors.toList());
        List<User> filteredLovers = filtered(user, query.users(unfilteredLoverIds), friends, chais);

        // if there's just one lover, they the one
        if (filteredLovers.size() == 1)
            return new Chai(userId, filteredLovers.get(0).getUserId(), score(friends, user, filteredLovers.get(0), true));

        // if there's more than one, let the lovers battle it out
        else if (filteredLovers.size() > 1)
            return defaultToFriends(user, filteredLovers, friends, true);

        // no lovers? grab a fresh filtered pile and send them away
        else {
            return defaultToFriends(user, filtered(user, query.users(), friends, chais), friends, false);
        }
    }

    // filter candidates with the boolean checker
    public List<User> filtered (User user, List<User> unfiltered, List<Friends> friends, List<Chai> chais) {
        return unfiltered.stream().filter(candidate -> checker.associatives(user, candidate, friends)
            && checker.nonAssociatives(user, candidate, chais))
            .collect(Collectors.toList());
    }

    private Chai defaultToFriends (User user, List<User> candidates, List<Friends> friends, boolean hasMatch) {
        String userId = user.getUserId();

        // filter for mutual friends
        List<Friends> weHaveFriends = friends.stream().filter(friend -> friend.getCount() > 0
                && candidates.contains(new User(friend.getOtherUser(userId))))
                .collect(Collectors.toList());

        // if you have one mutual friend match, score and return!
        if (weHaveFriends.size() == 1) {
            String winnerId = weHaveFriends.get(0).getOtherUser(userId);
            User winner = candidates.stream().filter(candidate -> candidate.getUserId().equals(winnerId)).findFirst().get();
            return new Chai(userId, winnerId, score(friends, user, winner, hasMatch));
        }
        // if you have mutliple mutual friend matches, rank them
        if (weHaveFriends.size() > 0) {
            List<User> toRank = candidates.stream()
                    .filter(candidate -> weHaveFriends.contains(new Friends(Arrays.asList(userId, candidate.getUserId()))))
                    .collect(Collectors.toList());
            return defaultToDistance(user, toRank, friends, hasMatch);
        }

        // if not, off to distance you go
        return defaultToDistance(user, candidates, friends, hasMatch);
    }

    private Chai defaultToDistance (User user, List<User> candidates, List<Friends> friends, boolean hasMatch) {
        if (!pincodeHandler.valid(user.getPincode()))
            return defaultToPincode(user, candidates);

        // get the nearest user with a valid pincode
        UserScore winner = candidates.stream().filter(candidate -> pincodeHandler.valid(user.getPincode()))
                .map(candidate -> new UserScore(candidate, score(friends, user, candidate, hasMatch)))
                .max((t1, t2) -> Double.compare(t1.score, t2.score)).get();
        return new Chai(user.getUserId(), winner.user.getUserId(), winner.score);
    }

    private Chai defaultToPincode (User user, List<User> candidates) {
        List<User> samePincode = candidates.stream().filter(candidate -> user.getPincode() == candidate.getPincode())
                .collect(Collectors.toList());
        if (samePincode.size() > 0)
            return new Chai(user.getUserId(), samePincode.get(random.nextInt(samePincode.size())).getUserId(), 0.14);
        else
            return defaultToRandom(user, candidates);
    }

    private Chai defaultToRandom (User user, List<User> candidates) {
        return new Chai(user.getUserId(), candidates.get(random.nextInt(candidates.size())).getUserId(), 0.05);
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

    // scoring convenience method
    private double score(Friends friends, User user1, User user2, boolean hasMatch) {
        double distance = pincodeHandler.distance(user1.getPincode(), user2.getPincode());
        return scorer.score(friends, distance, hasMatch);
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