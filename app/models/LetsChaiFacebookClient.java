package models;

import com.restfb.*;
import com.restfb.types.TestUser;
import jongo.types.FacebookAccessToken;
import org.jongo.MongoCollection;
import play.Play;

import java.io.IOException;
import com.restfb.util.StringUtils;

/**
 * Created by kedar on 3/27/14.
 */
public class LetsChaiFacebookClient extends DefaultFacebookClient {

    protected String appId;
    private String appSecret;
    protected final String loginScope =
        "basic_info,email,user_education_history,user_location,user_birthday,user_photos,friends_about_me,friends_birthday,user_relationships,friends_relationships";
    protected final String redirectUri = "login/code/";
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
        MongoCollection testUsers =  jongo.Connection.getJongoInstance().getCollection("facebook_test_users");
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

    public FacebookAccessToken obtainExtendedAccessToken (String accessToken) {
        return new FacebookAccessToken(obtainExtendedAccessToken(getAppId(), appSecret, accessToken));
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
}
