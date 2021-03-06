package classes;

import models.User;
import org.apache.commons.lang3.Validate;
import play.Logger;
import types.Flag;
import types.ProfileQuestion;

import java.util.List;

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
            Validate.isTrue(!user.getCity().trim().equals(""));
            Validate.notNull(user.getOccupation());
            Validate.isTrue(!user.getOccupation().trim().equals(""));
            for (ProfileQuestion q: user.getQuestions()) {
                Validate.isTrue(!q.getAnswer().trim().equals(""));
            }
            user.removeFlag(Flag.INCOMPLETE_PROFILE);
        } catch (Exception e) {
            user.addFlag(Flag.INCOMPLETE_PROFILE);
            return false;
        }

        // picture check
        try {
            Validate.isTrue(user.getPictureCount() > 1); // minimum two pictures
            user.removeFlag(Flag.NO_PICTURES);
        } catch (Exception e) {
            user.addFlag(Flag.NO_PICTURES);
            return false;
        }
        user.addFlag(Flag.READY_TO_CHAI);
        return true;
    }
}
