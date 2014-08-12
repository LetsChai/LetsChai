import classes.LetsChaiBooleanChecker;
import classes.PincodeHandler;
import com.mongodb.MongoClient;
import models.Pincode;
import org.jongo.Jongo;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.Assert;
import play.test.FakeApplication;
import play.test.Helpers;

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
        Assert.notNull(handler);
        Assert.isTrue(handler.valid(560001));
        Assert.isTrue(handler.valid(560014));
    }

    @Test
    public void bangaloreDistances () {
        Assert.isTrue(handler.distance(560001,560014) > 12);
        Assert.isTrue(handler.distance(560001,560014) < 13);
    }

    @AfterClass
    public static void close () {
        Helpers.stop(app);
    }
}
