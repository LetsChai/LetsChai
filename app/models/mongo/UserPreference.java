package models.mongo;

import models.preferences.Gender;
import models.preferences.Religion;
import org.apache.commons.lang3.Range;
import org.jongo.MongoCollection;
import uk.co.panaxiom.playjongo.PlayJongo;

/**
 * Created by kedar on 5/14/14.
 */
public class UserPreference implements MongoModel {

    private String userId;
    private Gender gender;
    private Range<Integer> age;
    private Religion religion;

    public static MongoCollection getCollection () {
        return PlayJongo.getCollection("user_preferences");
    }

    public UserPreference (String userId, Gender gender, Range<Integer> age) {
        this.gender = gender;
        this.age = age;
        this.religion = religion;
    }
}
