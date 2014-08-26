package classes;

import models.Chai;
import models.User;
import types.ChaiResults;
import types.Match;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by kedar on 8/22/14.
 */
public class MatchingAlgoStats {

    private Query query = new Query();
    private PincodeHandler pincodeHandler;

    public MatchingAlgoStats () {
        pincodeHandler = PincodeHandler.getInstance();
    }

    public void stats () {
        List<Chai> chais = query.chais();
        List<Match> matches = query.matchesNoNames();
        Map<String, User> users = query.users().stream().collect(Collectors.toMap(User::getUserId, user -> user));
        Map<String, ChaiResults> results = query.chaiResults();
        for (Chai chai: chais) {
            User receiver = users.get(chai.getReceiver());
            User target = users.get(chai.getTarget());
            double distance = pincodeHandler.distance(receiver.getPincode(), target.getPincode());
        }

        // Get correlations for each individual  item
        // Weight correlation values into algorithm

    }

}
