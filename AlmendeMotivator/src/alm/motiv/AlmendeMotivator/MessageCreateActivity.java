package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.models.User;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import java.util.ArrayList;

/**
 * Created by Gebruiker on 22-4-14.
 */
public class MessageCreateActivity extends Activity {
    private Spinner spinnerFriends;

    private String challengee;

    private String[] facebookFriends = {"loading..."};
    private String[] facebookFriendsName = {"loading..."};


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messagecreate);

        spinnerFriends = (Spinner) findViewById(R.id.spinner_getFriends);
        //GET FRIENDS
        spinnerFriends.setOnTouchListener(Spinner_OnTouch);

        spinnerFriends.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                challengee = facebookFriends[spinnerFriends.getSelectedItemPosition()];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        DatabaseThread2 dbT = new DatabaseThread2();
        dbT.execute();
    }

    public void updateFriends() {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, facebookFriendsName);
        spinnerFriends.setAdapter(spinnerArrayAdapter);
    }

    private View.OnTouchListener Spinner_OnTouch = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                updateFriends();
            }
            return false;
        }
    };

    class DatabaseThread2 extends AsyncTask<String, String, String> {
        protected String doInBackground(String... args) {
            MongoClient client = Database.getInstance();
            DB db = client.getDB(Database.uri.getDatabase());
            DBCollection userCollection = db.getCollection("user");
            userCollection.setObjectClass(User.class);

            Session session = Session.getActiveSession();

            Request request = new Request(session, "me", null, HttpMethod.GET);
            Response response = request.executeAndWait();

            User curUser = new User();
            curUser.put("facebookID", Cookie.getInstance().userEntryId);
            User newUser = (User) userCollection.find(curUser).toArray().get(0);


            ArrayList<String> arrayMessages = (ArrayList<String>) newUser.get("friends");

            String[] facebookFriendsTemp = new String[arrayMessages.toArray().length];
            String[] facebookFriendsNameTemp = new String[arrayMessages.toArray().length];


            for (int i = 0; i < arrayMessages.toArray().length; i++) {
                facebookFriendsTemp[i] = arrayMessages.toArray()[i].toString().replace("{ "  + '"' + "facebookID" + '"' + " : " + '"',"").replace('"' + "}","");

                request = new Request(session, facebookFriendsTemp[i], null, HttpMethod.GET);
                response = request.executeAndWait();

                if (response.getError() != null) {
                    System.out.println("NOPE");
                } else {
                    GraphUser graphUser = response.getGraphObjectAs(GraphUser.class);
                    facebookFriendsNameTemp[i] = graphUser.getName();
                }
            }

            facebookFriends = facebookFriendsTemp;
            facebookFriendsName = facebookFriendsNameTemp;
            return null;
        }
    }

    public void sendMessage() {
        finish();
        Intent home = new Intent(MessageCreateActivity.this, MessageActivity.class);
        startActivity(home);
        //return;
    }

    public void onBackPressed() {
        finish();
        Intent home = new Intent(MessageCreateActivity.this, MessageActivity.class);
        startActivity(home);
        return;
    }
}
