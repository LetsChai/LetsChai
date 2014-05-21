package controllers;

import models.mongo.UserProfile;
import play.data.Form;
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
        UserProfile user = UserProfile.findOne(session().get("user"));
        Form<UserProfile> profileForm = Form.form(UserProfile.class);
        return ok(chai.render(user, profileForm, false));
    }

    public static Result editprofile () {
        UserProfile user = UserProfile.findOne(session().get("user"));
        Form<UserProfile> profileForm = Form.form(UserProfile.class);
        return ok(chai.render(user, profileForm, true));
    }

    public static Result updateProfile () {
        return ok();
    }

    public static Result preferences () {
        return ok(preferences.render());
    }

    public static Result settings () {
        return ok(settings.render());
    }
}
