package models;

import com.restfb.FacebookClient;

import java.util.Date;

/**
 * Created by kedar on 4/7/14.
 */
public class FacebookAccessToken {

    protected String userId;
    protected String accessToken;
    protected Date expires;

    public FacebookAccessToken (FacebookClient.AccessToken token) {
        this.accessToken = token.getAccessToken();
        this.expires = token.getExpires();
    }
    public void setUserId (String id) {
        userId = id;
    }

    public String getUserId () {
        return userId;
    }

    public static FacebookAccessToken fromQueryString (String query) {
        return new FacebookAccessToken(FacebookClient.AccessToken.fromQueryString(query));
    }

    public static FacebookAccessToken fromQueryString (String query, String userId) {
        FacebookAccessToken instance = fromQueryString(query);
        instance.setUserId(userId);
        return instance;
    }

    public String getAccessToken () {
        return accessToken;
    }

    public Date getExpires () {
        return expires;
    }
}
