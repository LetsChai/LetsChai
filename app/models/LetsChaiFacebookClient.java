package models;

import com.restfb.*;
import com.restfb.types.TestUser;
import org.jongo.MongoCollection;
import play.Play;

import java.io.IOException;
import com.restfb.util.StringUtils;
import play.libs.WS;
import play.mvc.Result;

import static play.libs.F.Function;
import static play.libs.F.Promise;

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

    // constructor
    public LetsChaiFacebookClient() {
        super(Play.application().configuration().getString("facebook.app_access_token"), Play.application().configuration().getString("facebook.app_secret"));
        appId = Play.application().configuration().getString("facebook.app_id");
        appSecret = Play.application().configuration().getString("facebook.app_secret");
    }

    public String getLoginUrl () {
        return "https://www.facebook.com/dialog/oauth?client_id=" +
                Play.application().configuration().getString("facebook.app_id") +
                "&redirect_uri=" + Play.application().configuration().getString("application.baseURL") + "login/code/" +
                "&scope=" + loginScope;
    }

    public TestUser createTestUser () {
        TestUser user = publish(getAppId() + "/accounts/test-users", TestUser.class);
        MongoCollection testUsers =  Connection.getJongoInstance().getCollection("facebook_test_users");
        testUsers.save(user);
        return user;
    }

    // Credit to Val @ http://stackoverflow.com/questions/13671694/restfb-using-a-facebook-app-to-get-the-users-access-token
    // for this function
    public FacebookAccessToken obtainUserAccessToken (String code) {
        WebRequestor wr = new DefaultWebRequestor();
        WebRequestor.Response accessTokenResponse = null;
        try {
            accessTokenResponse = wr.executeGet(
                    "https://graph.facebook.com/oauth/access_token?client_id=" + getAppId() + "&redirect_uri=" +
                            Play.application().configuration().getString("application.baseURL") + "login/code/"
                            + "&client_secret=" + appSecret + "&code=" + code);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return FacebookAccessToken.fromQueryString(accessTokenResponse.getBody());
    }

    public Promise<FacebookAccessToken> obtainUserAccessTokenAsync (String code) {
        final Promise<FacebookAccessToken> tokenPromise = WS.url(
                "https://graph.facebook.com/oauth/access_token?client_id=" + getAppId() + "&redirect_uri=" +
                        Play.application().configuration().getString("application.baseURL") + "login/code/"
                        + "&client_secret=" + appSecret + "&code=" + code
        ).get().map(
                new Function<WS.Response, FacebookAccessToken>() {
                    public FacebookAccessToken apply (WS.Response response) {
                        return FacebookAccessToken.fromQueryString(response.getBody());
                    }
                }
        );
        return tokenPromise;
    }

    public FacebookAccessToken obtainExtendedAccessToken (String accessToken) {
        return new FacebookAccessToken(obtainExtendedAccessToken(getAppId(), appSecret, accessToken));
    }

    public Promise<FacebookAccessToken> obtainExtendedAccessTokenAsync (String accessToken) {
        final Promise<FacebookAccessToken> tokenPromise = WS.url(
                "https://graph.facebook.com/oauth/access_token?grant_type=fb_exchnage_token&client_id=" + getAppId()
                        + "&client_secret=" + appSecret + "&fb_exchange_token=" + accessToken
        ).get().map(
                new Function<WS.Response, FacebookAccessToken>() {
                    public FacebookAccessToken apply (WS.Response response) {
                        return FacebookAccessToken.fromQueryString(response.getBody());
                    }
                }
        );
        return tokenPromise;

    }

    public void setAccessToken(FacebookAccessToken token) {
        // accessToken is set in parent class
        this.accessToken = StringUtils.trimToNull(token.getAccessToken());
    }

    protected String getRedirectUri () {
        return Play.application().configuration().getString("application.baseURL") + redirectUri;
    }

    public String getAppId() {
        return appId;
    }

    public <T> Promise<T> fetchObjectAsync(String object, Class<T> objectType, Parameter... parameters) {
        verifyParameterPresence("object", object);
        verifyParameterPresence("objectType", objectType);
        return WS.url("http://graph.facebook.com/" + object + "&access_token=" + accessToken).get().map(response ->
            jsonMapper.toJavaObject(response.getBody(), objectType)
        );
    }
}
