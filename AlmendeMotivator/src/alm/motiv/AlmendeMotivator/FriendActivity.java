package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.adapters.FriendsAdapter;
import alm.motiv.AlmendeMotivator.facebook.FacebookMainActivity;
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
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.mongodb.*;

import java.lang.reflect.Array;
import java.util.*;

public class FriendActivity extends Activity {
    private FriendsAdapter adapter;
    private GraphUser friend;
    private User user = null;
    private GridView friendListView;
    private List<DBObject> allUsers = null;
    private boolean manageFriends = true;
    private boolean initializedFriends = false;
    private int positionSelectedFriend = 0;

    //buttons
    private Button btnFollowMoreFriends;

    //textviews
    private TextView lblFriendsYouFollow;

    //menu
    private String[] mMenuOptions;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendsmenu);
        btnFollowMoreFriends=(Button)findViewById(R.id.followFriends);
        lblFriendsYouFollow=(TextView)findViewById(R.id.lblFriendsYouFollow);

        //for the menu
        mMenuOptions = getResources().getStringArray(R.array.profile_array);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_menu, mMenuOptions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        new DatabaseThread().execute("select");
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
                        Cookie.getInstance().facebookFriends = hasSportopiaAccount(usersArray);
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

                if(manageFriends){
                    positionSelectedFriend=position;
                    showPopUpUnfollow();
                }else{
                    showPopUp(position);
                }
            }
        });


    }

     //we want to sort the usersArray alphabetically
     public static Comparator<GraphUser> sortUsers = new Comparator<GraphUser>() {
        @Override
        public int compare(GraphUser first, GraphUser second) {
            String user1 = first.getName().toLowerCase();
            String user2 = second.getName().toLowerCase();

            //ascending order
            return user1.compareTo(user2);
        }
    };

    private AlertDialog helpDialog;
    private void showPopUpUnfollow(){
        LayoutInflater inflater = getLayoutInflater();

        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setView(inflater.inflate(R.layout.popup_friend, null));
        /*helpBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        helpBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });*/

        helpDialog = helpBuilder.create();
        helpDialog.show();
    }

    public void onVisitProfilePressed(View v){
        Intent displayFriend = new Intent(this, ProfileActivity.class);
        displayFriend.putExtra("viewFriendProfile", true);
        displayFriend.putExtra("facebookIdFriend", friend.getId());
        startActivity(displayFriend);
    }

    public void onUnfollowFriendPressed(View v){
        adapter.removeModel(positionSelectedFriend);
        new DatabaseThread().execute("remove");
        helpDialog.dismiss();
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

                    if(manageFriends){
                        //we want only to show the facebookfriends that the user follows so that he can manage it
                        for(GraphUser facebookFriend: facebookFriends){
                            if(set.contains(facebookFriend.getId())){
                                result.add(facebookFriend);
                            }
                        }
                        Collections.sort(result, sortUsers);
                        return result;
                    }

            //compare strings and put the facebook user that's not yet followed by the user in result
            for(GraphUser facebookFriend: facebookFriends){
                if(!set.contains(facebookFriend.getId())){
                    result.add(facebookFriend);
                }
            }
            Collections.sort(result, sortUsers);
            return result;
        }
        return facebookFriends;

    }

    public ArrayList<GraphUser> hasSportopiaAccount(ArrayList<GraphUser> facebookFriends){

        //we use this method so that in the friendlist only the facebookfriends with a sportopia account are showed
        ArrayList<GraphUser> result =new ArrayList<GraphUser>();

        if(facebookFriends!=null){
            Set<String> set = new HashSet<String>();
            //fill our set, we are going to compare strings
            for(DBObject aFriend: allUsers){
                set.add((String) aFriend.get("facebookID"));
            }

            //compare strings and put the facebook user that also has a sportopia account in the array
            for(GraphUser facebookFriend: facebookFriends){
                if(set.contains(facebookFriend.getId())){
                    result.add(facebookFriend);
                }
            }
            return result;
        }
        return null;
    }

    public void onFollowFriendsPressed(View v){
        manageFriends = false;
        btnFollowMoreFriends.setVisibility(View.GONE);
        lblFriendsYouFollow.setText("Choose friends to follow");
        addFriendsToList(compareFriends());
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Menu.selectItem(position, FriendActivity.this);
        }
    }

    class DatabaseThread extends AsyncTask<String, String, String> {
        private ProgressDialog simpleWaitDialog;

        protected String doInBackground(String... args) {
            MongoClient client = Database.getInstance();
            DB db = client.getDB(Database.uri.getDatabase());
            DBCollection userCollection = db.getCollection("user");
            userCollection.setObjectClass(User.class);

            User match = new User();
            match.put("facebookID", Cookie.getInstance().userEntryId);
            if(args[0].equals("select")){
                user = (User) userCollection.find(match).toArray().get(0);
                allUsers = userCollection.find().toArray();
            }else{
                //update the user with new friends/followers
                User aFriend = new User();
                aFriend.put("facebookID", friend.getId());

                BasicDBObject update = new BasicDBObject();
                if(args[0].equals("remove")){
                    //removes friend from array with pull key
                    update.put("$pull", new BasicDBObject("friends", aFriend));
                }else{
                    update.put("$push", new BasicDBObject("friends", aFriend));
                }
                userCollection.update(match, update);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            simpleWaitDialog = ProgressDialog.show(FriendActivity.this,
                    "Please wait", "Loading");

        }

       protected void onPostExecute(String result) {
          simpleWaitDialog.dismiss();
          if(!initializedFriends){
              initFriends();
              initializedFriends=true;
          }
        }
    }
}
