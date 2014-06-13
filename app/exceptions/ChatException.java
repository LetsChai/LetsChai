package exceptions;

/**
 * Created by kedar on 6/10/14.
 */
public class ChatException extends Exception {

    public ChatException(String msg, Exception e) {
        super(msg, e);
    }
}
