package jongo.types;

import com.restfb.FacebookClient;

import java.util.Date;

/**
 * Created by kedar on 4/7/14.
 */
public class FacebookAccessToken extends FacebookClient.AccessToken {

    private int fb_uid;

    public FacebookAccessToken () {}

    public void setId (int id) {
        fb_uid = id;
    }
}
