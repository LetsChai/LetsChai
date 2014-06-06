import models.Pincode;
import models.SecretChaiSauce;
import models.User;
import org.jongo.Jongo;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import play.test.FakeApplication;
import play.test.Helpers;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by kedar on 6/4/14.
 */
public class SecretChaiSauceTest {

    private static FakeApplication app;
    private SecretChaiSauce sauce;
    private Jongo jongo;
    private Map<String, User> users;
    private Map<Integer, Pincode> pincodes;

    @BeforeClass
    public static void startApplication() {
        app = Helpers.fakeApplication();
        Helpers.start(app);
    }

    @Before
    public void before () throws UnknownHostException {
        jongo = Connection.getJongoInstance();
        assertNull("pincodes not null", pincodes);
        assertNull("users not null", users);
        loadPincodes();
        loadUsers();
        assertTrue("no pincodes found", pincodes.size() > 0);
        assertTrue("no users found", users.size() > 0);
        sauce = new SecretChaiSauce(users, pincodes);
    }

    @Test
    public void distanceScore () {
        double[][] distances = {
                {560001, 560014, 2.0/6.0},
                {560020, 560021, 6.0/6.0},
                {560080, 560001, 5.0/6.0},
                {560021, 560045, 3.0/6.0},
                {560005, 560007, 5.0/6.0},
                {560005, 560005, 6.0/6.0},
                {560003, 560004, 4.0/6.0},
                {560004, 560082, 1.0/6.0},
                {560057, 560099, 0/6.0}
        };
        for (double[] entry: distances) {
            double score = sauce.distanceScore((int)entry[0], (int)entry[1]);
            String error = String.format("Wrong score assigned, calculated:%d, actual:%d ", entry[2], score);
            assertFalse(error + "less than 0", score < 0);
            assertTrue(error, entry[2] == score);
        }
    }

    public void loadUsers () {
        if (users != null)
            return;

        users = new HashMap<String, User>();
        Iterable<User> userIterable = jongo.getCollection("production_users").find().as(User.class);
        for (User user: userIterable) {
            users.put(user.getUserId(), user);
        }
    }

    public void loadPincodes () {
        if (pincodes != null)
            return;

        pincodes = new HashMap<Integer, Pincode>();
        Iterable<Pincode> pincodeIterable = jongo.getCollection("pincode_gmaps").find().as(Pincode.class);
        for (Pincode pin: pincodeIterable) {
            pincodes.put(pin.getPincode(), pin);
        }
    }
}
