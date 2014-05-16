package models.mongo;

import com.restfb.FacebookClient;
import org.jongo.MongoCollection;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.util.Date;

/**
 * Created by kedar on 5/14/14.
 */
public class UserAccessToken {

    protected String userId;
    protected String accessToken;
    protected Date expires;

    public static MongoCollection getCollection () {
        return PlayJongo.getCollection("user_access_tokens");
    }

    public UserAccessToken (FacebookClient.AccessToken token) {
        this.accessToken = token.getAccessToken();
        this.expires = token.getExpires();
    }
    public void setUserId (String id) {
        userId = id;
    }

    public String getUserId () {
        return userId;
    }

    public static UserAccessToken fromQueryString (String query) {
        return new UserAccessToken(FacebookClient.AccessToken.fromQueryString(query));
    }

    public String getAccessToken () {
        return accessToken;
    }

    public Date getExpires () {
        return expires;
    }
}
