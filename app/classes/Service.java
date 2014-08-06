package classes;

import clients.LetsChaiFacebookClient;
import models.Chai;
import models.User;
import org.joda.time.DateTime;
import play.Logger;
import play.libs.F;
import types.Flag;
import types.Permission;

import java.util.Date;
import java.util.List;

/**
 * Created by kedar on 8/5/14.
 */
public class Service {

    public static void algorithm () {
        SecretChaiSauce sauce = new SecretChaiSauce(PincodeHandler.getInstance());
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
            fb.getPermissions(user.getUserId()).onRedeem(permissions -> {
                query.updatePermissions(user.getUserId(), permissions);
                query.deleteFlag(user.getUserId(), Flag.NEW_USER);
                Logger.info("Updated permissions for user " + user.getUserId());
            });
        }
    }
}
