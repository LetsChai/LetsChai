package actions;

import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.SimpleResult;

/**
 * Created by kedar on 6/16/14.
 */
public class AuthAction extends Action.Simple {

    @Override
    public F.Promise<SimpleResult> call (Http.Context ctx) throws Throwable {
        F.Promise<SimpleResult> ret = null;
        ctx.args.put("auth", true);

        // redirect to login if not logged in
        if (!ctx.session().containsKey("user"))
            ret = F.Promise.pure(redirect("login"));
        else
            ret = delegate.call(ctx);

        return ret;
    }
}
