package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.facebook.FacebookMainActivity;
import alm.motiv.AlmendeMotivator.facebook.FacebookManager;
import alm.motiv.AlmendeMotivator.models.Challenge;
import alm.motiv.AlmendeMotivator.models.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
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
import java.util.Date;

/**
 * Created by Kevin on 26/03/2014.
 */
public class ChallengeViewActivity extends Activity implements Serializable {
    private Intent home;
    private Intent k;
    private Intent intent;

    private String[] mMenuOptions;
    private ListView mDrawerList;
    private TextView title;
    private TextView challenger;
    private TextView challengee;
    private TextView content;
    private TextView evidence;
    private TextView reward;
    private String id;

    //when a user adds a comment, this object will be used
    private Message message = new Message();

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

        if (intent.getExtras().getString("status").equals("accepted")) {
            updateButtons("complete");
        } else if (intent.getExtras().getString("status").equals("completed")) {
            updateButtons("evidence");
        } else if (intent.getExtras().getString("status").equals("closed")) {
            updateButtons("closed");
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
        new DatabaseThread().execute("accept");
        updateButtons("complete");
    }

    public void onCompletePressed(View v) {
        Intent newIntent = new Intent(this, ChallengeEvidence.class);
        newIntent.putExtra("evidenceAmount", intent.getExtras().getInt("evidenceAmount"));
        newIntent.putExtra("title", intent.getExtras().getString("title"));
        newIntent.putExtra("challengeid", id);
        this.startActivity(newIntent);
    }

    public void onEvidencePressed(View v) {
        new DatabaseThread().execute("select");
    }

    public void onDeclinePressed(View v) {
        new DatabaseThread().execute("decline");
    }

    public void onCommentPressed(View v) {
        showPopup();
    }

