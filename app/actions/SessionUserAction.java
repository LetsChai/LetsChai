package actions;

import classes.ReadyToChaiChecker;
import models.User;
import play.Logger;
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

        // Admin deactivated users
        if (user.hasFlag(Flag.ADMIN_DEACTIVATED) && !path.contains("editprofile") && !path.contains("editpictures") && !path.contains("deactivated"))
            return F.Promise.pure(redirect(controllers.routes.Application.deactivatedUser()));

        // Deactivated users
        boolean activatePage = path.contains("activate") && ctx.request().method().equals("POST");
        if (user.hasFlag(Flag.DEACTIVATED) && !user.hasFlag(Flag.ADMIN_DEACTIVATED) && !activatePage && !path.contains("deactivated"))
            return F.Promise.pure(redirect(controllers.routes.Application.deactivatedUser()));

        // make sure user profile is ready to go
        if (!user.hasFlag(Flag.READY_TO_CHAI) && !path.contains("editprofile") && !path.contains("editpictures")) {
            ReadyToChaiChecker checker = new ReadyToChaiChecker();
            checker.check(user);    // this will set/remove flags as well
            user.update();  // save flags to Mongo

            // redirect if not ready to chai
            if (user.hasFlag(Flag.INCOMPLETE_PROFILE))
                return F.Promise.promise(() -> redirect(controllers.routes.Application.editProfile()));
            if (user.hasFlag(Flag.NO_PICTURES))
                return F.Promise.promise(() -> redirect(controllers.routes.Application.editPictures()));
        }

        ctx.args.put("user", user);
        return delegate.call(ctx);
    }
}
