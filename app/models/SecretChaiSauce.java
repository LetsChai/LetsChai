package models;

import seeds.PincodeSeed;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.lang.Iterable;
import java.util.HashMap;

/**
 * Created by kedar on 5/24/14.
 */

public class SecretChaiSauce {

    private HashMap<String, User> users = new HashMap<>();
    private HashMap<Integer, Pincode> pincodes = new HashMap<>();


    public SecretChaiSauce () {}

    public void run () {

        // create giant table of all the users
        Iterable<User> userIterable = PlayJongo.getCollection("test_users").find().as(User.class);
        for (User u: userIterable) {
            users.put(u.getUserId(), u);
        }

        // get all the pincodes
        Iterable<Pincode> pincodeIterable = PlayJongo.getCollection("profiles_gmaps").find("{'city':'Bangalore'}").as(Pincode.class);
        for (Pincode p: pincodeIterable) {
            pincodes.put(p.getPincode(), p);
        }

        // algorithm
        for (User current: userIterable) {
            for (User candidate: userIterable) {

            }
        }
    }

    // returns the integer value of the boolean check (true: 1, false: 0)
    public int booleanChecks (User current, User candidate) {
        UserPreference currentPref = current.getPreferences();
        UserPreference candidatePref = candidate.getPreferences();

        Pincode userPincode = pincodes.get(current.getPincode());
        Pincode candidatePincode = pincodes.get(candidate.getPincode());

        boolean check =
            // age
            currentPref.getAge().contains(candidate.getAge()) &&
            candidatePref.getAge().contains(current.getAge()) &&
            // gender
            currentPref.getGender().equals(candidate.getGenderGiven()) &&
            candidatePref.getGender().equals(current.getGenderGiven()) &&
            // religion
            Religion.contains(currentPref.getReligion(), candidate.getReligion()) &&
            Religion.contains(candidatePref.getReligion(), current.getReligion()) &&
            // distance < 50
            userPincode.distanceFrom(candidatePincode) < 50 &&
            // rejection

            // facebook friends
            !current.isFriendsWith(candidate.getUserId());

        return check ? 1:0;
    }

    public double distanceScore (int userPincode, int candidatePincode) {
        double distance = pincodes.get(userPincode).distanceFrom(pincodes.get(candidatePincode));
        int score = 0; // out of 6 for now
        if (distance < 2.5) {
            score = 6;
        } else if (distance < 5) {
            score = 5;
        } else if (distance < 7.5) {
            score = 4;
        } else if (distance < 10) {
            score = 3;
        } else if (distance < 15) {
            score = 2;
        } else if (distance < 25) {
            score = 1;
        }
        return score / 6;
    }

    public double mutualFriendScore (User current, User other) {
        return 0;
    }

}