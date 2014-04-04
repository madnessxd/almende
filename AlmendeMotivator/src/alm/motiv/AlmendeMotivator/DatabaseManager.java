package alm.motiv.AlmendeMotivator;

/**
 * Created by AsterLaptop on 3/23/14.
 */
import android.os.AsyncTask;
import com.mongodb.*;
import alm.motiv.AlmendeMotivator.models.Challenge;

public class DatabaseManager {

    public DatabaseManager(){
        new DatabaseManagerThread().execute();
    }

    /**
     * Background Async Task to Create new product
     * */
    class DatabaseManagerThread extends AsyncTask<String, String, String> {
        /**
         * Creating product
         * */
        protected String doInBackground(String... args) {
            // To connect to mongodb server

            MongoClient client = Database.getInstance();
            DB db = client.getDB(Database.uri.getDatabase());
            DBCollection challengeCollection = db.getCollection("challenge");
            challengeCollection.setObjectClass(Challenge.class);

            //TODO create challenge with parameters
            Challenge challenge = new Challenge();
            challengeCollection.insert(challenge, WriteConcern.ACKNOWLEDGED);
            return null;
        }
    }
}