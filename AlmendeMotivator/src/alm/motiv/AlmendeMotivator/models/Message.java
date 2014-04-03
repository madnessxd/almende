package alm.motiv.AlmendeMotivator.models;

import com.mongodb.BasicDBObject;

/**
 * Created by AsterLaptop on 4/1/14.
 */
public class Message extends BasicDBObject {

    public Message(){}
    public Message(String title, String content, String date, String author, String receiver, String category,
                   String liked){
    }
}
