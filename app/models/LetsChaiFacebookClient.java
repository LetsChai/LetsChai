package models;

import com.restfb.DefaultFacebookClient;
import com.restfb.Parameter;
import com.restfb.types.TestUser;
import controllers.Login;
import controllers.routes.*;
import org.jongo.MongoCollection;
import play.Play;
import play.mvc.Call;

/**
 * Created by kedar on 3/27/14.
 */
public class LetsChaiFacebookClient extends DefaultFacebookClient {

    protected String appId;
    private String appSecret;
    protected AccessToken token;
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
                "&redirect_uri=" + controllers.routes.Login.extractCode("").absoluteURL(Login.request()) +
                "&scope=" + getScope();
    }

    public TestUser createTestUser () {
        TestUser user = publish(getAppId() + "/accounts/test-users", TestUser.class);
        MongoCollection testUsers =  Connection.getJongoInstance().getCollection("facebook_test_users");
        testUsers.save(user);
        return user;
    }

    public String getScope() {
        return loginScope;
    }

    public AccessToken obtainUserAccessTokenFromCode (String code) {
        AccessToken token = fetchObject("oauth/access_token", AccessToken.class,
                Parameter.with("client_id", getAppId()),
                Parameter.with("client_secret", getAppSecret()),
                Parameter.with("redirect_uri", controllers.routes.Login.extractCode("").absoluteURL(Login.request())),
                Parameter.with("code", code));

        return token;
    }

    public String getAppId() {
        return appId;
    }

    private String getAppSecret() {
        return appSecret;
    }

}
