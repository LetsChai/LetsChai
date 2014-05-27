package models;

import org.apache.commons.lang3.text.WordUtils;

/**
 * Created by kedar on 5/14/14.
 */
public enum Gender {
    MALE, FEMALE;

    public String lowerCase () {
        return this.toString().toLowerCase();
    }

    public String capitalize () {
        return WordUtils.capitalize(lowerCase());
    }
}
