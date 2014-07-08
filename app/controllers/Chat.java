package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Chai;
import models.LetsChaiChat;
import models.OpenFire;
import models.User;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;
import play.Logger;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.chat;

import java.util.List;

/**
 * Created by kedar on 6/9/14.
 */
public class Chat extends Controller {

    public static Result chat () {
        User user = User.findOne(session().get("user"));
        List<User> matches = user.getMatchedUsers();
        return ok(chat.render(matches));
    }

    public static WebSocket<JsonNode> socket () {
        LetsChaiChat chat = new LetsChaiChat(session().get("user"));
        return chat.execute();
    }
}
