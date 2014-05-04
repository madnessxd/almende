package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.adapters.MessageAdapter;
import alm.motiv.AlmendeMotivator.facebook.FacebookMainActivity;
import alm.motiv.AlmendeMotivator.facebook.FacebookManager;
import alm.motiv.AlmendeMotivator.models.Challenge;
import alm.motiv.AlmendeMotivator.models.Message;
import alm.motiv.AlmendeMotivator.models.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
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
import java.util.zip.Inflater;

/**
 * Created by Kevin on 26/03/2014.
 */
public class ChallengeViewActivity extends Activity implements Serializable {
    private Intent home;
    private Intent k;
    private Intent intent;

    private String[] mMenuOptions;
    private ListView mDrawerList;
    private String id;

    //when a user adds a comment, this object will be used
    private Message message = new Message();

    private ListView messagesListview;

    private Challenge currentChallenge = null;

    private ArrayList<BasicDBObject> messages = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        mMenuOptions = getResources().getStringArray(R.array.profile_array);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_menu, mMenuOptions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        intent = getIntent();
        messagesListview = (ListView) findViewById(R.id.lstMessages);
        View headerView = View.inflate(this, R.layout.activity_challenge_header, null);
        View footerView = View.inflate(this, R.layout.activity_challenge_footer, null);
        messagesListview.addHeaderView(headerView);
        messagesListview.addFooterView(footerView);

        id = intent.getExtras().getString("id");

