package models.mongo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.preferences.AgeRange;
import models.preferences.Gender;
import models.preferences.Religion;
import org.jongo.MongoCollection;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.util.List;

/**
 * Created by kedar on 5/14/14.
 */
public class UserPreference implements MongoModel {

    private String userId;
    private Gender gender;
    private AgeRange age;
    private List<Religion> religion;

    public UserPreference () {}

    public static MongoCollection getCollection () {
        return PlayJongo.getCollection("user_preferences");
    }

    public static UserPreference findOne (String userId) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return getCollection().findOne("{'userId': '#'}", userId).as(UserPreference.class);
    }

    public UserPreference (String userId, Gender gender, AgeRange age) {
        this.userId = userId;
        this.gender = gender;
        this.age = age;
    }

    public String getUserId() {
        return userId;
    }

    public Gender getGender() {
        return gender;
    }

    public AgeRange getAge() {
        return age;
    }

    public List<Religion> getReligion() {
        return religion;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setAge(AgeRange age) {
        this.age = age;
    }

    public void setReligion(List<Religion> religion) {
        this.religion = religion;
    }

    public boolean matchesReligion(Religion test) {
        for (Religion check: this.religion) {
            if (test.equals(check))
                return true;
        }
        return false;
    }
}
