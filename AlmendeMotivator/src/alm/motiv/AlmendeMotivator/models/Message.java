package alm.motiv.AlmendeMotivator.models;

import com.mongodb.BasicDBObject;

/**
 * Created by AsterLaptop on 4/1/14.
 */
public class Message extends BasicDBObject {

    public Message(){

    }

    public Message(String receiver, String author, String title, String content, String date, String category, String liked){
        put("Receiver", receiver);
        put("Author", author);
        put("Title", title);
        put("Content", content);
        put("Date", date);
        put("Category", category);
        put("Liked", liked);
    }
}
