package controllers;

import com.restfb.FacebookClient;
import com.restfb.types.TestUser;
import com.restfb.types.User;
import jongo.Connection;
import jongo.types.UserProfile;
import models.LetsChaiFacebookClient;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.stringdump;

/**
 * Created by kedar on 3/27/14.
 */
public class Login extends Controller {

    public static Result fbLoginRedirect () {
        LetsChaiFacebookClient fb = new LetsChaiFacebookClient();
        return redirect(fb.getLoginUrl());
    }

    public static Result extractCode (String code) {
        LetsChaiFacebookClient fb = new LetsChaiFacebookClient();

        // get and set access token
        FacebookClient.AccessToken token = fb.obtainUserAccessToken(code);
        FacebookClient.AccessToken extendedToken = fb.obtainExtendedAccessToken(token.getAccessToken());
        fb.setAccessToken(extendedToken);

        // save access token
        Connection.getJongoInstance().getCollection("facebook_access_tokens").save(extendedToken);

        // get user info
        UserProfile me = fb.fetchObject("me", UserProfile.class);

        // save user info
        Connection.getJongoInstance().getCollection("user_profiles").save(me);

        return ok(index.render(me));
    }

    public static Result createTestUser() {
        LetsChaiFacebookClient fb = new LetsChaiFacebookClient();
        TestUser user = fb.createTestUser();
        return redirect(user.getLoginUrl());
    }

    public static Result loginTestUser() {
        TestUser user = Connection.getJongoInstance().getCollection("facebook_test_users").find().sort("{_id:-1}").limit(1).as(TestUser.class).iterator().next();
        return redirect(user.getLoginUrl());
    }
}
