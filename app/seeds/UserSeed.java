package seeds;

import models.*;

import java.util.Date;
import java.util.List;

/**
 * Created by kedar on 5/24/14.
 */
public class UserSeed {
    public String userId;
    public String firstName;
    public String lastName;
    public String link;
    public Date birthday;
    public String email;
    public String gender;
    public boolean verified;
    public String city;
    public int pincode;
    public Gender genderGiven;
    public int height;
    public Religion religion;
    public List<Education> education;
    public List<ProfileQuestion> questions;
    public String occupation;

    public UserPreferenceSeed preferences;
    public UserAccessToken accessToken;
}
