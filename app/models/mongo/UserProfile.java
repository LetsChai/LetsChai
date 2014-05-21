package models.mongo;

import com.restfb.types.User;
import models.ProfileQuestion;
import models.QuestionGenerator;
import models.preferences.Gender;
import models.preferences.Religion;
import org.apache.commons.lang3.text.WordUtils;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.jongo.MongoCollection;
import uk.co.panaxiom.playjongo.PlayJongo;

/**
 * Created by kedar on 5/14/14.
 */
public class UserProfile extends User implements MongoModel {

    private int pincode;
    private Gender genderGiven;
    private int height; // centimeters
    private Religion religion = Religion.valueOf("NO_PREFERENCE");
    private ProfileQuestion[] questions;

    public static MongoCollection getCollection() {
        return PlayJongo.getCollection("user_profiles");
    }

    public static UserProfile findOne(String id) {
        return getCollection().findOne("{'id': '" + id + "'}").as(UserProfile.class);
    }

    public void setGenderGiven(Gender genderGiven) {
        this.genderGiven = genderGiven;
    }

    public int getPincode() {
        return pincode;
    }

    public void setPincode(int pincode) {
        this.pincode = pincode;
    }

    public int getAge () {
        DateTime born = new DateTime(getBirthdayAsDate());
        DateTime now = new DateTime();
        return Years.yearsBetween(born, now).getYears();
    }

    public String getCity () {
        return getLocation().getName();
    }

    public int getHeight () { return height; }

    public String getReligion () {
        return WordUtils.capitalize(religion.toString().replace('_', ' ').toLowerCase());
    }

    public Religion getReligionAsEnum () {
        return religion;
    }

    public Religion[] religionValues () {
        return religion.values();
    }

    public String getCollege () {
        for (Education e: getEducation()) {
            if (e.getType().trim().equals("College"))
                return e.getSchool().getName();
        }
        return "";
    }

    public void generateQuestions () {
        questions = QuestionGenerator.generate(4);
    }

}
