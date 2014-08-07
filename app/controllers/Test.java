package controllers;

import actions.Auth;
import classes.PincodeHandler;
import classes.Query;
import classes.SecretChaiSauce;
import classes.Service;
import clients.LetsChaiFacebookClient;
import com.google.common.collect.Lists;
import models.Chai;
import models.Friends;
import models.User;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import play.Logger;
import play.Play;
import play.libs.Akka;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.duration.Duration;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by kedar on 5/23/14.
 */
public class Test extends Controller {

    public static Result test() {
        Query query = new Query();
        List<User> production = Lists.newArrayList(PlayJongo.getCollection("production_users").find().as(User.class));
        List<User> permissioned = Lists.newArrayList(PlayJongo.getCollection("users_permissions").find().as(User.class));
        for(User user: production) {
            for (User perm: permissioned) {
                if (user.equals(perm)) {
                    user.setPermissions(perm.getPermissions());
                    PlayJongo.getCollection("users_export").save(user);
                }
            }
        }
        return ok("Success");
    }

    public static Result test2 () {
        return ok(DateTimeZone.getDefault().toString());
    }

    public static Result algorithm () {
        Akka.system().scheduler().scheduleOnce(
                Duration.Zero(),
                Service::algorithm,
                Akka.system().dispatcher()
        );
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
