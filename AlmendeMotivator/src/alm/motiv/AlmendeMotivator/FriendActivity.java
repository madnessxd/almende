package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.adapters.FriendsAdapter;
import alm.motiv.AlmendeMotivator.facebook.FacebookManager;
import alm.motiv.AlmendeMotivator.misc.CustomCallback;
import alm.motiv.AlmendeMotivator.models.User;
import android.app.Activity;
import android.app.AlertDialog;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class FriendActivity extends Activity {
    FriendsAdapter adapter;
    GraphUser friend;
    User user = null;
    GridView friendListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendsmenu);

        //get the user object
        if (user == null) {
            new DatabaseThread().execute("select");
        }
        initFriends();
    }

    public void initFriends() {
        FacebookManager.getFriendsOfUser(new CustomCallback() {
            @Override
            public Object callback(Object object) {
                if (object != null && object instanceof List) {
                    List<GraphUser> users = (List<GraphUser>) object;

                    ArrayList<GraphUser> usersArray = new ArrayList<GraphUser>();
                    usersArray.addAll(users);
                    addFriendsToList(usersArray);
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
        //our array with facebook friends minus the friends the user already follows
        /*ArrayList<GraphUser> displayFriends=new ArrayList<GraphUser>();
        ArrayList currentFriends = user.getFriends();

        if(currentFriends!=null){
            //loop through all the friends the user is following
            Iterator<BasicDBObject> iterator = currentFriends.iterator();
            while (iterator.hasNext()) {
                BasicDBObject userFriend = iterator.next();
                for (GraphUser facebookFriend : users) {
                    if (!facebookFriend.getId().equals(userFriend.get("facebookID"))) {
                        //add friend to final facebook friends array when he/she doesn't equal current friends (followers)
                        displayFriends.add(facebookFriend);
                    }else{
                        System.out.println("i already follow youuu");
                    }
                }
            }
            adapter.setModels(displayFriends);
        }else{
            adapter.setModels(users);
        }
        friendListView.setAdapter(adapter);
        */
        adapter.setModels(users);
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(FriendActivity.this, MainMenuActivity.class));
        return;
    }

    class DatabaseThread extends AsyncTask<String, String, String> {
        /**
         * Creating product
         */
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

                userCollection.update(match,update);
            }

            return null;
        }

        /*protected void onPostExecute(String result) {
            //we have to do the friends bit here, because we need the user's followers list to filter the facebook friends
            System.out.println("RESSULTOO" +  result);
            initFriends();
        }*/
    }
}
