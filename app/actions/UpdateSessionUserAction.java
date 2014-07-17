package actions;

import models.User;
import play.libs.F;
import play.mvc.*;

/**
 * Created by kedar on 7/9/14.
 */
public class UpdateSessionUserAction extends Action.Simple {

    public F.Promise<SimpleResult> call (Http.Context ctx) throws Throwable {
        return delegate.call(ctx).map(result -> {
            User user = (User) ctx.args.get("user");
            User.getCollection().update("{'userId': '#'}", user.getUserId()).with(user);
            return result;
        });
    }
}
