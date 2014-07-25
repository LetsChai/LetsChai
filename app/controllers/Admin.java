package controllers;

import clients.LetsChaiFacebookClient;
import com.google.common.collect.Lists;
import models.User;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import types.Friends;
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
    public static F.Promise<Result> cacheFBData () {
        List <User> users = Lists.newArrayList(User.findAll());
        List<F.Promise<Friends>> promiseList = new ArrayList<>();

        // filter users
        Integer lowerLimit = 130;
        Integer upperLimit = 150;
        List<User> first10Users = new ArrayList<>();
        for (int i=0; i<users.size(); i++) {
            if (i%20 == 10)
                first10Users.add(users.get(i));
        }

        // previously queried list
        Map<String, List<String>> queried = new HashMap<>();
        for (User user: users) {
            queried.put(user.getUserId(), new ArrayList<String>());
        }

        for (User user: first10Users) {
            LetsChaiFacebookClient fb = new LetsChaiFacebookClient(user.getAccessToken().getAccessToken());
            String userId = user.getUserId();
            for (User partner: users) {
                String partnerId = partner.getUserId();

                if (queried.get(userId).contains(partnerId) || queried.get(partnerId).contains(userId))
                    continue;

                F.Promise<Friends> friend = fb.getMutualFriendsWithUsers(userId, partnerId);
                promiseList.add(friend);
                queried.get(userId).add(partnerId);
            }
        }

        // convert promiseList to single promise
        F.Promise<Friends>[] promiseArray = new F.Promise[promiseList.size()];
        for (int i=0; i < promiseList.size(); i++) {
            promiseArray[i] = promiseList.get(i);
        }
        F.Promise<List<Friends>> combinedPromise = F.Promise.sequence(promiseArray);

        return combinedPromise.map(list -> {
            PlayJongo.getCollection("friends_cache").insert(list.toArray());
            return ok(String.valueOf(promiseList.size()));
        });
    }

}
