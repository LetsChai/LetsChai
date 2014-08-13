import classes.LetsChaiBooleanChecker;
import classes.PincodeHandler;
import com.mongodb.MongoClient;
import models.Pincode;
import org.jongo.Jongo;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import play.test.FakeApplication;
import play.test.Helpers;
import org.junit.Assert;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kedar on 8/12/14.
 */
public class DistanceTest {
    static FakeApplication app;
    static PincodeHandler handler;

    @BeforeClass
    public static void db () throws UnknownHostException {
        app = Helpers.fakeApplication();
        Helpers.start(app);
        Iterable<Pincode> it = new Jongo(new MongoClient("localhost").getDB("LetsChai")).getCollection("pincodes_gmaps").find().as(Pincode.class);
        Map<Integer, Pincode> pins = new HashMap<>();
        for (Pincode p: it) {
            pins.put(p.getPincode(), p);
        }
        handler = new PincodeHandler(pins);
    }

    @Test
    public void initials () {
        Assert.assertNotNull(handler);
        Assert.assertTrue(handler.valid(560001));
        Assert.assertTrue(handler.valid(560014));
        Assert.assertFalse(handler.valid(560012));
    }

    @Test
    public void bangaloreDistances () {
        Assert.assertTrue(handler.distance(560014,560001) > 12);
        Assert.assertTrue(handler.distance(560001,560014) > 12);
        Assert.assertTrue(handler.distance(560014,560001) < 13);
        Assert.assertTrue(handler.distance(560001,560014) < 13);
        Assert.assertTrue(handler.distance(560001, 560012) == 10000);
        Assert.assertTrue(handler.distance(560012, 560001) == 10000);
        Assert.assertTrue(handler.distance(560001, 560001) == 0);
        Assert.assertTrue(handler.distance(754321, 754321) == 0);
    }

    @AfterClass
    public static void close () {
        Helpers.stop(app);
    }
}
