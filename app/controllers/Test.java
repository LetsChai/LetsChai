package controllers;

import models.Friends;
import models.OpenFire;
import models.SecretChaiSauce;
import models.User;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import play.Logger;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.util.Date;

/**
 * Created by kedar on 5/23/14.
 */
public class Test extends Controller {

    public static Result test() {
        Friends friends = PlayJongo.getCollection("production_users").findOne("{'userId': '10152202364431336'}").as(User.class).getMutualFriends("10152133015017596");
        return ok(friends.toString());
    }

    public static Result test2 () {

        return ok();
    }

    public static Result algorithm () {
        SecretChaiSauce sauce = new SecretChaiSauce();
        sauce.run();
        return ok();
    }

    public static WebSocket<String> webSocket () {
        return new WebSocket<String>() {
            @Override
            public void onReady(In<String> stringIn, Out<String> stringOut) {
                XMPPConnection smack = OpenFire.connection();
                try {
                    smack.login("varun", "sivakumar");
                } catch (XMPPException e) {
                    e.printStackTrace();
                }

                // chat listener
                smack.getChatManager().addChatListener(new ChatManagerListener() {
                    @Override
                    public void chatCreated(Chat chat, boolean b) {
                        Logger.info("hello from the websocket chat listener");
                        chat.addMessageListener(new MessageListener() {
                            @Override
                            public void processMessage(Chat chat, Message message) {
                                Logger.info(message.getBody() + ": from externally created chat");
                            }
                        });
                    }
                });

                // track websocket input
                stringIn.onMessage(new F.Callback<String>() {
                    @Override
                    public void invoke(String s) throws Throwable {
                        Logger.info(s + ": varun in");
                    }
                });

                Logger.info("socket initialized");
                stringOut.write("connection started by server");
            }
        };
    }
}
