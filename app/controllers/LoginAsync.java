package controllers;

import com.restfb.types.User;
import models.FacebookAccessToken;
import models.UserProfile;
import models.LetsChaiFacebookClient;
import play.mvc.Controller;
import play.mvc.Result;

import play.libs.F.Promise;

import java.util.List;


/**
 * Created by kedar on 5/8/14.
 */
public class LoginAsync extends Controller {

    public static Result fbLoginRedirect () {
        LetsChaiFacebookClient fb = new LetsChaiFacebookClient();
        return redirect(fb.getLoginUrl());
    }

//    public static Result extractCode (String code) {
//        final LetsChaiFacebookClient fb = new LetsChaiFacebookClient();
//
//        // swap access token for code
//        final Promise<FacebookAccessToken> userTokenPromise = fb.obtainUserAccessTokenAsync(code);
//
//        // set token on client
//        final Promise<Boolean> clientSet = userTokenPromise.map((FacebookAccessToken userToken) -> {
//            fb.setAccessToken(userToken);
//            return true;
//        });
//
//        // get extended token
//        final Promise<FacebookAccessToken> extendedTokenPromise =
//            clientSet.flatMap( boo ->
//                userTokenPromise.flatMap(userToken ->
//                        fb.obtainExtendedAccessTokenAsync(userToken.getAccessToken())));
//
//        // get user info
//        final Promise<UserProfile> userPromise =
//            clientSet.flatMap(boo ->
//                fb.fetchObjectAsync("me", UserProfile.class));
//
//        // get user friends
//        final Promise<List<User>> friendListPromise =
//            clientSet.map(boo ->
//                fb.fetchConnection("me/friends", User.class).getData());
//
//
//    }
}
