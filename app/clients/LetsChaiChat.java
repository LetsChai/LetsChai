package clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import leodagdag.play2morphia.MorphiaPlugin;
import models.*;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jongo.MongoCollection;
import org.mongodb.morphia.Morphia;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.mvc.WebSocket;
import exceptions.ChatException;
import uk.co.panaxiom.playjongo.PlayJongo;
import views.html.chat;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kedar on 6/10/14.
 */
public class LetsChaiChat {

    private final String ADMIN_USERNAME = "admin";
    private final String ADMIN_PASSWORD = "1n1H23m";
    private final String DOMAIN = "letschai-localhost/Smack";
    private final String SERVER_ADDRESS = "localhost";
    private final Integer SERVER_PORT = 5222;

    private XMPPConnection smack;
    private ChatManager chatManager;
    private WebSocket<JsonNode> socket;
    private String userId;
    private WebSocket.Out<JsonNode> socketOut;
    private WebSocket.In<JsonNode> socketIn;

    private Map<String, Chat> chats = new HashMap<>();

    public static MongoCollection getCollection () {
        return PlayJongo.getCollection("chats");
    }

    public LetsChaiChat(String userId, List<Chai.HalfChai> matches) throws ChatException {
        connect();
        try { loginUser(userId); }
        catch (ChatException e) { // if the login fails, the user probably doesn't exist, try creating it
            disconnect();
            connect();
            loginAdmin();
            createAccount(userId);
            disconnect();
            connect();
            loginUser(userId);
        }

        // chat listener with message listener attached
        chatManager = ChatManager.getInstanceFor(smack);
        chatManager.addChatListener((chat, createdLocally) -> {
            if (!createdLocally) {
                chat.addMessageListener(messageListener());
                Logger.info("Chat started by " + chat.getParticipant());
                chats.put(stripDomain(chat.getParticipant()), chat);
            }
        });

        // create chats with all matches, incoming chats will overwrite these if need be
        for (Chai.HalfChai match: matches) {
            try {
                chats.put(match.getUserId(), chatManager.createChat(userWithDomain(match.getUserId()), messageListener()));
            } catch (Exception e) {
                Logger.error("Could not create chat with " + match.getUserId());
            }
        }
    }

    public void connect () throws ChatException {
        ConnectionConfiguration config = new ConnectionConfiguration(SERVER_ADDRESS, SERVER_PORT);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        smack = new XMPPTCPConnection(config);
        try {
            smack.connect();
        } catch (Exception e) {
            throw new ChatException("Connection failure, check to see if chat server is running", e);
        }
    }

    public void disconnect () throws ChatException {
        try {
            smack.disconnect();
        } catch (Exception e) {
            throw new ChatException(e.getMessage(), e);
        }
    }

    private void loginAdmin () throws ChatException {
        try {
            smack.login(ADMIN_USERNAME, ADMIN_PASSWORD);
        } catch (Exception e) {
            throw new ChatException("failed to login admin", e);
        }
    }

    private void loginUser (String userId) throws ChatException {
        try {
            smack.login(userId, userId);
        } catch (Exception e) {
            throw new ChatException("failed to login user " + userId, e);
        }
    }

    public WebSocket<JsonNode> execute () {
        socket = new WebSocket<JsonNode>() {
            @Override
            public void onReady(In<JsonNode> jsonIn, Out<JsonNode> jsonOut) {
                socketOut = jsonOut;
                socketIn = jsonIn;
                jsonIn.onMessage(onMessageCallback());
            }
        };
        return socket;
    }

    private void createAccount (String userId) throws ChatException {
        AccountManager accounts = AccountManager.getInstance(smack);
        try {
            accounts.createAccount(userId, userId);
        } catch (Exception e) {
            throw new ChatException("failed to create account for user " + userId, e);
        }
    }

    public void closeSocket () {
        socketOut.close();
    }

    private void sendChat (String from, String to, String message) throws ChatException {
        try {
            chats.get(to).sendMessage(message);
            new models.Message(from, to, message).save();
            Logger.info("Message sent");
        } catch (Exception e) {
            throw new ChatException("Error sending chat", e);
        }
    }

    private MessageListener messageListener () {
        return new MessageListener() {
            @Override
            public void processMessage(Chat chat, Message message) {
                ObjectNode json = Json.newObject();
                json.put("from", stripDomain(message.getFrom()));
                json.put("message", message.getBody());
                json.put("to", message.getTo());
                Logger.info("message received from " + message.getFrom());
                Logger.info(json.toString());
                socketOut.write(json);
            }
        };
    }

    // websocket callback for incoming JSON
    private F.Callback<JsonNode> onMessageCallback () {
        return new F.Callback<JsonNode>() {
            @Override
            public void invoke(JsonNode json) throws Throwable {
                String to = json.get("to").asText();
                String from = json.get("from").asText();
                String message = json.get("message").asText();
                Logger.info("sending message...");
                Logger.info(json.toString());
                try {
                    sendChat(from, to, message);
                } catch (Exception e) {
                    // inform sender of error here
                    e.printStackTrace();
                }
            }
        };
    }

    private String userWithDomain (String userId) {
        return userId.indexOf("@") > 0 ?
                userId :
                userId + "@" + DOMAIN;
    }

    private String stripDomain (String participant) {
        return participant.split("@")[0];
    }

}
