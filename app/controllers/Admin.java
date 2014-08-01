package controllers;

import classes.FriendCacher;
import classes.UserHandler;
import clients.LetsChaiFacebookClient;
import com.google.common.collect.Lists;
import models.User;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import models.Friends;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kedar on 7/23/14.
 */
public class Admin extends Controller {

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
        List<User> all = UserHandler.getInstance().all();
        FriendCacher cacher = FriendCacher.forUser(all.get(number));
        return cacher.update(all).map(bool -> ok());
    }

    public static Result deleteUser (String userId) {
        User user = User.findOne(userId);
        PlayJongo.getCollection("deleted_users").save(user);
        User.getCollection().remove("{'userId':'#'}", userId);
        return ok("deleted " + userId, ": " + user.getName());
    }

    public static Result restoreUser (String userId) {
        User user = PlayJongo.getCollection("deleted_users").findOne("{'userId': '#'}", userId).as(User.class);
        User.getCollection().save(user);
        PlayJongo.getCollection("deleted_users").remove("{'userId': '#'}", userId);
        return ok("restored " + userId, ": " + user.getName());
    }

}
