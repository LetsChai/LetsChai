package controllers;

import actions.Auth;
import classes.ChaiHandler;
import com.fasterxml.jackson.databind.JsonNode;
import clients.LetsChaiChat;
import exceptions.ChatException;
import models.Chai;
import models.Message;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import uk.co.panaxiom.playjongo.PlayJongo;
import views.html.chat;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kedar on 6/9/14.
 */
public class Chat extends Controller {

    @Auth.WithUser
    public static Result chat () {
        String userId = session().get("user");
        List<Chai> matches = ChaiHandler.getMatches(userId);

        // no matches? redirect
        if (matches.size() == 0)
            return redirect(controllers.routes.Chat.noMatches());

        List<Chai.HalfChai> halfChais = matches.stream().map(chai -> chai.getOtherHalf(userId)).collect(Collectors.toList());
        List<Message> messages = Message.find(userId);
        return ok(chat.render(halfChais, messages, userId));
    }

    public static WebSocket<JsonNode> socket () throws ChatException {
        String userId = session().get("user");
        List<Chai> matches = ChaiHandler.getMatches(userId);
        List<Chai.HalfChai> halfChais = matches.stream().map(chai -> chai.getOtherHalf(userId)).collect(Collectors.toList());
        LetsChaiChat chat = new LetsChaiChat(session().get("user"), halfChais);
        return chat.execute();
    }

    @Auth.Basic
    public static Result noMatches () {
        return ok(views.html.nomatches.render());
    }
}
