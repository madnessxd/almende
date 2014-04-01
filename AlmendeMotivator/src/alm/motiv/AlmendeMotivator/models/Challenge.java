package alm.motiv.AlmendeMotivator.models;

import com.mongodb.BasicDBObject;

/**
 * Created by AsterLaptop on 3/31/14.
 */
public class Challenge extends BasicDBObject {
    private static final long serialVersionUID = 1L;
    public String title;
    public String content;

    public Challenge(){

    }

    public Challenge(String title, String content){
        put("title", title);
        put("content", content);
    }
}
