package classes;

import models.Chai;
import models.User;
import org.joda.time.DateTime;
import play.Logger;

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
    public static void newUserActions (int duration) {
        Query query = new Query();
        FriendCacher cacher = new FriendCacher(PincodeHandler.getInstance());

        //
        Date since = new DateTime().minusMillis(duration).toDate();
        List<User> newUsers = query.newUsers(since);


        Logger.info("Starting friend caching");
        cacher.cache(newUsers).onRedeem(bool -> {
            Logger.info("Finished caching friends");
        });
    }

    // everytime a user logs in, conduct the following
    // currently not in use
    public static void onLogin (String userId, String tempAccessToken) {
        // update the lastLogin field for user
        // swap the tempAccessToken for an extended one then update the user

    }
}
