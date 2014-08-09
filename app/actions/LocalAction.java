package actions;

import play.Play;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.SimpleResult;

/**
 * Created by kedar on 6/16/14.
 */
public class LocalAction extends Action.Simple {

    @Override
    public F.Promise<SimpleResult> call (Http.Context ctx) throws Throwable {
        F.Promise<SimpleResult> ret = null;

        // redirect to login if not logged in
        if (Play.application().configuration().getString("openfire.name").equals("letschai-localhost"))
            return delegate.call(ctx);

        return F.Promise.pure(forbidden("Cannot access from production environment"));
    }
}
