package exceptions;

/**
 * Created by kedar on 6/7/14.
 */
public class InvalidPincodeException extends Exception {

    private int pincode;
    private String userId;

    public InvalidPincodeException (int pincode, String userId) {
        super(String.format("Invalid pincode %d for user %s", pincode, userId));
        this.pincode = pincode;
        this.userId = userId;
    }

    public InvalidPincodeException (int pincode) {
        super(String.format("Invalid pincode %d", pincode));
        this.pincode = pincode;
    }

    public InvalidPincodeException(String message) {
        super(message);
    }

    public InvalidPincodeException () {
        super("Invalid pincode");
    }

    public int getPincode() {
        return pincode;
    }

    public String getUserId() {
        return userId;
    }
}
