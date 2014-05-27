package models;

/**
 * Created by kedar on 5/26/14.
 */
public class Pincode {
    private String country;
    private int pincode;
    private String landmark;
    private String state;
    private String state_abbr;
    private String city;
    private double latitude;
    private double longitude;

    public String getCountry() {
        return country;
    }

    public int getPincode() {
        return pincode;
    }

    public String getLandmark() {
        return landmark;
    }

    public String getState() {
        return state;
    }

    public String getState_abbr() {
        return state_abbr;
    }

    public String getCity() {
        return city;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double distanceFrom (Pincode otherPincode) {
        return Location.calculateLatLngDistance(getLatitude(), getLongitude(), otherPincode.getLatitude(), otherPincode.getLongitude());
    }
}
