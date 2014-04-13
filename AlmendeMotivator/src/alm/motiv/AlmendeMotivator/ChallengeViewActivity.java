package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.facebook.FacebookMainActivity;
import alm.motiv.AlmendeMotivator.facebook.FacebookManager;
import alm.motiv.AlmendeMotivator.models.Challenge;
import alm.motiv.AlmendeMotivator.models.User;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import org.bson.types.ObjectId;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kevin on 26/03/2014.
 */
public class ChallengeViewActivity extends Activity implements Serializable {
    Intent home;
    Intent k;
    private String[] mMenuOptions;
    private ListView mDrawerList;
    TextView title;
    TextView challenger;
    TextView challengee;
    TextView content;
    TextView evidence;
    TextView reward;
    String id;
    Intent intent;
    private DatabaseThread db = new DatabaseThread();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        mMenuOptions = getResources().getStringArray(R.array.profile_array);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_menu, mMenuOptions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        intent = getIntent();
        updateUI();
    }

    public void updateUI() {
        title = (TextView) findViewById(R.id.txtStaticChallengeName);
        challenger = (TextView) findViewById(R.id.txtChallenger);
        challengee = (TextView) findViewById(R.id.txtChallengee);
        content = (TextView) findViewById(R.id.viewChallengeContent);
        evidence = (TextView) findViewById(R.id.viewChallengeEvidence);
        reward = (TextView) findViewById(R.id.viewChallengeReward);
        id = intent.getExtras().getString("id");

        title.setText(intent.getExtras().getString("title"));
        challenger.setText(intent.getExtras().getString("challenger"));
        challengee.setText(intent.getExtras().getString("challengee"));
        content.setText(intent.getExtras().getString("content"));
        evidence.setText(intent.getExtras().getInt("evidenceAmount") + " " + intent.getExtras().getString("evidenceType"));
        reward.setText(intent.getExtras().getString("reward"));

        if(intent.getExtras().getString("status").equals("accepted")){
            updateButtons("complete");
        }else if(intent.getExtras().getString("status").equals("completed")){
            updateButtons("evidence");
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    public void selectItem(int pos) {
        switch (pos) {
            case 0:
                k = new Intent(ChallengeViewActivity.this, ProfileActivity.class);
                break;
            case 1:
                k = new Intent(ChallengeViewActivity.this, MessageActivity.class);
                break;
            case 2:
                k = new Intent(ChallengeViewActivity.this, ChallengesMenuActivity.class);
                break;
            case 3:
                k = new Intent(ChallengeViewActivity.this, FriendActivity.class);
                break;
            case 4:
                FacebookManager.logout();
                k = new Intent(ChallengeViewActivity.this, FacebookMainActivity.class);
                break;
        }
        finish();
        startActivity(k);
    }

    public void onAcceptPressed(View v) {
        db.execute("accept");
        updateButtons("complete");
    }

    public void onCompletePressed(View v) {
        Intent newIntent = new Intent(this, ChallengeEvidence.class);
        newIntent.putExtra("evidenceAmount", intent.getExtras().getInt("evidenceAmount"));
        newIntent.putExtra("title", intent.getExtras().getString("title"));
        newIntent.putExtra("challengeid", id);
        this.startActivity(newIntent);
    }

    public void onEvidencePressed(View v){
        db.execute("select");
    }

    public void onDeclinePressed(View v) {
        db.execute("decline");
    }

    public void updateButtons(String status){
        Button accept = (Button)findViewById(R.id.btnAccept);
        Button decline = (Button)findViewById(R.id.btnDecline);

        accept.setVisibility(View.GONE);
        decline.setVisibility(View.GONE);

        if(status.equals("complete")){
            Button complete = (Button)findViewById(R.id.btnComplete);
            complete.setVisibility(View.VISIBLE);
        }else{
            Button evidence = (Button)findViewById(R.id.btnEvidence);
            evidence.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        home = new Intent(ChallengeViewActivity.this, ChallengeOverviewActivity.class);
        startActivity(home);
        return;
    }

    class DatabaseThread extends AsyncTask<String, String, Challenge> {
        private ProgressDialog simpleWaitDialog;

        @Override
        protected void onPreExecute() {
            simpleWaitDialog = ProgressDialog.show(ChallengeViewActivity.this,
                    "Please wait", "Processing");

        }

        protected void onPostExecute(Challenge result) {
            simpleWaitDialog.setMessage("Process completed.");
            simpleWaitDialog.dismiss();
            Toast.makeText(ChallengeViewActivity.this, "The evidence is placed in your downloads", Toast.LENGTH_LONG).show();

        }

        protected Challenge doInBackground(String... args) {
            MongoClient client = Database.getInstance();
            DB db = client.getDB(Database.uri.getDatabase());
            DBCollection challengeCollection = db.getCollection("challenge");
            challengeCollection.setObjectClass(Challenge.class);

            // get the current user from database
            Challenge current = new Challenge();
            current.put("_id", new ObjectId(id));
            Challenge aChallenge = (Challenge) challengeCollection.findOne(current);

            ArrayList<BasicDBObject> evidenceList = aChallenge.getEvidence();

            if(args[0]=="select"){
                GridFS gfsPhoto = new GridFS(db, "challenge");

                for(BasicDBObject evidence: evidenceList){
                    String evidenceID = evidence.get("evidenceID").toString();

                    GridFSDBFile image = gfsPhoto.findOne(new ObjectId(evidenceID));

                    InputStream inputStream = image.getInputStream();

                    OutputStream outputStream = null;

                    try {
                        // write the inputStream to a FileOutputStream
                        outputStream = new FileOutputStream(new File(android.os.Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS + "/" +image.getFilename()+".jpg"));

                        int read = 0;
                        byte[] bytes = new byte[1024];

                        while ((read = inputStream.read(bytes)) != -1) {
                            outputStream.write(bytes, 0, read);
                        }

                        System.out.println("Done!");

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (outputStream != null) {
                            try {
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }else{
                //args[0] holds the status
                updateQuery(current, aChallenge, challengeCollection, args[0]);
            }

            return null;
        }

        private void updateQuery(Challenge current, Challenge newChallenge, DBCollection challengeCollection, String status) {
            newChallenge.setStatus(status);

            //overwrite the old one with the new one
            challengeCollection.findAndModify(current, newChallenge);
        }
    }
}
