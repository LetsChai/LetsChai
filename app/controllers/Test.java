package controllers;

import actions.Auth;
import classes.*;
import clients.LetsChaiFacebookClient;
import models.Chai;
import models.Friends;
import models.User;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import play.Logger;
import play.Play;
import play.api.mvc.WebSocket;
import play.libs.Akka;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.duration.Duration;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by kedar on 5/23/14.
 */
@Auth.Local
public class Test extends Controller {

    public static Result test() {
        String varun = Play.application().configuration().getString("fb.varun");
        String kedar = Play.application().configuration().getString("fb.kedar");
        Admin.forceMatch(varun , kedar);
        return ok();
    }

    public static Result test2 () {
        Query q = new Query();
        List<Chai> chais = q.todaysChaisNoDecision();
        Map<String,User> users = q.users().stream().collect(Collectors.toMap(User::getUserId, user -> user));
        List<Chai> toUpdate = new ArrayList<>();
        for (Chai chai: chais) {
            if (new DateTime(chai.getReceived()).isAfterNow())
                continue;
            DateTime lastLogin = new DateTime(users.get(chai.getReceiver()).getLastLogin());
            DateTime yesterday = new DateTime().minusDays(1);
            if (!chai.hasDecided() && lastLogin.isBefore(yesterday))
                toUpdate.add(chai);
        }

        return ok(String.valueOf(toUpdate.size()));
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

    public static play.mvc.WebSocket<String> socket () {
        return new play.mvc.WebSocket<String>() {
            @Override
            public void onReady(In<String> stringIn, Out<String> stringOut) {
                stringOut.write("hello");
                stringIn.onMessage(stringOut::write);
                stringIn.onClose(() -> Logger.info("socket closed"));
            }
        };
    }

}
