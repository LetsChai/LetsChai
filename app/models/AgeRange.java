package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by kedar on 5/22/14.
 */
public class AgeRange {

    private int minimum;
    private int maximum;

    @JsonIgnore
    private final int MAXIMUM_AGE = 30;

    @JsonIgnore
    private final int MINIMUM_AGE = 18;

    public AgeRange () {
        minimum = MINIMUM_AGE;
        maximum = MAXIMUM_AGE;
    }

    public AgeRange(int minimum, int maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }
    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    public boolean contains (int age) {
        return age >= minimum && age <= maximum;
    }
}
