package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.adapters.ChallengeAdapter;
import alm.motiv.AlmendeMotivator.facebook.FacebookMainActivity;
import alm.motiv.AlmendeMotivator.facebook.FacebookManager;
import alm.motiv.AlmendeMotivator.models.Challenge;
import alm.motiv.AlmendeMotivator.models.User;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import java.util.ArrayList;

public class ChallengesMenuActivity extends Activity {
    Intent home;
    Intent k;
    ChallengeAdapter adapter;
    private String[] mMenuOptions;
    private ListView mDrawerList;
    private ListView lstNewChallenges;
    private Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challengesmenu);
        mMenuOptions = getResources().getStringArray(R.array.profile_array);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_menu, mMenuOptions));
        //mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        adapter = new ChallengeAdapter(this);
        activity = this;
        getChallenge();
    }

    //Get a single challenge and put it in the listView
    public void getChallenge() {
        //lstNewChallenges = (ListView)findViewById(R.id.grdNewChallenges);
        //lstNewChallenges.setAdapter(adapter);

        DatabaseThread db = new DatabaseThread();
        db.execute();
    }

    class DatabaseThread extends AsyncTask<String, String, String> {

        public ArrayList<Challenge> getChallengeArray() {
            return challengeAray;
        }

        public void setChallengeAray(ArrayList<Challenge> challengeAray) {
            this.challengeAray = challengeAray;
        }

        private ArrayList<Challenge> challengeAray;

        protected String doInBackground(String... args) {
            MongoClient client = Database.getInstance();
            DB db = client.getDB(Database.uri.getDatabase());
            DBCollection challengeCollection = db.getCollection("challenge");
            challengeCollection.setObjectClass(Challenge.class);

            // get the current user from database
            Challenge current = new Challenge();
            //Find all the challenges that have me as the challenger. These are my issued challenges
            current.put("challenger", Cookie.getInstance().userEntryId);

            ArrayList<Challenge> challengeArray = new ArrayList<Challenge>();
            int collectionSize = challengeCollection.find(current).toArray().size();
            for (int i = 0; i < collectionSize; i++) {
                challengeArray.add((Challenge) challengeCollection.find(current).toArray().get(i));
            }
            setChallengeAray(challengeArray);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            ListView lst = (ListView)findViewById(R.id.lstIsseudChallenges);
            ChallengeAdapter adapter = new ChallengeAdapter(activity);

            adapter.setChallenges(getChallengeArray());
            lst.setAdapter(adapter);
        }
    }

    //No idea why this class is here
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    public void selectItem(int pos) {
        switch (pos) {
            case 0:
                k = new Intent(ChallengesMenuActivity.this, ProfileActivity.class);
                break;
            case 1:
                k = new Intent(ChallengesMenuActivity.this, MessageActivity.class);
                break;
            case 2:
                k = new Intent(ChallengesMenuActivity.this, ChallengesMenuActivity.class);
                break;
            case 3:
                k = new Intent(ChallengesMenuActivity.this, FriendActivity.class);
                break;
            case 4:
                FacebookManager.logout();
                k = new Intent(ChallengesMenuActivity.this, FacebookMainActivity.class);
                break;
        }
        finish();
        startActivity(k);
    }

    @Override
    public void onBackPressed() {
        finish();
        home = new Intent(ChallengesMenuActivity.this, MainMenuActivity.class);
        startActivity(home);

        return;
    }

    public void onCreatePressed(View v) {
        startActivity(new Intent(ChallengesMenuActivity.this, ChallengeCreateActivity.class));
    }
}
