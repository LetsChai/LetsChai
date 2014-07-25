package classes;

import types.Friends;

/**
 * Created by kedar on 7/25/14.
 */
public class LetsChaiScorer {

    public final Double MUTUAL_FRIEND_WEIGHT = 0.35;
    public final Double DISTANCE_WEIGHT = 0.14;
    public final Double LIKED_MATCH_WEIGHT = 0.51;

    public LetsChaiScorer () {}

    public Double mutualFriendScore (Friends friends) {
        int count = friends.getCount();
        Double unweighted = friends.getFriends().size() > 0 ? 1.0 :
            count <= 0 ? 0.0 :
            count == 1 ? 0.5 :
            count == 2 ? 0.65 :
            count == 3 ? 0.8 :
            0.95;

        return unweighted * MUTUAL_FRIEND_WEIGHT;
    }

    public Double distanceScore (Double distance) {
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
        Double normalized = (double) score / 6;

        return normalized * DISTANCE_WEIGHT;
    }

    public Double matchScore (Boolean hasMatch) {
        Double unweighted = hasMatch ? 1.0 : 0.0;
        return unweighted * LIKED_MATCH_WEIGHT;
    }
}
