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

        // make sure user profile is ready to go, but automatically allow access to editpictures and editprofile
        String path = ctx.request().path();
        if (!user.hasFlag(Flag.READY_TO_CHAI) && !path.contains("editprofile") && !path.contains("editpictures")) {
            ReadyToChaiChecker checker = new ReadyToChaiChecker();
            checker.check(user);
            if (user.hasFlag(Flag.INCOMPLETE_PROFILE))
                return F.Promise.promise(() -> redirect(controllers.routes.Application.editProfile()));
            if (user.hasFlag(Flag.NO_PICTURES))
                return F.Promise.promise(() -> redirect(controllers.routes.Application.uploadPictures()));
        }

        ctx.args.put("user", user);
        return delegate.call(ctx);
    }
}
