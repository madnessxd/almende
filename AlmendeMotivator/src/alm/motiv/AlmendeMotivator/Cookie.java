package alm.motiv.AlmendeMotivator;

import com.facebook.model.GraphUser;

import java.util.ArrayList;

/**
 * Created by AsterLaptop on 4/2/14.
 */
public class Cookie {

    //a singleton for creating an instance of mongo, because we only need one
    public String userEntryId;
    public ArrayList<GraphUser> facebookFriends=null;
    public String userName;

    private static Cookie instance =null;

    private Cookie(){}

    public static synchronized Cookie getInstance() {
        if(instance==null){
            instance=new Cookie();
        }
        return instance;
    }

}
