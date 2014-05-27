package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.jongo.MongoCollection;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by kedar on 5/22/14.
 */
public class User {

    // from com.restfb.types.User
    private String userId;
    private String firstName;
    private String lastName;
    private String link;
    private Date birthday;
    private String email;
    private String gender;
    private boolean verified;
    private String city; // from User.location.name

    // other fields
    private UserAccessToken accessToken;
    private UserPreference preferences = new UserPreference();

    private int pincode;
    private Gender genderGiven;     // to compare the gender they give us vs from facebook
    private int height; // in centimeters
    private Religion religion = Religion.valueOf("NO_PREFERENCE");
    private List<models.Education> education = new ArrayList<Education>(); // currently set to size of 2
    private List<ProfileQuestion> questions;
    private String occupation;

    // non-stored fields
    @JsonIgnore
    private List<com.restfb.types.User> friends;

    // for Jackson
    private User () {}

    public User(com.restfb.types.User user) {
        this.userId = user.getId();
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
        return PlayJongo.getCollection("users");
    }

    public static User findOne (String userId) {
        return getCollection().findOne("{'userId': '#'}", userId).as(User.class);
    }

    public static Iterable<User> findAll () {
        return getCollection().find().as(User.class);
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

    public void setAnswers (String[] answers) {
        if (answers.length != questions.size())
            throw new IllegalArgumentException("Number of answers must match number of questions");
        for (int i=0; i < answers.length; i++) {
            questions.get(i).setAnswer(answers[i]);
        }
    }

    public void generateQuestions () {
        questions = QuestionGenerator.generate(4);
    }

    public int getAge () {
        DateTime born = new DateTime(getBirthday());
        DateTime now = new DateTime();
        return Years.yearsBetween(born, now).getYears();
    }

    public Date getBirthday () {
        return birthday;
    }

    public String getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getLink() {
        return link;
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

    public String getCity() {
        return city;
    }

    public UserAccessToken getAccessToken() {
        return accessToken;
    }

    public UserPreference getPreferences() {
        return preferences;
    }

    public int getPincode() {
        return pincode;
    }

    public Gender getGenderGiven() {
        return genderGiven;
    }

    public int getHeight() {
        return height;
    }

    public Religion getReligion() {
        return religion;
    }

    public List<Education> getEducation() {
        return education;
    }

    public List<ProfileQuestion> getQuestions() {
        return questions;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setAccessToken(UserAccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public void setPreferences(UserPreference preferences) {
        this.preferences = preferences;
    }

    public void setPincode(int pincode) {
        this.pincode = pincode;
    }

    public void setGenderGiven(Gender genderGiven) {
        this.genderGiven = genderGiven;
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

    public List<com.restfb.types.User> loadFriends () {
        if (friends != null)
            return friends;

        // lazy loading
        LetsChaiFacebookClient fb = new LetsChaiFacebookClient();
        fb.setAccessToken(accessToken.getAccessToken());
        return fb.fetchConnection("user/friends", com.restfb.types.User.class).getData();
    }

    public boolean isFriendsWith(String userId) {
        loadFriends();
        for (com.restfb.types.User friend: friends) {
            if (friend.getId() == userId) {
                return true;
            }
        }
        return false;
    }

    public void getMutualFriends () {
        LetsChaiFacebookClient fb = new LetsChaiFacebookClient();
        fb.setAccessToken(accessToken.getAccessToken());

    }

    public ZodiacSign getZodiacSign () {
        return ZodiacSign.fromDate(birthday);
    }

}
