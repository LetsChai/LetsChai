package controllers;

import com.restfb.types.TestUser;
import models.*;
import models.mongo.*;
import models.preferences.Gender;
import org.apache.commons.lang3.Range;
import play.mvc.Controller;
import play.mvc.Result;
import uk.co.panaxiom.playjongo.PlayJongo;

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

    public static Result register(String accessToken, int ageMin, int ageMax, String genderPref, String gender, int pincode) {

        // setup client with access token
        LetsChaiFacebookClient fb = new LetsChaiFacebookClient();
        fb.setAccessToken(accessToken);

        // get user profile
        models.mongo.UserProfile profile = fb.fetchObject("me", models.mongo.UserProfile.class);
        profile.setPincode(pincode);
        profile.setGenderGiven(Gender.valueOf(gender));
        profile.generateQuestions();

        // swap for extended access token
        UserAccessToken extendedAccessToken = fb.obtainExtendedAccessToken(accessToken);
        extendedAccessToken.setUserId(profile.getId());

        // create user preferences object
        UserPreference preferences = new UserPreference(profile.getId(), Gender.valueOf(genderPref), Range.between(ageMin, ageMax));

        // save them all
        models.mongo.UserProfile.getCollection().save(profile);
        UserAccessToken.getCollection().save(extendedAccessToken);
        UserPreference.getCollection().save(preferences);

        return redirect(controllers.routes.Application.thankyou());
    }
}
