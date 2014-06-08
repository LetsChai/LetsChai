package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restfb.Parameter;
import exceptions.InvalidPincodeException;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kedar on 5/23/14.
 */
public class Test extends Controller {

    public static Result test() {
        User user = User.getCollection().findOne().as(User.class);
        return ok(user.getFriends().getCount().toString());
    }

    public static Result test2 () {
        User user = User.getCollection().findOne().as(User.class);
        return ok(String.format("%d %d", user.getBirthday().getTime(), new Date().getTime()));
    }

    public static Result algorithm () {
        SecretChaiSauce sauce = new SecretChaiSauce();
        sauce.run();
        return ok();
    }
}
