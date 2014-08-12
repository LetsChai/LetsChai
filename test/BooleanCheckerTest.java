import classes.LetsChaiBooleanChecker;
import classes.PincodeHandler;
import com.mongodb.MongoClient;
import models.Friends;
import models.User;
import org.joda.time.DateTime;
import org.jongo.Jongo;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import play.Logger;
import play.test.FakeApplication;
import play.test.Helpers;
import types.AgeRange;
import org.junit.Assert;
import types.Flag;
import types.Gender;
import types.Religion;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kedar on 8/12/14.
 */


public class BooleanCheckerTest {
    private User user1;
    private User user2;
    private List<Friends> friends = new ArrayList<>();
    private static Jongo jongo;
    private static LetsChaiBooleanChecker checker;
    private static FakeApplication app;

    @BeforeClass
    public static void db () throws UnknownHostException {
        app = Helpers.fakeApplication();
        Helpers.start(app);
        jongo = new Jongo(new MongoClient("localhost").getDB("LetsChai"));
        checker = new LetsChaiBooleanChecker(PincodeHandler.getInstance());
    }

    @Before
    public void setup () {
        user1 = jongo.getCollection("defaults").findOne("{'userId':'1'}").as(User.class);
        user2 = jongo.getCollection("defaults").findOne("{'userId':'2'}").as(User.class);

        flagSet("r", "r");
        pincodeSet(560001, 560001);
        genderSet(user1, 'm', 'f');
        genderSet(user2, 'f', 'm');
        ageSet(user1, 25, 18, 30);
        ageSet(user2, 25, 18, 30);
        religionSet(user1, 'n', "n");
        religionSet(user2, 'n', "n");
    }

    @Test
    public void initials () {
        Assert.assertNotNull(jongo);
        Assert.assertNotNull(user1);
        Assert.assertNotNull(user2);
        Assert.assertTrue(checker.individuals(user1));
        Assert.assertTrue(checker.individuals(user2));
        Assert.assertTrue(checker.associatives(user1, user2, friends));
        Assert.assertTrue(checker.associatives(user2, user1, friends));
    }

    @Test
    public void flagChecks () {
        flagSet("r", "r");
        Assert.assertTrue(checker.associatives(user1, user2, friends));
        Assert.assertTrue(checker.associatives(user2, user1, friends));
        Assert.assertTrue(checker.individuals(user1));
        Assert.assertTrue(checker.individuals(user2));

        flagSet("rd", "rd");
        Assert.assertFalse(checker.associatives(user1, user2, friends));
        Assert.assertFalse(checker.associatives(user2, user1, friends));
        Assert.assertFalse(checker.individuals(user1));
        Assert.assertFalse(checker.individuals(user2));

        flagSet("", "");
        Assert.assertFalse(checker.associatives(user1, user2, friends));
        Assert.assertFalse(checker.associatives(user2, user1, friends));
        Assert.assertFalse(checker.individuals(user1));
        Assert.assertFalse(checker.individuals(user2));

        flagSet("d", "d");
        Assert.assertFalse(checker.associatives(user1, user2, friends));
        Assert.assertFalse(checker.associatives(user2, user1, friends));
        Assert.assertFalse(checker.individuals(user1));
        Assert.assertFalse(checker.individuals(user2));
    }

    @Test
    public void pincodeChecks () {
        pincodeSet(560001, 560002);
        Assert.assertTrue(checker.associatives(user1, user2, friends));
        Assert.assertTrue(checker.associatives(user2, user1, friends));
        Assert.assertTrue(checker.individuals(user1));
        Assert.assertTrue(checker.individuals(user2));

        pincodeSet(110002, 560001);
        Assert.assertFalse(checker.associatives(user1, user2, friends));
        Assert.assertFalse(checker.associatives(user2, user1, friends));
        Assert.assertFalse(checker.individuals(user1));
        Assert.assertTrue(checker.individuals(user2));

        pincodeSet(110002, 400020);
        Assert.assertFalse(checker.associatives(user1, user2, friends));
        Assert.assertFalse(checker.associatives(user2, user1, friends));
        Assert.assertFalse(checker.individuals(user1));
        Assert.assertFalse(checker.individuals(user2));

    }

    @Test
    public void ageChecks () {
        ageSet(user1, 25, 25, 30);
        ageSet(user2, 25, 25, 30);
        Assert.assertTrue(checker.associatives(user1, user2, friends));
        Assert.assertTrue(checker.associatives(user2, user1, friends));

        ageSet(user1, 25, 25, 30);
        ageSet(user2, 18, 25, 30);
        Assert.assertFalse(checker.associatives(user1, user2, friends));
        Assert.assertFalse(checker.associatives(user2, user1, friends));

        ageSet(user1, 25, 25, 30);
        ageSet(user2, 18, 25, 30);
        Assert.assertFalse(checker.associatives(user1, user2, friends));
        Assert.assertFalse(checker.associatives(user2, user1, friends));

        ageSet(user1, 23, 25, 30);
        ageSet(user2, 29, 21, 26);
        Assert.assertTrue(checker.associatives(user1, user2, friends));
        Assert.assertTrue(checker.associatives(user2, user1, friends));
    }

