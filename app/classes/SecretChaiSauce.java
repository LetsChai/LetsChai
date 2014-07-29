package classes;

import models.Chai;
import models.User;
import play.libs.F;
import types.Flag;
import models.Friends;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by kedar on 5/24/14.
 */

public class SecretChaiSauce {

    private static SecretChaiSauce INSTANCE;

    private final Double MINIMUM_REQUIRED_SCORE = 0.01;

    private UserHandler userHandler;
    private ChaiHandler chaiHandler;
    private FriendHandler friendHandler;
    private PincodeHandler pincodeHandler;
    private LetsChaiBooleanChecker checker;
    private LetsChaiScorer scorer;

    public static SecretChaiSauce getInstance () {
        if (INSTANCE != null)
            return INSTANCE;

        INSTANCE = new SecretChaiSauce(UserHandler.getInstance(), PincodeHandler.getInstance(), FriendHandler.getInstance(), ChaiHandler.getInstance());
        return INSTANCE;
    }

    private SecretChaiSauce (UserHandler userHandler, PincodeHandler pincodeHandler, FriendHandler friendHandler, ChaiHandler chaiHandler) {
        this.userHandler = userHandler;
        this.pincodeHandler = pincodeHandler;
        this.friendHandler = friendHandler;
        this.chaiHandler = chaiHandler;
        checker = new LetsChaiBooleanChecker(friendHandler, pincodeHandler, chaiHandler);
        scorer = new LetsChaiScorer();
    }

    public void run () {
        List<Chai> todaysChais = new ArrayList<>();

        userHandler.valid().stream()
                .forEach(user -> user.addFlags(pincodeHandler.flags(user.getPincode())));

        List<User> validUsers = userHandler.valid();
        for (User user: validUsers) {
            Chai best;
            try {
                best = validUsers.stream().filter(candidate -> checker.associatives(user, candidate))
                        .filter(candidate -> checker.nonAssociatives(user, candidate))
                        .map(candidate -> chai(user, candidate))
                        .max(chaiHandler::compare).get();
            } catch (NoSuchElementException e) {    // thrown by max if no chais are present
                continue;
            }

            // don't save really bad matches
            if (best.getScore() < MINIMUM_REQUIRED_SCORE)
                continue;

            todaysChais.add(best);
        }

        userHandler.overwrite();
        chaiHandler.insert(todaysChais);
    }

    private Chai chai (User user, User candidate) {
        String userId = user.getUserId();
        String candidateId = candidate.getUserId();

        Friends mutuals = friendHandler.mutualFriends(userId, candidateId);
        Double distance = pincodeHandler.distance(user.getPincode(), candidate.getPincode());
        Boolean match = chaiHandler.likedMatchExists(candidateId, userId);

        Double score;
        if (mutuals == null)
            score = scorer.partialScore(distance, match);
        else
            score = scorer.score(mutuals, distance, match);

        return new Chai(user, candidate, score, mutuals);
    }


}