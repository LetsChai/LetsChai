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

public class SecretChaiSauce {

    private PincodeHandler pincodeHandler;
    private LetsChaiScorer scorer;
    private LetsChaiBooleanChecker checker;
    private Query query;

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

        // do you already have a chai for today? Let's say for now that there must be atleast 22 hours between generated chais
        List<Chai> chais = query.chais(userId);
        DateTime yesterday = new DateTime().minusHours(22);
        if (chais.stream().filter(chai -> chai.getReceiver().equals(userId) && new DateTime(chai.getReceived()).isAfter(yesterday)).count() > 0) {
            Logger.info("already has chai");
            return null;
        }

        // do you have a lover out there waiting for you?
        List<Chai> youLikeMe = chais.stream().filter(chai -> chai.getTarget().equals(userId) && chai.getDecision()
                && !chais.contains(new Chai(chai.getTarget(), chai.getReceiver(), 0.0)))
                .collect(Collectors.toList());
        if (youLikeMe.size() > 0)
            return new Chai(userId, youLikeMe.stream().max(Chai::compare).get().getReceiver(), 0.51);

        // filter out the unwanteds
        List<Friends> friends = query.friends(userId);
        List<User> candidates = query.users().stream().filter(candidate -> checker.associatives(user, candidate, friends)
                && checker.nonAssociatives(user, candidate, chais))
                .collect(Collectors.toList());
        if (candidates.size() == 0) { // for the rare match-less user
            Logger.info("match-less user");
            return null;
        }

        // do you have mutual friends with someone?
        List<Friends> weHaveFriends = friends.stream().filter(friend -> friend.getCount() > 0
                && candidates.contains(new User(friend.getOtherUser(userId))))
                .collect(Collectors.toList());
        if (weHaveFriends.size() > 0) {
            Friends best = weHaveFriends.stream().max(Friends::compare).get();
            return new Chai(userId, best.getOtherUser(userId), scorer.mutualFriendScore(best));
        }

        // do you atleast have a valid pincode?
        if (pincodeHandler.valid(user.getPincode())) {
            // cool get the nearest user with a valid pincode
            Integer userPin = user.getPincode();
            F.Tuple<User,Double> winner = candidates.stream().filter(candidate -> pincodeHandler.valid(user.getPincode()))
                    .map(candidate -> new F.Tuple<User,Double>(candidate, scorer.distanceScore(pincodeHandler.distance(userPin, candidate.getPincode()))))
                    .max((t1, t2) -> Double.compare(t1._2, t2._2)).get();
            return new Chai(userId, winner._1.getUserId(), winner._2);
        }

        // fine, you better hope someone has the same shitty pincode as you
        Random random = new Random();
        List<User> samePincode = candidates.stream().filter(candidate -> user.getPincode() == candidate.getPincode())
                .collect(Collectors.toList());
        if (samePincode.size() > 0)
            return new Chai(userId, samePincode.get(random.nextInt(samePincode.size())).getUserId(), 0.14);

        // fuck it you're a loser, you get a rando, congratulations
        return new Chai(userId, candidates.get(random.nextInt(candidates.size())).getUserId(), 0.05);
    }
}