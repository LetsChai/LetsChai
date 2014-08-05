package classes;

import models.Chai;
import models.Friends;
import models.User;
import types.Flag;
import types.Religion;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by kedar on 7/25/14.
 */
public final class LetsChaiBooleanChecker {

    private PincodeHandler pincodeHandler;
    
    public LetsChaiBooleanChecker (PincodeHandler pincodeHandler) {
        this.pincodeHandler = pincodeHandler;
    }

    // true if they pass, false if they don't
    public Boolean associatives (User current, User candidate, List<Friends> friendList) {  // i don't like this friendList business, should change later
        String currentId = current.getUserId();
        String candidateId = candidate.getUserId();
        User.Preferences currentPref = current.getPreferences();
        User.Preferences candidatePref = candidate.getPreferences();

        boolean friends;
        List <Friends> entry = friendList.stream().filter(obj -> obj.getUsers().containsAll(Arrays.asList(currentId, candidateId)))
                .collect(Collectors.toList());
        if (entry.size() > 0)
            friends = entry.get(0).isFriends();
        else
            friends = false; // default

        return  !current.equals(candidate) && !friends   // same user & friends
                && current.hasFlag(Flag.READY_TO_CHAI) && candidate.hasFlag(Flag.READY_TO_CHAI)
                && currentPref.getAge().contains(candidate.getAge()) && candidatePref.getAge().contains(current.getAge())   // age
                && currentPref.getGender().equals(candidate.getGenderGiven()) && candidatePref.getGender().equals(current.getGenderGiven()) // gender
                && Religion.contains(currentPref.getReligion(), candidate.getReligion()) && Religion.contains(candidatePref.getReligion(), current.getReligion())   // religion
                && pincodeHandler.distance(current.getPincode(), candidate.getPincode()) < 50; // distance
    }

    // true if they pass
    public Boolean nonAssociatives (User user, User candidate, List<Chai> userChais) {
        return !userChais.contains(new Chai(user.getUserId(), candidate.getUserId(), 0)); // user doesn't have previous Chai for partner
    }

    // here for reference, these checks actually happen in UserHandler
    public Boolean individuals (User user) {
        return !user.hasFlag(Flag.DEACTIVATED)
                && pincodeHandler.inBangalore(user.getPincode())
                && user.hasFlag(Flag.READY_TO_CHAI);
    }

}
