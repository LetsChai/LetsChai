package controllers;

import models.ProfileQuestion;
import models.QuestionGenerator;
import models.UserProfile;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

/**
 * Created by kedar on 5/20/14.
 */
public class Session extends Controller {

    public static Result populate (String id) {
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
        List<ProfileQuestion> questions = QuestionGenerator.generate(4);
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
