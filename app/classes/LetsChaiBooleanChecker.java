package classes;

import models.User;
import play.libs.F;
import types.Flag;
import types.Religion;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kedar on 7/25/14.
 */
public final class LetsChaiBooleanChecker {

    private FriendHandler friendHandler;
    private PincodeHandler pincodeHandler;
    private ChaiHandler chaiHandler;

    
    public LetsChaiBooleanChecker (FriendHandler friendHandler, PincodeHandler pincodeHandler, ChaiHandler chaiHandler) {
        this.friendHandler = friendHandler;
        this.pincodeHandler = pincodeHandler;
        this.chaiHandler = chaiHandler;
    }


    // true if they pass, false if they don't
    public Boolean associatives (User current, User candidate) {
        String currentId = current.getUserId();
        String candidateId = candidate.getUserId();
        User.Preferences currentPref = current.getPreferences();
        User.Preferences candidatePref = candidate.getPreferences();

        return  !currentId.equals(candidateId) && !friendHandler.isFriends(currentId, candidateId, false)   // same user & friends
                && currentPref.getAge().contains(candidate.getAge()) && candidatePref.getAge().contains(current.getAge())   // age
                && currentPref.getGender().equals(candidate.getGenderGiven()) && candidatePref.getGender().equals(current.getGenderGiven()) // gender
                && Religion.contains(currentPref.getReligion(), candidate.getReligion()) && Religion.contains(candidatePref.getReligion(), current.getReligion())   // religion
                && pincodeHandler.distance(current.getPincode(), candidate.getPincode()) < 50; // distance
    }

    // true if they pass
    public Boolean nonAssociatives (User user, User candidate) {
        return !chaiHandler.exists(user.getUserId(), candidate.getUserId()); // user doesn't have previous Chai for partner
    }

    // here for reference, these checks actually happen in UserHandler
    private Boolean individuals (User user) {
        return !user.hasFlag(Flag.DEACTIVATED) && !user.hasFlag(Flag.NOT_IN_BANGALORE);
    }

}
