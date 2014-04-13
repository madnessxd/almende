package alm.motiv.AlmendeMotivator;

/**
 * Created by AsterLaptop on 4/13/14.
 */

import java.util.ArrayList;
import java.util.List;

import alm.motiv.AlmendeMotivator.models.ChallengeHeader;
import alm.motiv.AlmendeMotivator.adapters.Item;
import alm.motiv.AlmendeMotivator.models.Challenge;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.mongodb.*;

public class ChallengeOverviewActivity extends Activity implements OnItemClickListener {

    ArrayList<Item> items = new ArrayList<Item>();
    ListView listview = null;
    DatabaseThread DT = new DatabaseThread();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challengeoverview);

        listview = (ListView) findViewById(R.id.listView_main);
        DT.execute();
    }

    public void initListview() {
        List<DBObject> send = DT.sendChallenges;
        List<DBObject> received = DT.receivedChallenges;

        items.add(new ChallengeHeader("Challenges you send"));
        if (send != null) {
            for (DBObject aChallenge : send) {
                items.add((Item) aChallenge);
            }
        } else {
            items.add(new ChallengeHeader("No challenges send"));
        }

        items.add(new ChallengeHeader("Challenges you received"));
        if (received != null) {
            for (DBObject aChallenge : received) {
                items.add((Item) aChallenge);
            }
        } else {
            items.add(new ChallengeHeader("No challenges received"));
        }

        EntryAdapter adapter = new EntryAdapter(this, items);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent home = new Intent(ChallengeOverviewActivity.this, MainMenuActivity.class);
        startActivity(home);
        return;
    }


    @Override
    public void onItemClick(AdapterView arg0, View arg1, int position, long arg3) {

        Challenge item = (Challenge) items.get(position);
        //Toast.makeText(this, "You clicked " + item.getTitle() , Toast.LENGTH_SHORT).show();

        //Open the challengeViewActivity and give the current selected Challenge to the activity
        Intent intent = new Intent(this, ChallengeViewActivity.class);
        //TODO This works as a cheap workaround because I can't send a Serializable object. Fix
        intent.putExtra("title", item.getTitle());
        intent.putExtra("challenger", item.getChallenger());
        intent.putExtra("challengee", item.getChallengee());
        intent.putExtra("content", item.getContent());
        intent.putExtra("evidenceAmount", item.getEvidenceAmount());
        intent.putExtra("evidenceType", item.getEvidenceType());
        intent.putExtra("reward", item.getReward());
        intent.putExtra("status", item.getStatus());
        intent.putExtra("id", item.getID().toString());
        this.startActivity(intent);
    }

    private class DatabaseThread extends AsyncTask<String, String, String> {
        public List<DBObject> sendChallenges = null;
        public List<DBObject> receivedChallenges = null;

        ProgressDialog simpleWaitDialog;

        @Override
        protected void onPreExecute() {
            simpleWaitDialog = ProgressDialog.show(ChallengeOverviewActivity.this,
                    "Please wait", "Processing");
        }

        @Override
        protected void onPostExecute(String string) {
            simpleWaitDialog.setMessage("Process completed.");
            simpleWaitDialog.dismiss();
            initListview();
        }

        @Override
        protected String doInBackground(String... args) {
            MongoClient client = Database.getInstance();
            DB db = client.getDB(Database.uri.getDatabase());
            DBCollection challengeCollection = db.getCollection("challenge");
            challengeCollection.setObjectClass(Challenge.class);

            //find al the challenges the user send
            Challenge query1 = new Challenge();
            query1.put("challenger", Cookie.getInstance().userEntryId);
            sendChallenges = challengeCollection.find(query1).toArray();

            //find al the challenges the user received
            Challenge query2 = new Challenge();
            query2.put("challengee", Cookie.getInstance().userEntryId);
            receivedChallenges = challengeCollection.find(query2).toArray();

            return "succes";
        }

    }
}
