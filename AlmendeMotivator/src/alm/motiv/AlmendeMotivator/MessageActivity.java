package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.facebook.FacebookMainActivity;
import alm.motiv.AlmendeMotivator.facebook.FacebookManager;
import alm.motiv.AlmendeMotivator.models.Message;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.mongodb.*;

import java.util.ArrayList;

public class MessageActivity extends Activity{
    Intent home;
    Intent k;
    private String[] mMenuOptions;
    private ListView mDrawerList;

    private ListView listView;
    private ArrayList<String> runningMessages = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messagesmenu);

        UpdateMessages u = new UpdateMessages();
        u.execute();

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

    public void createMessage(View v){
        k = new Intent(MessageActivity.this, MessageViewActivity.class);
        finish();
        startActivity(k);
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

    public void getMessages(DBCollection userCollection){

        DBObject query = QueryBuilder.start("Author").is(Cookie.getInstance().userEntryId).get();
        DBCursor myCursor = userCollection.find(query);

        while(myCursor.hasNext()){
            DBObject testObj = myCursor.next();
            runningMessages.add("Send: " + testObj.get("Receiver").toString());
        }

        query = QueryBuilder.start("Receiver").is(Cookie.getInstance().userEntryId).get();
        myCursor = userCollection.find(query);

        while(myCursor.hasNext()){
            DBObject testObj = myCursor.next();
            runningMessages.add("Received: " + testObj.get("Author").toString());
        }

    }

    public void showMessages(){
        listView = (ListView) findViewById(R.id.messageList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, runningMessages);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new MessageClickListener());
    }


    private class MessageClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            //GA NAAR DE JUISTE MESSAGE
            System.out.println(runningMessages.get(position));
            String butName = runningMessages.get(position);

            k = new Intent(MessageActivity.this, MessageViewActivity.class);

            if(butName.contains("Send: ")){
                butName = butName.replace("Send: ","");
                k.putExtra("challenger", Cookie.getInstance().userEntryId);
                k.putExtra("challengee", butName);
            }

            if(butName.contains("Received: ")){
                butName = butName.replace("Received: ","");
                k.putExtra("challenger", butName);
                k.putExtra("challengee", Cookie.getInstance().userEntryId);
            }

            finish();
            startActivity(k);
        }
    }

    public void selectItem(int pos){
        switch (pos){
            case 0:
                k = new Intent(MessageActivity.this, ProfileActivity.class);
                break;
            case 1:
                k = new Intent(MessageActivity.this, MessageActivity.class);
                break;
            case 2:
                k = new Intent(MessageActivity.this, ChallengeOverviewActivity.class);
                break;
            case 3:
                k = new Intent(MessageActivity.this, FriendActivity.class);
                break;
            case 4:
                FacebookManager.logout();
                k = new Intent(MessageActivity.this, FacebookMainActivity.class);
                break;
        }
        finish();
        startActivity(k);
    }
    @Override
    public void onBackPressed() {
        finish();
        home = new Intent(MessageActivity.this, ChallengeOverviewActivity.class);
        startActivity(home);
        return;
    }

}
