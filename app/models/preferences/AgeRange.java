package models.preferences;

/**
 * Created by kedar on 5/22/14.
 */
public class AgeRange {

    private int minimum;
    private int maximum;

    public AgeRange () {}   // for Jackson

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
}
