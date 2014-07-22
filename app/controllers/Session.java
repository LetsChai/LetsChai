package controllers;

import play.Play;
import types.ProfileQuestion;
import classes.QuestionGenerator;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

/**
 * Created by kedar on 5/20/14.
 */
public class Session extends Controller {

    public static Result populate (String name) {
        String id = Play.application().configuration().getString("fb." + name);
        session("user", id);
        return ok("populated user id: " + id);
    }

    public static Result dump () {
        return ok(session().toString());
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
