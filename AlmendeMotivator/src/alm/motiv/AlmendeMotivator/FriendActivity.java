package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.adapters.FriendsAdapter;
import alm.motiv.AlmendeMotivator.facebook.FacebookManager;
import alm.motiv.AlmendeMotivator.misc.CustomCallback;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.ListView;
import com.facebook.model.GraphUser;

import java.util.ArrayList;
import java.util.List;

public class FriendActivity extends Activity{
    FriendsAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendsmenu);

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

        GridView friendListView = (GridView) findViewById(R.id.friendsList);
        adapter = new FriendsAdapter(this);
        friendListView.setAdapter(adapter);
    }

    public void addFriendsToList(ArrayList<GraphUser> users){
        adapter.setModels(users);
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(FriendActivity.this, MainMenuActivity.class));
        return;
    }
}
