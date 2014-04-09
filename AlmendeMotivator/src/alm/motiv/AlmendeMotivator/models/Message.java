package alm.motiv.AlmendeMotivator.models;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.ArrayList;

/**
 * Created by AsterLaptop on 4/1/14.
 */
public class Message extends BasicDBObject {

    //private ArrayList<String> receivedMessages = new ArrayList<String>();

    public Message(){

    }

    public Message(String receiver, String author, String title, ArrayList<String> receivedMessages, String date, String category, String liked){
        put("Receiver", receiver);
        put("Author", author);
        put("Title", title);
        put("Content", receivedMessages);
        put("Date", date);
        put("Category", category);
        put("Liked", liked);
    }
}
