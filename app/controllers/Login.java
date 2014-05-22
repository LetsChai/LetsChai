package controllers;

import com.restfb.types.TestUser;
import models.*;
import models.mongo.*;
import models.preferences.AgeRange;
import models.preferences.Gender;
import org.apache.commons.lang3.Range;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import uk.co.panaxiom.playjongo.PlayJongo;
import views.html.thankyou;
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
        models.mongo.UserProfile profile = new UserProfile(fb.fetchObject("me", com.restfb.types.User.class));
        profile.setPincode(pincode);
        profile.setGenderGiven(Gender.valueOf(gender));
        profile.generateQuestions();

        // check for verified profile
        if (!profile.isVerified())
            return redirect(controllers.routes.Login.unverified());

        // check to make sure user hasn't registered before
        if (UserProfile.findOne(profile.getId()) != null) {
            return redirect(controllers.routes.Application.thankyou());
        }

        // swap for extended access token
        UserAccessToken extendedAccessToken = fb.obtainExtendedAccessToken(accessToken);
        extendedAccessToken.setUserId(profile.getId());

        // create user preferences object
        UserPreference preferences = new UserPreference(profile.getId(), Gender.valueOf(genderPref), new AgeRange(ageMin, ageMax));

        // save them all
        models.mongo.UserProfile.getCollection().save(profile);
        UserAccessToken.getCollection().save(extendedAccessToken);
        UserPreference.getCollection().save(preferences);

        return redirect(controllers.routes.Application.thankyou());
    }

    public static Result unverified () {
        return ok(unverified.render());
    }
}
