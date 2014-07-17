package models;

import play.libs.F;

/**
 * Created by kedar on 7/16/14.
 */
public class UserChaiTuple {

    User user;
    Chai chai;
    F.Promise<Boolean> passes;

    private UserChaiTuple (User user, Chai chai) {
        this.user = user;
        this.chai = chai;
    }

    public static UserChaiTuple from(User user, Chai chai) {
        return new UserChaiTuple(user, chai);
    }

    public User getUser() {
        return user;
    }

    public Chai getChai() {
        return chai;
    }

    public void setChai(Chai chai) {
        this.chai = chai;
    }

    public F.Promise<Boolean> getPasses() {
        return passes;
    }

    public void setPasses(F.Promise<Boolean> passes) {
        this.passes = passes;
    }
}