    @Test
    public void genderChecks () {
        genderSet(user1, 'm', 'm');
        genderSet(user2, 'm', 'm');
        Assert.assertTrue(checker.associatives(user1, user2, friends));
        Assert.assertTrue(checker.associatives(user2, user1, friends));

        genderSet(user1, 'f', 'f');
        genderSet(user2, 'f', 'f');
        Assert.assertTrue(checker.associatives(user1, user2, friends));
        Assert.assertTrue(checker.associatives(user2, user1, friends));

        genderSet(user1, 'm', 'f'); // fails
        genderSet(user2, 'm', 'f');
        Assert.assertFalse(checker.associatives(user1, user2, friends));
        Assert.assertFalse(checker.associatives(user2, user1, friends));

        genderSet(user1, 'f', 'm'); // fails
        genderSet(user2, 'f', 'm');
        Assert.assertFalse(checker.associatives(user1, user2, friends));
        Assert.assertFalse(checker.associatives(user2, user1, friends));

        genderSet(user1, 'm', 'f');
        genderSet(user2, 'f', 'm');
        Assert.assertTrue(checker.associatives(user1, user2, friends));
        Assert.assertTrue(checker.associatives(user2, user1, friends));

    }

    @Test
    public void religionChecks () {
        religionSet(user1, 'h', "hm");
        religionSet(user2, 'h', "h");
        Assert.assertTrue(checker.associatives(user1, user2, friends));
        Assert.assertTrue(checker.associatives(user2, user1, friends));

        religionSet(user1, 'h', "n");
        religionSet(user2, 'h', "n");
        Assert.assertTrue(checker.associatives(user1, user2, friends));
        Assert.assertTrue(checker.associatives(user2, user1, friends));

        religionSet(user1, 'm', "n");
        religionSet(user2, 'j', "nj");
        Logger.info(user2.getPreferences().getReligion().toString());
        Assert.assertTrue(checker.associatives(user1, user2, friends));
        Assert.assertTrue(checker.associatives(user2, user1, friends));

        religionSet(user1, 'j', "j");
        religionSet(user2, 'b', "b");
        Assert.assertFalse(checker.associatives(user1, user2, friends));
        Assert.assertFalse(checker.associatives(user2, user1, friends));

        religionSet(user1, 's', "n");
        religionSet(user2, 'c', "bcj");
        Assert.assertFalse(checker.associatives(user1, user2, friends));
        Assert.assertFalse(checker.associatives(user2, user1, friends));
    }

    @AfterClass
    public static void cleanup () {
        Helpers.stop(app);
    }

    private void ageSet (User user, int userAge, int userPrefMin, int userPrefMax) {
        user.setBirthday(new DateTime().minusYears(userAge).toDate());
        user.getPreferences().setAge(new AgeRange(userPrefMin, userPrefMax));
    }

    private void genderSet (User user, char gender, char orientation) {
        switch (gender) {
            case 'm': user.setGender("male"); break;
            case 'f': user.setGender("female"); break;
        }
        switch (orientation) {  // straight, gay
            case 'm': user.getPreferences().setGender(Gender.MALE); break;
            case 'f': user.getPreferences().setGender(Gender.FEMALE); break;
        }
    }

    private void religionSet (User user, char religion, String pref) {
        switch (religion) {
            case 'n': user.setReligion(Religion.NO_PREFERENCE); break;
            case 'h': user.setReligion(Religion.HINDU); break;
            case 'm': user.setReligion(Religion.MUSLIM); break;
            case 'j': user.setReligion(Religion.JAIN); break;
            case 'c': user.setReligion(Religion.CHRISTIAN); break;
            case 'b': user.setReligion(Religion.BUDDHIST); break;
            case 's': user.setReligion(Religion.SIKH);
            default: break;
        }
        List<Religion> prefs = new ArrayList<>();
        for (int i=0; i<pref.length(); i++) {
            switch(pref.charAt(i)) {
                case 'n': prefs.add(Religion.NO_PREFERENCE); break;
                case 'h': prefs.add(Religion.HINDU); break;
                case 'm': prefs.add(Religion.MUSLIM); break;
                case 'j': prefs.add(Religion.JAIN); break;
                case 'c': prefs.add(Religion.CHRISTIAN); break;
                case 'b': prefs.add(Religion.BUDDHIST); break;
                case 's': prefs.add(Religion.SIKH); break;
                default: break;
            }
        }
        user.getPreferences().setReligion(prefs);
    }

    private void pincodeSet (int user1pin, int user2pin) {
        user1.setPincode(user1pin);
        user2.setPincode(user2pin);
    }

    private void flagSet (String user1flags, String user2flags) {
        user1.getFlags().clear();
        user2.getFlags().clear();
        for (int i=0; i< user1flags.length(); i++) {
            switch(user1flags.charAt(i)) {
                case 'r': user1.addFlag(Flag.READY_TO_CHAI); break;
                case 'd': user1.addFlag(Flag.DEACTIVATED); break;
            }
        }
        for (int i=0; i< user2flags.length(); i++) {
            switch(user2flags.charAt(i)) {
                case 'r': user2.addFlag(Flag.READY_TO_CHAI); break;
                case 'd': user2.addFlag(Flag.DEACTIVATED); break;
            }
        }
    }
}
