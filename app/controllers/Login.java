package controllers;

import com.restfb.types.TestUser;
import models.*;
import models.User;
import models.AgeRange;
import models.Gender;
import play.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import uk.co.panaxiom.playjongo.PlayJongo;
import views.html.nobirthday;
import views.html.thankyou;
import views.html.unverified;

/**
 * Created by kedar on 3/27/14.
 */
public class Login extends Controller {

    public static Result createTestUser() {
        LetsChaiFacebookClient fb = new LetsChaiFacebookClient(Play.application().configuration().getString("facebook.app_access_token"));
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
        LetsChaiFacebookClient fb = new LetsChaiFacebookClient(accessToken);

        // get and set user profile
        User user = new User(fb.fetchObject("me", com.restfb.types.User.class));
        user.setPincode(pincode);
        user.setGenderGiven(Gender.valueOf(gender));
        user.generateQuestions();

        // swap for extended access token
        user.setAccessToken(fb.obtainExtendedAccessToken(accessToken));

        // check and set which permissions got authorized
        user.updatePermissions();

        // create user preferences object
        user.setPreferences(
                new UserPreference(
                        Gender.valueOf(genderPref), new AgeRange(ageMin, ageMax)));

        // check to make sure user hasn't registered before
        if (User.findOne(user.getUserId()) != null) {
            return redirect(controllers.routes.Application.thankyou());
        }

        // check for verified profile and age
        if (!user.isVerified()) {
            PlayJongo.getCollection("unverified_users").save(user);
            return redirect(controllers.routes.Login.unverified());
        }
        if (user.getBirthday() == null)  {
            PlayJongo.getCollection("nobirthday_users").save(user);
            return redirect(controllers.routes.Login.noBirthday());
        }
        if (user.getAge() < 18) {
            PlayJongo.getCollection("too_young_users").save(user);
            return redirect(controllers.routes.Login.tooYoung());
        }
        if (user.getAge() > 30) {
            PlayJongo.getCollection("too_old_users").save(user);
            return redirect(controllers.routes.Login.tooOld());
        }

        // if it passes all checks
        User.getCollection().save(user);

        return redirect(controllers.routes.Application.thankyou());
    }

    public static Result noBirthdayToVerified () {
        DynamicForm post = Form.form().bindFromRequest();
        String access_token = post.get("access_token");
        LetsChaiFacebookClient fb = new LetsChaiFacebookClient(access_token);

        com.restfb.types.User fbUser = fb.fetchObject("me", com.restfb.types.User.class);
        String query = String.format("{'userId': '%s'}", fbUser.getId());
        User mongoUser = PlayJongo.getCollection("nobirthday_users").findOne(query).as(User.class);

        mongoUser.setBirthday(fbUser.getBirthdayAsDate());

        User.getCollection().save(mongoUser);
        PlayJongo.getCollection("nobirthday_users").remove(query);

        return ok(thankyou.render());
    }

    public static Result unverified () {
        return ok(unverified.render("We're sorry", "Your profile cannot be verified"));
    }

    public static Result tooYoung () {
        return ok(unverified.render("Are you between 18 and 30?", "We see that you might be too young to use Let's Chai."));
    }

    public static Result tooOld () {
        return ok(unverified.render("Are you between 18 and 30?", "We see that you might be too old to use Let's Chai."));
    }

    public static Result noBirthday () {
        return ok(nobirthday.render());
    }

    public static Result logout () {
        session().clear();
        return redirect(controllers.routes.Application.landing());
    }

}
