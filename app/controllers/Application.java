package controllers;

import jongo.Connection;
import jongo.types.UserProfile;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
        UserProfile profile = Connection.getJongoInstance().getCollection("user_profiles").findOne().as(UserProfile.class);
        return ok(index.render(profile));
    }

    public static Result landing () {
        return ok(landing.render());
    }

}
