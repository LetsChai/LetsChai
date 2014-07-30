package clients;

import com.amazonaws.auth.BasicAWSCredentials;
import play.Play;

/**
 * Created by kedar on 7/10/14.
 */
public class LetsChaiAWS {

    private static final String S3_BUCKET = Play.application().configuration().getString("aws.s3.url");

    public static BasicAWSCredentials getCredentials () {
        return new BasicAWSCredentials(
                Play.application().configuration().getString("aws.accesskey"),
                Play.application().configuration().getString("aws.secretkey"));
    }

    public static String s3Link (String key) {
        return S3_BUCKET + key;
    }
}
