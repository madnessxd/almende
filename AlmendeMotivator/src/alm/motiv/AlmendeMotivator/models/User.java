package alm.motiv.AlmendeMotivator.models;

import com.mongodb.BasicDBObject;

import java.util.Date;

/**
 * Created by AsterLaptop on 4/1/14.
 */
public class User extends BasicDBObject {
   // private String facebookID;

    public User(){}

    public User(String facebookID, String name){
        put("facebookID",facebookID);
        put("name",name);
    }

}
