package controllers;

import actions.Auth;
import classes.Query;
import clients.LetsChaiChat;
import com.fasterxml.jackson.databind.JsonNode;
import exceptions.ChatException;
import models.Message;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import types.Match;
import views.html.chat;

import java.util.List;

/**
 * Created by kedar on 6/9/14.
 */
public class Chat extends Controller {

    @Auth.WithUser
    public static Result chat () {
        String userId = session().get("user");
        Query query = new Query();
        List<Match> matches = query.matches(userId);

        // no matches? redirect
        if (matches.size() == 0)
            return redirect(controllers.routes.Chat.noMatches());

        List<Message> messages = query.messages(userId);
        return ok(chat.render(matches, messages, userId));
    }

    public static WebSocket<JsonNode> socket () throws ChatException {
        LetsChaiChat chat = new LetsChaiChat(session().get("user"));
        return chat.execute();
    }

    @Auth.Basic
    public static Result noMatches () {
        return ok(views.html.nomatches.render());
    }
}
