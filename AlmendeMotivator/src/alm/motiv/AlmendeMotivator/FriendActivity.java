package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.adapters.FriendsAdapter;
import alm.motiv.AlmendeMotivator.facebook.FacebookManager;
import alm.motiv.AlmendeMotivator.misc.CustomCallback;
import alm.motiv.AlmendeMotivator.models.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;
import com.facebook.model.GraphUser;
import com.mongodb.*;

import java.lang.reflect.Array;
import java.util.*;

public class FriendActivity extends Activity {
    FriendsAdapter adapter;
    GraphUser friend;
    User user = null;
    GridView friendListView;

    boolean initializedFriends = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendsmenu);

        //get the user object
        if (user == null) {
            new DatabaseThread().execute("select");
        }
    }

    public void initFriends(){
        FacebookManager.getFriendsOfUser(new CustomCallback() {
            @Override
            public Object callback(Object object) {
                if (object != null && object instanceof List) {
                    List<GraphUser> users = (List<GraphUser>) object;

                    ArrayList<GraphUser> usersArray = new ArrayList<GraphUser>();
                    usersArray.addAll(users);

                    //we want to save the facebookfriends in case we need to access it somewhere else?
                    if(Cookie.getInstance().facebookFriends==null){
                        Cookie.getInstance().facebookFriends=usersArray;
                    }

                    addFriendsToList(compareFriends());
                }
                return null;
            }
        });
        friendListView = (GridView) findViewById(R.id.friendsList);
        adapter = new FriendsAdapter(this);
        friendListView.setAdapter(adapter);
        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                friend = adapter.getItem(position);
                showPopUp(position);
            }
        });
    }

    private void showPopUp(final int position) {
        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle(friend.getName());
        helpBuilder.setMessage("Do you want to follow " + friend.getName());
        helpBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       adapter.removeModel(position);
                       new DatabaseThread().execute("insert");
                    }
                });

        helpBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });

        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }

    public void addFriendsToList(ArrayList<GraphUser> users) {
        adapter.setModels(users);
    }

    public ArrayList compareFriends(){
        ArrayList<GraphUser> facebookFriends = Cookie.getInstance().facebookFriends;

        ArrayList<BasicDBObject> currentFriends = user.getFriends();

        ArrayList<GraphUser> result =new ArrayList<GraphUser>();

        if(currentFriends!=null){
            Set<String> set = new HashSet<String>();
            //fill our set, we are going to compare strings
            for(BasicDBObject aFriend: currentFriends){
                set.add((String) aFriend.get("facebookID"));
            }

            //compare strings and put the facebook user that's not yet followed by the user in result
            for(GraphUser facebookFriend: facebookFriends){
                if(!set.contains(facebookFriend.getId())){
                    result.add(facebookFriend);
                }
            }
            return result;
        }
        return facebookFriends;

    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(FriendActivity.this, MainMenuActivity.class));
        return;
    }

    class DatabaseThread extends AsyncTask<String, String, String> {
        protected String doInBackground(String... args) {
            MongoClient client = Database.getInstance();
            DB db = client.getDB(Database.uri.getDatabase());
            DBCollection userCollection = db.getCollection("user");
            userCollection.setObjectClass(User.class);

            User match = new User();
            match.put("facebookID", Cookie.getInstance().userEntryId);
            if(args[0]=="select"){
                user = (User) userCollection.find(match).toArray().get(0);
            }else if(args[0]!="select"){
                //update the user with new friends/followers
                User newFriend = new User();
                newFriend.put("facebookID", friend.getId());

                BasicDBObject update = new BasicDBObject();
                update.put("$push", new BasicDBObject("friends", newFriend));

                userCollection.update(match, update);
            }

            return null;
        }

       protected void onPostExecute(String result) {
          if(!initializedFriends){
              initFriends();
              initializedFriends=true;
          }
        }
    }
}
