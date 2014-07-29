package classes;

import com.google.common.collect.Lists;
import models.Chai;
import org.joda.time.DateTime;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kedar on 7/25/14.
 */
public class ChaiHandler {

    private static ChaiHandler INSTANCE;

    private List<Chai> chais;

    public static ChaiHandler getInstance () {
        if (INSTANCE != null)
            return INSTANCE;

        Iterable<Chai> docs = PlayJongo.getCollection("chais").find().as(Chai.class);
        List<Chai> chais = new ArrayList<>();
        for (Chai doc: docs) {
            chais.add(doc);
        }

        INSTANCE = new ChaiHandler(chais);
        return INSTANCE;
    }

    public static List<Chai> getMatches (String userId) {
        List<Chai> chais = Lists.newArrayList(PlayJongo.getCollection("chais").find("{'halfChais.#.decision': true}", userId).as(Chai.class));
        return chais.stream().filter(chai -> chai.isMatch()).collect(Collectors.toList());
    }

    public static Chai todaysChai (String userId) {
        Date oneDayAgo = new DateTime().minusDays(1).toDate();
        return PlayJongo.getCollection("chais").findOne("{'halfChais.#.received': {'$gt': #} }", userId, oneDayAgo).as(Chai.class);
    }

    public static void setTodaysDecision (String userId, Boolean decision) {
        Date oneDayAgo = new DateTime().minusDays(1).toDate();
        PlayJongo.getCollection("chais").update("{'halfChais.#.received':{'$gt': #}}", userId, oneDayAgo)
                .with("{$set: {'decision': #, decided: #}}", decision, new Date());
    }

    public ChaiHandler (List<Chai> chais) {
        this.chais = chais;
    }

    public Boolean exists (String userId1, String userId2) {
        for (Chai chai: chais) {
            if (chai.hasUsers(userId1, userId2))
                return true;
        }
        return false;
    }

    public Chai get (String userId1, String userId2) {
        for (Chai chai: chais) {
            if (chai.hasUsers(userId1, userId2))
                return chai;
        }
        return null;
    }

    // true if userId1 has a Chai for userId2 and userId1 liked userId2
    public Boolean likedMatchExists (String userId1, String userId2) {
        if (!exists(userId1, userId2))
            return false;
        Chai chai = get(userId1, userId2);
        return chai.getDecision(userId1);
    }

    public int compare (Chai chai1, Chai chai2) {
        Double score = chai1.getScore() - chai2.getScore();
        if (score == 0)
            return 0;
        if (score < 0)
            return -1;
        return 1;
    }

    public void insert (List<Chai> chais) {
        PlayJongo.getCollection("chais").insert(chais.toArray());
    }

}
