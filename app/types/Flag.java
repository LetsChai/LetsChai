package types;

/**
 * Created by kedar on 6/7/14.
 */
public enum Flag {
    // INVALID_PINCODE is for a pincode that doesn't exist in our database
    // NOT_IN_BANGALORE is for outside the pincode range 560001-560999
    INVALID_PINCODE, NOT_IN_BANGALORE, NO_MATCHES, READY_TO_CHAI, INCOMPLETE_PROFILE, NO_PICTURES, DEACTIVATED;
}
