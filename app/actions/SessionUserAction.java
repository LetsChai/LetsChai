package actions;

import models.User;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.SimpleResult;
import play.mvc.With;

/**
 * Created by kedar on 7/9/14.
 */

public class SessionUserAction extends Action.Simple {

    public F.Promise<SimpleResult> call (Http.Context ctx) throws Throwable {
        ctx.args.put("user", User.findOne(ctx.session().get("user")));
        return delegate.call(ctx);
    }
}
