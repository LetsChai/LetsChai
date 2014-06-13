package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.LetsChaiChat;
import models.OpenFire;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;
import play.Logger;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.chat;

/**
 * Created by kedar on 6/9/14.
 */
public class Chat extends Controller {

    public static Result chat () {
        return ok(chat.render());
    }

    public static WebSocket<JsonNode> socket () {
        LetsChaiChat chat = new LetsChaiChat(session().get("user"));
        return chat.execute();
    }
}
