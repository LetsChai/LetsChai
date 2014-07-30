package clients;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import play.Play;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

/**
 * Created by kedar on 7/10/14.
 */
public class LetsChaiAWS {

    private static final String S3_URL = Play.application().configuration().getString("aws.s3.url");
    private static final String S3_BUCKET = Play.application().configuration().getString("aws.s3.bucket");
    private static final String ACCESS_KEY = Play.application().configuration().getString("aws.accesskey");
    private static final String SECRET_KEY = Play.application().configuration().getString("aws.secretkey");

    private AmazonS3 s3;

    public static BasicAWSCredentials getCredentials () {
        return new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
    }

    public static String s3Link (String key) {
        return S3_URL + key;
    }

    public LetsChaiAWS () {
        s3 = new AmazonS3Client(getCredentials());
    }

    public String uploadBase64Image (String base64Image, String contentType, String key) {
        byte[] byteArray = Base64.getDecoder().decode(base64Image);
        InputStream inputStream = new ByteArrayInputStream(byteArray);

        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(byteArray.length);
        meta.setContentType(contentType);

        s3.putObject(S3_BUCKET, key, inputStream, meta);

        return s3Link(key);
    }

}
