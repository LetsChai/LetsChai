package models;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.codec.binary.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.jongo.MongoCollection;
import play.Logger;
import play.Play;
import play.libs.F;
import play.libs.WS;
import uk.co.panaxiom.playjongo.PlayJongo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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
    private UserAccessToken accessToken;
    private UserPreference preferences = new UserPreference();

    private int pincode;
    private Gender genderGiven;     // to compare the gender they give us vs from facebook
    private int height; // in centimeters
    private Religion religion = Religion.valueOf("NO_PREFERENCE");
    private List<models.Education> education = new ArrayList<Education>(); // currently set to size of 2
    private List<ProfileQuestion> questions;
    private String occupation;
    private List<Chai> chais = new ArrayList<Chai>();
    private EnumSet<Flag> flags = EnumSet.noneOf(Flag.class);
    private EnumSet<Permission> permissions = EnumSet.noneOf(Permission.class);
    private List<String> pictures = new ArrayList<>();

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

    public String getName () {
        return firstName + " " + lastName;
    }
    public ZodiacSign getZodiacSign () {
        return ZodiacSign.fromDate(birthday);
    }

    public F.Promise<Friends> getFriends () {
        if (friends != null)
            return F.Promise.promise(() -> friends);
        return getMutualFriends(userId).map(myFriends -> {
            this.friends = myFriends;
            return myFriends;
        });
    }

    public F.Promise<Friends> getMutualFriends (String friendId) {
        LetsChaiFacebookClient fb = new LetsChaiFacebookClient(accessToken.getAccessToken());
        return fb.getMutualFriends(friendId);
    }

    public F.Promise<Boolean> isFriendsWith(String friendId) {
        LetsChaiFacebookClient fb = new LetsChaiFacebookClient(accessToken.getAccessToken());
        return fb.areFriends(userId, friendId);
    }

    // get the chai between this user and the specified userId, returns null if none exists
    public Chai getChaiWith (String userId) {
        for (Chai chai: chais) {
            if (chai.getUserId() == userId)
                return chai;
        }
        return null;
    }

    public Chai getChai (String userId) {
        return getChaiWith(userId);
    }

    public boolean hasChai (String userId) {
        for (Chai chai: chais) {
            if (chai.getUserId() == userId)
                return true;
        }
        return false;
    }

    public void addChai (Chai chai) {
        chais.add(chai);
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

    public void updatePermissions () {
        String url = String.format("https://graph.facebook.com/v2.0/%s/permissions?access_token=%s", userId, accessToken.getAccessToken());
        String jsonString = null;
        try {
            jsonString = Request.Get(url).execute().returnContent().asString();
        } catch (ClientProtocolException e) {
            Logger.error("ClientProtocolException during request: " + url);
            return;
        } catch (IOException e) {
            Logger.error("IOException during request: " + url);
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = null;
        try {
            response = mapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            Logger.error("JsonProcessingException in JSON: " + jsonString);
            e.printStackTrace();
            return;
        } catch (IOException e) {
            Logger.error("IOException while parsing JSON: " + jsonString);
            e.printStackTrace();
            return;
        } catch (NullPointerException e) {
            Logger.error(url + " returned an empty response");
            return;
        }
        if (!response.has("data")) {
            Logger.error(" node 'data' could not be found in JSON response");
            return;
        }

        permissions.clear();
        for (JsonNode j: response.path("data")) {
            if (j.path("status").asText().equals("granted"))
                permissions.add(Permission.valueOf(j.path("permission").asText().toUpperCase()));
        }
    }

    public void updateMongo () {
        String query = String.format("{'userId': '%s'}", userId);
        getCollection().update(query).with(this);
    }

    public List<Chai> getMatches () {
        List<Chai> matches = new ArrayList<>();
        for (Chai chai: chais) {
            if (chai.isMatch())
                matches.add(chai);
        }
        return matches;
    }

    public List<User> getMatchedUsers () {
        List<Chai> matches = getMatches();

        List<String> userIds = new ArrayList<>();
        for (Chai chai: matches) {
            userIds.add(chai.getUserId());
        }

        Iterable<User> userIterable = User.findMultiple(userIds);
        List<User> result = new ArrayList<>();
        for (User user: userIterable) {
            result.add(user);
        }

        return result;
    }

    public void uploadBase64Image (String base64Image, String contentType, Integer slot) {
        AmazonS3 s3 = new AmazonS3Client(LetsChaiAWS.getCredentials());
        byte[] byteArray = Base64.getDecoder().decode(base64Image);
        InputStream inputStream = new ByteArrayInputStream(byteArray);

        String key = String.format("user_pictures/%s_%d", getUserId(), slot);
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(byteArray.length);
        meta.setContentType(contentType);

        s3.putObject("letschai", key, inputStream, meta);

        initializePictures();
        String url = Play.application().configuration().getString("aws.s3url") + key;
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


}
