package clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.restfb.*;
import com.restfb.types.TestUser;
import models.User;
import org.jongo.MongoCollection;
import play.Play;

import com.restfb.util.StringUtils;
import play.libs.F;
import play.libs.WS;
import types.Friends;
import uk.co.panaxiom.playjongo.PlayJongo;

/**
 * Created by kedar on 3/27/14.
 */
public class LetsChaiFacebookClient extends DefaultFacebookClient {

    protected String appId;
    private String appSecret;
    protected final String loginScope =
        "basic_info,email,user_education_history,user_location,user_birthday,user_photos,friends_about_me,friends_birthday,user_relationships,friends_relationships";
    protected final String redirectUri = "login/code/";
    protected final String graphBaseUrl = "http://";

    public LetsChaiFacebookClient (String accessToken) {
        super(Play.application().configuration().getString("facebook.app_access_token"), Play.application().configuration().getString("facebook.app_secret"));
        appId = Play.application().configuration().getString("facebook.app_id");
        appSecret = Play.application().configuration().getString("facebook.app_secret");
        this.setAccessToken(accessToken);
    }

    public String getLoginUrl () {
        return "https://www.facebook.com/dialog/oauth?client_id=" +
                Play.application().configuration().getString("facebook.app_id") +
                "&redirect_uri=" + Play.application().configuration().getString("application.baseURL") + "login/code/" +
                "&scope=" + loginScope;
    }

    public TestUser createTestUser () {
        TestUser user = publish(getAppId() + "/accounts/test-users", TestUser.class);
        MongoCollection testUsers =  PlayJongo.getCollection("facebook_test_users");
        testUsers.save(user);
        return user;
    }

    // Credit to Val @ http://stackoverflow.com/questions/13671694/restfb-using-a-facebook-app-to-get-the-users-access-token
    // for this function
//    public FacebookAccessToken obtainUserAccessToken (String code) {
//        WebRequestor wr = new DefaultWebRequestor();
//        WebRequestor.Response accessTokenResponse = null;
//        try {
//            accessTokenResponse = wr.executeGet(
//                    "https://graph.facebook.com/oauth/access_token?client_id=" + getAppId() + "&redirect_uri=" +
//                            Play.application().configuration().getString("application.baseURL") + "login/code/"
//                            + "&client_secret=" + appSecret + "&code=" + code);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return FacebookAccessToken.fromQueryString(accessTokenResponse.getBody());
//    }

//    public Promise<FacebookAccessToken> obtainUserAccessTokenAsync (String code) {
//        final Promise<FacebookAccessToken> tokenPromise = WS.url(
//                "https://graph.facebook.com/oauth/access_token?client_id=" + getAppId() + "&redirect_uri=" +
//                        Play.application().configuration().getString("application.baseURL") + "login/code/"
//                        + "&client_secret=" + appSecret + "&code=" + code
//        ).get().map(
//                new Function<WS.Response, FacebookAccessToken>() {
//                    public FacebookAccessToken apply (WS.Response response) {
//                        return FacebookAccessToken.fromQueryString(response.getBody());
//                    }
//                }
//        );
//        return tokenPromise;
//    }

    public User.AccessToken obtainExtendedAccessToken (String accessToken) {
        return new User.AccessToken(obtainExtendedAccessToken(getAppId(), appSecret, accessToken));
    }

//    public Promise<FacebookAccessToken> obtainExtendedAccessTokenAsync (String accessToken) {
//        final Promise<FacebookAccessToken> tokenPromise = WS.url(
//                "https://graph.facebook.com/oauth/access_token?grant_type=fb_exchnage_token&client_id=" + getAppId()
//                        + "&client_secret=" + appSecret + "&fb_exchange_token=" + accessToken
//        ).get().map(
//                new Function<WS.Response, FacebookAccessToken>() {
//                    public FacebookAccessToken apply (WS.Response response) {
//                        return FacebookAccessToken.fromQueryString(response.getBody());
//                    }
//                }
//        );
//        return tokenPromise;
//
//    }

    public void setAccessToken(String token) {
        // accessToken is set in parent class
        this.accessToken = StringUtils.trimToNull(token);
    }

    protected String getRedirectUri () {
        return Play.application().configuration().getString("application.baseURL") + redirectUri;
    }

    public String getAppId() {
        return appId;
    }

//    public <T> Promise<T> fetchObjectAsync(String object, Class<T> objectType, Parameter... parameters) {
//        verifyParameterPresence("object", object);
//        verifyParameterPresence("objectType", objectType);
//        return WS.url("http://graph.facebook.com/" + object + "&access_token=" + accessToken).get().map(response ->
//            jsonMapper.toJavaObject(response.getBody(), objectType)
//        );
//    }

    public F.Promise<Boolean> areFriends (String userId1, String userId2) {
        WS.WSRequestHolder request = WS.url(String.format("https://graph.facebook.com/v2.0/%s/friends/%s", userId1, userId2))
                .setQueryParameter("access_token", accessToken);

        F.Promise<JsonNode> jsonResponse = request.get().map(response -> response.asJson());

        return jsonResponse.map(json -> {
            if (json.path("data").has(0))
                return true;
            return false;
        });
    }

    public F.Promise<Friends> getMutualFriends (String friendId) {
       WS.WSRequestHolder request = WS.url("https://graph.facebook.com/v2.0/" + friendId)
            .setQueryParameter("fields", "context.fields(mutual_friends)")
            .setQueryParameter("access_token", accessToken);

        F.Promise<JsonNode> jsonResponse = request.get().map(response -> response.asJson());
        return jsonResponse.map(json -> {
            Friends friends = new Friends();
            for(JsonNode j: json.path("context").path("mutual_friends").path("data")) {
                friends.addFriend(j);
            }
            friends.setCount(json.path("context").path("mutual_friends").path("summary").path("total_count").asInt());
            return friends;
        });
    }

    public static String profilePictureURL (String userId) {
        return String.format("http://graph.facebook.com/%s/picture?type=square", userId);
    }
}
