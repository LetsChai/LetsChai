package types;

import org.apache.commons.lang3.text.WordUtils;

import java.util.List;

/**
 * Created by kedar on 5/14/14.
 */
public enum Religion {
    HINDU, MUSLIM, JAIN, CHRISTIAN, BUDDHIST, SIKH, NO_PREFERENCE;

    public String lowerCase () {
        return this.toString().replace('_', ' ').toLowerCase();
    }

    public String capitalize () {
        return WordUtils.capitalize(lowerCase());
    }
    // because Scala can't access the class attribute
    public static Religion[] getValues () {
        return Religion.class.getEnumConstants();
    }

    public boolean contains (Religion candidate) {
        return this.equals(candidate) || this.equals(Religion.NO_PREFERENCE);
    }

    public static boolean contains (List<Religion> list, Religion candidate) {
        for (Religion r: list) {
            if (r.contains(candidate))
                return true;
        }
        return false;
    }
}
