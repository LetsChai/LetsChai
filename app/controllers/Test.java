package controllers;

import classes.Service;
import clients.LetsChaiFacebookClient;
import models.Friends;
import models.User;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.joda.time.DateTimeZone;
import play.Play;
import play.libs.Akka;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.duration.Duration;

import java.util.Date;

/**
 * Created by kedar on 5/23/14.
 */
public class Test extends Controller {


    public static Result test() {
        ConnectionConfiguration config = new ConnectionConfiguration("54.179.188.102", 5222);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        XMPPConnection smack = new XMPPTCPConnection(config);
        try {
            smack.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String userId = Play.application().configuration().getString("fb.veena");
        try {
            smack.login(userId, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ChatManager manager = ChatManager.getInstanceFor(smack);
        String otherId = Play.application().configuration().getString("fb.kedar") + "@letschai-aws/Smack";
        Chat ch = manager.createChat(otherId, (chat, baby) -> chat.close());
        try {
            ch.sendMessage("this better work");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ok(smack.getUser());
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
