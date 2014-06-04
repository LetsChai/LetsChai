package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restfb.Parameter;
import models.LetsChaiFacebookClient;
import models.Location;
import models.SecretChaiSauce;
import models.User;
import org.apache.commons.httpclient.HttpClient;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.methods.HttpGet;
import org.jongo.MongoCollection;
import play.mvc.Controller;
import play.mvc.Result;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kedar on 5/23/14.
 */
public class Test extends Controller {

    public static Result test() {
        MongoCollection userColl = PlayJongo.getCollection("production_users");
        User user = userColl.findOne("{'userId':'#'}", "10154096696385538").as(User.class);
        User partner = userColl.findOne("{'userId': '#'}", "10152481121747442").as(User.class);
        SecretChaiSauce sauce = new SecretChaiSauce();
        Double rating = sauce.chaiScore(user, partner);
        return ok(rating.toString());
    }


    public static Result test2 () {
        User user = User.getCollection().findOne().as(User.class);
        return ok(String.format("https://graph.facebook.com/v2.0/%s/friends/%s?access_token=%s", user.getUserId(), "10152471075807154", user.getAccessToken().getAccessToken()));
    }

    public static Result algorithm () {
        SecretChaiSauce sauce = new SecretChaiSauce();
        sauce.run();
        return ok();
    }
}
