package controllers;

import com.restfb.types.TestUser;
import com.restfb.types.User;
import jongo.Connection;
import jongo.types.FacebookAccessToken;
import jongo.types.FacebookFriends;
import jongo.types.UserProfile;
import models.LetsChaiFacebookClient;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Function0;
import scala.concurrent.Promise;

import java.util.List;

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
        FacebookAccessToken token = fb.obtainUserAccessToken(code);
        FacebookAccessToken extendedToken = fb.obtainExtendedAccessToken(token.getAccessToken());
        fb.setAccessToken(extendedToken);

        // get user info
        UserProfile me = fb.fetchObject("me", UserProfile.class);

        // save access token
        extendedToken.setUserId(me.getId());
        Connection.getJongoInstance().getCollection("facebook_access_tokens").save(extendedToken);

        // save user info
        Connection.getJongoInstance().getCollection("user_profiles").save(me);

        // get user friends
        List<User> myFriendList = fb.fetchConnection("me/friends", User.class).getData();

        // save user friends
        FacebookFriends myFriends = new FacebookFriends(me.getId(), myFriendList);
        Connection.getJongoInstance().getCollection("facebook_friends").save(myFriends);

        return redirect(controllers.routes.Application.thankyou());
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
