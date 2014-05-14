package controllers;

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
        return ok(chai.render(false));
    }

    public static Result editprofile () {
        return ok(chai.render(true));
    }

    public static Result preferences () {
        return ok(preferences.render());
    }

    public static Result settings () {
        return ok(settings.render());
    }
}
