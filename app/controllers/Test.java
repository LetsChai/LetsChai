package controllers;

import classes.ChaiHandler;
import classes.SecretChaiSauce;
import clients.LetsChaiFacebookClient;
import com.google.common.collect.Lists;
import models.*;
import play.Play;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import models.Friends;
import models.Pincode;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by kedar on 5/23/14.
 */
public class Test extends Controller {

    public static Result test() {
        String sidhuId = Play.application().configuration().getString("fb.sidhu");
        List<Chai> matches = ChaiHandler.getMatches(sidhuId);
        return ok(String.valueOf(matches.size()));
    }

    public static Result test2 () {
        String arpita = Play.application().configuration().getString("fb.arpita");
        return ok();
    }

    public static Result algorithm () {
        SecretChaiSauce sauce = SecretChaiSauce.getInstance();
        sauce.run();
        return ok();
    }

    public static F.Promise<Result> latency () {
        String userId = session().get("user");
        Date start = new Date();
        User user = User.findOne(userId);
        user.update();
        Date database = new Date();

        // fb test
        LetsChaiFacebookClient fb = new LetsChaiFacebookClient(user.getAccessToken().getAccessToken());
        String arpita = Play.application().configuration().getString("fb.arpita");
        Date startPromise = new Date();
        F.Promise<Friends> friends = fb.getMutualFriends(arpita, user.getUserId());
        return friends.map(freeds -> {
            Date end = new Date();
            return ok(String.format("facebook: %d, database (read+update): %d", end.getTime() - startPromise.getTime(), database.getTime() - start.getTime()));
        });

    }

}
