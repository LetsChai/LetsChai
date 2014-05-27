package seeds;

import uk.co.panaxiom.playjongo.PlayJongo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by kedar on 5/23/14.
 */
public class PreferenceSeeder {

    FieldGenerator g = new FieldGenerator();

    public UserPreferenceSeed createDocument () {
        UserPreferenceSeed doc = new UserPreferenceSeed();
        doc.gender = g.generateGender();
        doc.age = g.generateAgeRange();
        doc.religion = Arrays.asList(g.generateReligion(), g.generateReligion());

        return doc;
    }
}
