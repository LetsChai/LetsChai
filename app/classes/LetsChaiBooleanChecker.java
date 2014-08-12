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
    public boolean associatives (User current, User candidate, List<Friends> friendList) {  // i don't like this friendList business, should change later
        if (!individuals(current) || !individuals(candidate))
            return false;

        String currentId = current.getUserId();
        String candidateId = candidate.getUserId();
        User.Preferences currentPref = current.getPreferences();
        User.Preferences candidatePref = candidate.getPreferences();

        boolean friends;
        List <Friends> entry = friendList.stream().filter(obj -> obj.getUsers().containsAll(Arrays.asList(currentId, candidateId)))
                .collect(Collectors.toList());
        friends = entry.size() > 0 && entry.get(0).isFriends(); // defaults to false

        return  !current.equals(candidate) && !friends   // same user & friends
                && currentPref.getAge().contains(candidate.getAge()) && candidatePref.getAge().contains(current.getAge())   // age
                && currentPref.getGender().toString().equals(candidate.getGender().toUpperCase()) && candidatePref.getGender().toString().equals(current.getGender().toUpperCase()) // gender
                && Religion.contains(currentPref.getReligion(), candidate.getReligion()) && Religion.contains(candidatePref.getReligion(), current.getReligion());   // religion
    }

    // true if they pass
    public boolean nonAssociatives (User user, User candidate, List<Chai> userChais) {
        return !userChais.contains(new Chai(user.getUserId(), candidate.getUserId(), 0)); // user doesn't have previous Chai for partner
    }

    // here for reference, these checks actually happen in UserHandler
    public boolean individuals (User user) {
        return !user.hasFlag(Flag.DEACTIVATED)
                && pincodeHandler.inBangalore(user.getPincode())
                && user.hasFlag(Flag.READY_TO_CHAI);
    }

}
