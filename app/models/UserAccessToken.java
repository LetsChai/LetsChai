package models;

import com.restfb.FacebookClient;
import org.jongo.MongoCollection;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.util.Date;

/**
 * Created by kedar on 5/14/14.
 */
public class UserAccessToken {

    protected String accessToken;
    protected Date expires;

    public static MongoCollection getCollection () {
        return PlayJongo.getCollection("user_access_tokens");
    }

    public static UserAccessToken findOne (String userId) {
        return getCollection().findOne("{'userId': '#'}", userId).as(UserAccessToken.class);
    }

    public UserAccessToken () {}

    public UserAccessToken (FacebookClient.AccessToken token) {
        this.accessToken = token.getAccessToken();
        this.expires = token.getExpires();
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
