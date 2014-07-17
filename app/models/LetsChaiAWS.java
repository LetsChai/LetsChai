package models;

import com.amazonaws.auth.BasicAWSCredentials;
import play.Play;

/**
 * Created by kedar on 7/10/14.
 */
public class LetsChaiAWS {

    public static BasicAWSCredentials getCredentials () {
        return new BasicAWSCredentials(
                Play.application().configuration().getString("aws.accesskey"),
                Play.application().configuration().getString("aws.secretkey"));
    }
}
