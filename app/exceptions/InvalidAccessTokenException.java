package exceptions;

/**
 * Created by kedar on 7/16/14.
 */
public class InvalidAccessTokenException extends Exception {

    private String userId;

    public InvalidAccessTokenException(String userId) {
        super("Invalid access token for user " + userId);
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
