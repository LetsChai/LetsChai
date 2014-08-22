package models;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.Validate;
import org.jongo.MongoCollection;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.util.Date;
import java.util.List;

/**
 * Created by kedar on 7/21/14.
 */
public class Message {

    String from;
    String to;
    String message;
    Date timestamp;

    private Message () {} // for Jackson

    public Message (String from, String to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.timestamp = new Date();
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Boolean hasUser (String userId) {
        return from.equals(userId) || to.equals(userId);
    }

    public String getOtherUser (String userId) {
        Validate.isTrue(hasUser(userId));
        if (from.equals(userId))
            return to;
        return from;
    }

}
