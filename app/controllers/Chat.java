package controllers;

import actions.Auth;
import classes.Query;
import clients.LetsChaiChat;
import com.fasterxml.jackson.databind.JsonNode;
import exceptions.ChatException;
import models.Message;
import play.Logger;
import play.Play;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import types.Match;
import views.html.chat;

import java.util.List;
import java.util.Map;

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

        String socketURL = Play.application().configuration().getString("chat.socket.url");
        List<Message> messages = query.messages(userId);
        return ok(chat.render(matches, messages, userId, socketURL));
    }

    public static WebSocket<JsonNode> socket () throws ChatException {
        LetsChaiChat chat = null;
        try {
            chat = new LetsChaiChat(session().get("user"));
            return chat.execute();
        } catch (ChatException e) {
            Logger.error(e.getMessage());
            throw e;
        }
    }

    @Auth.Basic
    public static Result noMatches () {
        return ok(views.html.nomatches.render());
    }

    // backup route for when the chat server is down
    @Auth.Basic
    public static Result save () {
        Map<String, String[]> post = request().body().asFormUrlEncoded();
        String from = post.get("from")[0];
        String to = post.get("to")[0];
        String message = post.get("message")[0];
        Message chat = new Message(from, to, message);
        Query query = new Query();
        query.saveMessage(chat);
        return ok("chat saved");
    }
}
