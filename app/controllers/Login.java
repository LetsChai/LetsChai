package controllers;

import com.restfb.FacebookClient;
import com.restfb.types.TestUser;
import models.Connection;
import models.LetsChaiFacebookClient;
import org.jongo.MongoCollection;
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
        FacebookClient.AccessToken token = fb.obtainUserAccessToken(code);
        FacebookClient.AccessToken extendedToken = fb.obtainExtendedAccessToken(token.getAccessToken());
        MongoCollection accessTokenCollection = Connection.getJongoInstance().getCollection("facebook_access_tokens");

        accessTokenCollection.save(extendedToken);

        return redirect(controllers.routes.Application.index());
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
