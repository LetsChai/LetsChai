package models.preferences;

import org.apache.commons.lang3.text.WordUtils;

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
}
