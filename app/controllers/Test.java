package controllers;

import actions.Auth;
import actions.AuthAction;
import models.*;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import play.Logger;
import play.Play;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import play.mvc.With;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by kedar on 5/23/14.
 */
public class Test extends Controller {

    public static F.Promise<Result> test() {
//        String arpitaId = Play.application().configuration().getString("fb.arpita");
//        String siddharthaId = Play.application().configuration().getString("fb.siddhartha");
        String id1 = "10152399160156251";
        String id2 = Play.application().configuration().getString("fb.siddhartha");
        User user1 = PlayJongo.getCollection("production_users").findOne(String.format("{'userId': '%s'}", id1)).as(User.class);
        return user1.isFriendsWith(id2).map(friends -> ok(friends.toString()));
    }

    public static Result test2 () {
        Iterable<User> users = User.findMultiple(Arrays.asList(""));

        return ok();
    }

    public static F.Promise<Result> algorithm () {
        List<User> users = StreamSupport.stream(PlayJongo.getCollection("production_users").find().as(User.class).spliterator(), false).collect(Collectors.toList());
        List<Pincode> pincodes = StreamSupport.stream(Pincode.findAll().spliterator(), false).collect(Collectors.toList());
        SecretChaiSauce sauce = new SecretChaiSauce(users, pincodes);
        return sauce.run().map(bool -> ok());
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
