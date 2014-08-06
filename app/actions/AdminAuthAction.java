package actions;

import classes.Query;
import models.User;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.SimpleResult;

/**
 * Created by kedar on 8/6/14.
 */
public class AdminAuthAction extends Action.Simple {

    @Override
    public F.Promise<SimpleResult> call (Http.Context ctx) throws Throwable {
        F.Promise<SimpleResult> ret = null;
        Query query = new Query();
        User user = User.findOne(ctx.session().get("user"));

        if (user.isAdmin())
            ret = delegate.call(ctx);
        else
            ret = F.Promise.pure(forbidden("Sorry, you do not have permission to access this page"));

        return ret;
    }
}