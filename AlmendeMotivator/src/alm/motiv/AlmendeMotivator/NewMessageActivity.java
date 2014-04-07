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

import java.util.ArrayList;

public class NewMessageActivity extends Activity{
    Intent home;
    Intent k;
    private String[] mMenuOptions;
    private ListView mDrawerList;

    private ListView listView;

    private String message;
    private String date;

    private String facebookId;

    private ArrayList<String> receivedMessages = new ArrayList<String>();
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        getFacebookID(Session.getActiveSession());

        showMessages();

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

    public void showMessages(){
        listView = (ListView) findViewById(R.id.listView);
        //System.out.println(receivedMessages.toString());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, receivedMessages);

        listView.setAdapter(adapter);
    }

    public void sendMessage(View v){
        EditText mEdit = (EditText)findViewById(R.id.messageInput);
        message = mEdit.getText().toString();

        Time now = new Time();
        now.setToNow();
        date = (now.year + "/" + now.month + "/" + now.monthDay + "-" + now.hour + ":" + now.minute + ":" + now.second);

        DatabaseThread db = new DatabaseThread();
        db.execute();
        Toast.makeText(getApplicationContext(), "Message Send!", Toast.LENGTH_LONG).show();
        mEdit.setText("");
        showMessages();
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
            MongoClient client = Database.getInstance();
            DB db = client.getDB(Database.uri.getDatabase());
            DBCollection userCollection = db.getCollection("messages");
            userCollection.setObjectClass(Message.class);

            Message current = new Message();
            current.put("Receiver", Cookie.getInstance().userEntryId);

            Message challenge = new Message(facebookId, facebookId, "Test Message", message, date, "Normal message", "0");
            userCollection.insert(challenge, WriteConcern.ACKNOWLEDGED);

            //get messages
            int noOfMessages = userCollection.find(current).toArray().size();
            for(int i = 0; i < noOfMessages; i++){
                Message newUser = (Message) userCollection.find(current).toArray().get(i);
                System.out.println(newUser.values().toArray()[4]);
                if(!receivedMessages.contains((String) newUser.values().toArray()[4]))
                receivedMessages.add((String) newUser.values().toArray()[4]);
            }
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