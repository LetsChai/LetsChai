package classes;

import clients.LetsChaiFacebookClient;
import models.Chai;
import models.User;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import play.Logger;
import play.libs.F;
import types.Flag;
import types.Permission;

import java.util.List;

/**
 * Created by kedar on 8/5/14.
 */
public class Service {

    public static void algorithm () {
        MatchingAlgo sauce = new CompactChaiSauce(PincodeHandler.getInstance());
        Query query = new Query();
        List<User> users = query.users();

        for (User user: users) {
            Chai next = sauce.nextChai(user);
            if (next != null) {
                query.saveChai(next);
                Logger.info("saving for user " + user.getUserId());
            }
        }
    }

    public static void friendUpdate () {
        Query query = new Query();
        FriendCacher cacher = new FriendCacher(PincodeHandler.getInstance());

        Logger.info("Caching friends for 5 random users");
        cacher.cache(query.randomUsers(5)).onRedeem(bool -> Logger.info("Friend caching complete"));
    }

    // duration is in milliseconds, states how far back we should look for new users
    public static void newUserActions () {
        Query query = new Query();
        FriendCacher cacher = new FriendCacher(PincodeHandler.getInstance());

        // get new users
        List<User> newUsers = query.newUsers();

        // cache friends
        Logger.info("Starting friend caching for new users");
        cacher.cache(newUsers).map(bool -> {
            Logger.info("Finished caching friends for new users");
            return true;
        });

        // update user permissions
        Logger.info("Updating new user permissions");
        for (User user: newUsers) {
            LetsChaiFacebookClient fb = new LetsChaiFacebookClient(user.getAccessToken().getAccessToken());
            F.Promise<List<Permission>> promise = fb.getPermissions(user.getUserId());
            promise.onRedeem(permissions -> {
                query.updatePermissions(user.getUserId(), permissions);
                query.deleteFlag(user.getUserId(), Flag.NEW_USER);
                Logger.info("Updated permissions for user " + user.getUserId());
            });
            promise.onFailure(e -> Logger.error(e.getMessage(), e));
        }
    }

    public static void matchEmail () {
        SimpleEmail mail = new SimpleEmail();
        mail.setHostName("smtp.gmail.com");
        mail.setSmtpPort(465);
        mail.setAuthenticator(new DefaultAuthenticator("kedarmail@gmail.com", "K3rn3LTo3"));
        try {
            mail.setFrom("do-not-reply@letschai.com", "Let's Chai");
            mail.addTo("varun@letschai.com");
            mail.addCc("kedariyer26@gmail.com");
            mail.setMsg("Test email from the server");
            mail.setSubject("Test email");
            mail.setSSLOnConnect(true);
            mail.send();
        } catch (EmailException e) {
            e.printStackTrace();
        }

    }
}
