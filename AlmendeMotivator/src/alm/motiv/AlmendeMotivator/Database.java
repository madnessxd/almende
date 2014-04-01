package alm.motiv.AlmendeMotivator;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.net.UnknownHostException;

/**
 * Created by AsterLaptop on 3/31/14.
 */
public class Database {
    //a singleton for creating an instance of mongo, because we only need one

    public static MongoClientURI uri  = new MongoClientURI("mongodb://almende_admin:admin100@174.129.114.106:37077/almende");

    private static MongoClient instance =null;

    private Database(){}

    public static synchronized MongoClient getInstance() {
        if(instance==null){
            try {
                instance = new MongoClient(uri);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

}
