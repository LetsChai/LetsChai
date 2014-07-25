package classes;

import models.User;
import play.libs.F;
import types.Pincode;
import types.Religion;

/**
 * Created by kedar on 7/25/14.
 */
public class LetsChaiBooleanChecker {
    FriendHandler friendHandler;
    PincodeHandler pincodeHandler;

    public LetsChaiBooleanChecker (FriendHandler friendHandler, PincodeHandler pincodeHandler) {
        this.friendHandler = friendHandler;
        this.pincodeHandler = pincodeHandler;
    }

    public Boolean localAssociatives (User user, User other) {
        User.Preferences currentPref = current.getPreferences();
        User.Preferences candidatePref = candidate.getPreferences();
        Pincode userPincode = pincodes.get(current.getValidPincode());
        Pincode candidatePincode = pincodes.get(candidate.getValidPincode());

        return  !current.getUserId().equals(candidate.getUserId())
                && currentPref.getAge().contains(candidate.getAge()) && candidatePref.getAge().contains(current.getAge())   // age
                && currentPref.getGender().equals(candidate.getGenderGiven()) && candidatePref.getGender().equals(current.getGenderGiven()) // gender
                && Religion.contains(currentPref.getReligion(), candidate.getReligion()) && Religion.contains(candidatePref.getReligion(), current.getReligion())   // religion
                && userPincode.distanceFrom(candidatePincode) < 50; // distance
    }

    public F.Promise<Boolean> externalAssociatives (User user, User other) {
        return friendHandler.isFriends(user, other).map(bool -> !bool);
    }

    public Boolean localIndividuals (User user) {

    }

    public F.Promise<Boolean> externalIndividuals (User user) {
        return F.Promise.promise(() -> true);
    }

}
