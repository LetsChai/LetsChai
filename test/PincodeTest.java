import models.Pincode;
import org.jongo.Jongo;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import play.test.FakeApplication;
import play.test.Helpers;

import java.net.UnknownHostException;
import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by kedar on 6/4/14.
 */
public class PincodeTest {

    private static FakeApplication app;
    Map<Integer,Pincode> pincodes = new HashMap<>();
    Jongo jongo;

    @BeforeClass
    public static void startApplication() {
        app = Helpers.fakeApplication();
        Helpers.start(app);
    }

    @Before
    public void loadPincodes () throws UnknownHostException {
        jongo = Connection.getJongoInstance();
        for (Pincode p: jongo.getCollection("pincodes_gmaps").find().as(Pincode.class)) {
            pincodes.put(p.getPincode(), p);
        }
    }

    @Test
    public void checkPincodes () {
        assertTrue("no pincodes found", pincodes.size() > 0);

        List<Integer> pins = Arrays.asList(560001, 560014, 560020, 560021, 560045, 560005, 560004, 560082, 560080, 560003, 560057, 560099);
        for (int pin: pins) {
            assertNotNull(String.valueOf(pin) + " does not exist", pincodes.get(pin));
            assertNotNull(String.valueOf(pin) + " does not have a latitude", pincodes.get(pin).getLatitude());
            assertNotNull(String.valueOf(pin) + " does not have a longitude", pincodes.get(pin).getLongitude());
        }
    }

    @Test
    public void distances () {
        double[][] distances = {
                {560001, 560014, 12.385},
                {560020, 560021, 1.093},
                {560080, 560001, 4.21},
                {560021, 560045, 8.195},
                {560005, 560007, 4.517},
                {560005, 560005, 0},
                {560003, 560004, 6.290},
                {560004, 560082, 18.727},
                {560057, 560099, 28.907}
        };
        for (double[] entry: distances) {
            double distance = pincodes.get((int)entry[0]).distanceFrom(pincodes.get((int) entry[1]));
            String zip1 = String.valueOf(entry[0]);
            String zip2 = String.valueOf(entry[1]);
            String actualString = String.valueOf(entry[2]);
            String calcString = String.valueOf(distance);
            String error = String.format("Miscalculated distance between pincodes %s and %s. Actual=%s, Calculated=%s", zip1, zip2, actualString, calcString);
            if (entry[2] == 0.0)
                assertTrue(error, entry[2] == distance);
            else {
                assertFalse(error + " less than 0", distance < 0);
                assertTrue(error + " too low", distance > 0.95 * entry[2]);
                assertTrue(error + " too high", distance < 1.05 * entry[2]);
            }
        }
    }


}
