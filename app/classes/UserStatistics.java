package classes;

import models.User;
import types.Flag;

import java.util.List;

/**
 * Created by kedar on 8/12/14.
 */
public class UserStatistics {

    private List<User> users;

    public UserStatistics (List<User> users) {
        this.users = users;
    }

    public int count () {
        return users.size();
    }

    public int readyCount () {
        return (int) users.stream().filter(user -> user.hasFlag(Flag.READY_TO_CHAI)).count();
    }

    public int bangaloreCount () {
        return (int) users.stream().filter(user -> user.getPincode() > 560000 && user.getPincode() < 561000).count();
    }

    public int femaleCount () {
        return (int) users.stream().filter(user -> user.getGender().equals("female")).count();
    }

    public int readyBangaloreCount() {
        return (int) users.stream().filter(user -> user.getPincode() > 560000 && user.getPincode() < 561000)
                .filter(user -> user.hasFlag(Flag.READY_TO_CHAI)).count();
    }

    public int readyBangaloreFemaleCount () {
        return (int) users.stream().filter(user -> user.getPincode() > 560000 && user.getPincode() < 561000)
                .filter(user -> user.hasFlag(Flag.READY_TO_CHAI))
                .filter(user -> user.getGender().equals("female")).count();
    }
}
