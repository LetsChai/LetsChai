import com.mongodb.MongoClient;
import org.jivesoftware.smack.XMPPConnection;
import org.jongo.Jongo;

import java.net.UnknownHostException;

/**
 * Created by kedar on 6/4/14.
 */
public class Connection {

    private static MongoClient mongo;
    private static Jongo jongo;
    private static XMPPConnection openfire;

    public static Jongo getJongoInstance () throws UnknownHostException {
        if (jongo == null) {
            mongo = new MongoClient("localhost", 27017);
            jongo = new Jongo(mongo.getDB("LetsChai"));
        }
        return jongo;
    }

    public static XMPPConnection openfire () {
        if (openfire == null)
            openfire = new XMPPConnection("localhost");
        return openfire;
    }
}
