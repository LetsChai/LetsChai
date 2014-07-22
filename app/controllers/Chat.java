package controllers;

import actions.Auth;
import com.fasterxml.jackson.databind.JsonNode;
import clients.LetsChaiChat;
import exceptions.ChatException;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.chat;

import java.util.List;

/**
 * Created by kedar on 6/9/14.
 */
public class Chat extends Controller {

    @Auth.WithUser
    public static Result chat () {
        User user = (User) ctx().args.get("user");
        List<User> matches = user.getMatchedUsers();
        return ok(chat.render(matches));
    }

    public static WebSocket<JsonNode> socket () throws ChatException {
        LetsChaiChat chat = new LetsChaiChat(session().get("user"));
        return chat.execute();
    }
}
