package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.facebook.FacebookMainActivity;
import alm.motiv.AlmendeMotivator.models.Challenge;
import alm.motiv.AlmendeMotivator.models.User;
import android.app.*;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gebruiker on 21-5-14.
 */

public class MongoPull extends IntentService {

    //Notification
    private long lastLogin;
    private ArrayList<String> updateList = new ArrayList<String>();
    private Boolean updateNotification = false;
    private CheckUpdates CU;

    public MongoPull() {
        super("MongoPull");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("ja");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private class CheckUpdates extends AsyncTask<String, String, String> {
        public List<DBObject> sendChallenges = null;
        public List<DBObject> receivedChallenges = null;

        ProgressDialog simpleWaitDialog = null;

        @Override
        protected String doInBackground(String... args) {
            if(Cookie.getInstance().internet){
                try{
                    MongoClient client = Database.getInstance();
                    DB db = client.getDB(Database.uri.getDatabase());
                    DBCollection challengeCollection = db.getCollection("challenge");
                    challengeCollection.setObjectClass(Challenge.class);
                    DBCollection userCollection = db.getCollection("user");
                    userCollection.setObjectClass(User.class);

                    //find al the challenges the user send
                    Challenge query1 = new Challenge();
                    query1.put("challenger", Cookie.getInstance().userEntryId);
                    sendChallenges = challengeCollection.find(query1).toArray();

                    //find al the challenges the user received
                    Challenge query2 = new Challenge();
                    query2.put("challengee", Cookie.getInstance().userEntryId);
                    receivedChallenges = challengeCollection.find(query2).toArray();

                    if(sendChallenges.size() != 0){
                        for(int i = 0 ; i < sendChallenges.size() ; i++){
                            updateList.add(sendChallenges.get(i).get("Date").toString());
                        }
                    }
                    if(receivedChallenges.size() != 0) {
                        for(int i = 0 ; i < receivedChallenges.size() ; i++){
                            updateList.add(receivedChallenges.get(i).get("Date").toString());
                        }
                    }

                    //updateList

                    //Update user last login time/date
                    User current = new User();
                    current.put("facebookID", Cookie.getInstance().userEntryId);
                    User updateUser = (User) userCollection.find(current).toArray().get(0);

                    lastLogin = updateUser.getLoginDate();

                    updateUser.updateLoginDate();
                    userCollection.findAndModify(current, updateUser);

                }catch(Exception e){
                    System.out.println(e);
                }
            }
            return null;
        }
    }

    public void showNotification(){
        try{
            Intent myIntent = new Intent(this, FacebookMainActivity.class);
            PendingIntent myPendingIntent = PendingIntent.getActivity(this, 0, myIntent, 0);
            //TODO: if statement updateList vullen
            //NOTIFICATION
            for(int i = 0 ; i < updateList.size() ; i++){
                if(lastLogin < Long.parseLong(updateList.get(i))){
                    updateNotification = true;
                }
            }
            if(updateNotification == true){
                String message = "One of your Challenges has been updated";


                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.sportoptia)
                                .setContentTitle("Sportopia")
                                .setContentIntent(myPendingIntent)
                                .setAutoCancel(true)
                                .setContentText(message);
                int mNotificationId = 001;
                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mNotifyMgr.notify(mNotificationId, mBuilder.build());
                updateNotification = false;
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        while(true){
            CU = new CheckUpdates();
            try {
                System.out.println("test");
                Thread.sleep(10000);
                if(CU.getStatus().toString().equals("FINISHED")){
                    CU = new CheckUpdates();
                }
                if(CU.getStatus().toString().equals("PENDING")){
                    showNotification();
                    CU.execute();
                }
            } catch (Exception e){}
        }
    }
}
