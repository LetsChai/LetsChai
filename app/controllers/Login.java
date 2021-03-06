package controllers;

import clients.LetsChaiFacebookClient;
import com.restfb.types.TestUser;
import models.User;
import play.Logger;
import play.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import types.AgeRange;
import types.Flag;
import types.Gender;
import types.Permission;
import uk.co.panaxiom.playjongo.PlayJongo;
import views.html.nobirthday;
import views.html.thankyou;
import views.html.unverified;

import java.util.Date;
import java.util.List;

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

    public static F.Promise<Result> login () {
        DynamicForm post = Form.form().bindFromRequest();
        String accessToken = post.get("access_token");
        LetsChaiFacebookClient fb = new LetsChaiFacebookClient(accessToken);
        F.Promise<User> userPromise = fb.fetchObjectAsync("me", com.restfb.types.User.class).map(User::new);

        return userPromise.flatMap(fbUser -> {
            User user = User.findOne(fbUser.getUserId());
            if (user != null) { // if the user exists
                User.update(user.getUserId(), "{$set: {'lastLogin': #} }", new Date());

                // check for expiring access token
                Long oneMonth = (long) 86400 * (long) 30 * (long) 1000;
                if (new Date().getTime() + oneMonth > user.getAccessToken().getExpires().getTime()) {
                    Logger.info("updating access token");
                    return fb.obtainExtendedAccessTokenAsync().map(extendedToken -> {
                        user.setAccessToken(extendedToken);
                        user.update();
                        session().put("user", user.getUserId());
                        return redirect(controllers.routes.Application.chai());
                    });
                }

                // no pre-processing required, straight redirect
                session().put("user", user.getUserId());
                return F.Promise.pure(redirect(controllers.routes.Application.chai()));
            }
            // user doesn't exist
            return register(post, fbUser);
        });
    }

    public static F.Promise<Result> register(DynamicForm post, User user) {
        // if necessary POST data isn't present, redirect to newusersurvey
        if (!post.data().containsKey("age_min"))
            return F.Promise.promise(() -> redirect(controllers.routes.Application.newusersurvey()));

        // parse POST
        String accessToken = post.get("access_token");
        int ageMin = Integer.parseInt(post.get("age_min"));
        int ageMax = Integer.parseInt(post.get("age_max"));
        String genderPref = post.get("gender_pref");
        int pincode = Integer.parseInt(post.get("pincode"));
        String gender = post.get("gender");

        // setup client with access token
        LetsChaiFacebookClient fb = new LetsChaiFacebookClient(accessToken);

        // make Facebook API calls
        F.Promise<User.AccessToken> extendedTokenPromise = fb.obtainExtendedAccessTokenAsync();

        // set POST properties while we wait
        user.setPincode(pincode);
        user.setGenderGiven(Gender.valueOf(gender));
        user.generateQuestions();
        user.setPreferences(new User.Preferences(Gender.valueOf(genderPref), new AgeRange(ageMin, ageMax)));
        user.updateLastLogin();
        user.addFlag(Flag.NEW_USER);

        // set extended token and permissions
        return extendedTokenPromise.map(extendedToken -> {
            user.setAccessToken(extendedToken);
            return user;
        }).map(p -> {
            // verifications and checks
            if (!user.isVerified()) {
                PlayJongo.getCollection("unverified_users").save(user);
                return redirect(controllers.routes.Login.unverified());
            }
            if (user.getBirthday() == null) {
                PlayJongo.getCollection("nobirthday_users").save(user);
                return redirect(controllers.routes.Login.noBirthday());
            }
            if (user.getAge() < 18) {
                PlayJongo.getCollection("too_young_users").save(user);
                return redirect(controllers.routes.Login.tooYoung());
            }
            if (user.getAge() > 34) {
                PlayJongo.getCollection("too_old_users").save(user);
                return redirect(controllers.routes.Login.tooOld());
            }

            // if it passes all checks
            session().put("user", user.getUserId());
            User.getCollection().save(user);
            return redirect(controllers.routes.Application.editProfile());
        });
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
