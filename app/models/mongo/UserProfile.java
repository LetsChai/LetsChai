package models.mongo;

import com.restfb.types.User;
import models.preferences.Gender;
import org.jongo.MongoCollection;
import uk.co.panaxiom.playjongo.PlayJongo;

/**
 * Created by kedar on 5/14/14.
 */
public class UserProfile extends User implements MongoModel {

    private int pincode;
    private Gender genderGiven;

    public static MongoCollection getCollection() {
        return PlayJongo.getCollection("user_profiles");
    }

    public void setGenderGiven(Gender genderGiven) {
        this.genderGiven = genderGiven;
    }

    public int getPincode() {
        return pincode;
    }

    public void setPincode(int pincode) {
        this.pincode = pincode;
    }
}
