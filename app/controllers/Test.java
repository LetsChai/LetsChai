package controllers;

import classes.SecretChaiSauce;
import models.*;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import types.Pincode;
import uk.co.panaxiom.playjongo.PlayJongo;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by kedar on 5/23/14.
 */
public class Test extends Controller {

    public static F.Promise<Result> test() {
        return F.Promise.promise(() -> ok());
    }

    public static Result test2 () throws IOException, SmackException, XMPPException {
        ConnectionConfiguration config = new ConnectionConfiguration("localhost", 5222);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        XMPPConnection smack = new XMPPTCPConnection(config);
        smack.connect();
        smack.login("admin", "1n1H23m");
        AccountManager manager = AccountManager.getInstance(smack);
        manager.createAccount("tester", "tester");
        return ok();
    }

    public static F.Promise<Result> algorithm () {
        List<User> users = StreamSupport.stream(PlayJongo.getCollection("users").find().as(User.class).spliterator(), false).collect(Collectors.toList());
        List<Pincode> pincodes = StreamSupport.stream(Pincode.findAll().spliterator(), false).collect(Collectors.toList());
        SecretChaiSauce sauce = new SecretChaiSauce(users, pincodes);
        return sauce.run().map(bool -> ok());
    }
}
