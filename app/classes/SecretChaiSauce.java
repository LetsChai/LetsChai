package classes;

import models.Chai;
import models.User;
import org.apache.commons.lang3.Validate;
import play.Logger;
import play.libs.F;
import types.Flag;
import types.Friends;
import types.Pincode;
import types.Religion;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by kedar on 5/24/14.
 */

public class SecretChaiSauce {

    private List<User> users;
    private Map<Integer, Pincode> pincodes = new HashMap<>();

    private List<User> validUsers;

    public SecretChaiSauce (List<User> users, List<Pincode> pincodeList) {
        Validate.notNull(users);
        Validate.notNull(pincodeList);
        Validate.isTrue(users.size() > 0);
        Validate.isTrue(pincodeList.size() > 0);

        this.users = users;
        pincodeList.stream().forEach(pin -> pincodes.put(pin.getPincode(), pin));   // convert list to map
    }

    public F.Promise<Boolean> run () {
        Long time = new Date().getTime();
        PlayJongo.getCollection("users_backup_" + time.toString()).insert(users.toArray()); // backup users

        users.stream()
                .filter(user -> !Pincode.inBangalore(user.getPincode()))
                .forEach(user -> user.addFlag(Flag.NOT_IN_BANGALORE));

        users.stream()
                .filter(user -> !isValidPincode(user.getPincode()))
                .forEach(user -> user.addFlag(Flag.INVALID_PINCODE));

        users.stream()
                .filter(user -> user.hasFlag(Flag.INVALID_PINCODE) && !user.hasFlag(Flag.NOT_IN_BANGALORE))
                .forEach(user -> user.setAlternatePincode(560001)); // need to discount for this later

        validUsers = users.stream()
                .filter(user -> !user.hasFlag(Flag.NOT_IN_BANGALORE))
                .collect(Collectors.toList());

       List<F.Promise<F.Tuple<User, Chai>>> chaiList = validUsers.stream()
                .map(user -> F.Promise.promise(() -> user).zip(bestChai(user)))
                .collect(Collectors.toList());

       F.Promise<F.Tuple<User,Chai>>[] chais = new F.Promise[validUsers.size()];
       for (int i=0; i<validUsers.size(); i++) {
           chais[i] = chaiList.get(i);
       }

       return F.Promise.sequence(chais).map(tupleList -> {
            tupleList.forEach(tuple -> {
                if (tuple._2 != null)       // To cover the off chance of NO_MATCHES
                    tuple._1.addChai(tuple._2);
            });
            PlayJongo.getCollection("users").drop();
            PlayJongo.getCollection("users").insert(users.toArray());
            return true;
       });
    }

    // all invalid pincodes MUST BE FILTERED OUT before invoking this function
    public F.Promise<Chai> bestChai (User user) {
        List<F.Promise<Chai>> possibleChais = new ArrayList<F.Promise<Chai>>();
        double localMaxScore = 0;

        for (User partner: validUsers) {
            if (user.hasChai(partner.getUserId()))
                continue;
            if (!passesMutualLocalBooleans(user, partner))
                continue;
            final double localScore = localChaiScore(user, partner);
            if (localScore + 0.35 < localMaxScore)
                continue;
            F.Promise<Friends> mutualFriends = user.getMutualFriends(partner.getUserId());
            F.Promise<Double> mutualFriendScore = mutualFriends.map(friends -> mutualFriendScore(friends));
            F.Promise<Boolean> externalBools = passesMutualExternalBooleans(user, partner);

            F.Promise<Double> chaiScore = mutualFriendScore.zip(externalBools)
                    .map(t -> (t._1 * .35 + localScore) * (t._2 ? 1 : 0));
            F.Promise<Chai> possibleChai = chaiScore.zip(mutualFriends)
                    .map(t -> new Chai(user.getUserId(), partner.getUserId(), t._1, t._2));

            possibleChais.add(possibleChai);
        }

        if (possibleChais.size() == 0) {
            Logger.info(user.getUserId().toString() + " has no possible matches");
            user.addFlag(Flag.NO_MATCHES);
            return F.Promise.promise( () -> null);
        } else {
            Logger.info(user.getUserId().toString() + " found atleast one match");
        }

        // because of a bug in Play, it must be converted to array
        F.Promise<Chai> [] promiseArray = new F.Promise[possibleChais.size()];
        for (int i=0; i < possibleChais.size(); i++) {
            promiseArray[i] = possibleChais.get(i);
        }

        F.Promise<List<Chai>> listPromise = F.Promise.sequence(promiseArray);

        return listPromise.map(chaiList -> Collections.max(chaiList, (chai1, chai2) ->
                        Double.compare(chai1.getScore(), chai2.getScore())));
    }

    public double localChaiScore (User user, User partner)  {
        return 0.51 * matchScore(user, partner) + 0.14 * distanceScore(user.getValidPincode(), partner.getValidPincode());
    }

    // all boolean checks that can be carried out with external API calls
    public boolean passesMutualLocalBooleans (User current, User candidate) {
        User.Preferences currentPref = current.getPreferences();
        User.Preferences candidatePref = candidate.getPreferences();
        Pincode userPincode = pincodes.get(current.getValidPincode());
        Pincode candidatePincode = pincodes.get(candidate.getValidPincode());

        return  current.getUserId() != candidate.getUserId()
                && currentPref.getAge().contains(candidate.getAge()) && candidatePref.getAge().contains(current.getAge())   // age
                && currentPref.getGender().equals(candidate.getGenderGiven()) && candidatePref.getGender().equals(current.getGenderGiven()) // gender
                && Religion.contains(currentPref.getReligion(), candidate.getReligion()) && Religion.contains(candidatePref.getReligion(), current.getReligion())   // religion
                && userPincode.distanceFrom(candidatePincode) < 50; // distance
    }

    public F.Promise<Boolean> passesMutualExternalBooleans (User current, User candidate) {
        return current.isFriendsWith(candidate.getUserId())
                .map(bool -> !bool);     // return the opposite of the function call
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
        return (double) score / 6;
    }

    public double mutualFriendScore(Friends mutualFriends) {
        int count = mutualFriends.getCount();
        return mutualFriends.getFriends().size() > 0 ? 1.0 :
            count <= 0 ? 0.0 :
            count == 1 ? 0.5 :
            count == 2 ? 0.65 :
            count == 3 ? 0.8 :
            0.95;
    }

    public F.Promise<Double> mutualFriendScore (User current, User other) {
        return current.getMutualFriends(other.getUserId())
                .map( mutualFriends -> mutualFriendScore(mutualFriends));
    }

    // return 1 if the partner has been matched and accepted the chai, 0 otherwise
    public double matchScore (User user, User partner) {
        Chai chai = partner.getChai(user.getUserId());
        return chai == null ? 0 :
                chai.getMyDecision().isYes() ? 1 : 0;
    }

    public boolean isValidPincode (int pincode) {
        return pincodes.get(pincode) != null;
    }
}