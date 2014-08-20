import classes.LetsChaiBooleanChecker;
import classes.PincodeHandler;
import com.mongodb.MongoClient;
import org.jongo.Jongo;
import org.junit.Before;
import org.junit.BeforeClass;
import play.test.FakeApplication;
import play.test.Helpers;

import java.net.UnknownHostException;

/**
 * Created by kedar on 8/19/14.
 */
public class FriendCacherTest {

    private static FakeApplication app;
    private static Jongo jongo;

    @BeforeClass
    public static void db () throws UnknownHostException {
        app = Helpers.fakeApplication();
        Helpers.start(app);
        jongo = new Jongo(new MongoClient("localhost").getDB("LetsChai"));
    }

    @Before
    public void setup () {

    }
}
