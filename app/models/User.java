package models;

import classes.QuestionGenerator;
import clients.LetsChaiAWS;
import clients.LetsChaiFacebookClient;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restfb.FacebookClient;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.jongo.MongoCollection;
import play.Logger;
import play.Play;
import play.libs.F;
import types.*;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.Base64;

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
    private AccessToken accessToken;
    private Preferences preferences = new Preferences();

    private int pincode;
    private Gender genderGiven;     // to compare the gender they give us vs from facebook
    private int height; // in centimeters
    private Religion religion = Religion.valueOf("NO_PREFERENCE");
    private List<Education> education = new ArrayList<Education>(); // currently set to size of 2
    private List<ProfileQuestion> questions;
    private String occupation;
    private EnumSet<Flag> flags = EnumSet.noneOf(Flag.class);
    private EnumSet<Permission> permissions = EnumSet.noneOf(Permission.class);
    private List<String> pictures = new ArrayList<>();
    private Date lastLogin;

    // non-stored fields
    @JsonIgnore
    private Friends friends;
    @JsonIgnore
    private Integer alternatePincode;

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
        if (user.getLocation() != null)
            this.city = user.getLocation().getName();
    }

    public User(String id, String firstName, String lastName) {
        this.userId = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public static MongoCollection getCollection() {
        return PlayJongo.getCollection("users");
    }

    public static User findOne (String userId) {
        return getCollection().findOne("{'userId': '#'}", userId).as(User.class);
    }

    public static Iterable<User> findMultiple (List<String> userIds) {
        String query = "{'userId': {'$in': [";
        if (userIds.size() > 0) {
            for (String id: userIds) {
                query += String.format("'%s',", id);
            }
            query = query.substring(0, query.length()-1);
        }
        query += "] }}";

        return getCollection().find(query).as(User.class);
    }

    public static Iterable<User> findAll () {
        return getCollection().find().as(User.class);
    }

    public static Boolean exists (String userId) {
        return getCollection().find("{'userId': '#'}", userId).limit(1).as(User.class).iterator().hasNext();
    }

    public static void update (String userId, String query, Object... parameters) {
        getCollection().update("{'userId': '#'}", userId).with(query, parameters);
    }

    public List<Education> getEducationForView () {
        List<Education> result = new ArrayList<Education>();
        for (Education e: education) {
            result.add(e);
        }
        while (result.size() < 2) {
            result.add(new Education());
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

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public Preferences getPreferences() {
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

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public void setPreferences(Preferences preferences) {
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

    public Date getLastLogin() {
        return lastLogin;
    }

    public void updateLastLogin() {
        this.lastLogin = new Date();
    }

    public String getName () {
        return firstName + " " + lastName;
    }
    public ZodiacSign getZodiacSign () {
        return ZodiacSign.fromDate(birthday);
    }

    public EnumSet<Flag> getFlags () {
        return flags;
    }

    public void addFlag (Flag flag) {
        flags.add(flag);
    }

    public EnumSet<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions (List<Permission> perms) {
        for (Permission p: perms) {
            this.permissions.add(p);
        }
    }

    public void uploadBase64Image (String base64Image, String contentType, Integer slot) {
        LetsChaiAWS aws = new LetsChaiAWS();
        String key = String.format("user_pictures/%s_%d", userId, slot);
        String url = aws.uploadBase64Image(base64Image, contentType, key);

        initializePictures();
        pictures.set(slot, url);
    }

    private void initializePictures () {
        while (pictures.size() < 4) {
            pictures.add(null);
        }
    }

    public List<String> getPictures() {
        return pictures;
    }


    public void setDefaultPicture (String url) {
        initializePictures();
        int i=0;
        for (String picture: pictures) {
            if (picture == null)
                pictures.set(i, url);
            i++;
        }
    }

    public void removePicture (int slot) {
        pictures.set(slot, null);
    }

    // force the browser to grab fresh load of pictures
    public void forceNoCachePictures () {
        for (int i=0; i < pictures.size(); i++) {
            String timestamp = String.valueOf(new Date().getTime());
            pictures.set(i, pictures.get(i).concat("?" + timestamp));
        }
    }

    public Integer getPictureCount () {
        int i=0;
        for (String pic: pictures) {
            if (pic != null)
                i++;
        }
        return i;
    }

    public Integer getAlternatePincode() {
        return alternatePincode;
    }

    public void setAlternatePincode(Integer alternatePincode) {
        this.alternatePincode = alternatePincode;
    }

    // checks if there's an alternate pincode (which means the real pincode) is invalid, and gives it if it exists, else gives the real pincode
    public int getValidPincode () {
        if (alternatePincode == null)
            return pincode;
        return alternatePincode;
    }

    public boolean hasAlternatePincode () {
        return alternatePincode != null;
    }

    public boolean hasFlag(Flag flag) {
        return flags.contains(flag);
    }

    public void removeFlag (Flag flag) {
        flags.remove(flag);
    }

    public void addFlags (List<Flag> flagList) {
        for (Flag f: flagList) {
            addFlag(f);
        }
    }

    // updates document in MongoDB
    public void update () {
        getCollection().update("{'userId': '#'}", getUserId()).with(this);
    }

    public F.Promise<Boolean> updatePermissions () {
        LetsChaiFacebookClient fb = new LetsChaiFacebookClient(accessToken.getAccessToken());
        return fb.getPermissions(userId).map(perms -> {
            Logger.info(perms.toString());
            setPermissions(perms);
            return true;
        });
    }

    /**
     * Created by kedar on 5/14/14.
     */
    public static class AccessToken {

        protected String accessToken;
        protected Date expires;

        public static MongoCollection getCollection () {
            return PlayJongo.getCollection("user_access_tokens");
        }

        public static AccessToken findOne (String userId) {
            return getCollection().findOne("{'userId': '#'}", userId).as(AccessToken.class);
        }

        public AccessToken() {}

        public AccessToken(FacebookClient.AccessToken token) {
            this.accessToken = token.getAccessToken();
            this.expires = token.getExpires();
        }

        public static AccessToken fromQueryString (String query) {
            return new AccessToken(FacebookClient.AccessToken.fromQueryString(query));
        }

        public String getAccessToken () {
            return accessToken;
        }

        public Date getExpires () {
            return expires;
        }
    }

    /**
     * Created by kedar on 5/14/14.
     */
    public static class Preferences {

        private Gender gender;
        private AgeRange age = new AgeRange();
        private List<Religion> religion = Arrays.asList(Religion.NO_PREFERENCE);

        public Preferences() {}

        public static MongoCollection getCollection () {
            return PlayJongo.getCollection("user_preferences");
        }

        public static Preferences findOne (String userId) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            return getCollection().findOne("{'userId': '#'}", userId).as(Preferences.class);
        }

        public Preferences(Gender gender, AgeRange age) {
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
}
