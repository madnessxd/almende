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
        setName(name);
    }

    public void setAbout(String about){
        put("about", about);
    }

    public void setAge(String age){
        put("age", age);
    }

    public void setCity(String city){
        put("city", city);
    }

    //TODO make arraylist, subarray inside user
    public void setSports(String sports){
        put("sports", sports);
    }

    public void setName(String name){
        put("name", name);
    }

    public void setGoal(String goal){
        put("goal",goal);
    }

}
