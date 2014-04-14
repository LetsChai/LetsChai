package models;

import com.restfb.*;
import com.restfb.types.TestUser;
import com.restfb.FacebookClient.AccessToken;
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
    protected String loginScope =
        "basic_info,email,user_education_history,user_location,user_birthday,user_photos,friends_about_me,friends_birthday,user_relationships,friends_relationships";

    // constructor
    public LetsChaiFacebookClient() {
        super(Play.application().configuration().getString("facebook.app_access_token"), Play.application().configuration().getString("facebook.app_secret"));
        appId = Play.application().configuration().getString("facebook.app_id");
        appSecret = Play.application().configuration().getString("facebook.app_secret");
    }

    public String getLoginUrl () {
        return "https://www.facebook.com/dialog/oauth?client_id=" +
                Play.application().configuration().getString("facebook.app_id") +
                "&redirect_uri=" + "http://localhost:9000/login/code/" +
                "&scope=" + getScope();
    }

    public TestUser createTestUser () {
        TestUser user = publish(getAppId() + "/accounts/test-users", TestUser.class);
        MongoCollection testUsers =  jongo.Connection.getJongoInstance().getCollection("facebook_test_users");
        testUsers.save(user);
        return user;
    }

    public String getScope() {
        return loginScope;
    }

    // Credit to Val @ http://stackoverflow.com/questions/13671694/restfb-using-a-facebook-app-to-get-the-users-access-token
    // for this function
    public DefaultFacebookClient.AccessToken obtainUserAccessToken (String code) {
        WebRequestor wr = new DefaultWebRequestor();
        WebRequestor.Response accessTokenResponse = null;
        try {
            accessTokenResponse = wr.executeGet(
                    "https://graph.facebook.com/oauth/access_token?client_id=" + getAppId() + "&redirect_uri=" +  "http://localhost:9000/login/code/"
                            + "&client_secret=" + getAppSecret() + "&code=" + code);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return DefaultFacebookClient.AccessToken.fromQueryString(accessTokenResponse.getBody());
    }

    public String getAppId() {
        return appId;
    }

    private String getAppSecret() {
        return appSecret;
    }

    public com.restfb.FacebookClient.AccessToken obtainExtendedAccessToken (String token) {
        return obtainExtendedAccessToken(getAppId(), getAppSecret(), token);
    }

    public void setAccessToken(com.restfb.FacebookClient.AccessToken token) {
        this.accessToken = StringUtils.trimToNull(token.getAccessToken());
    }
}
