package seeds;

import models.QuestionGenerator;
import org.joda.time.DateTime;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.util.*;

/**
 * Created by kedar on 5/23/14.
 */
public class UserSeeder {

    private int maxSeedSize = 5000;
    private HashSet<Integer> userIds = new HashSet<Integer>();
    Random random = new Random();
    FieldGenerator g = new FieldGenerator();
    PreferenceSeeder preferenceSeeder = new PreferenceSeeder();

    ArrayList<UserSeed> documents= new ArrayList<UserSeed> ();

    public void seed (int seedSize) {
        if (seedSize > maxSeedSize)
            throw new IllegalArgumentException("seedSize exceeds maxSeedSize");
        List<String> userIds = generateUserIds(seedSize);
        for (String id: userIds) {
            documents.add(createDocument(id));
        }
        PlayJongo.getCollection("test_users").insert(documents.toArray());
    }

    public List<String> generateUserIds (int amount) {
        List<String> ids = new ArrayList<String>();
        while (ids.size() < amount) {
            String id = String.valueOf(random.nextInt(99999999));
            if (ids.contains(id)) {
                continue;
            } else {
                ids.add(id);
            }
        }
        return ids;
    }

    public UserSeed createDocument (String id) {
        UserSeed seed = new UserSeed();
        seed.userId = id;
        seed.firstName = g.generateString(6);
        seed.lastName = g.generateString(6);
        seed.link = "http://facebook.com";
        seed.birthday = g.generateDate(new DateTime(1985, 1, 1, 0, 0).toDate(), new DateTime(1996, 1, 1, 0, 0).toDate());
        seed.email = "test@letschai.com";
        seed.gender = g.generateGender().lowerCase();
        seed.verified = g.generateBoolean();
        seed.city = "Bangalore";
        seed.pincode = g.generateBangalorePincode();
        seed.genderGiven = g.generateGender();
        seed.height = 120 + random.nextInt(50);
        seed.religion = g.generateReligion();
        seed.education = Arrays.asList(g.generateEducation(), g.generateEducation());
        seed.questions = QuestionGenerator.generate(4);
        seed.occupation = g.generateString(6);

        seed.preferences = preferenceSeeder.createDocument();
        return seed;
    }
}
