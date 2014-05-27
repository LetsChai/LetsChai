package controllers;

import models.Location;
import models.SecretChaiSauce;
import play.mvc.Controller;
import play.mvc.Result;
import seeds.PincodeSeed;
import uk.co.panaxiom.playjongo.PlayJongo;

/**
 * Created by kedar on 5/23/14.
 */
public class Test extends Controller {

    public static Result test() {
        PincodeSeed start = PlayJongo.getCollection("pincodes_gmaps").findOne("{'pincode': 560001}").as(PincodeSeed.class);
        PincodeSeed end = PlayJongo.getCollection("pincodes_gmaps").findOne("{'pincode': 560002}").as(PincodeSeed.class);
        double distance = Location.distFrom(start.latitude, start.longitude, end.latitude, end.longitude);
        return ok(String.valueOf(distance));
    }


    public static Result test2 () {
        return ok();
    }

    public static Result algorithm () {
        SecretChaiSauce sauce = new SecretChaiSauce();
        sauce.run();
        return ok();
    }
}
