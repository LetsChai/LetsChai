package models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jongo.MongoCollection;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.util.Arrays;
import java.util.List;

/**
 * Created by kedar on 5/14/14.
 */
public class UserPreference {

    private Gender gender;
    private AgeRange age = new AgeRange();
    private List<Religion> religion = Arrays.asList(Religion.NO_PREFERENCE);

    public UserPreference () {}

    public static MongoCollection getCollection () {
        return PlayJongo.getCollection("user_preferences");
    }

    public static UserPreference findOne (String userId) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return getCollection().findOne("{'userId': '#'}", userId).as(UserPreference.class);
    }

    public UserPreference (Gender gender, AgeRange age) {
        this.gender = gender;
        this.age = age;
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
