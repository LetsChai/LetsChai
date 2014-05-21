package controllers;

import models.ProfileQuestion;
import models.QuestionGenerator;
import models.mongo.UserProfile;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by kedar on 5/20/14.
 */
public class Session extends Controller {

    public static Result populate () {
        String id = "1219293476";
        session("user", id);
        return ok("populated user id: " + id);
    }

    public static Result dump () {
        return ok(session().toString());
    }

    public static Result profile () {
        String user = session("user");
        UserProfile usere = UserProfile.findOne(user);
        if (usere == null)
            return ok("empty");
        return ok(usere.toString());
    }

    public static Result questions () {
        ProfileQuestion[] questions = QuestionGenerator.generate(4);
        String result = "";
        for (ProfileQuestion q: questions)
            result = result + q.toString();
        return ok(result);
    }

    public static Result clear () {
        session().clear();
        return ok("session cleared");
    }
}
