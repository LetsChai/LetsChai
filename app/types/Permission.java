package types;

/**
 * Created by kedar on 6/7/14.
 */
public enum Permission {
    INSTALLED, PUBLIC_PROFILE, USER_BIRTHDAY, USER_FRIENDS, EMAIL, USER_LOCATION, USER_PHOTOS;

    public String lowerCase () {
        return this.toString().replace('_', ' ').toLowerCase();
    }

    public String lowerCaseNoSpaces () {
        return this.toString().toLowerCase();
    }
}
