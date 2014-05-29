package controllers;

import com.restfb.types.TestUser;
import models.*;
import models.User;
import models.AgeRange;
import models.Gender;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import uk.co.panaxiom.playjongo.PlayJongo;
import views.html.unverified;

/**
 * Created by kedar on 3/27/14.
 */
public class Login extends Controller {

    public static Result createTestUser() {
        LetsChaiFacebookClient fb = new LetsChaiFacebookClient();
        TestUser user = fb.createTestUser();
        return redirect(user.getLoginUrl());
    }

    public static Result loginTestUser() {
        TestUser user = PlayJongo.getCollection("facebook_test_users").find().sort("{_id:-1}").limit(1).as(TestUser.class).iterator().next();
        return redirect(user.getLoginUrl());
    }

    public static Result register() {
        // get POST data
        DynamicForm post = Form.form().bindFromRequest();
        String accessToken = post.get("access_token");
        int ageMin = Integer.parseInt(post.get("age_min"));
        int ageMax = Integer.parseInt(post.get("age_max"));
        String genderPref = post.get("gender_pref");
        int pincode = Integer.parseInt(post.get("pincode"));
        String gender = post.get("gender");

        // setup client with access token
        LetsChaiFacebookClient fb = new LetsChaiFacebookClient();
        fb.setAccessToken(accessToken);

        // get and set user profile
        User user = new User(fb.fetchObject("me", com.restfb.types.User.class));
        user.setPincode(pincode);
        user.setGenderGiven(Gender.valueOf(gender));
        user.generateQuestions();

        // check for verified profile and age
        if (!user.isVerified())
            return redirect(controllers.routes.Login.unverified());
        if (user.getAge() < 18)
            return redirect(controllers.routes.Login.tooYoung());
        if (user.getAge() > 30)
            return redirect(controllers.routes.Login.tooOld());

        // check to make sure user hasn't registered before
        if (User.findOne(user.getUserId()) != null) {
            return redirect(controllers.routes.Application.thankyou());
        }

        // swap for extended access token
        user.setAccessToken(fb.obtainExtendedAccessToken(accessToken));

        // create user preferences object
        user.setPreferences(
                new UserPreference(
                        Gender.valueOf(genderPref), new AgeRange(ageMin, ageMax)));

        // save them all
        User.getCollection().save(user);

        return redirect(controllers.routes.Application.thankyou());
    }

    public static Result unverified () {
        return ok(unverified.render("Your profile cannot be verified"));
    }

    public static Result tooYoung () {
        return ok(unverified.render("You are too young to use this site."));
    }

    public static Result tooOld () {
        return ok(unverified.render("You are too old to use this site."));
    }

    public static Result logout () {
        session().clear();
        return redirect(controllers.routes.Application.landing());
    }
}
