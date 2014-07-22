package classes;

/**
 * Created by kedar on 5/26/14.
 */
public class Location {

    private void geocodeAndSave () {
//        Iterable<Pincode> pincodes = PlayJongo.getCollection("pincodes").find("{'city': 'Bangalore'}").as(Pincode.class);
//        Geocoder geo = new Geocoder();
//
//        for (Pincode pincode: pincodes) {
//            GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(String.format("%d, India", pincode.pincode)).setLanguage("en").getGeocoderRequest();
//            GeocodeResponse geocoderResponse = geo.geocode(geocoderRequest);
//            for (GeocoderResult g: geocoderResponse.getResults()) {
//                pincode.latitude = g.getGeometry().getLocation().getLat().floatValue();
//                pincode.longitude = g.getGeometry().getLocation().getLng().floatValue();
//                PlayJongo.getCollection("pincodes_gmaps").save(pincode);
//            }
//        }
    }

    public static double calculateLatLngDistance (double lat1, double lng1, double lat2, double lng2) {
        return 0;
    }

    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
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

}
