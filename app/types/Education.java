package types;

import com.restfb.types.User;
import org.apache.commons.lang3.text.WordUtils;

/**
 * Created by kedar on 5/21/14.
 */
public class Education {

    private String school;
    private EducationType type;

    public enum EducationType {
        BACHELORS, MASTERS, PHD, HIGH_SCHOOL;

        public String lowerCase () {
            return this.toString().replace('_', ' ').toLowerCase();
        }

        public String capitalized () {
            return WordUtils.capitalizeFully(lowerCase());
        }
    }

    public Education () {} // for Jongo

    public Education (String school, EducationType type) {
        this.school = school;
        this.type = type;
    }

    public String getSchool() {
        return school;
    }

    public EducationType getType() {
        return type;
    }
}
