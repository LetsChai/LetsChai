package classes;

import com.google.common.collect.Lists;
import de.undercouch.bson4jackson.types.ObjectId;
import models.*;
import org.joda.time.DateTime;
import org.jongo.MongoCollection;
import org.jongo.Oid;
import types.*;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by kedar on 8/1/14.
 */
public class Query {
    private MongoCollection CHAIS = PlayJongo.getCollection("chais");
    private MongoCollection USERS = PlayJongo.getCollection("users");
    private MongoCollection FRIENDS = PlayJongo.getCollection("friends");
    private MongoCollection MESSAGES = PlayJongo.getCollection("chats");

    public List<Chai> chais (String userId) {
        return Lists.newArrayList(CHAIS.find("{'$or': [{'receiver': '#'}, {'target': '#'}] }", userId, userId).as(Chai.class));
    }

    public List<User> users () {
        return Lists.newArrayList(USERS.find().as(User.class));
    }

    public List<Friends> friends (String userId) {
        return Lists.newArrayList(FRIENDS.find("{'users':'#'}", userId).as(Friends.class));
    }

    public Friends friends (String userId1, String userId2) {
        return FRIENDS.findOne("{'users': {'$all': ['#', '#'] }}", userId1, userId2).as(Friends.class);
    }

    public void saveChai (Chai chai) {
        CHAIS.save(chai);
    }

    public Chai todaysChai (String userId) {
        Date yesterday = new DateTime().minusDays(1).toDate();
        return CHAIS.findOne("{'receiver': '#', 'received': {'$gt': #}}", userId, yesterday).as(Chai.class);
    }

    public void updateChai (String userId, Chai chai) {
        Date yesterday = new DateTime().minusDays(1).toDate();
        CHAIS.update("{'receiver': '#', 'received': {'$gt': #}}", userId, yesterday).with(chai);
    }

    public List<Match> matches (String userId) {
        Iterable<Chai> likes = CHAIS.find("{'receiver': '#', 'decision': true}", userId).as(Chai.class);
        Iterable<Chai> likeBacks = CHAIS.find("{'target': '#', 'decision': true}", userId).as(Chai.class);
        List<Match> matches = new ArrayList<>();
        List<String> matchIds = new ArrayList<>();
        for (Chai like: likes) {
            for (Chai likeBack: likeBacks) {
                if (like.getTarget().equals(likeBack.getReceiver()))
                    matches.add(new Match(like, likeBack));
                    matchIds.add(like.getTarget());
            }
        }
        for (User user: USERS.find("{'userId': {'$in': #}}", matchIds).as(User.class)) {
            for (Match match: matches) {
                if (match.getTargeted().getReceiver().equals(user.getUserId()))
                    match.setTargetName(user.getName());
            }
        }

        return matches;
    }

    public List<Message> messages (String userId) {
         return Lists.newArrayList(MESSAGES.find("{'$or':[{'from': '#'}, {'to': '#'}]}", userId, userId).as(Message.class));
    }

    public void insertFriends (List<Friends> friends) {
        FRIENDS.insert(friends.toArray());
    }

    public List<User> newUsers () {
        return Lists.newArrayList(USERS.find("{'flags': 'NEW_USER'}").as(User.class));
    }

    public void updatePermissions (String userId, List<Permission> permissions) {
        USERS.update("{'userId': '#'}", userId).with("{'$set': {'permissions': #}}", permissions);
    }

    public void updatePermissions (String userId, EnumSet<Permission> permissions) {
        USERS.update("{'userId': '#'}", userId).with("{'$set': {'permissions': #}}", permissions);
    }

    public void pushFlag (String userId, Flag flag) {
        USERS.update("{'userId': '#'}", userId).with("{'$push': {'flags': #}}", flag);
    }

    public void deleteFlag (String userId, Flag flag) {
        USERS.update("{'userId': '#'}", userId).with("{'$pull': {'flags': #}}", flag);
    }
}
