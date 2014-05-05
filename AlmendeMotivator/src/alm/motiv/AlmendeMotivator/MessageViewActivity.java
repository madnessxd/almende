package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.facebook.FacebookMainActivity;
import alm.motiv.AlmendeMotivator.facebook.FacebookManager;
import alm.motiv.AlmendeMotivator.models.Message;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.*;
import com.mongodb.*;

import java.util.ArrayList;

public class MessageViewActivity extends Activity{
    Intent home;
    Intent k;
    private String[] mMenuOptions;
    private ListView mDrawerList;
    private ListView listView;
    private String message;
    private String date;
    private String facebookId = Cookie.getInstance().userEntryId;
    private ArrayList<String> receivedMessages = new ArrayList<String>();

    Intent intent;

    private String challenger;
    private String challengee;

    class GetReceiver extends AsyncTask<String, String, String> {
        protected String doInBackground(String... args) {
            MongoClient client = Database.getInstance();
            DB db = client.getDB(Database.uri.getDatabase());
            DBCollection userCollection = db.getCollection("messages");
            userCollection.setObjectClass(Message.class);

            //DBObject query = QueryBuilder.start("Author").is(challenger).get();
            //query = QueryBuilder.start("Receiver").is(challengee).get();
            //DBCursor cursor = userCollection.find(query);

            challenger = intent.getExtras().getString("challenger");
            challengee = intent.getExtras().getString("challengee");

            BasicDBObject query = new BasicDBObject();
            query.put("Author", challenger);
            query.put("Receiver", challengee);
            DBCursor cursor = userCollection.find(query);

            System.out.println("ff tellen: " + cursor.count());
            if(cursor.count()==0){
                challengee = intent.getExtras().getString("challenger");
                challenger = intent.getExtras().getString("challengee");
            }

            System.out.println("a");
            return null;
        }
        @Override
        protected void onPostExecute(String string) {


            UpdateMessages u = new UpdateMessages();
            u.execute();
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        intent = getIntent();

        GetReceiver gr = new GetReceiver();
        gr.execute();

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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, receivedMessages);

        listView.setAdapter(adapter);
    }

    public void sendMessage(View v) throws InterruptedException {
        EditText mEdit = (EditText)findViewById(R.id.messageInput);
        message = Cookie.getInstance().userName + ": " + mEdit.getText().toString();
        Time now = new Time();
        now.setToNow();
        date = (now.year + "/" + now.month + "/" + now.monthDay + "-" + now.hour + ":" + now.minute + ":" + now.second);

        DatabaseThread db = new DatabaseThread();
        db.execute();
        Toast.makeText(getApplicationContext(), "Message Send!", Toast.LENGTH_LONG).show();
        mEdit.setText("");
    }

    class UpdateMessages extends AsyncTask<String, String, String> {
        protected String doInBackground(String... args) {
            MongoClient client = Database.getInstance();
            DB db = client.getDB(Database.uri.getDatabase());
            DBCollection userCollection = db.getCollection("messages");
            userCollection.setObjectClass(Message.class);

            getMessages(userCollection);

            return null;
        }
        @Override
        protected void onPostExecute(String string) {
            showMessages();
        }
    }

    class DatabaseThread extends AsyncTask<String, String, String> {
        protected String doInBackground(String... args) {
            MongoClient client = Database.getInstance();
            DB db = client.getDB(Database.uri.getDatabase());
            DBCollection userCollection = db.getCollection("messages");
            userCollection.setObjectClass(Message.class);

            //DBObject query = QueryBuilder.start("Author").is(challenger).get();
            //query = QueryBuilder.start("Receiver").is(challengee).get();
            //DBCursor cursor = userCollection.find(query);

            BasicDBObject query = new BasicDBObject();
            query.put("Author", challenger);
            query.put("Receiver", challengee);
            DBCursor cursor = userCollection.find(query);

            System.out.println("ff tellen: " + cursor.count());
            if(cursor.count()==0){
                ArrayList<String> messages = new ArrayList<String>();
                messages.add(message);

                Message challenge = new Message(challenger, challengee, "Test Message", messages, date, "Normal message", "0");
                userCollection.insert(challenge, WriteConcern.ACKNOWLEDGED);

            } else{
                BasicDBObject update = new BasicDBObject();
                update.put("$push", new BasicDBObject("Content", message));

                Message newFriend = new Message();
                newFriend.put("Receiver", challengee);

                userCollection.update(newFriend, update);
            }

            getMessages(userCollection);

            return null;
        }
        @Override
        protected void onPostExecute(String string) {
            showMessages();
        }
    }

    public void getMessages(DBCollection userCollection){
        Message current = new Message();
        current.put("Receiver", challengee);
        current.put("Author", challenger);

        if(userCollection.find(current).toArray().size() > 0){
            Message newUser = (Message) userCollection.find(current).toArray().get(0);

            ArrayList<String> arrayMessages = (ArrayList<String>)newUser.get("Content");
            int noOfMessages = arrayMessages.size();
            receivedMessages.clear();
            for(int i = 0; i < noOfMessages; i++){
                receivedMessages.add(arrayMessages.get(i));
            }
        }
    }

    public void selectItem(int pos){
        switch (pos){
            case 0:
                k = new Intent(MessageViewActivity.this, ProfileActivity.class);
                break;
            case 1:
                k = new Intent(MessageViewActivity.this, MessageActivity.class);
                break;
            case 2:
                k = new Intent(MessageViewActivity.this, ChallengeOverviewActivity.class);
                break;
            case 3:
                k = new Intent(MessageViewActivity.this, FriendActivity.class);
                break;
            case 4:
                FacebookManager.logout();
                k = new Intent(MessageViewActivity.this, FacebookMainActivity.class);
                break;
        }
        finish();
        startActivity(k);
    }
    @Override
    public void onBackPressed() {
        finish();
        home = new Intent(MessageViewActivity.this, MessageActivity.class);
        startActivity(home);
        return;
    }

}
