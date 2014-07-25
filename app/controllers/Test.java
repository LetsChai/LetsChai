package controllers;

import classes.SecretChaiSauce;
import clients.LetsChaiFacebookClient;
import com.google.common.collect.Lists;
import models.*;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import play.Play;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import types.Friends;
import types.Pincode;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by kedar on 5/23/14.
 */
public class Test extends Controller {

    public static Result test() {
        List<User> users = Lists.newArrayList(User.findAll());
        return ok(users.get(30).getUserId());
    }

    public static Result test2 () {
        String arpita = Play.application().configuration().getString("fb.arpita");
        return ok();
    }

    public static F.Promise<Result> algorithm () {
        List<User> users = StreamSupport.stream(User.findAll().spliterator(), false).collect(Collectors.toList());
        List<Pincode> pincodes = StreamSupport.stream(Pincode.findAll().spliterator(), false).collect(Collectors.toList());
        List<Friends> friends = StreamSupport.stream(Friends.getFullCache().spliterator(), false).collect(Collectors.toList());

        SecretChaiSauce sauce = new SecretChaiSauce(users, pincodes, friends);

        return sauce.run().map(bool -> ok());
    }

    public static F.Promise<Result> latency () {
        String userId = session().get("user");
        Date start = new Date();
        User user = User.findOne(userId);
        user.update();
        Date database = new Date();
        LetsChaiFacebookClient fb = new LetsChaiFacebookClient(user.getAccessToken().getAccessToken());
        String arpita = Play.application().configuration().getString("fb.arpita");
        Date startPromise = new Date();
        F.Promise<Friends> friends = user.getMutualFriends(arpita);
        return friends.map(freeds -> {
            Date end = new Date();
            return ok(String.format("facebook: %d, database (read+update): %d", end.getTime() - startPromise.getTime(), database.getTime() - start.getTime()));
        });

    }

}
