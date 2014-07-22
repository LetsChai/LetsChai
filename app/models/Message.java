package models;

import com.google.common.collect.Lists;
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

    public static MongoCollection getCollection () {
        return PlayJongo.getCollection("chats");
    }

    public static List<models.Message> find (String userId) {
        return Lists.newArrayList(getCollection().find(String.format("{$or:{'from':'%s', 'to': '%s'} }", userId, userId))
                .as(models.Message.class));
    }

    public void save () {
        getCollection().save(this);
    }

}
