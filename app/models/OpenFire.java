package models;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

/**
 * Created by kedar on 6/10/14.
 */
public class OpenFire {

    public static XMPPConnection connection () {
        XMPPConnection openfire = new XMPPConnection("localhost");
        try {
            openfire.connect();
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        return openfire;
    }
}
