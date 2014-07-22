package classes;

import models.User;
import org.apache.commons.lang3.Validate;
import types.Flag;
import types.ProfileQuestion;

/**
 * Created by kedar on 7/22/14.
 */
public class ReadyToChaiChecker {

    public Boolean check (User user) {
        // profile check
        try {
            Validate.notNull(user.getHeight());
            Validate.isTrue(user.getHeight() > 0);
            Validate.notNull(user.getCity());
            Validate.isTrue(user.getCity().trim() != "");
            Validate.isTrue(user.getEducation().size() > 0 || user.getOccupation() != null);
            for (ProfileQuestion q: user.getQuestions()) {
                Validate.isTrue(q.getAnswer().trim() != "");
            }
        } catch (Exception e) {
            user.addFlag(Flag.INCOMPLETE_PROFILE);
            return false;
        }

        // picture check
        try {
            Validate.isTrue(user.getPictures().size() > 1); // minimum two pictures
        } catch (Exception e) {
            user.addFlag(Flag.NO_PICTURES);
            return false;
        }
        user.removeFlag(Flag.INCOMPLETE_PROFILE);
        user.removeFlag(Flag.NO_PICTURES);
        user.addFlag(Flag.READY_TO_CHAI);
        return true;
    }
}
