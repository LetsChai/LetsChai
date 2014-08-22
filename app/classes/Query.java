package classes;

import com.google.common.collect.Lists;
import de.undercouch.bson4jackson.types.ObjectId;
import models.*;
import org.joda.time.DateTime;
import org.jongo.MongoCollection;
import org.jongo.Oid;
import play.Logger;
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
    private MongoCollection DELETED_USERS = PlayJongo.getCollection("deleted_users");

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
        List<Chai> likes = Lists.newArrayList(CHAIS.find("{'receiver': '#', 'decision': true}", userId).as(Chai.class));
        List<Chai> likeBacks = Lists.newArrayList(CHAIS.find("{'target': '#', 'decision': true}", userId).as(Chai.class));
        List<Match> matches = new ArrayList<>();
        List<String> matchIds = new ArrayList<>();
        for (Chai like: likes) {
            for (Chai likeBack: likeBacks) {
                if (like.getTarget().equals(likeBack.getReceiver())) {
                    matches.add(new Match(like, likeBack));
                    matchIds.add(like.getTarget());
                }
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

    public List<Match> matchesNoNames () {
        Set<Match> matches = new HashSet<>();
        List<Chai> chais = likeChais();
        for (Chai chai1: chais) {
            for (Chai chai2: chais) {
                if (chai1.getReceiver().equals(chai2.getTarget()) && chai1.getTarget().equals(chai2.getReceiver())) {
                    matches.add(new Match(chai1, chai2));
                }
            }
        }
        return matches.stream().collect(Collectors.toList());
    }

    public List<Chai> chais () {
        return Lists.newArrayList(CHAIS.find().as(Chai.class));
    }

    public List<Chai> likeChais () {
        return Lists.newArrayList(CHAIS.find("{'decision':true}").as(Chai.class));
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

    public long userCount () {
        return USERS.count();
    }

    public List<User> randomUsers (int amount) {
        Random random = new Random();

        int count = (int) userCount();
        List<Integer> randoms = new ArrayList<>();
        for (int i=0; i < 5; i++) {
            randoms.add(random.nextInt(count));
        }

        List<User> users = new ArrayList<>();
        for (Integer i: randoms) {
            users.add(USERS.find().skip(i).limit(1).as(User.class).iterator().next());
        }

        return users;
    }

    public void updateFriends (Friends friends) {
        FRIENDS.save(friends);
    }

    public long readyUserCount () {
        return USERS.count("{'flags':'READY_TO_CHAI'}");
    }

    public List<Chai> todaysChais () {
        Date yesterday = new DateTime().minusDays(1).toDate();
        return Lists.newArrayList(CHAIS.find("{'received': {'$gt': #} }", yesterday).as(Chai.class));
    }

    public List<User> users (List<String> ids) {
        return Lists.newArrayList(USERS.find("{'userId': {'$in': # }}", ids).as(User.class));
    }

    public void deleteUser (User user) {
        DELETED_USERS.save(user);
        USERS.remove("{'userId': '#'}", user.getUserId());
    }

    public List<Chai> todaysChaisNoDecision () {
        Date yesterday = new DateTime().minusDays(1).toDate();
        return Lists.newArrayList(CHAIS.find("{'decided': {'$exists':false}, 'received': {'$gt': #}}", yesterday).as(Chai.class));
    }

    public void updateChai (Chai chai) {
        CHAIS.save(chai);
    }

    public void saveMessage (Message message) {
        Logger.info("Saving message");
        MESSAGES.save(message);
        addUserNotification(message.getTo(), message.getFrom());
    }

    public void insertChais (List<Chai> chais) {
        CHAIS.insert(chais.toArray());
    }

    public Chai chai (String receiver, String target) {
        return CHAIS.findOne("{'receiver': '#', 'target': '#'}", receiver, target).as(Chai.class);
    }

    public List<Message> messages () {
        return Lists.newArrayList(MESSAGES.find().as(Message.class));
    }

    public boolean hasMessaged (String from, String to) {
        return MESSAGES.find("{'from': '#', 'to': '#'}", from , to).as(Message.class).iterator().hasNext();
    }

    public Map<String,ChaiResults> chaiResults () {
        List<Chai> chais = chais();
        Map<String, ChaiResults> results = new HashMap<>();
        for (Chai chai: chais) {
            try {
                results.get(chai.getTarget()).addChai(chai);
            } catch (NullPointerException e) {
                ChaiResults r = new ChaiResults(chai.getTarget());
                r.addChai(chai);
                results.put(chai.getTarget(), r);
            }
        }
        return results;
    }

    public void addUserNotification (String userId, String fromId) {
        USERS.update("{'userId': '#'}", userId).with("{'$push':{'chatNotifications':'#'}}", fromId);
    }

    public void clearUserNotifications (String userId) {
        USERS.update("{'userId': '#'}", userId).with("{'$set': {'chatNotifications':[] } }");
    }

    public void removeUserNotification (String userId, String fromId) {
        USERS.update("{'userId': '#'}", userId).with("{'$pull':{'chatNotifications':'#'}}", fromId);
    }
}
