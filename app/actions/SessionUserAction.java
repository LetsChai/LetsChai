package actions;

import classes.ReadyToChaiChecker;
import models.User;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.SimpleResult;
import types.Flag;

/**
 * Created by kedar on 7/9/14.
 */

public class SessionUserAction extends Action.Simple {

    public F.Promise<SimpleResult> call (Http.Context ctx) throws Throwable {
        User user = User.findOne(ctx.session().get("user"));
        String path = ctx.request().path();

        // No deactivated users allowed
        if (user.hasFlag(Flag.DEACTIVATED) && !path.contains("activate") && !ctx.request().method().equals("post"))
            return F.Promise.promise(() -> redirect(controllers.routes.Application.deactivatedUser()));

        // make sure user profile is ready to go, but automatically allow access to editpictures and editprofile
        if (!user.hasFlag(Flag.READY_TO_CHAI) && !path.contains("editprofile") && !path.contains("editpictures")) {
            ReadyToChaiChecker checker = new ReadyToChaiChecker();
            checker.check(user);    // this will set/remove flags as well

            if (user.hasFlag(Flag.INCOMPLETE_PROFILE))
                return F.Promise.promise(() -> redirect(controllers.routes.Application.editProfile()));
            if (user.hasFlag(Flag.NO_PICTURES))
                return F.Promise.promise(() -> redirect(controllers.routes.Application.uploadPictures()));
        }

        ctx.args.put("user", user);
        return delegate.call(ctx);
    }
}
