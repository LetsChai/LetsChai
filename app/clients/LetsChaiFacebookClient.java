package clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restfb.*;
import com.restfb.types.TestUser;
import models.User;
import org.jongo.MongoCollection;
import play.Logger;
import play.Play;

import play.libs.F;
import play.libs.WS;
import models.Friends;
import types.Permission;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kedar on 3/27/14.
 */
public class LetsChaiFacebookClient extends DefaultFacebookClient {

    protected String appId;
    private String appSecret;
    protected final String loginScope =
        "basic_info,email,user_education_history,user_location,user_birthday,user_photos,friends_about_me,friends_birthday,user_relationships,friends_relationships";
    protected final String redirectUri = "login/code/";
    protected final String graphBaseUrl = "https://graph.facebook.com/v2.0/";

    public LetsChaiFacebookClient (String accessToken) {
        super(Play.application().configuration().getString("facebook.app_access_token"), Play.application().configuration().getString("facebook.app_secret"));
        appId = Play.application().configuration().getString("facebook.app_id");
        appSecret = Play.application().configuration().getString("facebook.app_secret");
        this.accessToken = accessToken;
    }

    public TestUser createTestUser () {
        TestUser user = publish(appId + "/accounts/test-users", TestUser.class);
        MongoCollection testUsers =  PlayJongo.getCollection("facebook_test_users");
        testUsers.save(user);
        return user;
    }

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

    public F.Promise<User.AccessToken> obtainExtendedAccessTokenAsync () {
        return WS.url(graphBaseUrl + "oauth/access_token")
            .setQueryParameter("grant_type", "fb_exchange_token")
            .setQueryParameter("client_id", appId)
            .setQueryParameter("client_secret", appSecret)
            .setQueryParameter("fb_exchange_token", accessToken)
            .get()
                .map(response -> FacebookClient.AccessToken.fromQueryString(response.getBody()))
                .map(User.AccessToken::new);
    }

    protected String getRedirectUri () {
        return Play.application().configuration().getString("application.baseURL") + redirectUri;
    }

    public <T> F.Promise<T> fetchObjectAsync(String object, Class<T> objectType, Parameter... parameters) {
        verifyParameterPresence("object", object);
        verifyParameterPresence("objectType", objectType);

        return WS.url(graphBaseUrl + object).setQueryParameter("access_token", accessToken).get()
                .map(response -> jsonMapper.toJavaObject(response.getBody(), objectType));
    }

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

    public F.Promise<Friends> getMutualFriends (String userId, String friendId) {
        Friends friends = new Friends(Arrays.asList(userId, friendId));
        return WS.url("https://graph.facebook.com/v2.0/" + friendId)
                .setQueryParameter("fields", "context.fields(mutual_friends)")
                .setQueryParameter("access_token", accessToken)
                .get()
                .map(response -> {
                    friends.setMutualFriends(response.asJson());
                    return friends;
                });
    }

    public static String profilePictureURL (String userId) {
        return String.format("http://graph.facebook.com/%s/picture?type=square", userId);
    }

    public F.Promise<List<Permission>> getPermissions (String userId) {

        String url = String.format("https://graph.facebook.com/v2.0/%s/permissions?access_token=%s", userId, accessToken);
        ObjectMapper mapper = new ObjectMapper();
        List<Permission> permissions = new ArrayList<>();

        return WS.url(graphBaseUrl + userId + "/permissions")
                .setQueryParameter("access_token", accessToken)
                .get()
                .map(response -> {
                    for (JsonNode j : mapper.readTree(response.getBody()).path("data")) {
                        permissions.add(Permission.valueOf(j.path("permission").asText().toUpperCase()));
                    }
                    return permissions;
                });
    }
}
