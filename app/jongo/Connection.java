package jongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.jongo.Jongo;
import play.Play;

import java.net.UnknownHostException;

/**
 * Created by kedar on 3/19/14.
 * Singleton, designed to centralize all connection calls
 **/
public class Connection {

    private static MongoClient MONGO_INSTANCE;
    private static Jongo JONGO_INSTANCE;

    private Connection () {}

    private static MongoClient getMongoInstance () {
        if (MONGO_INSTANCE == null) {
            try {
                MONGO_INSTANCE = new MongoClient(new MongoClientURI(Play.application().configuration().getString("mongo.host")));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return MONGO_INSTANCE;
    }

    public static Jongo getJongoInstance () {
        if (JONGO_INSTANCE == null)
            JONGO_INSTANCE = new Jongo(getMongoInstance().getDB(Play.application().configuration().getString("mongo.db")));
        return JONGO_INSTANCE;
    }

}
