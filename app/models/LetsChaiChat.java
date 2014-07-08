package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.mvc.WebSocket;
import exceptions.ChatException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kedar on 6/10/14.
 */
public class LetsChaiChat {

    private XMPPConnection smack;
    private WebSocket<JsonNode> socket;
    private String userId;
    private final String DOMAIN = "kedar-dell-system-inspiron-n4110";
    private WebSocket.Out<JsonNode> socketOut;
    private WebSocket.In<JsonNode> socketIn;

    private Map<String, Chat> chats = new HashMap<>();

    public LetsChaiChat () {
        this("admin", "1n1H23m");
    }

    public LetsChaiChat (String userId) {
        this(userId, userId);
    }

    public LetsChaiChat(String username, String password) {
        smack = new XMPPConnection("localhost");
        try {
            smack.connect();
        } catch (XMPPException e) {
            Logger.error("Error connecting to Openfire Server");
            e.printStackTrace();
        }
        this.userId = username;
        try {
            smack.login(username, password);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    public WebSocket<JsonNode> execute () {
        // create the websocket for the chat
        socket = new WebSocket<JsonNode>() {
            @Override
            public void onReady(In<JsonNode> jsonIn, Out<JsonNode> jsonOut) {
                socketOut = jsonOut;
                socketIn = jsonIn;
                jsonIn.onMessage(onMessageCallback());
            }
        };

        // listen for new chats
        smack.getChatManager().addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean locallyCreated) {
                if (!locallyCreated) {
                    chat.addMessageListener(messageListener());
                }
            }
        });

        return socket;
    }

    private void createAccount (String userId) {
        try {
            smack.getAccountManager().createAccount(userId, userId);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    public void closeSocket () {
        socketOut.close();
    }

    private void sendChat (String username, String message) {
        if (chats.containsKey(username)) {
            try {
                chats.get(username).sendMessage(message);
            } catch (XMPPException e) {
                e.printStackTrace();
            }
        } else { // new chat
            Chat chat = smack.getChatManager().createChat(username, messageListener());
            chats.put(username, chat);
            try {
                chat.sendMessage(message);
            } catch (XMPPException e) {
                e.printStackTrace();
            }
        }
    }

    private MessageListener messageListener () {
        return new MessageListener() {
            @Override
            public void processMessage(Chat chat, Message message) {
                ObjectNode json = Json.newObject();
                String from = message.getFrom();
                json.put("from", from.split("@")[0]);
                json.put("message", message.getBody());
                Logger.info(String.format("from %s, %s", from, message.getBody()));
                socketOut.write(json);
            }
        };
    }

    private F.Callback<JsonNode> onMessageCallback () {
        return new F.Callback<JsonNode>() {
            @Override
            public void invoke(JsonNode json) throws Throwable {
                String to = json.get("to").asText() + "@" + DOMAIN;
                String message = json.get("message").asText();
                Logger.info(String.format("to %s, %s", to, message));
                sendChat(to, message);
            }
        };
    }

}