        //first database call because we need information about the challenge
        new DatabaseThread().execute("get challenge");
    }

    public void updateUI() {
        TextView title = (TextView) findViewById(R.id.txtStaticChallengeName);
        TextView challenger = (TextView) findViewById(R.id.txtChallenger);
        TextView challengee = (TextView) findViewById(R.id.txtChallengee);
        TextView content = (TextView) findViewById(R.id.viewChallengeContent);
        TextView evidence = (TextView) findViewById(R.id.viewChallengeEvidence);
        TextView reward = (TextView) findViewById(R.id.viewChallengeReward);


        title.setText(currentChallenge.getTitle());
        challenger.setText(currentChallenge.getChallenger());
        challengee.setText(currentChallenge.getChallengee());
        content.setText(currentChallenge.getContent());
        evidence.setText(currentChallenge.getEvidenceAmount() + " " + currentChallenge.getEvidenceType());
        reward.setText(currentChallenge.getReward());

        updateButtons("");

        //get comments from challenge
        messages = currentChallenge.getComments();
        if (messages == null) {
            Message emptyMessage = new Message();
            emptyMessage.setContent("This challenge doesn't have comments.");
            emptyMessage.setAuthor(" ");
            emptyMessage.setDate(new Date());
            messages = new ArrayList<BasicDBObject>();
            messages.add(emptyMessage);
        }
        MessageAdapter adapter = new MessageAdapter(this, messages);
        messagesListview.setAdapter(adapter);

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
                k = new Intent(ChallengeViewActivity.this, ChallengeOverviewActivity.class);
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
        currentChallenge.setStatus("accepted");
        new DatabaseThread().execute("");
        updateButtons("");
    }

    public void onCompletePressed(View v) {
        String gps = "no GPS";
        try{
            gps = getGPS();
        }catch(Exception e){
            System.out.println("no gps" + e);
        }
        Intent newIntent = new Intent(this, ChallengeEvidence.class);
        newIntent.putExtra("evidenceAmount", currentChallenge.getEvidenceAmount());
        newIntent.putExtra("title", currentChallenge.getTitle());
        newIntent.putExtra("challengeid", id);
        newIntent.putExtra("gps", gps);
        this.startActivity(newIntent);
    }

    public String getGPS() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        MyLocationListener locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        locationListener.onLocationChanged(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));

        return locationListener.getLocation();
    }

    public void onEvidencePressed(View v) {
        new DatabaseThread().execute("select");
    }

    public void onDeclinePressed(View v) {
        currentChallenge.setRated("declined");
        new DatabaseThread().execute("");
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
                    currentChallenge.setRated("Approved");
                    currentChallenge.setStatus("closed");
                    new DatabaseThread().execute("");
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
                    currentChallenge.setStatus("closed");
                    currentChallenge.setRated("Disapproved");
                    new DatabaseThread().execute("");
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
            message.setReceiver(currentChallenge.getChallengee());
            message.setLiked("false");
            message.setContent(content.getText().toString());
            message.setDate(new Date());
            return true;
        }
    }

    public void updateButtons(String status) {
        String temp = currentChallenge.getStatus();
        Boolean userMadeChallenge = false;

        if (currentChallenge.getChallenger().equals(Cookie.getInstance().userEntryId)) {
            userMadeChallenge = true;
        }

        Button accept = (Button) findViewById(R.id.btnAccept);
        Button decline = (Button) findViewById(R.id.btnDecline);
        Button complete = (Button) findViewById(R.id.btnComplete);

        if (temp.equals("new_challenge") && (!userMadeChallenge)) {
            accept.setVisibility(View.VISIBLE);
            decline.setVisibility(View.VISIBLE);
        }

        if (temp.equals("accepted") && !(userMadeChallenge)) {
            complete.setVisibility(View.VISIBLE);

            //hide previous buttons
            accept.setVisibility(View.GONE);
            decline.setVisibility(View.GONE);
        }

        if (temp.equals("closed")) {
            LinearLayout buttonRow = (LinearLayout) findViewById(R.id.buttonRow);
            buttonRow.setVisibility(View.GONE);
        }

        if (temp.equals("completed")) {
            Button evidence = (Button) findViewById(R.id.btnEvidence);
            evidence.setVisibility(View.VISIBLE);
            Button approve = (Button) findViewById(R.id.btnApprove);
            approve.setVisibility(View.VISIBLE);

            //hide our complete button
            complete.setVisibility(View.GONE);
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
       /* String categories[] = {"Motivational", "Meet Up", "Inspirational", "Other"};
        final ListView lv = (ListView) convertView.findViewById(R.id.lstCategories);
        lv.setVisibility(View.VISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categories);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                message.setCategory(lv.getItemAtPosition(i).toString());
            }
        });*/

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
                    message.setReceiver(currentChallenge.getChallengee());
                    message.setLiked("false");
                    message.setDate(new Date());
                    message.setContent(content.getText().toString());
                    new DatabaseThread().execute("unchanged");
                    updateMessagesInListview();
                }

                d.dismiss();
            }
        });
    }

    public void updateMessagesInListview() {
        if(messages!=null){
            messages.add(message);
        }
        ((BaseAdapter)((HeaderViewListAdapter)messagesListview.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
    }

    class DatabaseThread extends AsyncTask<String, String, Challenge> {
        private ProgressDialog simpleWaitDialog;
        private Boolean updateUI = false;
        private DB db = null;
        private DBCollection challengeCollection;
        private Challenge current;

        @Override
        protected void onPreExecute() {
            simpleWaitDialog = ProgressDialog.show(ChallengeViewActivity.this,
                    "Please wait", "Processing");

        }

        protected void onPostExecute(Challenge result) {
            simpleWaitDialog.setMessage("Process completed.");
            simpleWaitDialog.dismiss();
            if (updateUI) {
                updateUI();
            }
        }

        protected Challenge doInBackground(String... args) {

            MongoClient client = Database.getInstance();
            db = client.getDB(Database.uri.getDatabase());
            challengeCollection = db.getCollection("challenge");
            challengeCollection.setObjectClass(Challenge.class);

            // get the current challenge from database
            current = new Challenge();
            current.put("_id", new ObjectId(id));

            if (currentChallenge == null) {
                //this variable tells us that the view needs to be constructed for use
                updateUI = true;
                currentChallenge = (Challenge) challengeCollection.findOne(current);
                return null;
            }

            if (args[0].equals("select")) {
                ArrayList<BasicDBObject> evidenceList = currentChallenge.getEvidence();
                downloadEvidence(evidenceList);
                return null;
            } else if (args[0].equals("unchanged")) {
                //if the status is unchanged, we want to add a comment
                addCommentToChallenge();
                return null;
            } else if (args[0] == "") {
                updateQuery();
            }
            return null;
        }

        private void addCommentToChallenge() {
            Challenge challenge = (Challenge) challengeCollection.findOne(current);
            //for some reason we need to get the currentchallenge again from mongodb otherwise it won't update the document

            challengeCollection.update(challenge, new BasicDBObject("$push", new BasicDBObject("comments", message)));
        }

        private void updateQuery() {
            if(currentChallenge.getStatus().equals("closed")&&currentChallenge.getRated().equals("Approved")){
                updateXP();
            }
            challengeCollection.findAndModify(current, currentChallenge);
        }

        private void updateXP(){
            DBCollection userCollection = db.getCollection("user");

            User match = new User();
            match.put("facebookID", currentChallenge.getChallengee().toString());

            User update = (User)userCollection.findOne(match);
            int reward = currentChallenge.getEvidenceAmount()*100;
            try{
                update.setXP(update.getXP()+reward);
            }catch (Exception e){
                update.setXP(reward);
            }

            userCollection.update(match, update);
        }

        private void downloadEvidence(ArrayList<BasicDBObject> evidenceList) {
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

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
