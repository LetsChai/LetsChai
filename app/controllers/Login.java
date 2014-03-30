package controllers;

import com.restfb.FacebookClient;
import com.restfb.types.TestUser;
import models.Connection;
import models.LetsChaiFacebookClient;
import play.mvc.Controller;
import play.mvc.Result;

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
        FacebookClient.AccessToken token = fb.obtainUserAccessTokenFromCode(code);
        return ok(token.toString());
    }

    public static Result createTestUser() {
        LetsChaiFacebookClient fb = new LetsChaiFacebookClient();
        TestUser user = fb.createTestUser();
        return redirect(user.getLoginUrl());
    }

    public static Result loginTestUser() {
        TestUser user = Connection.getJongoInstance().getCollection("facebook_test_users").findOne().as(TestUser.class);
        return redirect(user.getLoginUrl());
    }
}
