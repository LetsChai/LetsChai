package models;

import seeds.PincodeSeed;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.lang.Iterable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kedar on 5/24/14.
 */

public class SecretChaiSauce {

    private Map<String, User> users;
    private Map<Integer, Pincode> pincodes;


    public SecretChaiSauce () {}

    // user and pincode injection for testing purposes
    public SecretChaiSauce (Map<String, User> users, Map<Integer, Pincode> pincodes) {
        this();
        this.users = users;
        this.pincodes = pincodes;
    }

    public void run () {
        loadPincodes();
        loadUsers();

        // algorithm
        for (User user: users.values()) {
            user.addChai(getBestMatch(user));
        }
    }

    public Chai getBestMatch (User user) {
        double bestScore = 0;
        User bestMatch = null;
        for (User partner: users.values()) {
            if (user.getChaiWith(partner.getUserId()) == null)
                continue;
            double chaiScore = chaiScore(user,partner);
            if (chaiScore > bestScore) {
                bestScore = chaiScore;
                bestMatch = partner;
            }
        }
        return new Chai(user.getUserId(), bestMatch.getUserId());
    }

    public double chaiScore (User user, User partner) {
        return booleanChecks(user, partner) *
                (0.14 * distanceScore(user.getPincode(), partner.getPincode()) + 0.35 * mutualFriendScore(user, partner)) +
        0.51 * matchScore(user,partner);
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
        Friends mutualFriends = current.getMutualFriends(other.getUserId());
        if (mutualFriends.getFriends().size() > 0)
            return 1;
        int count = mutualFriends.getCount();
        if (count <= 0)
            return 0;
        else if (count == 1)
            return 0.5;
        else if (count == 2)
            return 0.65;
        else if (count == 3)
            return 0.8;
        return 0.95;
    }

    public double matchScore (User user, User partner) {
        Chai chai = user.getChaiWith(partner.getUserId());
        if (chai == null)
            return 0;
        if (chai.getChoice(partner.getUserId()) == true)
            return 1;
        return 0;
    }

    public void loadPincodes () {
        if (pincodes != null)
            return;

        pincodes = new HashMap<>();
        Iterable<Pincode> pincodeIterable = PlayJongo.getCollection("profiles_gmaps").find("{'city':'Bangalore'}").as(Pincode.class);
        for (Pincode p: pincodeIterable) {
            pincodes.put(p.getPincode(), p);
        }
    }

    // create giant table of all the users
    public void loadUsers () {
        if (users != null)
            return;

        users = new HashMap<>();
        Iterable<User> userIterable = PlayJongo.getCollection("test_users").find().as(User.class);
        for (User u: userIterable) {
            users.put(u.getUserId(), u);
        }
    }

}