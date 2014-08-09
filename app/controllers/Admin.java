package controllers;

import actions.Auth;
import classes.FriendCacher;
import classes.PincodeHandler;
import classes.Query;
import com.google.common.collect.Lists;
import models.User;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import uk.co.panaxiom.playjongo.PlayJongo;
import views.html.admin;

import java.util.List;

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
        return ok(admin.render(q.users()));
    }
}
