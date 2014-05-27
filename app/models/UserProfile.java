package models;

import com.restfb.types.User;
import org.apache.commons.lang3.text.WordUtils;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.jongo.MongoCollection;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by kedar on 5/14/14.
 */
public class UserProfile {

    // from com.restfb.types.User
    private String id;
    private String firstName;
    private String lastName;
    private String link;
    private Date birthday;
    private String email;
    private String gender;
    private boolean verified;
    private String city; // from User.location.name

    // custom fields
    private int pincode;
    private Gender genderGiven;     // to compare the gender they give us vs from facebook
    private int height; // in centimeters
    private Religion religion = Religion.valueOf("NO_PREFERENCE");
    private List<models.Education> education = new ArrayList<models.Education>(); // currently set to size of 2
    private List<ProfileQuestion> questions;
    private String occupation;

    public UserProfile () {} // for Jongo

    public UserProfile (User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.link = user.getLink();
        this.birthday = user.getBirthdayAsDate();
        this.email = user.getEmail();
        this.gender = user.getGender();
        this.verified = user.getVerified();
        this.city = user.getLocation().getName();
    }

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
        DateTime born = new DateTime(getBirthday());
        DateTime now = new DateTime();
        return Years.yearsBetween(born, now).getYears();
    }

    public int getHeight () { return height; }

    public String getReligion () {
        return WordUtils.capitalize(religion.toString().replace('_', ' ').toLowerCase());
    }

    public Religion getReligionAsEnum () {
        return religion;
    }

    public Religion[] religionValues () {
        return Religion.values();
    }

    public void generateQuestions () {
        questions = QuestionGenerator.generate(4);
    }

    public models.Education.EducationType[] getEducationTypes () {
        return models.Education.EducationType.class.getEnumConstants();
    }

    public String getOccupation () {
        return occupation;
    }

    public List<models.Education> getEducationForView () {
        List<models.Education> result = new ArrayList<models.Education>();
        for (models.Education e: education) {
            result.add(e);
        }
        while (result.size() < 2) {
            result.add(new models.Education());
        }
        return result;
    }

    public List<ProfileQuestion> getQuestions () {
        return questions;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getCity() {
        return city;
    }

    public String getLastName() {
        return lastName;
    }

    public String getLink() {
        return link;
    }

    public Date getBirthday() {
        return birthday;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public boolean isVerified() {
        return verified;
    }

    public Gender getGenderGiven() {
        return genderGiven;
    }

    public List<models.Education> getEducation() {
        return education;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setReligion(Religion religion) {
        this.religion = religion;
    }

    public void setEducation(List<Education> education) {
        this.education = education;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setAnswers (String[] answers) {
        if (answers.length != questions.size())
            throw new IllegalArgumentException("Number of answers must match number of questions");
        for (int i=0; i < answers.length; i++) {
            questions.get(i).setAnswer(answers[i]);
        }
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
