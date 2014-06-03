package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.facebook.FacebookMainActivity;
import alm.motiv.AlmendeMotivator.facebook.FacebookManager;
import alm.motiv.AlmendeMotivator.models.Message;
import alm.motiv.AlmendeMotivator.models.User;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import com.facebook.*;
import com.facebook.model.GraphUser;
import com.google.analytics.tracking.android.EasyTracker;
import com.mongodb.*;
import org.json.JSONObject;

import java.util.ArrayList;

public class MessageActivity extends Activity{
    Intent home;
    Intent k;
    private String[] mMenuOptions;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;

    private ListView listView;
    private ArrayList<String> runningMessages = new ArrayList<String>();

    private ArrayList<String> nameArray = new ArrayList<String>();

    private ProgressDialog simpleWaitDialog;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messagesmenu);

        UpdateMessages u = new UpdateMessages();
        u.execute();

        //showMessages();

        mMenuOptions = getResources().getStringArray(R.array.profile_array);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_menu, mMenuOptions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    }


    //on menu pressed
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            mDrawerLayout.openDrawer(Gravity.LEFT);
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Menu.selectItem(position, MessageActivity.this);
        }
    }

    class GetNameThread extends AsyncTask<String, String, String> {
        protected String doInBackground(String... args) {
            if(Cookie.getInstance().internet){
                try{
                    Session session = Session.getActiveSession();

                    Request request = new Request(session, "me", null, HttpMethod.GET);
                    Response response = request.executeAndWait();


                    for (int i = 0; i < runningMessages.size(); i++){
                        request = new Request(session, runningMessages.get(i), null, HttpMethod.GET);
                        response = request.executeAndWait();

                        if (response.getError() != null) {
                            System.out.println("NOPE");
                        } else {
                            GraphUser graphUser = response.getGraphObjectAs(GraphUser.class);
                            nameArray.add(graphUser.getName());
                            System.out.println(graphUser.getName());
                        }
                    }
                }catch(Exception e){
                    System.out.println(e);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                simpleWaitDialog.dismiss();
                simpleWaitDialog = null;
            } catch (Exception e) {
                // nothing
            }
            showMessages();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        //google analytics
        EasyTracker.getInstance(this).activityStart(this);  // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        //google analytics
        EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }

    public void createMessage(View v){
        k = new Intent(MessageActivity.this, MessageCreateActivity.class);
        finish();
        startActivity(k);
    }

    class UpdateMessages extends AsyncTask<String, String, String> {
        protected String doInBackground(String... args) {
            if(Cookie.getInstance().internet){
                try{
                    MongoClient client = Database.getInstance();
                    DB db = client.getDB(Database.uri.getDatabase());
                    DBCollection userCollection = db.getCollection("messages");
                    userCollection.setObjectClass(Message.class);

                    getMessages(userCollection);
                }catch (Exception e){
                    System.out.println(e);
                }
            }

            return null;
        }
        @Override
        protected void onPreExecute() {
            simpleWaitDialog = ProgressDialog.show(MessageActivity.this,
            "Please wait", "Processing");
        }

        @Override
        protected void onPostExecute(String string) {
            try {
                simpleWaitDialog.dismiss();
                simpleWaitDialog = null;
            } catch (Exception e) {
                // nothing
            }
            showMessages();
        }
    }

    public void getMessages(DBCollection userCollection){
        try{

            DBObject query = QueryBuilder.start("Author").is(Cookie.getInstance().userEntryId).get();
            DBCursor myCursor = userCollection.find(query);

            while(myCursor.hasNext()){
                DBObject testObj = myCursor.next();
                runningMessages.add(testObj.get("Receiver").toString());
            }

            query = QueryBuilder.start("Receiver").is(Cookie.getInstance().userEntryId).get();
            myCursor = userCollection.find(query);

            while(myCursor.hasNext()){
                DBObject testObj = myCursor.next();
                runningMessages.add(testObj.get("Author").toString());
            }
            GetNameThread dbT = new GetNameThread();
            dbT.execute();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public void showMessages(){
        listView = (ListView) findViewById(R.id.messageList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
               R.layout.list_item_detail_message, nameArray);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new MessageClickListener());
    }


    private class MessageClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            //GA NAAR DE JUISTE MESSAGE
            String butName = runningMessages.get(position);

            k = new Intent(MessageActivity.this, MessageViewActivity.class);

                k.putExtra("challenger", Cookie.getInstance().userEntryId);
                k.putExtra("challengee", butName);

            finish();
            startActivity(k);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        home = new Intent(MessageActivity.this, ChallengeOverviewActivity.class);
        startActivity(home);
        return;
    }

}