    public void onApprovePressed(View v) {

        // Use an EditText view to get user input.
        final EditText content = new EditText(this);

        final AlertDialog d = new AlertDialog.Builder(this)
                .setPositiveButton("Approve", null)
                .setNegativeButton("Disapprove", null)
                .setTitle("Decision time")
                .setMessage("Decide if the evidence that is added meets your expectations. " +
                        "If not, be so kind to tell why." +
                        "Remember that the person cannot redo the challenge. It will be closed after " +
                        "your decision.")
                .setView(content)
                .show();

        d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (popUpValidation(content)) {
                    new DatabaseThread().execute("closed", "approved", content.getText().toString());
                    d.dismiss();
                } else {
                    return;
                }
            }
        });

        d.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (popUpValidation(content)) {
                    new DatabaseThread().execute("closed", "disapproved", content.getText().toString());
                    d.dismiss();
                } else {
                    return;
                }
            }
        });
    }

    private boolean popUpValidation(EditText content) {
        Boolean success = true;
        String category = null;

        if (!Validation.hasText(content)) {
            success = false;
        }

        if (!success) {
            Toast.makeText(ChallengeViewActivity.this, "Please fill in everything", Toast.LENGTH_LONG).show();
            return false;
        } else {
            message.setAuthor(Cookie.getInstance().userEntryId);
            message.setTitle("Evidence approvement");
            message.setReceiver(intent.getExtras().getString("challengee"));
            message.setLiked("false");
            message.setDate(new Date());
            return true;
        }
    }

    public void updateButtons(String status) {
        Button accept = (Button) findViewById(R.id.btnAccept);
        Button decline = (Button) findViewById(R.id.btnDecline);

        accept.setVisibility(View.GONE);
        decline.setVisibility(View.GONE);

        if (status.equals("complete")) {
            Button complete = (Button) findViewById(R.id.btnComplete);
            complete.setVisibility(View.VISIBLE);
        } else if (status.equals("closed")) {
            LinearLayout buttonRow = (LinearLayout) findViewById(R.id.buttonRow);
            buttonRow.setVisibility(View.GONE);
        } else {
            Button evidence = (Button) findViewById(R.id.btnEvidence);
            evidence.setVisibility(View.VISIBLE);
            Button approve = (Button) findViewById(R.id.btnApprove);
            approve.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        home = new Intent(ChallengeViewActivity.this, ChallengeOverviewActivity.class);
        startActivity(home);
        return;
    }

    public void showPopup() {
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.popup_comment, null);

        //input for content for the comment
        final EditText content = (EditText) convertView.findViewById(R.id.txtContent);


        //listview so that the categoryf of the comment can be selected
        String categories[] = {"Motivational", "Meet Up", "Inspirational", "Other"};
        final ListView lv = (ListView) convertView.findViewById(R.id.lstCategories);
        lv.setVisibility(View.VISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categories);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                message.setCategory(lv.getItemAtPosition(i).toString());
            }
        });

        final AlertDialog d = new AlertDialog.Builder(this)
                .setPositiveButton("Add Comment", null)
                .setNegativeButton("Cancel", null)
                .setView(convertView)
                .setTitle("Add a comment")
                .show();

        d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Boolean success = true;
                String category = null;
                try {
                    //we put this in a try catch because .getCatgeory can crash when it isn't set
                    category = message.getCatgeory();
                } catch (Exception e) {
                    success = false;
                }

                if (!Validation.hasText(content)) {
                    success = false;
                }

                if (!success) {
                    Toast.makeText(ChallengeViewActivity.this, "Please fill in everything", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    message.setAuthor(Cookie.getInstance().userEntryId);
                    message.setTitle("Comment");
                    message.setReceiver(intent.getExtras().getString("challengee"));
                    message.setLiked("false");
                    message.setDate(new Date());
                    new DatabaseThread().execute("unchanged");
                }

                d.dismiss();
            }
        });
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
        }

        protected Challenge doInBackground(String... args) {
            MongoClient client = Database.getInstance();
            DB db = client.getDB(Database.uri.getDatabase());
            DBCollection challengeCollection = db.getCollection("challenge");
            challengeCollection.setObjectClass(Challenge.class);

            // get the current challenge from database
            Challenge current = new Challenge();
            current.put("_id", new ObjectId(id));

            Challenge aChallenge = (Challenge) challengeCollection.findOne(current);
            ArrayList<BasicDBObject> evidenceList = aChallenge.getEvidence();

            if (args[0].equals("select")) {
                downloadEvidence(db, evidenceList);
                return null;
            } else if (args[0].equals("unchanged")) {
                //if the status is unchanged, we want to add a comment
                addCommentToChallenge(aChallenge, challengeCollection);
                return null;
            }

            updateQuery(current, aChallenge, challengeCollection, args);
            return null;
        }

        private void addCommentToChallenge(Challenge current, DBCollection challengeCollection) {
            challengeCollection.update(current, new BasicDBObject("$push", new BasicDBObject("comments", message)));
        }

        private void updateQuery(Challenge current, Challenge newChallenge, DBCollection challengeCollection, String[] args) {
            if (args[0].equals("closed")) {
                newChallenge.setRated(args[1]);
                newChallenge.put("ratedMessage", args[2]);
            }
            newChallenge.setStatus(args[0]);

            //overwrite the old one with the new one
            challengeCollection.findAndModify(current, newChallenge);
        }

        private void downloadEvidence(DB db, ArrayList<BasicDBObject> evidenceList) {
            GridFS gfsPhoto = new GridFS(db, "challenge");

            for (BasicDBObject evidence : evidenceList) {
                String evidenceID = evidence.get("evidenceID").toString();

                GridFSDBFile image = gfsPhoto.findOne(new ObjectId(evidenceID));

                InputStream inputStream = image.getInputStream();

                OutputStream outputStream = null;

                try {
                    // write the inputStream to a FileOutputStream
                    outputStream = new FileOutputStream(new File(android.os.Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS + "/" + image.getFilename() + ".jpg"));

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
                            Toast.makeText(ChallengeViewActivity.this, "The evidence is placed in your downloads", Toast.LENGTH_LONG).show();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
