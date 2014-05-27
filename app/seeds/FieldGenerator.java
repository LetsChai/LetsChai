package seeds;

import models.Education;
import models.AgeRange;
import models.Gender;
import models.Religion;

import java.util.Date;
import java.util.Random;

/**
 * Created by kedar on 5/23/14.
 */
public class FieldGenerator {
    Random random = new Random();

    public boolean generateBoolean () {
        return random.nextInt(2) == 0;
    }

    public int generatePositiveInt (int length) {
        if (length < 1)
            throw new IllegalArgumentException("argument must be 1 or greater");
        return (int) Math.round(Math.random() * Math.pow(10, length - 1));
    }

    public String generateString (int length) {
        String res = "";
        while (length > 0) {
            res += (char)(random.nextInt(26) + 'a');
            length --;
        }
        return res;
    }

    public Religion generateReligion () {

        int pick = random.nextInt(Religion.values().length);
        return Religion.values()[pick];
    }

    public Date generateDate (Date startDate, Date endDate) {
        long timeBetween = (endDate.getTime() - startDate.getTime());
        long randomTime = startDate.getTime() + (long) (Math.random() * timeBetween);
        return new Date(randomTime);
    }

    public Gender generateGender () {
        return Gender.values()[random.nextInt(2)];
    }

    public int generateBangalorePincode () {
        return 560000 + random.nextInt(1000);
    }

    public Education generateEducation () {
        return new Education(generateString(7), generateEducationType());
    }

    public Education.EducationType generateEducationType () {
        int pick = random.nextInt(Education.EducationType.values().length);
        return Education.EducationType.values()[pick];
    }

    public AgeRange generateAgeRange () {
        int min = 18 + random.nextInt(9);
        int max = min + random.nextInt(31 - min);
        return new AgeRange(min, max);
    }


}
