package classes;

import org.apache.commons.lang3.Validate;
import types.Flag;
import models.Pincode;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kedar on 7/25/14.
 */
public class PincodeHandler {

    private static PincodeHandler INSTANCE;

    private Map<Integer, Pincode> pincodes;
    private static final String COLLECTION = "pincodes_gmaps";
    private final Integer ALTERNATE_PINCODE = 560001;

    // Singleton
    public static PincodeHandler getInstance () {
        if (INSTANCE != null)
            return INSTANCE;

        Iterable<Pincode> docs = PlayJongo.getCollection(COLLECTION).find().as(Pincode.class);
        Map<Integer, Pincode> fromMongo = new HashMap<>();
        for (Pincode pincode: docs) {
            fromMongo.put(pincode.getPincode(), pincode);
        }

        INSTANCE = new PincodeHandler(fromMongo);
        return INSTANCE;
    }

    public PincodeHandler (Map<Integer, Pincode> pincodes) {
        this.pincodes = pincodes;
    }

    public Boolean inBangalore (Integer pin) {
        return pin > 560000 && pin < 561000;
    }

    public Double distance (Integer pin1, Integer pin2) {
        return distance(pin1, pin2, ALTERNATE_PINCODE);
    }

    // distance between two pincodes
    // uses alternate pin for any invalid pins
    public Double distance (Integer pin1, Integer pin2, Integer alternatePin) {
        Validate.isTrue(valid(alternatePin));
        if (!valid(pin1))
            pin1 = alternatePin;
        if (!valid(pin2))
            pin2 = alternatePin;

        return latLongDistanceKm(
                pincodes.get(pin1).getLatitude(),
                pincodes.get(pin1).getLongitude(),
                pincodes.get(pin2).getLatitude(),
                pincodes.get(pin2).getLongitude()
        );
    }


    private double latLongDistanceKm (double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6378.1;
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        return dist;
    }

    public Boolean valid (Integer pin) {
        return pincodes.get(pin) != null;
    }

    public List<Flag> flags (Integer pin) {
        List<Flag> flags = new ArrayList<>();
        if (!inBangalore(pin))
            flags.add(Flag.NOT_IN_BANGALORE);
        if (!valid(pin))
            flags.add(Flag.INVALID_PINCODE);
        return flags;
    }

//    private void geocode (Integer pin) throws IOException {
//        Geocoder geo = new Geocoder();
//        GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(String.format("%d, India", pin)).setLanguage("en").getGeocoderRequest();
//        GeocodeResponse geocoderResponse = geo.geocode(geocoderRequest);
//        for (GeocoderResult g: geocoderResponse.getResults()) {
//            float latitude = g.getGeometry().getLocation().getLat().floatValue();
//            float longitude = g.getGeometry().getLocation().getLng().floatValue();
//        }
//    }

}
