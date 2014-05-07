package controllers;

import jongo.Connection;
import jongo.types.UserProfile;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public static Result landing () {
        return ok(landing.render());
    }

    public static Result thankyou () {
        return ok(thankyou.render());
    }

    public static Result newusersurvey () {
        return ok(newusersurvey.render());
    }

    public static Result chai () {
        return ok(chai.render());
    }
}
