package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.facebook.FacebookMainActivity;
import alm.motiv.AlmendeMotivator.facebook.FacebookManager;
import alm.motiv.AlmendeMotivator.models.Message;
import alm.motiv.AlmendeMotivator.models.User;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.View;
import android.widget.*;
import com.facebook.*;
import com.facebook.model.GraphUser;
import com.mongodb.*;

public class NewMessageActivity extends Activity{
    Intent home;
    Intent k;
    private String[] mMenuOptions;
    private ListView mDrawerList;

    private String message;
    private String date;

    private String facebookId;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        getFacebookID(Session.getActiveSession());

        mMenuOptions = getResources().getStringArray(R.array.profile_array);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_menu, mMenuOptions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }
    public void sendMessage(View v){
        EditText mEdit = (EditText)findViewById(R.id.messageInput);
        message = mEdit.getText().toString();
        System.out.println(message);

        Time now = new Time();
        now.setToNow();
        date = (now.year + "/" + now.month + "/" + now.monthDay + "-" + now.hour + ":" + now.minute);

        DatabaseThread db = new DatabaseThread();
        db.execute();
        Toast.makeText(getApplicationContext(), "Message Send!", Toast.LENGTH_LONG).show();
        mEdit.setText("");
    }

    private void getFacebookID(final Session session) {
        Request request = Request.newMeRequest(session,
                new Request.GraphUserCallback() {

                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        // If the response is successful
                        if (session == Session.getActiveSession()) {
                            if (user != null) {
                                facebookId = user.getId();
                                System.out.println(facebookId);
                            }
                        }
                        if (response.getError() != null) {
                            // Handle error
                        }
                    }
                });
        request.executeAsync();
    }

    class DatabaseThread extends AsyncTask<String, String, String> {
        protected String doInBackground(String... args) {
            // To connect to mongodb server
            MongoClient client = Database.getInstance();
            DB db = client.getDB(Database.uri.getDatabase());

            //get collection and attach class to it
            DBCollection userCollection = db.getCollection("messages");
            userCollection.setObjectClass(Message.class);

            //TODO Add Challengee from appFriendslist
            Message challenge = new Message(facebookId, facebookId, "Test Message", message, date, "Normal message", "0");
            userCollection.insert(challenge, WriteConcern.ACKNOWLEDGED);
            return null;
        }
    }

    public void selectItem(int pos){
        switch (pos){
            case 0:
                k = new Intent(NewMessageActivity.this, ProfileActivity.class);
                break;
            case 1:
                k = new Intent(NewMessageActivity.this, MessageActivity.class);
                break;
            case 2:
                k = new Intent(NewMessageActivity.this, ChallengesMenuActivity.class);
                break;
            case 3:
                k = new Intent(NewMessageActivity.this, FriendActivity.class);
                break;
            case 4:
                FacebookManager.logout();
                k = new Intent(NewMessageActivity.this, FacebookMainActivity.class);
                break;
        }
        finish();
        startActivity(k);
    }
    @Override
    public void onBackPressed() {
        finish();
        home = new Intent(NewMessageActivity.this, MainMenuActivity.class);
        startActivity(home);
        return;
    }

}
