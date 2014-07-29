package classes;

import models.User;
import types.Flag;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.util.*;

/**
 * Created by kedar on 7/25/14.
 */
public class UserHandler {

    private static UserHandler INSTANCE;

    private Map<String, User> users;

    public static UserHandler getInstance () {
        if (INSTANCE != null)
            return INSTANCE;

        Iterable<User> docs = PlayJongo.getCollection("users").find().as(User.class);
        Map<String, User> users = new HashMap<>();
        for (User doc: docs) {
            users.put(doc.getUserId(), doc);
        }

        INSTANCE = new UserHandler(users);

        return INSTANCE;
    }

    public UserHandler (Map<String, User> users) {
        this.users = users;
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public List<User> all () {
        List<User> userList = new ArrayList<>();
        for (User user: users.values()) {
            userList.add(user);
        }
        return userList;
    }

    public List<User> inBangalore () {
        List<User> userList = new ArrayList<>();
        for (User user: users.values()) {
            if (!user.hasFlag(Flag.NOT_IN_BANGALORE))
                userList.add(user);
        }
        return userList;
    }

    public List<User> valid () {
        List<User> userList = new ArrayList<>();
        for (User user: users.values()) {
            if (!user.hasFlag(Flag.NOT_IN_BANGALORE) && !user.hasFlag(Flag.DEACTIVATED))
                userList.add(user);
        }
        return userList;
    }

    public void backup() {
        Long time = new Date().getTime();
        PlayJongo.getCollection("users_backup_" + time.toString()).insert(all().toArray()); // backup users
    }

    public void overwrite () {
        backup();
        PlayJongo.getCollection("users").drop();
        PlayJongo.getCollection("users").insert(all().toArray());
    }
}
