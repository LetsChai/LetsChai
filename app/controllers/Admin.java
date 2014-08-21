package controllers;

import actions.Auth;
import classes.FriendCacher;
import classes.PincodeHandler;
import classes.Query;
import classes.UserStatistics;
import com.google.common.collect.Lists;
import models.Chai;
import models.Friends;
import models.Message;
import models.User;
import org.joda.time.DateTime;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import types.ChaiResults;
import types.Flag;
import types.Match;
import uk.co.panaxiom.playjongo.PlayJongo;
import views.html.admin;
import views.html.chai;
import views.html.profile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by kedar on 7/23/14.
 */
@Auth.Admin
public class Admin extends Controller {

    @SuppressWarnings("unchecked")
    public static F.Promise<Result> updateAllPermissions () {
        List<User> users = Lists.newArrayList(User.findAll());

        F.Promise<Boolean>[] promiseArray = new F.Promise[users.size()];
        for (int i=0; i < users.size(); i++) {
            promiseArray[i] = users.get(i).updatePermissions();
        }
        F.Promise<List<Boolean>> combinedPromise = F.Promise.sequence(promiseArray);
        return combinedPromise.map(list -> {
            PlayJongo.getCollection("users_permissions").insert(users.toArray());
            return ok();
        });
    }

    // cache friends and mutualfriends
    public static F.Promise<Result> cacheFBData (int number) {
        List<User> all = new Query().users();
        FriendCacher cacher = new FriendCacher(PincodeHandler.getInstance());
        return cacher.cache(all).map(bool -> ok());
    }

    public static Result deleteUser (String userId) {
        User user = User.findOne(userId);
        PlayJongo.getCollection("deleted_users").save(user);
        User.getCollection().remove("{'userId':'#'}", userId);
        return ok("deleted " + userId + ": " + user.getName());
    }

    public static Result restoreUser (String userId) {
        User user = PlayJongo.getCollection("deleted_users").findOne("{'userId': '#'}", userId).as(User.class);
        User.getCollection().save(user);
        PlayJongo.getCollection("deleted_users").remove("{'userId': '#'}", userId);
        return ok("restored " + userId + ": " + user.getName());
    }

    public static Result users () {
        Query q = new Query();
        Map<String,User> users = q.users().stream().collect(Collectors.toMap(User::getUserId, user -> user));
        UserStatistics stats = new UserStatistics(users.values().stream().collect(Collectors.toList()));
        Map<String,Chai> chais = q.todaysChais().stream().collect(Collectors.toMap(Chai::getReceiver, chai -> chai));
        List<Match> matches = q.matchesNoNames();
        Map<String, ChaiResults> rates = q.chaiResults();
        return ok(admin.render(users, stats, chais, matches, q, rates));
    }

    public static Result chai (String userId) {
        Query query =  new Query();
        Chai todaysChai = query.todaysChai(userId);

        if (todaysChai == null)   // no chai today!
            return ok("no chai today");

        User myMatch = User.findOne(todaysChai.getTarget());
        Friends friends = query.friends(userId, todaysChai.getTarget());
        if (friends == null)
            friends = new Friends(Arrays.asList(userId, myMatch.getUserId()));
        return ok(chai.render(myMatch, todaysChai, friends, 0));
    }

    public static Result profile (String userId) {
        User user = User.findOne(userId);
        return ok(profile.render(user));
    }

    public static Result forceMutualChai (String user1, String user2) {
        Query query = new Query();
        query.saveChai(new Chai(user1, user2, 0.49));
        query.saveChai(new Chai(user2, user1, 0.49));
        return ok("Successfully created chai");
    }

    public static Result deactivateUser (String userId) {
        Query query = new Query();
        query.pushFlag(userId, Flag.DEACTIVATED);
        query.pushFlag(userId, Flag.ADMIN_DEACTIVATED);
        return ok("successfully deactivated user");
    }

    public static Result activateUser (String userId) {
        Query query = new Query();
        query.deleteFlag(userId, Flag.DEACTIVATED);
        query.deleteFlag(userId, Flag.ADMIN_DEACTIVATED);
        return ok("successfully activated user");
    }

    public static Result repeatNoDecisionChais () {
        Query q = new Query();
        List<Chai> chais = q.todaysChaisNoDecision();
        Map<String,User> users = q.users().stream().collect(Collectors.toMap(User::getUserId, user -> user));
        for (Chai chai: chais) {
            if (new DateTime(chai.getReceived()).isAfterNow())
                continue;
            DateTime lastLogin = new DateTime(users.get(chai.getReceiver()).getLastLogin());
            DateTime yesterday = new DateTime().minusDays(1);
            if (!chai.hasDecided() && lastLogin.isBefore(yesterday)) {
                chai.repeatDate();
                q.updateChai(chai);
            }
        }
        return ok("successfully repeated chais");
    }

    public static Result forceMatch (String userId1, String userId2) {
        Query q = new Query();
        if (q.todaysChai(userId1) != null || q.todaysChai(userId2) != null)
            return ok("Chai already exists for specified user");

        List<Chai> chais = Arrays.asList(new Chai(userId1, userId2, 0), new Chai(userId2, userId1,0));
        chais.get(0).backdate();
        chais.get(1).backdate();
        chais.get(0).setDecision(true);
        chais.get(1).setDecision(true);
        q.insertChais(chais);
        return ok("successfully forced match");
    }
}
