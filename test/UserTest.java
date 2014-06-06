import models.User;
import org.joda.time.DateTime;
import org.jongo.Jongo;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import play.test.FakeApplication;
import play.test.Helpers;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.net.UnknownHostException;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.fakeRequest;

/**
 * Created by kedar on 6/4/14.
 */

public class UserTest {

    private List<User> users;
    public static FakeApplication app;
    private Jongo jongo;

    @BeforeClass
    public static void startApplication() {
        app = Helpers.fakeApplication();
        Helpers.start(app);
    }

    @Before
    public void loadUsers () throws UnknownHostException {
        jongo = Connection.getJongoInstance();
        users = new ArrayList<>();
        Iterable<User> userIterable = jongo.getCollection("production_users").find().as(User.class);
        assertNotNull(userIterable);

        for (User user: userIterable) {
            users.add(user);
        }
    }

    @After
    public void deleteUsers () {
        users.clear();
    }

    @Test
    public void checkMongoDocs () {
        for (User user: users) {
            assertNotNull("no birthday found", user.getBirthday());
            String error = String.format("user %s is younger than 18", user.getUserId());
            assertTrue(error, user.getBirthday().getTime() < new Date().getTime() - (long)18*(long)365*(long)86400*(long)1000);
            error = String.format("user %s is older than 30", user.getUserId());
            assertTrue(error, user.getBirthday().getTime() >  new Date().getTime() - (long)31*(long)365*(long)86400*(long)1000);
        }
    }

}
