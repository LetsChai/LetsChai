package models;

import org.apache.commons.lang3.text.WordUtils;
import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by kedar on 5/27/14.
 */
public enum ZodiacSign {
    CAPRICORN, AQUARIUS, PISCES, ARIES, TAURUS, GEMINI, CANCER, LEO, VIRGO, LIBRA, SCORPIO, SAGGITARIUS;

    // credit to http://introcs.cs.princeton.edu/java/13flow/Zodiac.java.html
    public static ZodiacSign fromDate (Date date) {
        DateTime jodaDate = new DateTime(date);
        int M = jodaDate.getMonthOfYear();
        int D = jodaDate.getDayOfMonth();

        if ((M == 12 && D >= 22 && D <= 31) || (M ==  1 && D >= 1 && D <= 19))
            return ZodiacSign.CAPRICORN;
        else if ((M ==  1 && D >= 20 && D <= 31) || (M ==  2 && D >= 1 && D <= 17))
            return ZodiacSign.AQUARIUS;
        else if ((M ==  2 && D >= 18 && D <= 29) || (M ==  3 && D >= 1 && D <= 19))
            return ZodiacSign.PISCES;
        else if ((M ==  3 && D >= 20 && D <= 31) || (M ==  4 && D >= 1 && D <= 19))
            return ZodiacSign.ARIES;
        else if ((M ==  4 && D >= 20 && D <= 30) || (M ==  5 && D >= 1 && D <= 20))
            return ZodiacSign.TAURUS;
        else if ((M ==  5 && D >= 21 && D <= 31) || (M ==  6 && D >= 1 && D <= 20))
            return ZodiacSign.GEMINI;
        else if ((M ==  6 && D >= 21 && D <= 30) || (M ==  7 && D >= 1 && D <= 22))
            return ZodiacSign.CANCER;
        else if ((M ==  7 && D >= 23 && D <= 31) || (M ==  8 && D >= 1 && D <= 22))
            return ZodiacSign.LEO;
        else if ((M ==  8 && D >= 23 && D <= 31) || (M ==  9 && D >= 1 && D <= 22))
            return ZodiacSign.VIRGO;
        else if ((M ==  9 && D >= 23 && D <= 30) || (M == 10 && D >= 1 && D <= 22))
            return ZodiacSign.LIBRA;
        else if ((M == 10 && D >= 23 && D <= 31) || (M == 11 && D >= 1 && D <= 21))
            return ZodiacSign.SCORPIO;
        else
            return ZodiacSign.SAGGITARIUS;
    }

    public String lowerCase () {
        return toString().toLowerCase();
    }

    public String capitalize ()  {
        return WordUtils.capitalize(lowerCase());
    }
}
